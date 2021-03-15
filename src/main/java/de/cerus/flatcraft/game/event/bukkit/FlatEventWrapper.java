package de.cerus.flatcraft.game.event.bukkit;

import de.cerus.flatcraft.game.event.FlatEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlatEventWrapper extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final FlatEvent event;

    public FlatEventWrapper(final FlatEvent event) {
        this.event = event;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public FlatEvent getEvent() {
        return this.event;
    }

}
