package net.plazmix.core.api.module;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Log4j2
public final class ModuleManager {

    @Getter
    protected final Map<String, CoreModule> moduleMap = new LinkedHashMap<>();


    @SneakyThrows
    public void loadModules(@NonNull File modulesFolder) {
        File[] moduleFileArray = modulesFolder.listFiles();

        if (moduleFileArray == null) {
            return;
        }

        for (File moduleFile : moduleFileArray) {
            loadModuleFile(moduleFile);
        }
    }

    @SneakyThrows
    public void loadModuleFile(@NonNull File moduleFile) {
        synchronized (moduleMap) {

            if (!moduleFile.getName().endsWith(".jar")) {
                return;
            }

            JarFile moduleJar = new JarFile(moduleFile);
            Enumeration<JarEntry> jarEntryEnumeration = moduleJar.entries();

            URLClassLoader urlClassLoader = new URLClassLoader(
                    new URL[]{moduleFile.toURI().toURL()}, this.getClass().getClassLoader()
            );

            while (jarEntryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = jarEntryEnumeration.nextElement();

                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                    continue;
                }

                String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
                className = className.replace('/', '.');

                Class<?> moduleMainClass = Class.forName(className, true, urlClassLoader);
                CoreModuleInfo coreModuleInfo = moduleMainClass.getDeclaredAnnotation(CoreModuleInfo.class);

                if (coreModuleInfo == null || (getModule(coreModuleInfo.name()) != null && getModule(coreModuleInfo.name()).isEnabled())) {
                    continue;
                }

                CoreModule coreModule = ((CoreModule) moduleMainClass.newInstance());

                coreModule.setModuleInfo(coreModuleInfo);
                coreModule.initialize(moduleFile, moduleJar);

                coreModule.enableModule();
                break;
            }
        }
    }

    public void handleAllModules(@NonNull CoreModuleHandler coreModuleHandler) {
        for (@NonNull CoreModule coreModule : moduleMap.values())
            coreModuleHandler.handle(coreModule);
    }

    public void handleModule(@NonNull String moduleName, @NonNull CoreModuleHandler coreModuleHandler) {
        CoreModule coreModule = getModule(moduleName);
        coreModuleHandler.handle(coreModule);
    }

    public CoreModule getModule(@NonNull String moduleName) {
        return moduleMap.get(moduleName.toLowerCase());
    }

}
