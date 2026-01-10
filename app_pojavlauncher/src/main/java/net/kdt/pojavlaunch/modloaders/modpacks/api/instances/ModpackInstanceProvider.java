package net.kdt.pojavlaunch.modloaders.modpacks.api.instances;

import net.kdt.pojavlaunch.instances.Instance;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ModpackInstanceProvider {

    @NotNull Instance provideInstance(ModDetail modDetail);

}
