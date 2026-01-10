package net.kdt.pojavlaunch.modloaders.modpacks.api;

import android.util.Log;

import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.instances.Instance;
import net.kdt.pojavlaunch.instances.InstanceManager;
import net.kdt.pojavlaunch.modloaders.modpacks.api.autoupdate.InstalledFileEntry;
import net.kdt.pojavlaunch.modloaders.modpacks.api.autoupdate.InstalledModpack;
import net.kdt.pojavlaunch.modloaders.modpacks.api.autoupdate.LatestInstallInfo;
import net.kdt.pojavlaunch.modloaders.modpacks.api.instances.ModpackInstanceProvider;
import net.kdt.pojavlaunch.modloaders.modpacks.imagecache.ModIconCache;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.progresskeeper.DownloaderProgressWrapper;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import git.artdeell.mojo.R;

public class ModpackInstaller {

    public static ModLoader installModpack(ModDetail modDetail, int selectedVersion, InstallFunction installFunction, ModpackInstanceProvider instanceProvider) throws IOException {
        String versionUrl = modDetail.versionUrls[selectedVersion];
        String versionHash = modDetail.versionHashes[selectedVersion];
        String modpackName = (modDetail.title.toLowerCase(Locale.ROOT) + " " + modDetail.versionNames[selectedVersion])
                .trim().replaceAll("[\\\\/:*?\"<>| \\t\\n]", "_");
        if (versionHash != null) {
            modpackName += "_" + versionHash;
        }
        if (modpackName.length() > 255) {
            modpackName = modpackName.substring(0, 255);
        }

        ModLoader modLoaderInfo;
        Instance instance = instanceProvider.provideInstance(modDetail);
//        Instance instance = InstanceManager.createInstance(i->{
//            i.name = modDetail.title;
//        }, modpackName.substring(0, Math.min(16,modpackName.length())));

        // Ignore modified files
        final File latestInstallInfoFile = new File(instance.getGameDirectory(), "latest_install_info");
        final List<String> ignoredFiles = new ArrayList<>();
        LatestInstallInfo oldLatestInstallInfo = null;
        if (instance.getGameDirectory().exists() && latestInstallInfoFile.exists()) {
            oldLatestInstallInfo = Tools.GLOBAL_GSON.fromJson(Tools.read(latestInstallInfoFile), LatestInstallInfo.class);
            for (InstalledFileEntry fileEntry : oldLatestInstallInfo.files) {
                File relativeFile = new File(instance.getGameDirectory(), fileEntry.path);
                if (relativeFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(relativeFile)) {
                        String relativeFileSha1 = DigestUtils.sha1Hex(fis);
                        if (!relativeFileSha1.equals(fileEntry.sha1)) {
                            ignoredFiles.add(fileEntry.path);
                            Log.i("ModpackInstaller", "Ignored file '" + fileEntry.path + "' when installing modpack " + modDetail.id +
                                    ": '" + relativeFileSha1 + "' is different of '" + fileEntry.sha1 + "'");
                        }
                    }
                }
            }
            latestInstallInfoFile.delete();
        }

        // Get the modpack file
        File modpackFile = new File(Tools.DIR_CACHE, modpackName + ".cf"); // Cache File
        try {
            byte[] downloadBuffer = new byte[8192];
            DownloadUtils.ensureSha1(modpackFile, versionHash, (Callable<Void>) () -> {
                DownloadUtils.downloadFileMonitored(versionUrl, modpackFile, downloadBuffer,
                        new DownloaderProgressWrapper(R.string.modpack_download_downloading_metadata,
                                ProgressLayout.INSTALL_MODPACK));
                return null;
            });

            // Install the modpack
            InstalledModpack installedModpack = installFunction.installModpack(modpackFile, instance.getGameDirectory(), ignoredFiles);
            if (installedModpack == null)
                throw new IOException("Unknown modpack mod loader information");
            modLoaderInfo = installedModpack.loader;

            if (modLoaderInfo == null)
                throw new IOException("Unknown modpack mod loader information");

            if (modLoaderInfo.requiresGuiInstallation()) {
                instance.installer = modLoaderInfo.createInstaller();
            } else {
                String versionId = modLoaderInfo.installHeadlessly();
                if (versionId == null) throw new IOException("Unknown mod loader version");
                instance.versionId = versionId;
            }
            instance.modpackVersionId = modDetail.versionIds[selectedVersion];
            instance.write();
            ModIconCache.writeInstanceImage(instance, modDetail.getIconCacheTag());

            InstanceManager.setSelectedInstance(instance);
            if (modLoaderInfo.requiresGuiInstallation()) {
                instance.installer.start();
            }
            // Delete removed files from modpack
            if (oldLatestInstallInfo != null) {
                for (InstalledFileEntry fileEntry : oldLatestInstallInfo.files) {
                    if (ignoredFiles.contains(fileEntry.path)) {
                        continue;
                    }
                    boolean found = false;
                    for (InstalledFileEntry installedFileEntry : installedModpack.files) {
                        if (installedFileEntry.path.equals(fileEntry.path)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        File file = new File(instance.getGameDirectory(), fileEntry.path);
                        file.delete();
                    }
                }
            }
            LatestInstallInfo latestInstallInfo = new LatestInstallInfo(instance.versionId, installedModpack.files);
            if (latestInstallInfoFile.exists()) {
                latestInstallInfoFile.delete();
            }
            FileUtils.write(latestInstallInfoFile, Tools.GLOBAL_GSON.toJson(latestInstallInfo));
        } catch (IOException e) {
            InstanceManager.removeInstance(instance);
            throw e;
        } finally {
            modpackFile.delete();
            ProgressLayout.clearProgress(ProgressLayout.INSTALL_MODPACK);
        }

        return modLoaderInfo;
    }

    interface InstallFunction {
        InstalledModpack installModpack(File modpackFile, File instanceDestination, List<String> ignoredFiles) throws IOException;
    }
}
