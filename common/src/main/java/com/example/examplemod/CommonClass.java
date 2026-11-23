package com.example.examplemod;

import com.example.examplemod.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

// Part of common/, shared by every loader. Code here can only use vanilla
// Minecraft, the libraries vanilla uses, and third-party libraries that ship
// loader-neutral binaries: no NeoForge events, no Fabric API. Write most of
// the mod here and call it from each loader's entry point.
public final class CommonClass {

    private CommonClass() {
    }

    public static void init() {
        Constants.LOG.info("Hello from common init on {}, in a {} environment!",
                Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        // What each loader offers in its own way goes through a small
        // interface in common/ with one implementation per loader (see
        // Services).
        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("{} is loaded", Constants.MOD_NAME);
        }
    }
}
