package me.robomonkey.versus.duel.eventlisteners;

import me.robomonkey.versus.duel.DuelManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEventListener implements Listener {

    private final DuelManager duelManager = DuelManager.getInstance();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!duelManager.isDueling(player)) return;

        // Označí hráče jako poraženého a duel se ukončí
        duelManager.registerDuelistDeath(player, false);
    }
}