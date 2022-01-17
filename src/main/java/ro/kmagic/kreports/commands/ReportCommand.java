package ro.kmagic.kreports.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.commands.handler.CommandInterface;
import ro.kmagic.kreports.data.types.events.ReportCreatedEvent;
import ro.kmagic.kreports.data.types.reports.Report;
import ro.kmagic.kreports.gui.MainMenu;
import ro.kmagic.kreports.utils.Utils;

public class ReportCommand implements CommandInterface {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Reports instance = Reports.getInstance();
        FileConfiguration messages = instance.getMessages();

        if(!sender.hasPermission("report.use")) {
            sender.sendMessage(Utils.color(messages.getString("no-permission")));
            return;
        }

        if(sender.hasPermission("reports.staff")) {
            sender.sendMessage(Utils.color(messages.getString("not-a-staff-command")));
            return;
        }

        switch(args.length) {
            case 0: {
                sender.sendMessage(Utils.color(messages.getString("no-player-specified")));
                break;
            }

            case 1: {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.color(messages.getString("only-player-command")));
                    break;
                }

                String reportedPlayer = args[0];

                if(Bukkit.getPlayerExact(reportedPlayer) == null) {
                    sender.sendMessage(Utils.color(messages.getString("invalid-player")));
                    break;
                }

                Player player = (Player) sender;

                MainMenu mainMenu = new MainMenu(player, args[0]);
                mainMenu.open();
                break;
            }

            case 2: {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.color(messages.getString("only-player-command")));
                    break;
                }

                String reportedPlayer = args[0];
                String reason = args[1];

                if (Bukkit.getPlayerExact(reportedPlayer) == null) {
                    sender.sendMessage(Utils.color(messages.getString("invalid-player")));
                    break;
                }

                if (instance.getReasonManager().getReason(reason) == null) {
                    sender.sendMessage(Utils.color(messages.getString("invalid-reason")));
                    break;
                }

                Player player = (Player) sender;

                if(instance.getMySQL().getReport(player.getName()) != null) {
                    sender.sendMessage(Utils.color(messages.getString("you-already-have-a-report-opened")));
                    break;
                }

                Player bukkitReported = Bukkit.getPlayerExact(reportedPlayer);

                Report report = new Report(player.getName(), bukkitReported.getName(), instance.getReasonManager().getReason(reason));
                instance.getServer().getPluginManager().callEvent(new ReportCreatedEvent(report));
            }
        }
    }
}
