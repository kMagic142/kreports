package ro.kmagic.kreports.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.commands.handler.CommandInterface;
import ro.kmagic.kreports.data.types.reports.Report;
import ro.kmagic.kreports.utils.Utils;

public class ClaimCommand implements CommandInterface {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Reports instance = Reports.getInstance();
        FileConfiguration messages = instance.getMessages();

        if(!sender.hasPermission("report.staff")) {
            sender.sendMessage(Utils.color(messages.getString("no-permission")));
            return;
        }

        if(args.length < 1) {
            sender.sendMessage(Utils.color(messages.getString("no-player-specified-claim")));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color(messages.getString("only-player-command")));
            return;
        }

        String reportPlayer = args[0];

        Player player = (Player) sender;
        Report report = instance.getMySQL().getReport(reportPlayer);

        if(report == null) {
            player.sendMessage(Utils.color(messages.getString("no-reports-opened-claim")));
            return;
        }

        String message = Utils.color(instance.getMessages().getString("claim-staff-notification")
                .replace("{claimer}", player.getName())
                .replace("{player}", report.getPlayer())
                .replace("{server}", report.getServer())
                .replace("{reason}", report.getReason().getName()));

        report.setClaimer(player.getName());
        instance.getRedis().reportClaimed(report, player.getName());
        if(report.getServer().equals(instance.getConfig().getString("server")))  player.sendMessage(message);
    }
}
