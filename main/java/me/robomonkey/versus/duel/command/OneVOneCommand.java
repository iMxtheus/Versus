package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.kit.Kit;
import me.robomonkey.versus.kit.KitManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

import java.util.ArrayList;
import java.util.List;

public class OneVOneCommand extends AbstractCommand implements CommandExecutor {

    private static final List<Player> queue = new ArrayList<>();
    private static final Kit DEFAULT_KIT = KitManager.getInstance().getDefaultKit();

    public OneVOneCommand() {
        super("1v1", "versus.duel");

        if (Versus.getInstance().getCommand("1v1") != null) {
            Versus.getInstance().getCommand("1v1").setExecutor(this);
        }

        setPlayersOnly(true);
        setPermissionRequired(false);
        setArgumentRequired(false);
        setUsage("/1v1");
        setDescription("Join the 1v1 queue.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;

        if (DuelManager.getInstance().isDueling(player)) {
            player.sendMessage("§cYou are already in a duel!");
            return;
        }

        if (!queue.contains(player)) {
            queue.add(player);
            player.sendTitle("§cFinding opponent...", "§cPlease wait!", 10, 999999, 10);

            if (queue.size() >= 2) {
                Player p1 = queue.remove(0);
                Player p2 = queue.remove(0);

                // Countdown 3 2 1 v podtitulu, hlavní titulek Opponent found!
                new BukkitRunnable() {
                    int countdown = 3;

                    @Override
                    public void run() {
                        if (countdown > 0) {
                            String subtitle = "§aTeleporting in " + countdown + " seconds...";
                            String title = "§aOpponent found!";
                            p1.sendTitle(title, subtitle, 5, 20, 5);
                            p2.sendTitle(title, subtitle, 5, 20, 5);
                            countdown--;
                        } else {
                            DuelManager.getInstance().setupDuel(p1, p2);
                            cancel();
                        }
                    }
                }.runTaskTimer(Versus.getInstance(), 0L, 20L); // 20 ticků = 1 sekunda
            }
        } else {
            player.sendMessage("§eYou are already in the 1v1 queue. Please wait for an opponent.");
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }

    public static void cancelQueue(Player player) {
        if (queue.contains(player)) {
            queue.remove(player);
            player.sendMessage("§cYou left the queue. Come back anytime to duel!");
        } else {
            player.sendMessage("§cYou are not currently in the 1v1 queue.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        callCommand(sender, args);
        return true;
    }
}