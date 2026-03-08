package me.robomonkey.versus.settings.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigSetCommand extends AbstractCommand {

    public ConfigSetCommand() {
        super("set", null);
        setUsage("/versus config set <name> <value>");
        setDescription("Changes a config setting named 'name' to 'value'.");
        // Odstranili jsme setMinArguments, kontrolujeme přímo níže
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        // Kontrola minimálního počtu argumentů
        if (args.length < 2) {
            sender.sendMessage(MessageUtil.color("&cUsage: /versus config set <name> <value>"));
            return;
        }

        String settingName = args[0].toUpperCase();

        if (!Settings.isSetting(settingName)) {
            sender.sendMessage(MessageUtil.color("&c'" + settingName + "' is not a real config option."));
            return;
        }

        Setting setting = Setting.valueOf(settingName);
        String option = buildArgs(args, 1, args.length);
        Object converted = Settings.tryConvertFromString(option, setting);

        if (converted == null) {
            sender.sendMessage(MessageUtil.color("&c'" + option + "' is not a proper value. " +
                    setting + " requires a " + setting.type.toString() + "."));
            return;
        }

        Settings.getInstance().changeSetting(setting, converted);
        sender.sendMessage(MessageUtil.color("&aSuccessfully set &b" + setting + " &ato &b'" + option + "' &ain the config file."));
        sender.sendMessage(MessageUtil.color("&eType &b/versus config save &eto save all changes."));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Setting.values())
                    .filter(setting -> setting.getType() != Setting.Type.INVALID)
                    .map(setting -> setting.toString().toLowerCase())
                    .collect(Collectors.toList());
        } else if (args.length > 1) {
            String settingName = args[0].toUpperCase();
            if (!Settings.isSetting(settingName)) return List.of();
            return Setting.valueOf(settingName).getType().getOptions();
        }
        return List.of();
    }
}