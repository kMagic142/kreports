package ro.kmagic.kreports.commands.handler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;

public class CommandHandler implements CommandExecutor {

    private static final HashMap<String, CommandInterface> commands = new HashMap<>();


    public void register(String name, CommandInterface cmd) {
        commands.put(name, cmd);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(args.length == 0 || !commands.containsKey(args[0])) {
            commands.get(commandLabel).onCommand(sender, args);
            return true;
        }

        if(args.length > 1) {
            commands.get(args[0]).onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            commands.get(args[0]).onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return true;
    }

}
