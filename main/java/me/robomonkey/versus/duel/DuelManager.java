package me.robomonkey.versus.duel;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.arena.Arena;
import me.robomonkey.versus.arena.ArenaManager;
import me.robomonkey.versus.duel.playerdata.DataManager;
import me.robomonkey.versus.duel.playerdata.PlayerData;
import me.robomonkey.versus.duel.request.RequestManager;
import me.robomonkey.versus.kit.Kit;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.EffectUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class DuelManager {

    private static DuelManager instance;

    private final HashMap<UUID, Duel> duelistMap = new HashMap<>();
    private final ArenaManager arenaManager = ArenaManager.getInstance();
    private final DataManager dataManager = new DataManager();
    private final Versus plugin = Versus.getInstance();

    private DuelManager() {
        instance = this;
        dataManager.loadDataMap();
    }

    public static DuelManager getInstance() {
        if (instance == null) new DuelManager();
        return instance;
    }

    public boolean isDueling(Player player) {
        return duelistMap.containsKey(player.getUniqueId());
    }

    public Duel getDuel(Player player) {
        return duelistMap.get(player.getUniqueId());
    }

    private void addDuel(Duel duel) {
        duel.getPlayers().forEach(player -> duelistMap.put(player.getUniqueId(), duel));
    }

    private void unregisterFromDuel(Player player) {
        duelistMap.remove(player.getUniqueId());
    }

    public void setupDuel(Player p1, Player p2) {
        Arena arena = arenaManager.getAvailableArena();
        if (arena == null) {
            p1.sendMessage("§cNo available arena!");
            p2.sendMessage("§cNo available arena!");
            return;
        }

        Duel duel = new Duel(arena, p1, p2);
        addDuel(duel);
        arenaManager.registerDuel(arena, duel);

        // Uložíme data hráčů
        duel.getPlayers().forEach(player -> dataManager.save(player, arena));

        // Teleport
        p1.teleport(arena.getSpawnLocationOne());
        p2.teleport(arena.getSpawnLocationTwo());

        // Připravíme hráče
        duel.getPlayers().forEach(this::preparePlayer);

        // Kit
        Kit kit = arena.getKit();
        duel.getPlayers().forEach(player -> player.getInventory().setContents(kit.getItems()));

        // Start countdown 3-2-1
        duel.startCountdown(() -> commenceDuel(duel));
    }

    private void preparePlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setInvulnerable(true);
        player.setAllowFlight(false);
        player.setLevel(0);
        player.setExp(0);

        // Vyčistí efekty
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 10));
        EffectUtil.unfreezePlayer(player);
        player.setItemOnCursor(null);
        player.closeInventory();
        player.stopAllSounds();
    }

    private void commenceDuel(Duel duel) {
        duel.setState(DuelState.ACTIVE);
        duel.getPlayers().forEach(player -> player.setInvulnerable(false));
    }

    public void registerDuelistDeath(Player loser, boolean fakeDeath) {
        Duel duel = getDuel(loser);
        if (duel == null) return;

        if (duel.getState() == DuelState.COUNTDOWN) {
            duel.cancelCountdown();
        }

        Optional<Player> winnerOpt = duel.getPlayers().stream()
                .filter(p -> !p.equals(loser))
                .findFirst();
        Player winner = winnerOpt.orElse(null);

        duel.end(winner, loser);
        stopDuel(duel);
    }

    private void stopDuel(Duel duel) {
        duel.setState(DuelState.ENDED);

        duel.getPlayers().forEach(this::unregisterFromDuel);
        arenaManager.removeDuel(duel);

        Player winner = duel.getWinner();
        Player loser = duel.getLoser();

        if (winner != null) {
            EffectUtil.sendTitle(winner, "§e§lVictory", 100, true);
            if (duel.isFireworksEnabled()) {
                EffectUtil.spawnFireWorks(winner.getLocation(), 1, 50, duel.getFireworkColor());
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> restoreData(winner, true), 100L);
        }

        if (loser != null) {
            loser.getWorld().strikeLightningEffect(loser.getLocation());
            if (Settings.is(Setting.INSTANT_RESPAWN)) {
                restoreData(loser, false);
            }
        }

        RequestManager.getInstance().notifyDuelCompletion();
    }

    public void restoreData(Player player, boolean winner) {
        if (!player.isOnline()) return;
        if (!dataManager.contains(player)) return;

        PlayerData data = dataManager.extractData(player);
        player.getInventory().setContents(data.items);
        player.setLevel(data.xpLevel);
        player.setExp(data.xpProgress);

        Location target = player.getWorld().getSpawnLocation();

        switch (Settings.getReturnOption(winner ? Setting.RETURN_WINNERS : Setting.RETURN_LOSERS)) {
            case PREVIOUS:
                if (data.previousLocation != null) target = data.previousLocation.toLocation();
                break;
            case CUSTOM:
                target = Settings.getLocation(winner ? Setting.WINNER_RETURN_LOCATION : Setting.LOSER_RETURN_LOCATION);
                break;
            case SPECTATE:
                if (data.arenaName != null) {
                    Arena arena = arenaManager.getArena(data.arenaName);
                    if (arena != null) target = arena.getSpectateLocation();
                }
                break;
            case SPAWN:
            default:
                break;
        }

        player.teleport(target);
    }

    public boolean hasStoredData(Player player) {
        return dataManager.contains(player);
    }
}