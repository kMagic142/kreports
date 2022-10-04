package ro.kmagic.kreports.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.commands.handler.CommandInterface;
import ro.kmagic.kreports.data.types.events.ReportClosedEvent;
import ro.kmagic.kreports.data.types.reports.Report;
import ro.kmagic.kreports.utils.Utils;

public class CloseCommand implements CommandInterface {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Reports instance = Reports.getInstance();
        FileConfiguration messages = instance.getMessages();

        if(!sender.hasPermission("report.use")) {
            sender.sendMessage(Utils.color(messages.getString("no-permission")));
            return;
        }

        if(args.length < 1) {
            if(!sender.hasPermission("report.staff")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.color(messages.getString("only-player-command")));
                    return;
                }

                Player player = (Player) sender;
                Report report = instance.getMySQL().getReport(player.getName());

                if(report == null) {
                    player.sendMessage(Utils.color(messages.getString("no-reports-opened")));
                    return;
                }

                if(report.getClaimer() != null) {
                    instance.getMySQL().setStaff(report.getClaimer(), instance.getMySQL().getStaff(report.getClaimer()) != null ? instance.getMySQL().getStaff(report.getClaimer()) : 0);
                }

                instance.getReportManager().removeReport(report);
                instance.getMySQL().deleteReport(report);
                instance.getServer().getPluginManager().callEvent(new ReportClosedEvent(report, false));
                instance.getRedis().reportClosed(report, false);
                player.sendMessage(Utils.color(instance.getMessages().getString("report-closed")));
            } else {
                sender.sendMessage(Utils.color(instance.getMessages().getString("use-staff-menu")));
            }
        } else {
            if(!sender.hasPermission("report.staff")) {
                sender.sendMessage(Utils.color(messages.getString("no-permission")));
                return;
            }

            Report report = instance.getMySQL().getReport(args[0]);

            if(report == null) {
                sender.sendMessage(Utils.color(messages.getString("no-reports-opened")));
                return;
            }

            instance.getReportManager().removeReport(report);
            instance.getMySQL().deleteReport(report);
            instance.getServer().getPluginManager().callEvent(new ReportClosedEvent(report, true));
            instance.getRedis().reportClosed(report, true);
        }

    }
}
