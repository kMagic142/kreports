package ro.kmagic.kreports.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.commands.handler.CommandInterface;
import ro.kmagic.kreports.gui.ReportsGUI;
import ro.kmagic.kreports.gui.StaffReportsGUI;
import ro.kmagic.kreports.utils.Utils;

public class ReportsCommand implements CommandInterface {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Reports instance = Reports.getInstance();
        FileConfiguration messages = instance.getMessages();

        if(args.length > 1 && args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("reports.reload")) return;
            sender.sendMessage(Utils.color("&a&lReloading Reports..."));
            instance.reload(sender);
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color(messages.getString("only-player-command")));
            return;
        }

        Player player = (Player) sender;

        if(player.hasPermission("report.staff")) {
            new StaffReportsGUI(player).updateMenu(20).open(player);
        } else {
            new ReportsGUI(player).updateMenu(20).open(player);
        }
    }
}
