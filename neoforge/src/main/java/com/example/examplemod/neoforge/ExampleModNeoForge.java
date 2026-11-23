package com.example.examplemod.neoforge;

import com.example.examplemod.CommonClass;
import com.example.examplemod.Constants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// NeoForge finds this class by its @Mod annotation. NeoForge and common/ are
// both available here.
@Mod(Constants.MOD_ID)
public class ExampleModNeoForge {

    public ExampleModNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();
    }
}
