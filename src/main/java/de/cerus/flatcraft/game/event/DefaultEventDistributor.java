package de.cerus.flatcraft.game.event;

import de.cerus.flatcraft.game.event.bukkit.FlatEventWrapper;
import de.cerus.flatcraft.game.event.listener.FlatEventListener;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;

public class DefaultEventDistributor implements FlatEventDistributor {

    private final Set<FlatEventListener> listeners = new HashSet<>();

    @Override
    public void distributeEvent(final FlatEvent event) {
        for (final FlatEventListener listener : this.listeners) {
            listener.handleEvent(event);
        }
        Bukkit.getPluginManager().callEvent(new FlatEventWrapper(event));
    }

    @Override
    public void registerListener(final FlatEventListener listener) {
        this.listeners.add(listener);
    }

}
