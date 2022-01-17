package ro.kmagic.kreports.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.commands.handler.CommandInterface;
import ro.kmagic.kreports.utils.Utils;

public class HelpCommand implements CommandInterface {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Reports instance = Reports.getInstance();
        FileConfiguration messages = instance.getMessages();

        if(!sender.hasPermission("report.help")) {
            sender.sendMessage(Utils.color(messages.getString("no-permission")));
            return;
        }

        for(String str : messages.getStringList("help-message")) {
            sender.sendMessage(Utils.color(str));
        }
    }
}
