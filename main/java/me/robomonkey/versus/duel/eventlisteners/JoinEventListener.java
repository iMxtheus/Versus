package me.robomonkey.versus.duel.eventlisteners;

import me.robomonkey.versus.duel.DuelManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEventListener implements Listener {

    private final DuelManager duelManager = DuelManager.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Pokud hráč není mrtvý a má uložená data, obnov je
        if (!player.isDead() && duelManager.hasStoredData(player)) {
            duelManager.restoreData(player, false);
        }
    }
}