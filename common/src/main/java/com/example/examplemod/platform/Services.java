package com.example.examplemod.platform;

import com.example.examplemod.Constants;
import com.example.examplemod.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

// Java's ServiceLoader finds the implementation of an interface that the
// running loader's jar provides. Each loader module lists its own in
// src/main/resources/META-INF/services/<fully qualified interface name>, a
// text file holding the fully qualified name of the implementing class.
public final class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    private Services() {
    }

    public static <T> T load(Class<T> clazz) {
        // This class's loader, not the thread's: the mod's classes may not
        // be visible from the thread that first touches this class.
        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No service found for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
