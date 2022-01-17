package ro.kmagic.kreports.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.reports.Report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Utils {

    public static void log(Level level, String msg) {
        Bukkit.getLogger().log(level, "[Reports] " + msg);
    }

    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    public static void error(String msg) {
        log(Level.SEVERE, "[Reports] " + msg);
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String color(Player player, String str) {
        return PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', str));
    }

    public static List<Component> formatComponentList(List<String> list) {
        List<Component> complist = new ArrayList<>();
        for(String str : list) {
            complist.add(Component.text(Utils.color(str)));
        }

        return complist;
    }

    public static void logToFile(Report report, String reply) {
        File logFile = Reports.getInstance().getLogFile();
        if (Reports.getInstance().getLogFile() != null) {
            try {
                String logMessage = "[{player} - {reportedPlayer} - {reason} - {claimer}] REPLY: {reply}"
                        .replace("{player}", report.getPlayer())
                        .replace("{reportedPlayer}", report.getReportedPlayer())
                        .replace("{reason}", report.getReason().getName())
                        .replace("{claimer}", report.getClaimer())
                        .replace("{reply}", reply);
                Files.write(Paths.get(logFile.toURI()), logMessage.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
