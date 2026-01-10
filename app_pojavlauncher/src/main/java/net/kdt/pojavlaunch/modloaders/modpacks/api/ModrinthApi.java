package net.kdt.pojavlaunch.modloaders.modpacks.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kdt.mcgui.ProgressLayout;

import br.com.cobblemonbrasil.androidlauncher.R;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.downloader.Downloader;
import net.kdt.pojavlaunch.downloader.TaskMetadata;
import net.kdt.pojavlaunch.mirrors.DownloadMirror;
import net.kdt.pojavlaunch.modloaders.modpacks.api.autoupdate.InstalledFileEntry;
import net.kdt.pojavlaunch.modloaders.modpacks.api.autoupdate.InstalledModpack;
import net.kdt.pojavlaunch.modloaders.modpacks.api.instances.ModpackInstanceProvider;
import net.kdt.pojavlaunch.modloaders.modpacks.models.Constants;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModrinthIndex;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchResult;
import net.kdt.pojavlaunch.utils.FileUtils;
import net.kdt.pojavlaunch.utils.ZipUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.TeeInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModrinthApi implements ModpackApi {
    private final ApiHandler mApiHandler;

    public ModrinthApi() {
        mApiHandler = new ApiHandler("https://api.modrinth.com/v2");
    }

    @Override
    public SearchResult searchMod(SearchFilters searchFilters, SearchResult previousPageResult) {
        ModrinthSearchResult modrinthSearchResult = (ModrinthSearchResult) previousPageResult;

        // Fixes an issue where the offset being equal or greater than total_hits is ignored
        if (modrinthSearchResult != null && modrinthSearchResult.previousOffset >= modrinthSearchResult.totalResultCount) {
            ModrinthSearchResult emptyResult = new ModrinthSearchResult();
            emptyResult.results = new ModItem[0];
            emptyResult.totalResultCount = modrinthSearchResult.totalResultCount;
            emptyResult.previousOffset = modrinthSearchResult.previousOffset;
            return emptyResult;
        }


        // Build the facets filters
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder facetString = new StringBuilder();
        facetString.append("[");
        facetString.append(String.format("[\"project_type:%s\"]", searchFilters.isModpack ? "modpack" : "mod"));
        if (searchFilters.mcVersion != null && !searchFilters.mcVersion.isEmpty())
            facetString.append(String.format(",[\"versions:%s\"]", searchFilters.mcVersion));
        facetString.append("]");
        params.put("facets", facetString.toString());
        params.put("query", searchFilters.name);
        params.put("limit", 50);
        params.put("index", "relevance");
        if (modrinthSearchResult != null)
            params.put("offset", modrinthSearchResult.previousOffset);

        JsonObject response = mApiHandler.get("search", params, JsonObject.class);
        if (response == null) return null;
        JsonArray responseHits = response.getAsJsonArray("hits");
        if (responseHits == null) return null;

        ModItem[] items = new ModItem[responseHits.size()];
        for (int i = 0; i < responseHits.size(); ++i) {
            JsonObject hit = responseHits.get(i).getAsJsonObject();
            items[i] = new ModItem(
                    Constants.SOURCE_MODRINTH,
                    hit.get("project_type").getAsString().equals("modpack"),
                    hit.get("project_id").getAsString(),
                    hit.get("title").getAsString(),
                    hit.get("description").getAsString(),
                    hit.get("icon_url").getAsString()
            );
        }
        if (modrinthSearchResult == null) modrinthSearchResult = new ModrinthSearchResult();
        modrinthSearchResult.previousOffset += responseHits.size();
        modrinthSearchResult.results = items;
        modrinthSearchResult.totalResultCount = response.get("total_hits").getAsInt();
        return modrinthSearchResult;
    }

    @Override
    public ModDetail getModDetails(ModItem item) {

        JsonArray response = mApiHandler.get(String.format("project/%s/version", item.id), JsonArray.class);
        if (response == null) return null;
        System.out.println(response);
        String[] names = new String[response.size()];
        String[] mcNames = new String[response.size()];
        String[] urls = new String[response.size()];
        String[] ids = new String[response.size()];
        String[] hashes = new String[response.size()];

        for (int i = 0; i < response.size(); ++i) {
            JsonObject version = response.get(i).getAsJsonObject();
            names[i] = version.get("name").getAsString();
            mcNames[i] = version.get("game_versions").getAsJsonArray().get(0).getAsString();
            urls[i] = version.get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
            ids[i] = version.get("id").getAsString();
            // Assume there may not be hashes, in case the API changes
            JsonObject hashesMap = version.getAsJsonArray("files").get(0).getAsJsonObject()
                    .get("hashes").getAsJsonObject();
            if (hashesMap == null || hashesMap.get("sha1") == null) {
                hashes[i] = null;
                continue;
            }

            hashes[i] = hashesMap.get("sha1").getAsString();
        }

        return new ModDetail(item, names, mcNames, urls, ids, hashes);
    }

    public ModItem getModelItemFromModpack(String slug) {
        JsonObject object = mApiHandler.get(String.format("project/%s", slug), JsonObject.class);
        if (object == null) {
            return null;
        }
        String id = object.get("id").getAsString();
        String title = object.get("title").getAsString();
        String description = object.get("description").getAsString();
        String iconUrl = object.get("icon_url").getAsString();
        return new ModItem(Constants.SOURCE_MODRINTH, true, id, title, description, iconUrl);
    }

    @Override
    public ModLoader installModpack(ModDetail modDetail, int selectedVersion, ModpackInstanceProvider instanceProvider) throws IOException {
        //TODO considering only modpacks for now
        return ModpackInstaller.installModpack(modDetail, selectedVersion, this::installMrpack, instanceProvider);
    }

    private static ModLoader createInfo(ModrinthIndex modrinthIndex) {
        if (modrinthIndex == null) return null;
        Map<String, String> dependencies = modrinthIndex.dependencies;
        String mcVersion = dependencies.get("minecraft");
        if (mcVersion == null) return null;
        String modLoaderVersion;
        if ((modLoaderVersion = dependencies.get("forge")) != null) {
            return new ModLoader(ModLoader.MOD_LOADER_FORGE, modLoaderVersion, mcVersion);
        }
        if ((modLoaderVersion = dependencies.get("fabric-loader")) != null) {
            return new ModLoader(ModLoader.MOD_LOADER_FABRIC, modLoaderVersion, mcVersion);
        }
        if ((modLoaderVersion = dependencies.get("quilt-loader")) != null) {
            return new ModLoader(ModLoader.MOD_LOADER_QUILT, modLoaderVersion, mcVersion);
        }
        if ((modLoaderVersion = dependencies.get("neoforge")) != null) {
            return new ModLoader(ModLoader.MOD_LOADER_NEOFORGE, modLoaderVersion, mcVersion);
        }

        return null;
    }

    private InstalledModpack installMrpack(File mrpackFile, File instanceDestination, List<String> ignoredFiles) throws IOException {
        try (ZipFile modpackZipFile = new ZipFile(mrpackFile)) {
            final List<InstalledFileEntry> files = new ArrayList<>();
            ModrinthIndex modrinthIndex = Tools.GLOBAL_GSON.fromJson(
                    Tools.read(ZipUtils.getEntryStream(modpackZipFile, "modrinth.index.json")),
                    ModrinthIndex.class);
            try {
                new ModrinthDownloader().startDownloads(modrinthIndex.files, instanceDestination, ignoredFiles, files);
            } catch (InterruptedException e) {
                throw new IOException("NIY: InterruptedException", e);
            }
            // Not added in downloader because exception may occur
            for (ModrinthIndex.ModrinthIndexFile indexFile : modrinthIndex.files) {
                if (!ignoredFiles.contains(indexFile.path)) {
                    files.add(new InstalledFileEntry(indexFile.path, indexFile.hashes.sha1));
                }
            }
            ProgressLayout.setProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.modpack_download_applying_overrides, 1, 2);
            zipExtractMrpackOverrides(modpackZipFile, "overrides/", instanceDestination, ignoredFiles, files);
            ProgressLayout.setProgress(ProgressLayout.INSTALL_MODPACK, 50, R.string.modpack_download_applying_overrides, 2, 2);
            zipExtractMrpackOverrides(modpackZipFile, "client-overrides/", instanceDestination, ignoredFiles, files);
            return new InstalledModpack(createInfo(modrinthIndex), files);
        }
    }

    private void zipExtractMrpackOverrides(ZipFile zipFile, String dirName, File destination, List<String> ignoredFiles, List<InstalledFileEntry> files) throws IOException {
        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

        int dirNameLen = dirName.length();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = zipEntries.nextElement();
            String entryName = zipEntry.getName();
            if (!entryName.startsWith(dirName) || zipEntry.isDirectory()) continue;
            String path = entryName.substring(dirNameLen);
            // Ignore if this file was modified by user
            if (ignoredFiles.contains(path)) {
                continue;
            }
            File zipDestination = new File(destination, path);
            FileUtils.ensureParentDirectory(zipDestination);
            try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                String zipSha1 = DigestUtils.sha1Hex(inputStream);
                files.add(new InstalledFileEntry(path, zipSha1));
                if (zipDestination.exists()) {
                    try (FileInputStream fis = new FileInputStream(zipDestination)) {
                        String fileSha1 = DigestUtils.sha1Hex(fis);
                        if (fileSha1.equals(zipSha1)) {
                            // Saved file is same
                            continue;
                        } else {
                            zipDestination.delete();
                        }
                    }
                }
            }
            try (InputStream inputStream = zipFile.getInputStream(zipEntry);
                 OutputStream outputStream = new FileOutputStream(zipDestination)) {
                IOUtils.copy(inputStream, outputStream);
            }
        }
    }

    class ModrinthSearchResult extends SearchResult {
        int previousOffset;
    }

    static class ModrinthDownloader extends Downloader {
        public ModrinthDownloader() {
            super(ProgressLayout.INSTALL_MODPACK);
        }

        protected void startDownloads(ModrinthIndex.ModrinthIndexFile[] indexFiles, File instanceDestination, List<String> ignoredFiles, List<InstalledFileEntry> files) throws IOException, InterruptedException {
            String absoluteInstancePath = instanceDestination.getAbsolutePath();
            ArrayList<TaskMetadata> taskMetadatas = new ArrayList<>(indexFiles.length);
            for (ModrinthIndex.ModrinthIndexFile file : indexFiles) {
                if (ignoredFiles.contains(file.path)) {
                    continue;
                }
                File targetPath = new File(instanceDestination, file.path);
                if (!targetPath.getAbsolutePath().startsWith(absoluteInstancePath))
                    throw new IOException("Bad path!");
                FileUtils.ensureParentDirectory(targetPath);
                if (targetPath.exists()) {
                    try (FileInputStream fis = new FileInputStream(targetPath)) {
                        String fileSha1 = DigestUtils.sha1Hex(fis);
                        if (fileSha1.equals(file.hashes.sha1)) {
                            // Saved file is same
                            continue;
                        } else {
                            targetPath.delete();
                        }
                    }
                }
                taskMetadatas.add(new TaskMetadata(
                        targetPath, new URL(file.downloads[0]), // TODO source selection
                        file.fileSize, file.hashes.sha1,
                        DownloadMirror.DOWNLOAD_CLASS_NONE
                ));
            }
            runDownloads(taskMetadatas);
        }
    }
}
