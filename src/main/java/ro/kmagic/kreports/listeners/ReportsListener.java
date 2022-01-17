package ro.kmagic.kreports.listeners;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.events.ReportClaimEvent;
import ro.kmagic.kreports.data.types.events.ReportClosedEvent;
import ro.kmagic.kreports.data.types.events.ReportCreatedEvent;
import ro.kmagic.kreports.data.types.events.ReportReplyEvent;
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

        if(reports.getConfig().getString("server").equalsIgnoreCase(report.getServer())) {
            String hastebinURL = null;

            if(report.getReason().getCategory() == ReportCategory.CHAT_REPORT) {
                Hastebin hastebin = new Hastebin();
                StringBuilder builder = new StringBuilder();
                for(String str : reports.getPlayerListener().map.get(report.getReportedPlayer())) {
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
            reports.getRedis().sendReport(report);


            TextComponent msg = new TextComponent(reports.getMessages().getString("click-to-claim"));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report claim " + report.getPlayer()));

            String message = Utils.color(reports.getMessages().getString("staff-notification")
                    .replace("{player}", report.getPlayer())
                    .replace("{reportedPlayer}", report.getReportedPlayer())
                    .replace("{server}", report.getServer())
                    .replace("{reason}", report.getReason().getName())
                    .replace("{click}", msg.toLegacyText()));



            int version = VersionUtility.getMinorVersion();

            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!player.hasPermission("report.staff")) continue;

                if(version < 9) {
                    player.playSound(player.getEyeLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getEyeLocation(), "entity.player.levelup", 1.0F, 1.0F);
                }

                player.sendMessage(message);
            }

            return;
        }

        reports.getReportManager().addReport(report);

        String message = Utils.color(reports.getMessages().getString("staff-notification")
                .replace("{player}", report.getPlayer())
                .replace("{reportedPlayer}", report.getReportedPlayer())
                .replace("{server}", report.getServer())
                .replace("{reason}", report.getReason().getName()));

        int version = VersionUtility.getMinorVersion();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!player.hasPermission("report.staff")) continue;

            if(version < 9) {
                player.playSound(player.getEyeLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            } else {
                player.playSound(player.getEyeLocation(), "entity.player.levelup", 1.0F, 1.0F);
            }

            player.sendMessage(message);
        }
    }

    @EventHandler
    public void onReportReply(ReportReplyEvent event) {
        Report report = event.getReport();
        String reply = event.getReply();
        Reports reports = Reports.getInstance();

        Utils.logToFile(report, reply);

        if(Bukkit.getPlayerExact(event.getReplier()) != null) {
            Player player = Bukkit.getPlayerExact(event.getReplier());

            player.sendMessage(Utils.color(player, reports.getMessages().getString("rely-format")).replace("{message}", reply));
            reports.getRedis().reportReply(report, reply, event.getReplier());
            return;
        }

        Player player = Bukkit.getPlayerExact(report.getPlayer());

        int version = VersionUtility.getMinorVersion();

        if(version < 9) {
            player.playSound(player.getEyeLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
        } else {
            player.playSound(player.getEyeLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
        }

        player.sendMessage(Utils.color(player, reports.getMessages().getString("rely-format")).replace("{message}", reply));
    }

    @EventHandler
    public void onReportClaimed(ReportClaimEvent event) {
        Report report = event.getReport();
        String claimer = report.getClaimer();
        Reports reports = Reports.getInstance();

        if (Bukkit.getPlayerExact(report.getClaimer()) != null) {
            reports.getRedis().reportClaimed(report);

            String message = Utils.color(reports.getMessages().getString("claim-staff-notification")
                    .replace("{claimer}", claimer)
                    .replace("{player}", report.getReportedPlayer())
                    .replace("{server}", report.getServer())
                    .replace("{reason}", report.getReason().getName()));

            int version = VersionUtility.getMinorVersion();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("report.staff")) continue;

                if (version < 9) {
                    player.playSound(player.getEyeLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getEyeLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
                }

                player.sendMessage(message);
            }

            if(report.getHastebinURL() != null) {
                Bukkit.getPlayerExact(report.getClaimer()).sendMessage(Utils.color(reports.getMessages().getString("chat-log").replace("{link}", report.getHastebinURL())));
            }
        } else {
            String message = Utils.color(reports.getMessages().getString("claim-staff-notification")
                    .replace("{claimer}", claimer)
                    .replace("{player}", report.getReportedPlayer())
                    .replace("{server}", report.getServer())
                    .replace("{reason}", report.getReason().getName()));

            int version = VersionUtility.getMinorVersion();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("report.staff")) continue;

                if (version < 9) {
                    player.playSound(player.getEyeLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getEyeLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
                }

                player.sendMessage(message);
            }


            Player player = Bukkit.getPlayerExact(report.getPlayer());

            if (player != null) {
                player.sendMessage(Utils.color(reports.getMessages().getString("report-claimed").replace("{player}", claimer)));

                if (version < 9) {
                    player.playSound(player.getEyeLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getEyeLocation(), "entity.player.levelup", 1.0F, 1.0F);
                }
            }
        }
    }

    @EventHandler
    public void onReportClosed(ReportClosedEvent event) {
        Report report = event.getReport();
        String claimer = report.getClaimer();
        Reports reports = Reports.getInstance();

        if (Bukkit.getPlayerExact(report.getClaimer()) != null || Bukkit.getPlayerExact(report.getPlayer()) != null) {
            reports.getRedis().reportClosed(report, event.isCloserStaff());
        }

        int version = VersionUtility.getMinorVersion();

        if (event.isCloserStaff()) {
            String message = Utils.color(reports.getMessages().getString("close-staff-notification")
                    .replace("{closer}", claimer)
                    .replace("{player}", report.getReportedPlayer()));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("report.staff")) continue;

                if (version < 9) {
                    player.playSound(player.getEyeLocation(), Sound.GHAST_DEATH, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getEyeLocation(), "entity.ghast.death", 1.0F, 1.0F);
                }

                player.sendMessage(message);
            }

            if(event.getPositive()) {
                if(Bukkit.getPlayerExact(report.getPlayer()) != null) {
                    Bukkit.getPlayerExact(report.getPlayer()).sendMessage(Utils.color(reports.getMessages().getString("report-closed-with-positive-response")
                            .replace("{reportedPlayer}", report.getReportedPlayer())));
                }
            } else if(!event.getPositive()) {
                if(Bukkit.getPlayerExact(report.getPlayer()) != null) {
                    Bukkit.getPlayerExact(report.getPlayer()).sendMessage(Utils.color(reports.getMessages().getString("report-closed-with-negative-response")
                            .replace("{reportedPlayer}", report.getReportedPlayer())));
                }
            } else {
                if(Bukkit.getPlayerExact(report.getPlayer()) != null) {
                    Bukkit.getPlayerExact(report.getPlayer()).sendMessage(Utils.color(reports.getMessages().getString("report-closed")));
                }
            }

        } else {
            String message = Utils.color(reports.getMessages().getString("close-own-staff-notification")
                    .replace("{closer}", claimer));

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

}
