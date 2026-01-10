package net.kdt.pojavlaunch.modloaders.modpacks.api.autoupdate;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class LatestInstallInfo {

    public static final int LATEST_FILE_VERSION = 1;

    public int version = LATEST_FILE_VERSION;
    public String modpackVersion;
    public List<InstalledFileEntry> files;

    public LatestInstallInfo(String modpackVersion, List<InstalledFileEntry> files) {
        this.modpackVersion = modpackVersion;
        this.files = files;
    }
}
