package ro.kmagic.kreports.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.commands.handler.CommandInterface;
import ro.kmagic.kreports.utils.Utils;

public class StaffCommand implements CommandInterface {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Reports instance = Reports.getInstance();
        FileConfiguration messages = instance.getMessages();

        if (!sender.hasPermission("report.admin")) {
            sender.sendMessage(Utils.color(messages.getString("no-permission")));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(Utils.color(messages.getString("staff-command-usage")));
            return;
        }

        if(args[0].equalsIgnoreCase("info")) {
            if(args.length < 2) {
                sender.sendMessage(Utils.color(messages.getString("no-player-specified-staff")));
                return;
            }

            String player = args[1];
            Integer resolved = instance.getMySQL().getStaff(player);

            if(resolved == null) {
                sender.sendMessage(Utils.color(messages.getString("invalid-player")));
                return;
            }

            sender.sendMessage(Utils.color(instance.getMessages().getString("staff-info")
                    .replace("{player}", player)
                    .replace("{resolved}", resolved.toString())));
            return;
        }

        if(args[0].equalsIgnoreCase("delete")) {
            if(args.length < 2) {
                sender.sendMessage(Utils.color(messages.getString("no-player-specified-staff")));
                return;
            }

            String player = args[1];
            Integer resolved = instance.getMySQL().getStaff(player);

            if(resolved == null) {
                sender.sendMessage(Utils.color(messages.getString("invalid-player")));
                return;
            }

            instance.getMySQL().removeStaff(player);
            sender.sendMessage(Utils.color(instance.getMessages().getString("staff-deleted")));
        }
    }
}