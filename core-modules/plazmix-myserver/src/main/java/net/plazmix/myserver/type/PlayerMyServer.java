package net.plazmix.myserver.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.api.utility.Directories;
import net.plazmix.core.api.utility.FileUtil;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.myserver.PlazmixMyServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerMyServer {

    private final String serverName;
    private final InetSocketAddress inetSocketAddress;

    private final CorePlayer owner;
    private final Collection<CorePlayer> moderatorCollection = new ArrayList<>();

    private final MyServerType serverType;
    private long startMillis;

    private Path serverFolder;
    private Process process;


    private final ExecutorService serverThread = Executors.newCachedThreadPool();

    public PlayerMyServer(@NonNull CorePlayer owner, @NonNull MyServerType myServerType) {
        this.serverName = myServerType.createServerName();
        this.inetSocketAddress = new InetSocketAddress(NumberUtil.randomInt(30_000, 40_000));

        this.owner = owner;
        this.serverType = myServerType;
    }

    private static final String SHELL_SCRIPT = ("java -server -Xmx256M -Dfile.encoding=UTF-8 -jar Spigot.jar");
    private static final String BATCH_SCRIPT = ("@ECHO OFF \ntitle %server_name% \n\n" + SHELL_SCRIPT);



    public void addModer(@NonNull CorePlayer corePlayer) {
        moderatorCollection.add(corePlayer);

        MyServerManager.INSTANCE.getPlayerMyServers().put(corePlayer.getName().toLowerCase(), this);
        corePlayer.sendMessage("§d§lMyServer §8:: §fВы были добавлены в список модераторов сервера " + owner.getDisplayName());
    }

    public void removeModer(@NonNull CorePlayer corePlayer) {
        moderatorCollection.remove(corePlayer);

        MyServerManager.INSTANCE.getPlayerMyServers().remove(corePlayer.getName().toLowerCase());
        corePlayer.sendMessage("§d§lMyServer §8:: §fВы были удалены из списка модераторов сервера " + owner.getDisplayName());
    }

    public boolean isModer(@NonNull CorePlayer corePlayer) {
        return moderatorCollection.contains(corePlayer);
    }

    public boolean isOnlineHere(@NonNull CorePlayer corePlayer) {
        return getCoreServer() != null && getCoreServer().getOnlinePlayers().contains(corePlayer);
    }

    public boolean isLeader(@NonNull CorePlayer corePlayer) {
        return owner.getName().equalsIgnoreCase(corePlayer.getName());
    }


    @SuppressWarnings("all")
    @SneakyThrows
    public boolean start() {
        Collection<File> availableServersFiles = Arrays.stream(serverType.getServersFolder().toFile().listFiles())
                .filter(serverFolder -> !MyServerManager.INSTANCE.isAvailable(serverFolder.toPath()))
                .collect(Collectors.toList());

        if (availableServersFiles.isEmpty()) {

            owner.sendMessage("§cВ категории серверов " + serverType.name().toLowerCase(Locale.ROOT) + " нет доступных арен :(");
            owner.sendMessage("§cПопробуйте использовать другую категорию или подождите, пока одна из арен освободится!");
            return false;
        }

        serverThread.submit(() -> {
            owner.sendMessage("§d§lMyServer §8:: §fВаш сервер был инициализирован с названием §a" + serverName);
            owner.sendMessage(" §eЧерез некоторое время Вы будете автоматически перемещены на него!");

            Path serverRunning = PlazmixMyServer.getInstance().getModuleFolder().toPath().resolve("RunningServers").resolve(serverName);
            Path serverShape = availableServersFiles.stream()
                    .skip((long) (availableServersFiles.size() * Math.random()))
                    .findFirst()
                    .orElse(null).toPath();

            Directories.copyDirectory(serverShape, serverFolder = serverRunning);

            // Create a server process...
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            String script = (isWindows ? BATCH_SCRIPT : SHELL_SCRIPT).replace("%server_name%", getServerName());

            try {

                // Change CoreConnector settings.
                if (Files.exists(serverFolder.resolve("plugins").resolve("CoreConnector"))) {
                    File serverConnectionProperty = serverFolder.resolve("plugins").resolve("CoreConnector").resolve("config.properties").toFile();

                    Properties properties = new Properties();
                    properties.load( new FileReader(serverConnectionProperty) );

                    properties.setProperty("server.name", serverName);
                    properties.store( new FileOutputStream(serverConnectionProperty), null );
                }

                // Change server settings
                if (Files.exists(serverFolder.resolve("server.properties"))) {
                    File serverProperty = serverFolder.resolve("server.properties").toFile();

                    Properties properties = new Properties();
                    properties.load( new FileReader(serverProperty) );


                    properties.setProperty("server-ip", "127.0.0.1");
                    properties.setProperty("server-port", String.valueOf(inetSocketAddress.getPort()));

                    properties.store( new FileOutputStream(serverProperty), null );
                }


                // create a file
                Path bashFile = serverFolder.resolve(isWindows ? "start.bat" : "start.sh");

                if (!Files.exists(bashFile)) {
                    Files.createFile(bashFile);
                }

                // build batch commands
                FileUtil.write(bashFile.toFile(), fileWriter -> fileWriter.write(script));

                // start the process
                ProcessBuilder processBuilder = new ProcessBuilder();

                if (isWindows) {
                    processBuilder.command("cmd.exe", "/c", "start", "start.bat");

                } else {

                    processBuilder.command("sh", "start.sh");
                }

                processBuilder.directory(serverFolder.toFile());
                process = processBuilder.start();
            }

            catch (Exception exception) {
                exception.printStackTrace();
            }

            new CommonScheduler(serverName + "-startprocess") {

                @Override
                @SneakyThrows
                public void run() {

                    if (isRunning()) {
                        cancel();

                        startMillis = System.currentTimeMillis();

                        owner.sendMessage("§d§lMyServer §8:: §fСервер §e" + serverName + " §fуспешно создан, идет подключение...");
                        owner.connectToServer(getCoreServer());

                        process.waitFor();
                        shutdown();
                    }
                }

            }.runTimer(10, 1, TimeUnit.SECONDS);
        });
        return true;
    }

    @SneakyThrows
    public void shutdown() {
        if (getCoreServer() != null) getCoreServer().restart();

        // Delete and destroy server process
        MyServerManager.INSTANCE.removeServer(owner);

        // Destroy process.
        if (process != null) {
            process.destroy();
            process = null;
        }

        // Print messages.
        owner.sendMessage("§d§lMyServer §8:: §fВаш сервер §e" + serverName + " §fбыл выключен и удален");

        for (CorePlayer corePlayer : moderatorCollection)
            corePlayer.sendMessage("§d§lMyServer §8:: §fСервер §e" + serverName + " §fбыл выключен и удален");

        // Shutdown the server in core.
        serverThread.shutdown();


        // Delete running server folder
        new CommonScheduler(serverName + "-remove-files") {
            // Не полностью удаляется.
            // Скорее всего это связано с тем, что он удаляется в процессе
            // выключения сервера, от чего и не может удалить некоторые файлы,
            // которые запущены ядром (((

            @Override
            public void run() {
                if (serverFolder != null) {
                    Directories.clearDirectory(serverFolder.toFile(), true);

                    serverFolder = null;
                }
            }

        }.runLater(1, TimeUnit.SECONDS);
    }

    public boolean isRunning() {
        return PlazmixCore.getInstance().getBukkitServer(serverName) != null;
    }

    public BukkitServer getCoreServer() {
        return PlazmixCore.getInstance().getBukkitServer(getServerName());
    }


    public void broadcast(@NonNull String message) {

        for (CorePlayer corePlayer : getCoreServer().getOnlinePlayers()) {
            corePlayer.sendMessage(message);
        }
    }

}
