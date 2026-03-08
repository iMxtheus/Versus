package me.robomonkey.versus.arena.command;

import me.robomonkey.versus.command.RootCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class RootArenaCommand extends RootCommand {

    public RootArenaCommand() {
        super("arena", "versus.arena");
        setUsage("/arena <create|delete|edit|set|list|visit>");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(getUsage());
            return;
        }

        String sub = args[0].toLowerCase();

        // odstraní první argument ("set", "create"...)
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (sub) {

            case "create":
                new CreateCommand().callCommand(sender, newArgs);
                break;

            case "delete":
                new DeleteCommand().callCommand(sender, newArgs);
                break;

            case "edit":
                new EditCommand().callCommand(sender, newArgs);
                break;

            case "set":
                new SetCommand().callCommand(sender, newArgs);
                break;

            case "list":
                new ListCommand().callCommand(sender, newArgs);
                break;

            case "visit":
                new VisitCommand().callCommand(sender, newArgs);
                break;

            default:
                sender.sendMessage(getUsage());
                break;
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of("create", "delete", "edit", "set", "list", "visit");
    }
}