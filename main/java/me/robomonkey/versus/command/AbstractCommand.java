package me.robomonkey.versus.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand {

    protected String command;
    protected String permission;

    private boolean playersOnly = false;
    private boolean permissionRequired = false;
    private boolean argumentRequired = false;
    private int maxArguments = -1;

    private String usage = "";
    private String description = "";

    private final List<AbstractCommand> subCommands = new ArrayList<>();

    public AbstractCommand(String command, String permission) {
        this.command = command;
        this.permission = permission;
    }

    public AbstractCommand() {}

    /** hlavní volání příkazu */
    public void dispatchCommand(CommandSender sender, String[] args) {

        if (args.length > 0) {
            for (AbstractCommand sub : subCommands) {
                if (sub.command.equalsIgnoreCase(args[0])) {
                    String[] subArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                    sub.dispatchCommand(sender, subArgs);
                    return;
                }
            }
        }

        if (playersOnly && !(sender instanceof Player)) {
            error(sender, "Only players can use this command.");
            return;
        }

        if (permissionRequired && permission != null && !sender.hasPermission(permission)) {
            error(sender, "You do not have permission.");
            return;
        }

        if (argumentRequired && args.length == 0) {
            error(sender, usage);
            return;
        }

        if (maxArguments != -1 && args.length > maxArguments) {
            error(sender, usage);
            return;
        }

        callCommand(sender, args);
    }

    /** Tab completion */
    public List<String> dispatchTabCompleter(CommandSender sender, String[] args) {
        List<String> completions = callCompletionsUpdate(sender, args);
        return completions == null ? new ArrayList<>() : completions;
    }

    /** Přidání podpříkazu */
    public void addSubCommand(AbstractCommand subCommand) {
        subCommands.add(subCommand);
    }

    /** Zjednodušená metoda spojení argumentů */
    protected String buildArgs(String[] args, int i, int length) {
        return String.join(" ", args);
    }

    /** abstraktní metody, musí implementovat každá třída */
    public abstract void callCommand(CommandSender sender, String[] args);
    public abstract List<String> callCompletionsUpdate(CommandSender sender, String[] args);

    /** Setter metody */
    public void setPlayersOnly(boolean playersOnly) {
        this.playersOnly = playersOnly;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    public void setArgumentRequired(boolean argumentRequired) {
        this.argumentRequired = argumentRequired;
    }

    public void setMaxArguments(int maxArguments) {
        this.maxArguments = maxArguments;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    /** Chyba a info zprávy */
    protected void error(CommandSender sender, String message) {
        sender.sendMessage("§c" + message);
    }

    protected void info(CommandSender sender, String message) {
        sender.sendMessage("§a" + message);
    }
}