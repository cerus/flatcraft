package de.cerus.flatcraft.game.security.context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

/**
 * Simple context class for the security provider. Is passed along when checking if actions are ok.
 * It is advised to use implementations of this class instead of depending on the raw data
 */
public abstract class FlatSecurityContext {

    public final Map<String, Object> rawDataMap = new HashMap<>();
    private final Player player;

    protected FlatSecurityContext(final Player player) {
        this.player = player;
    }

    protected void fillRawDataMap() {
        Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> !field.getName().equals("player"))
                .filter(field -> !field.getName().equals("rawDataMap"))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        final Object o = field.get(FlatSecurityContext.this);
                        this.rawDataMap.put("field_" + field.getName(), o);
                    } catch (final IllegalAccessException ignored) {
                    }
                });
    }

    public Player getPlayer() {
        return this.player;
    }

}
