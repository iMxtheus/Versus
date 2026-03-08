package me.robomonkey.versus.arena.command;

import me.robomonkey.versus.arena.*;
import me.robomonkey.versus.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetCommand extends AbstractCommand {

    public SetCommand() {
        super("set", "versus.arena.set");
        setUsage("/arena set <arena> <property>");
        setDescription("Changes a specific setting in an arena.");
        setPlayersOnly(true);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        boolean usingBuilder = ArenaBuilderCoordinator.getInstance().hasArenaBuilder(player);

        // 🔧 OŘEZÁNÍ argumentů (odstraníme "set")
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

        if (!usingBuilder && newArgs.length < 2) {
            sender.sendMessage(getUsage());
            return;
        }

        String arenaName = usingBuilder ? null : newArgs[0];
        String propertyName = usingBuilder ? newArgs[0] : newArgs[1];

        ArenaProperty property = ArenaProperty.fromString(propertyName);

        if (property == null) {
            error(sender, "No arena property exists with that name.");
            return;
        }

        if (usingBuilder) {

            ArenaBuilder builder = ArenaBuilderCoordinator.getInstance().getArenaBuilder(player);
            builder.handleArenaEdit(property);
            return;

        }

        ArenaManager arenaManager = ArenaManager.getInstance();
        Arena arena = arenaManager.getArena(arenaName);

        if (arena == null) {
            error(player, "No arena exists with the name '" + arenaName + "'.");
            return;
        }

        ArenaEditor.changeArenaProperty(arena, property, player);

        arenaManager.saveAllArenas();
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return ArenaManager.getInstance().getAllArenas()
                    .stream()
                    .map(Arena::getName)
                    .collect(Collectors.toList());
        }

        if (args.length == 3) {
            return Arrays.asList(
                    "spawn1",
                    "spawn2",
                    "center",
                    "spectate",
                    "kit"
            );
        }

        return null;
    }
}
