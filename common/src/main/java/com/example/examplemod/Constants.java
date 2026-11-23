package com.example.examplemod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants {

    // Must match mod_id in gradle.properties.
    public static final String MOD_ID = "examplemod";
    public static final String MOD_NAME = "Example Mod";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private Constants() {
    }
}
