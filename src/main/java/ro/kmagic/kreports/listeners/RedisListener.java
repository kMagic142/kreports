package ro.kmagic.kreports.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.events.*;
import ro.kmagic.kreports.data.types.reports.Report;

import java.util.Arrays;

public class RedisListener implements Listener {

    @EventHandler
    public void onPubSub(PubSubEvent event) {
        if(!event.getChannel().equals("topicraftreports")) return;
        String message = event.getMessage();

        if(message.startsWith("reportcreated")) {
            String[] msg = message.split(";");
            msg = Arrays.copyOfRange(msg, 1, msg.length);

            String player = msg[0];
            String reportedPlayer = msg[1];
            String reason = msg[2];
            String server = msg[3];
            String hastebinURL = msg[4];

            Bukkit.getPluginManager().callEvent(new ReportCreatedEvent(new Report(player, reportedPlayer, reason, server, false, null, hastebinURL)));
            return;
        }

        if(message.startsWith("reportreply")) {
            String[] msg = message.split(";");
            msg = Arrays.copyOfRange(msg, 1, msg.length);

            String player = msg[0];
            String replier = msg[1];
            String reply = msg[2];

            if(msg.length > 3) {
                reply = String.join(";", Arrays.copyOfRange(msg, 1, msg.length));
            }

            Bukkit.getPluginManager().callEvent(new ReportReplyEvent(Reports.getInstance().getMySQL().getReport(player), reply, replier));
            return;
        }

        if(message.startsWith("reportclaimed")) {
            String[] msg = message.split(";");
            msg = Arrays.copyOfRange(msg, 1, msg.length);

            String player = msg[0];
            String reportedPlayer = msg[1];
            String reason = msg[2];
            String claimer = msg[3];
            String server = msg[4];

            Bukkit.getPluginManager().callEvent(new ReportClaimEvent(new Report(player, reportedPlayer, reason, server, false, claimer, null)));
            return;
        }

        if(message.startsWith("reportclosed")) {
            String[] msg = message.split(";");
            msg = Arrays.copyOfRange(msg, 1, msg.length);

            String player = msg[0];
            String reportedPlayer = msg[1];
            String reason = msg[2];
            String claimer = msg[3];
            String server = msg[3];
            boolean isClosedByStaff = Boolean.parseBoolean(msg[4]);

            Bukkit.getPluginManager().callEvent(new ReportClosedEvent(new Report(player, reportedPlayer, reason, server, false, claimer, null), isClosedByStaff));
        }
    }

}
