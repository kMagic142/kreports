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

        String[] msg = message.split(";");
        msg = Arrays.copyOfRange(msg, 1, msg.length);

        if(message.startsWith("reportcreated")) {
            int id = 0;
            try {
                id = Integer.parseInt(msg[0]);
            } catch(NumberFormatException ignored) {}

            String server = msg[1];

            if(server.equals(Reports.getInstance().getConfig().getString("server"))) return;

            Report report = Reports.getInstance().getMySQL().getReport(id);

            Bukkit.getPluginManager().callEvent(new ReportCreatedEvent(report));
            return;
        }

        if(message.startsWith("reportreply")) {
            int id = 0;
            try {
                id = Integer.parseInt(msg[0]);
            } catch(NumberFormatException ignored) {}
            String replier = msg[1];
            String reply = msg[2];

            if(msg.length > 3) {
                reply = String.join(";", Arrays.copyOfRange(msg, 2, msg.length));
            }

            Bukkit.getPluginManager().callEvent(new ReportReplyEvent(Reports.getInstance().getMySQL().getReport(id), reply, replier));
            return;
        }

        if(message.startsWith("reportclaimed")) {
            int id = 0;
            try {
                id = Integer.parseInt(msg[0]);
            } catch(NumberFormatException ignored) {}

            String claimer = msg[2];

            Report report = Reports.getInstance().getMySQL().getReport(id);
            report.setClaimer(claimer);

            Bukkit.getPluginManager().callEvent(new ReportClaimEvent(report));
            return;
        }

        if(message.startsWith("reportclosed")) {
            try {
                Boolean.parseBoolean(msg[5]);
            } catch(Exception e) {
                String player = msg[0];
                String server = msg[1];
                boolean isClosedByStaff = Boolean.parseBoolean(msg[2]);
                String claimer = msg[3];
                String reportedPlayer = msg[4];

                if(server.equals(Reports.getInstance().getConfig().getString("server"))) return;

                Bukkit.getPluginManager().callEvent(new ReportClosedEvent(new Report(0, player, reportedPlayer, null, server, false, claimer, null, 0L), isClosedByStaff));
                return;
            }

            String player = msg[0];
            String server = msg[1];
            boolean isClosedByStaff = Boolean.parseBoolean(msg[2]);
            String claimer = msg[3];
            String reportedPlayer = msg[4];
            boolean positive = Boolean.parseBoolean(msg[5]);

            Bukkit.getPluginManager().callEvent(new ReportClosedEvent(new Report(0, player, reportedPlayer, null, server, false, claimer, null, 0L), isClosedByStaff, positive));
        }

        if(message.startsWith("conversationupdate")) {
            int id = 0;
            try {
                id = Integer.parseInt(msg[0]);
            } catch(NumberFormatException ignored) {}

            Report report = Reports.getInstance().getMySQL().getReport(id);
            boolean conversation = Boolean.parseBoolean(msg[1]);

            Bukkit.getPluginManager().callEvent(new ConversationUpdatedEvent(report, conversation));
        }
    }

}
