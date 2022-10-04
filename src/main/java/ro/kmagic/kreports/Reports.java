package ro.kmagic.kreports;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ro.kmagic.kreports.commands.*;
import ro.kmagic.kreports.commands.handler.CommandHandler;
import ro.kmagic.kreports.data.database.mysql.MySQL;
import ro.kmagic.kreports.data.database.redis.Redis;
import ro.kmagic.kreports.data.files.MessagesFile;
import ro.kmagic.kreports.listeners.PlayerListener;
import ro.kmagic.kreports.listeners.RedisListener;
import ro.kmagic.kreports.listeners.ReportsListener;
import ro.kmagic.kreports.managers.ReasonManager;
import ro.kmagic.kreports.managers.ReportsManager;
import ro.kmagic.kreports.utils.Utils;

import java.io.File;
import java.io.IOException;

public final class Reports extends JavaPlugin {

    private static Reports instance;

    private MessagesFile messagesFile;
    private File logFile;

    private MySQL mysql;
    private Redis redis;

    private ReasonManager reasonManager;
    private ReportsManager reportManager;

    private PlayerListener playerListener;

    @Override
    public void onLoad() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        logFile = new File(getDataFolder(), "reports.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                logFile.delete();
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onEnable() {
        instance = this;

        initData();
        loadModules();

        Utils.info("Reports successfully enabled.");
    }

    @Override
    public void onDisable() {
        if(redis.getTask() != null) redis.getTask().cancel();
        Utils.info("Reports successfully disabled.");
    }

    private void initData() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        mysql = new MySQL();
        redis = new Redis();

        messagesFile = new MessagesFile();
        reasonManager = new ReasonManager();
        reportManager = new ReportsManager();
        reasonManager.loadReasons();
        reportManager.loadReports();
    }

    private void loadModules() {
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.register("help", new HelpCommand());
        commandHandler.register("reload", new ReloadCommand());
        commandHandler.register("report", new ReportCommand());
        commandHandler.register("reply", new ReplyCommand());
        commandHandler.register("claim", new ClaimCommand());
        commandHandler.register("close", new CloseCommand());
        commandHandler.register("staff", new StaffCommand());

        getCommand("report").setExecutor(commandHandler);

        CommandHandler commandHandler2 = new CommandHandler();
        commandHandler2.register("reports", new ReportsCommand());

        getCommand("reports").setExecutor(commandHandler2);

        playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        Bukkit.getPluginManager().registerEvents(new ReportsListener(),  this);
        Bukkit.getPluginManager().registerEvents(new RedisListener(), this);
    }

    public void reload(CommandSender sender) {
        sender.sendMessage(Utils.color("&a&lReports &7- &fReloading MySQL..."));
        mysql = new MySQL();
        sender.sendMessage(Utils.color("&a&lReports &7- &fReloading Redis..."));
        redis = new Redis();
        sender.sendMessage(Utils.color("&a&lReports &7- &fReloading Messages..."));
        messagesFile.reload();
        sender.sendMessage(Utils.color("&a&lReports &7- &fClearing reason cache and reloading..."));
        reasonManager.clear();
        reasonManager.loadReasons();
        sender.sendMessage(Utils.color("&a&lReports &7- &fClearing report cache and reloading..."));
        reportManager.clear();
        reportManager.loadReports();
        sender.sendMessage(Utils.color("&a&lReports &7- &fReloading config..."));
        reloadConfig();
        sender.sendMessage(Utils.color("&a&lReports reloaded successfully."));
    }

    public FileConfiguration getMessages() {
        return messagesFile.getData();
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public Redis getRedis() {
        return redis;
    }

    public ReasonManager getReasonManager() {
        return reasonManager;
    }

    public ReportsManager getReportManager() {
        return reportManager;
    }

    public PlayerListener getPlayerListener() {
        return playerListener;
    }

    public File getLogFile() {
        return logFile;
    }

    public static Reports getInstance() {
        return instance;
    }
}
