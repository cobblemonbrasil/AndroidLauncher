package net.kdt.pojavlaunch.modloaders.modpacks.api.autoupdate;

import net.kdt.pojavlaunch.modloaders.modpacks.api.ModLoader;

import java.util.List;

public class InstalledModpack {

    public final ModLoader loader;
    public final List<InstalledFileEntry> files;

    public InstalledModpack(ModLoader loader, List<InstalledFileEntry> files) {
        this.loader = loader;
        this.files = files;
    }

}

