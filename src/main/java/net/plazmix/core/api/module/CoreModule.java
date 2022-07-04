package net.plazmix.core.api.module;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.connection.protocol.server.SBukkitCommandsPacket;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.ServerManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

@Getter
@Log4j2
public abstract class CoreModule {

    protected JarFile moduleJar;
    protected File moduleFile;

    protected String name;
    protected String author;

    protected String[] depends;

    protected File moduleFolder;

    protected long enableMillis;

    @Setter
    protected boolean enabled;

    @Setter
    protected CoreModuleInfo moduleInfo;

    protected final PlazmixCore core                  = PlazmixCore.getInstance();
    protected final CoreModuleManagement management = new CoreModuleManagement(this);


    protected abstract void onEnable();
    protected abstract void onDisable();


    protected void initialize(@NonNull File moduleFile, @NonNull JarFile moduleJar) {
        this.moduleJar = moduleJar;
        this.moduleFile = moduleFile;

        this.name = moduleInfo.name();
        this.author = moduleInfo.author();
        this.depends = moduleInfo.depends();

        this.moduleFolder = PlazmixCore.getInstance().getModulesFolder()
                .toPath()
                .resolve(name)
                .toFile();
    }

    public void enableModule() {
        setEnabled(true);
        log.info("[ModuleManager] Enable module " + name + " by " + author);

        PlazmixCore.getInstance().getModuleManager()
                .getModuleMap().put(getName().toLowerCase(), this);

        this.enableMillis = System.currentTimeMillis();
        this.onEnable();

        ServerManager serverManager = PlazmixCore.getInstance().getServerManager();
        serverManager.getBukkitServers().values().forEach(abstractServer -> abstractServer.sendPacket(new SBukkitCommandsPacket()));
    }

    public void disableModule() {
        setEnabled(false);
        log.info("[ModuleManager] Disable module " + name + " by " + author);

        management.unregisterCommands();
        management.unregisterListeners();

        onDisable();
    }

    public void reloadModule() {
        unloadModule();
        loadModule();
    }


    @SneakyThrows
    public void unloadModule() {
        disableModule();

        moduleJar.close();
    }

    @SneakyThrows
    public void loadModule() {
        PlazmixCore.getInstance().getModuleManager().loadModuleFile(moduleFile);
    }

    @SneakyThrows
    public void saveResource(@NonNull String resourceName) {
        Path moduleFolderPath = moduleFolder.toPath();
        Path resourcePath = moduleFolderPath.resolve(resourceName);

        if (!Files.exists(moduleFolderPath)) {
            Files.createDirectory(moduleFolderPath);
        }

        if (!Files.exists(resourcePath)) {
            Files.copy(getClass().getResourceAsStream(resourceName), resourcePath);
        }
    }

    public void execute(@NonNull AbstractServer server, @NonNull ModuleExecuteType executeType, @NonNull String key, Object value) {
        PlazmixCore.getInstance().execute(server, executeType, name, key, value);
    }

    public void executeBroadcast(@NonNull ModuleExecuteType executeType, @NonNull String key, Object value) {
        PlazmixCore.getInstance().executeBroadcast(executeType, name, key, value);
    }

}
