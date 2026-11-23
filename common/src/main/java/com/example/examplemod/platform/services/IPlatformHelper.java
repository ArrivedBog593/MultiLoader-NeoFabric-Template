package com.example.examplemod.platform.services;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform.
     *
     * @return the name of the current platform
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId the mod to check
     * @return true if the mod is loaded
     */
    boolean isModLoaded(String modId);

    /**
     * Checks if the game runs in a development environment (from the IDE).
     *
     * @return true in a development environment
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type.
     *
     * @return "development" or "production"
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }
}
