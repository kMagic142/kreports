package ro.kmagic.kreports.listeners;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.events.*;
import ro.kmagic.kreports.data.types.reports.Report;
import ro.kmagic.kreports.data.types.reports.ReportCategory;
import ro.kmagic.kreports.utils.Hastebin;
import ro.kmagic.kreports.utils.Utils;
import ro.kmagic.kreports.utils.VersionUtility;

import java.io.IOException;

public class ReportsListener implements Listener {

    @EventHandler
    public void onReportCreated(ReportCreatedEvent event) {
        Report report = event.getReport();
        Reports reports = Reports.getInstance();

        reports.getMySQL().addReport(report);

        for(String s : reports.getMessages().getStringList("staff-notification")) {
            TextComponent message = new TextComponent(Utils.color(s
                    .replace("{player}", report.getPlayer())
                    .replace("{reportedPlayer}", report.getReportedPlayer())
                    .replace("{server}", report.getServer())
                    .replace("{reason}", report.getReason().getName())
                    .replace("{click}", "")));

            if(s.contains("{click}")) {
                TextComponent msg = new TextComponent(Utils.color(reports.getMessages().getString("click-to-claim")));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report claim " + report.getPlayer()));
                message.addExtra(msg);
            }

            int version = VersionUtility.getMinorVersion();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("report.staff")) {
                    player.spigot().sendMessage(message);

                    if (version < 9) {
                        player.playSound(player.getEyeLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                    } else {
                        player.playSound(player.getEyeLocation(), "entity.player.levelup", 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onReportReply(ReportReplyEvent event) {
        Report report = event.getReport();
        String reply = event.getReply();
        Reports reports = Reports.getInstance();

        Utils.logToFile(report, reply);

        try {
            Player player = Bukkit.getPlayerExact(report.getPlayer());
            player.sendMessage(Utils.color(player, reports.getMessages().getString("reply-format")).replace("{message}", reply).replace("{player}", event.getReplier()));
        } catch(Exception ignored) {}

        try {
            Player player = Bukkit.getPlayerExact(report.getClaimer());

            int version = VersionUtility.getMinorVersion();

            if (version < 9) {
                player.playSound(player.getEyeLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
            } else {
                player.playSound(player.getEyeLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
            }

            player.sendMessage(Utils.color(player, reports.getMessages().getString("reply-format")).replace("{message}", reply).replace("{player}", event.getReplier()));
        } catch(Exception ignored) {}
    }

    @EventHandler
    public void onReportClaimed(ReportClaimEvent event) {
        Report report = event.getReport();
        String claimer = report.getClaimer();
        Reports reports = Reports.getInstance();

        String hastebinURL = null;

        if (report.getReason().getCategory() == ReportCategory.CHAT_REPORT) {
            Hastebin hastebin = new Hastebin();
            StringBuilder builder = new StringBuilder();
            for (String str : reports.getPlayerListener().map.get(report.getReportedPlayer())) {
                builder.append(str).append("\n");
            }

            try {
                hastebinURL = hastebin.post(builder.toString(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        report.setHastebinURL(hastebinURL);

        reports.getMySQL().addReport(report);

        if(Bukkit.getPlayerExact(report.getPlayer()) != null) {
            Bukkit.getPlayerExact(report.getPlayer()).sendMessage(Utils.color(reports.getMessages().getString("report-claimed").replace("{player}", claimer)));
            return;
        }

        if (Bukkit.getPlayerExact(claimer) != null) {
            if(report.getHastebinURL() != null) {
                Bukkit.getPlayerExact(claimer).sendMessage(Utils.color(reports.getMessages().getString("chat-log").replace("{link}", report.getHastebinURL())));
            }
        }

        String message = Utils.color(reports.getMessages().getString("claim-staff-notification")
                .replace("{claimer}", claimer)
                .replace("{player}", report.getPlayer())
                .replace("{server}", report.getServer())
                .replace("{reason}", report.getReason().getName()));

        int version = VersionUtility.getMinorVersion();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("report.staff")) {

                if (version < 9) {
                    player.playSound(player.getEyeLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getEyeLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
                }

                player.sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onReportClosed(ReportClosedEvent event) {
        Report report = event.getReport();
        String claimer = report.getClaimer();
        Reports reports = Reports.getInstance();

        int version = VersionUtility.getMinorVersion();

        if (event.isCloserStaff()) {
            String message = Utils.color(reports.getMessages().getString("close-staff-notification")
                    .replace("{closer}", claimer)
                    .replace("{player}", report.getPlayer()));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("report.staff")) continue;

                if (version < 9) {
                    player.playSound(player.getEyeLocation(), Sound.GHAST_DEATH, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getEyeLocation(), "entity.ghast.death", 1.0F, 1.0F);
                }

                player.sendMessage(message);
            }

            if(event.getPositive() != null) {
                if (event.getPositive()) {
                    if (Bukkit.getPlayerExact(report.getPlayer()) != null) {
                        Bukkit.getPlayerExact(report.getPlayer()).sendMessage(Utils.color(reports.getMessages().getString("report-closed-with-positive-response")
                                .replace("{reportedPlayer}", report.getReportedPlayer())));
                    }
                } else {
                    if (Bukkit.getPlayerExact(report.getPlayer()) != null) {
                        Bukkit.getPlayerExact(report.getPlayer()).sendMessage(Utils.color(reports.getMessages().getString("report-closed-with-negative-response")
                                .replace("{reportedPlayer}", report.getReportedPlayer())));
                    }
                }
            }

        } else {
            String message = Utils.color(reports.getMessages().getString("close-own-staff-notification")
                    .replace("{closer}", report.getPlayer()));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("report.staff")) continue;

                if (version < 9) {
                    player.playSound(player.getEyeLocation(), Sound.GHAST_DEATH, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getEyeLocation(), "entity.ghast.death", 1.0F, 1.0F);
                }

                player.sendMessage(message);
            }

        }

    }

    @EventHandler
    public void onConversationUpdated(ConversationUpdatedEvent event) {
        Report report = event.getReport();
        Reports reports = Reports.getInstance();

        if(Bukkit.getPlayerExact(report.getPlayer()) != null) {
            Player player = Bukkit.getPlayerExact(report.getPlayer());

            if(!event.isConversation()) {
                player.sendMessage(Utils.color(reports.getMessages().getString("conversation-closed")));
            } else {
                for(String s : reports.getMessages().getStringList("conversation-opened")) {
                    player.sendMessage(Utils.color(s.replace("{player}", report.getClaimer())));
                }

            }
        } else {
            try {
                Player player = Bukkit.getPlayerExact(report.getClaimer());

                if(!event.isConversation()) {
                    player.sendMessage(Utils.color(reports.getMessages().getString("conversation-closed")));
                } else {
                    for(String s : reports.getMessages().getStringList("conversation-opened")) {
                        player.sendMessage(Utils.color(s.replace("{player}", report.getPlayer())));
                    }
                }
            } catch(Exception ignored) {}
        }
    }

}
