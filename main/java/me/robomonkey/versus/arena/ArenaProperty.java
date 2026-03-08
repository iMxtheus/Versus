package me.robomonkey.versus.arena;

import java.util.ArrayList;
import java.util.List;

public enum ArenaProperty {

    CENTER_LOCATION("center of the arena",
            "Select the center of the arena."),

    SPAWN_LOCATION_ONE("first spawn location",
            "Location where the first player will spawn."),

    SPAWN_LOCATION_TWO("second spawn location",
            "Location where the second player will spawn."),

    SPECTATE_LOCATION("location for spectators",
            "Location where spectators will watch the duel."),

    KIT("kit for players",
            "Kit that players will receive in this arena.");

    private final String friendlyString;
    private final String explanation;

    ArenaProperty(String friendlyString, String explanation) {
        this.friendlyString = friendlyString;
        this.explanation = explanation;
    }

    public String toFriendlyString() {
        return friendlyString;
    }

    public String getExplanation() {
        return explanation;
    }

    public ArenaProperty getNextProperty() {
        switch (this) {
            case CENTER_LOCATION:
                return SPAWN_LOCATION_ONE;
            case SPAWN_LOCATION_ONE:
                return SPAWN_LOCATION_TWO;
            case SPAWN_LOCATION_TWO:
                return SPECTATE_LOCATION;
            case SPECTATE_LOCATION:
                return KIT;
            default:
                return null;
        }
    }

    public static ArenaProperty fromString(String name) {

        if (name == null) return null;

        name = name.toLowerCase();

        switch (name) {

            case "center":
            case "center_location":
                return CENTER_LOCATION;

            case "spawn1":
            case "spawn_location_one":
            case "spawn_location1":
                return SPAWN_LOCATION_ONE;

            case "spawn2":
            case "spawn_location_two":
            case "spawn_location2":
                return SPAWN_LOCATION_TWO;

            case "spectate":
            case "spectate_location":
                return SPECTATE_LOCATION;

            case "kit":
                return KIT;

            default:
                return null;
        }
    }

    public static List<String> suggestProperties(String input) {

        List<String> suggestions = new ArrayList<>();

        if (input == null) return suggestions;

        input = input.toLowerCase();

        for (ArenaProperty property : values()) {

            if (property.name().toLowerCase().contains(input) ||
                    property.toFriendlyString().toLowerCase().contains(input)) {

                suggestions.add(property.name().toLowerCase());
            }
        }

        return suggestions;
    }
}