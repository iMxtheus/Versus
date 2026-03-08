package me.robomonkey.versus.duel.eventlisteners;

import me.robomonkey.versus.duel.Duel;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.duel.DuelState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEventListener implements Listener {

    private final DuelManager duelManager = DuelManager.getInstance();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!duelManager.isDueling(player)) return;

        Duel duel = duelManager.getDuel(player);
        if (duel.getState() == DuelState.COUNTDOWN) {
            // Zkontroluje, zda hráč opravdu změnil pozici
            if (event.getFrom().distanceSquared(event.getTo()) > 0) {
                event.setCancelled(true);
            }
        }
    }
}