package com.example.examplemod.fabric;

import com.example.examplemod.CommonClass;
import com.example.examplemod.Constants;
import net.fabricmc.api.ModInitializer;

// The "main" entry point in fabric.mod.json. Fabric API and common/ are both
// available here.
public class ExampleModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
    }
}
