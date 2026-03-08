package me.robomonkey.versus.settings.command;

import me.robomonkey.versus.command.AbstractCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends AbstractCommand {

    public ReloadCommand() {
        super("reload", "versus.admin");
        setUsage("/versus reload");
        setDescription("Reload the plugin configuration.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aPlugin reloaded.");
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}