package me.robomonkey.versus.arena;

import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.EffectUtil;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ArenaBuilder {

    private ArenaProperty currentProperty;
    private final Player builder;
    private final Arena targetArena;

    private final ArenaBuilderCoordinator coordinator = ArenaBuilderCoordinator.getInstance();

    public ArenaBuilder(Player builder, String name) {
        this.builder = builder;
        this.targetArena = new Arena(name);
    }

    public Player getBuilder() {
        return builder;
    }

    public Arena getTargetArena() {
        return targetArena;
    }

    /**
     * Spustí další krok builderu
     */
    public void handleNextStep() {

        if (currentProperty == null) {
            currentProperty = ArenaProperty.CENTER_LOCATION;
        } else {
            currentProperty = currentProperty.getNextProperty();
        }

        if (currentProperty == null) {
            finalizeArena();
            return;
        }

        ArenaEditor.displayInstructionalMessage(targetArena, currentProperty, builder);
        builder.playSound(builder.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
    }

    /**
     * Nastaví property arény
     */
    public void handleArenaEdit(ArenaProperty property) {

        if (property == null) {
            builder.sendMessage(MessageUtil.get("&cInvalid arena property."));
            return;
        }

        ArenaEditor.changeArenaProperty(targetArena, property, builder, () -> {

            // pokud builder nastavuje správnou property → pokračuj
            if (currentProperty == property) {
                handleNextStep();
            }

        });

    }

    /**
     * Dokončí vytváření arény
     */
    private void finalizeArena() {

        builder.sendMessage(MessageUtil.get(
                "&pYou have completed the construction of the &h" +
                        targetArena.getName() + "&p arena!"
        ));

        EffectUtil.playSound(builder, Sound.ENTITY_CAT_AMBIENT);
        EffectUtil.spawnFireWorks(builder.getLocation(),
                Settings.getColor(Setting.FIREWORKS_COLOR));

        ArenaManager.getInstance().addArena(targetArena);

        coordinator.removeArenaBuilder(builder);
    }

}
