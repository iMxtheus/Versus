package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.RootCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RootDuelCommand extends RootCommand {

    public RootDuelCommand() {
        super("duel", "versus.duel");
        setUsage("/duel");
        setDescription("Main duel command.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        sender.sendMessage(getUsage());
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}