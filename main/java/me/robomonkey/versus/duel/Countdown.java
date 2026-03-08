package me.robomonkey.versus.duel;

import me.robomonkey.versus.Versus;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Countdown {

    private BukkitTask countdownTask;
    private final Versus plugin = Versus.getInstance();
    private int secondsRemaining;
    private final Player[] players;
    private Runnable onCountdownEnd;
    private Runnable onEachSecond;

    public Countdown(int duration, Player... players) {
        this.secondsRemaining = Math.max(duration, 1);
        this.players = players != null ? players : new Player[0];
    }

    public Countdown(int duration, Runnable onEnd, Player... players) {
        this(duration, players);
        this.onCountdownEnd = onEnd;
    }

    public void setOnCountdownEnd(Runnable onEnd) {
        this.onCountdownEnd = onEnd;
    }

    public void setOnEachSecond(Runnable onEach) {
        this.onEachSecond = onEach;
    }

    public void start() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (secondsRemaining > 0) {
                String color = getColorForTime(secondsRemaining);
                String bigNumber = String.valueOf(secondsRemaining);

                for (Player player : players) {
                    if (player == null || !player.isOnline()) continue;

                    player.sendTitle(
                            color + bigNumber,
                            "§7Duel starting in",
                            0, 25, 0
                    );

                    float pitch = secondsRemaining <= 3 ? 1.4f + (3 - secondsRemaining) * 0.4f : 1.0f;
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, pitch);
                }

                if (onEachSecond != null) {
                    onEachSecond.run();
                }

                secondsRemaining--;
            } else {
                finish();
            }
        }, 0L, 20L);
    }

    private String getColorForTime(int seconds) {
        if (seconds <= 3) return "§c";
        if (seconds <= 6) return "§6";
        return "§e";
    }

    private void finish() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        for (Player player : players) {
            if (player == null || !player.isOnline()) continue;

            // ZMENENÉ PRESNE TAK, AKO SI CHCEL:
            player.sendTitle("§a§lDUEL STARTED!", "", 10, 60, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 1.0f);
        }

        if (onCountdownEnd != null) {
            onCountdownEnd.run();
        }
    }

    public void cancel() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        for (Player player : players) {
            if (player != null && player.isOnline()) {
                player.resetTitle();
            }
        }
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }

    public boolean isRunning() {
        return countdownTask != null && !countdownTask.isCancelled();
    }

    public void initiateCountdown() {
        start();
    }
}