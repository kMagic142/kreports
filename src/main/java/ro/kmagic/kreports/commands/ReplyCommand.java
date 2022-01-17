package ro.kmagic.kreports.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.commands.handler.CommandInterface;
import ro.kmagic.kreports.data.types.events.ReportReplyEvent;
import ro.kmagic.kreports.data.types.reports.Report;
import ro.kmagic.kreports.utils.Utils;

public class ReplyCommand implements CommandInterface {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Reports instance = Reports.getInstance();
        FileConfiguration messages = instance.getMessages();

        if(!sender.hasPermission("report.use")) {
            sender.sendMessage(Utils.color(messages.getString("no-permission")));
            return;
        }

        if(args.length < 1) {
            sender.sendMessage(Utils.color(messages.getString("no-message")));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color(messages.getString("only-player-command")));
            return;
        }

        if(!sender.hasPermission("report.staff")) {
            Player player = (Player) sender;
            Report report = instance.getMySQL().getReport(player.getName());

            if(report == null) {
                player.sendMessage(Utils.color(messages.getString("no-reports-opened")));
                return;
            }

            if(report.getClaimer() == null) {
                player.sendMessage(Utils.color(messages.getString("not-claimed-yet")));
                return;
            }

            if(!report.isConversation()) {
                player.sendMessage(Utils.color(messages.getString("report-doesnt-have-conversation")));
                return;
            }

            String reply = String.join(" ", args);
            instance.getServer().getPluginManager().callEvent(new ReportReplyEvent(report, reply, player.getName()));
        } else {
            Player player = (Player) sender;
            Report report = instance.getMySQL().getReport(player.getName());

            if(report == null) {
                player.sendMessage(Utils.color(messages.getString("no-reports-claimed")));
                return;
            }

            if(!report.isConversation()) {
                player.sendMessage(Utils.color(messages.getString("report-doesnt-have-conversation")));
                return;
            }

            String reply = String.join(" ", args);
            instance.getServer().getPluginManager().callEvent(new ReportReplyEvent(report, reply, player.getName()));
        }
    }
}
