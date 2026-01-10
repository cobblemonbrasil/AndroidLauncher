package net.kdt.pojavlaunch.modloaders.modpacks.api.autoupdate;

public class InstalledFileEntry {

    public final String path;
    public final String sha1;

    public InstalledFileEntry(String path, String sha1) {
        this.path = path;
        this.sha1 = sha1;
    }
}
