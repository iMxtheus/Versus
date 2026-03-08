package me.robomonkey.versus.duel;

import me.robomonkey.versus.arena.Arena;
import me.robomonkey.versus.settings.Placeholder;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.EffectUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Duel {

    private final List<Player> players = new ArrayList<>();
    private final Arena arena;
    private DuelState state = DuelState.IDLE;

    private UUID winnerId;
    private UUID loserId;

    private Countdown countdown;

    private boolean isPublic = Settings.is(Setting.ANNOUNCE_DUELS);
    private boolean fightMusicEnabled = Settings.is(Setting.FIGHT_MUSIC_ENABLED);
    private boolean victoryMusicEnabled = Settings.is(Setting.VICTORY_MUSIC_ENABLED);
    private boolean victoryEffectsEnabled = Settings.is(Setting.VICTORY_EFFECTS_ENABLED);
    private boolean fireworksEnabled = Settings.is(Setting.FIREWORKS_ENABLED);
    private Color fireworkColor = Settings.getColor(Setting.FIREWORKS_COLOR);
    private Sound fightMusic = Settings.getSong(Setting.FIGHT_MUSIC);
    private Sound victorySong = Settings.getSong(Setting.VICTORY_MUSIC);

    public Duel(Arena arena, Player... duelists) {
        Collections.addAll(players, duelists);
        this.arena = arena;
    }

    // ---------------------------------------------
    // Gettery a Settery
    // ---------------------------------------------
    public List<Player> getPlayers() {
        return players;
    }

    public Arena getArena() {
        return arena;
    }

    public DuelState getState() {
        return state;
    }

    public void setState(DuelState state) {
        this.state = state;
    }

    public boolean isActive() {
        return state == DuelState.ACTIVE || state == DuelState.COUNTDOWN;
    }

    // Winner / Loser
    public Player getWinner() {
        return winnerId == null ? null : Bukkit.getPlayer(winnerId);
    }

    public void setWinner(Player winner) {
        if (winner != null) this.winnerId = winner.getUniqueId();
    }

    public Player getLoser() {
        return loserId == null ? null : Bukkit.getPlayer(loserId);
    }

    public void setLoser(Player loser) {
        if (loser != null) this.loserId = loser.getUniqueId();
    }

    // Countdown
    public Countdown getCountdown() {
        return countdown;
    }

    public void setCountdown(Countdown countdown) {
        this.countdown = countdown;
    }

    // Nastavení možností duelu
    public boolean isPublic() {
        return isPublic;
    }

    public boolean isFireworksEnabled() {
        return fireworksEnabled;
    }

    public boolean isVictoryEffectsEnabled() {
        return victoryEffectsEnabled;
    }

    public boolean isFightMusicEnabled() {
        return fightMusicEnabled;
    }

    public boolean isVictoryMusicEnabled() {
        return victoryMusicEnabled;
    }

    public Color getFireworkColor() {
        return fireworkColor == null ? Color.ORANGE : fireworkColor;
    }

    public Sound getFightMusic() {
        return fightMusic;
    }

    public Sound getVictorySong() {
        return victorySong;
    }

    // ---------------------------------------------
    // Hlavní logika
    // ---------------------------------------------

    // Ukončení duelu
    public void end(Player winner, Player loser) {
        setWinner(winner);
        setLoser(loser);
        if (countdown != null) countdown.cancel();
        setState(DuelState.ENDED);
        players.forEach(EffectUtil::unfreezePlayer);
    }

    // Spuštění countdownu
    public void startCountdown(Runnable onCountdownFinish) {
        setState(DuelState.COUNTDOWN);
        int countdownTime = Settings.getNumber(Setting.COUNTDOWN_DURATION);

        // Freeze hráče
        players.forEach(EffectUtil::freezePlayer);

        countdown = new Countdown(countdownTime, () -> {
            players.forEach(EffectUtil::unfreezePlayer);
            onCountdownFinish.run();
        });

        countdown.setOnCountdownEnd(() -> {
            String title = Settings.getMessage(Setting.COUNTDOWN_TITLE,
                    Placeholder.of("%seconds%", countdown.getSecondsRemaining()));
            String subtitle = Settings.getMessage(Setting.COUNTDOWN_MESSAGE,
                    Placeholder.of("%seconds%", countdown.getSecondsRemaining()));
            players.forEach(player -> {
                EffectUtil.sendTitle(player, title, 20, true);
                player.sendMessage(subtitle);
                EffectUtil.playSound(player, Sound.UI_BUTTON_CLICK);
            });
        });

        countdown.initiateCountdown();
    }

    public void cancelCountdown() {
        if (countdown != null) countdown.cancel();
        players.forEach(EffectUtil::unfreezePlayer);
    }

    // TODO: Implementace spectator módu
    public void spectate(Player player) {
    }
}