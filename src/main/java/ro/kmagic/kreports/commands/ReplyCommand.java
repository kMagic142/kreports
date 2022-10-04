package ro.kmagic.kreports.commands;

import org.bukkit.Bukkit;
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

        Player player = (Player) sender;
        String reply = String.join(" ", args);

        Report report;

        if(player.hasPermission("report.staff")) {
            report = instance.getMySQL().getReportClaimedBy(player.getName());
        } else {
            report = instance.getMySQL().getReport(player.getName());
        }

        if(report == null) {
            player.sendMessage(Utils.color(messages.getString("no-reports-opened")));
            return;
        }

        if(!report.isConversation()) {
            player.sendMessage(Utils.color(messages.getString("report-doesnt-have-conversation")));
            return;
        }


        instance.getRedis().reportReply(report, reply, player.getName());
        //if(report.getServer().equals(instance.getConfig().getString("server"))) Bukkit.getPluginManager().callEvent(new ReportReplyEvent(report, reply, player.getName()));
    }
}
