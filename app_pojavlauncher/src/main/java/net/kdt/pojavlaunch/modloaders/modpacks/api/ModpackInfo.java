package net.kdt.pojavlaunch.modloaders.modpacks.api;

import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;

public class ModpackInfo {

    public final ModItem item;
    public final ModDetail detail;

    public ModpackInfo(ModItem item, ModDetail detail) {
        this.item = item;
        this.detail = detail;
    }


}

