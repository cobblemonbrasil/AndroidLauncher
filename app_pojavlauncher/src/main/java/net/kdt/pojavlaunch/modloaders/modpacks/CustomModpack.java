package net.kdt.pojavlaunch.modloaders.modpacks;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.instances.Instance;
import net.kdt.pojavlaunch.instances.InstanceManager;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModpackInfo;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModrinthApi;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class CustomModpack {

    public static final File INSTANCE_DIR = new File(Tools.DIR_GAME_HOME, "custom_instances/cobblemon_realms_mobile");
    public static final String MODPACK_SLUG = "cobblemon-brasil-mobile";
    public static final ModrinthApi MODRINTH_API = new ModrinthApi();
    private static Instance instance;

    public static Instance getInstance() {
        load();
        return instance;
    }

    private static void load()  {
        if (instance != null) {
            return;
        }
        if (INSTANCE_DIR.exists()) {
            instance = InstanceManager.read(INSTANCE_DIR);
            if (instance != null) {
                return;
            }
        }
        instance = new Instance();
        try {
            FileUtils.ensureDirectory(INSTANCE_DIR);
            instance.mInstanceRoot = INSTANCE_DIR;
            instance.name = MODPACK_SLUG;
            instance.write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ModpackInfo fetchModpackInfo() {
        ModItem modItem = MODRINTH_API.getModelItemFromModpack(MODPACK_SLUG);
        if (modItem == null) {
            throw new IllegalStateException("Failed to load default modpack " + MODPACK_SLUG);
        }
        ModDetail modDetail = MODRINTH_API.getModDetails(modItem);
        if (modDetail == null) {
            throw new IllegalStateException("Failed to load default modpack " + MODPACK_SLUG);
        }
        return new ModpackInfo(modItem, modDetail);
    }

}
