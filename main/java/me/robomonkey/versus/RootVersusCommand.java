package me.robomonkey.versus;

import me.robomonkey.versus.command.RootCommand;
import me.robomonkey.versus.settings.command.ConfigCommand;
import me.robomonkey.versus.settings.command.SupportCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RootVersusCommand extends RootCommand {

    public RootVersusCommand() {
        super("versus", "versus.admin");
        setPlayersOnly(true);
        setPermissionRequired(true);
        setMaxArguments(1);

        addSubCommand(new ConfigCommand());
        addSubCommand(new SupportCommand());
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {}

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}