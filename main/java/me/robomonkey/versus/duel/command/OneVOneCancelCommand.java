package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OneVOneCancelCommand extends AbstractCommand {

    public OneVOneCancelCommand() {
        super("1v1cancel", "versus.duel");
        setPlayersOnly(true);
        setPermissionRequired(false);
        setArgumentRequired(false);
        setUsage("/1v1cancel");
        setDescription("Leave the 1v1 matchmaking queue.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        OneVOneCommand.cancelQueue(player);
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}