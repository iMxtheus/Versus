package me.robomonkey.versus.kit.command;

import me.robomonkey.versus.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SaveKitCommand extends AbstractCommand {

    public SaveKitCommand() {
        super("savekit", "versus.kit");
        setPlayersOnly(true);
        setUsage("/arena savekit <name>");
        setDescription("Saves a kit.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /arena savekit <name>");
            return;
        }

        String kitName = args[0];

        sender.sendMessage("§aKit §f" + kitName + " §asaved.");
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}