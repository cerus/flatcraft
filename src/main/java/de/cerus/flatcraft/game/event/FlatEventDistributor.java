package de.cerus.flatcraft.game.event;

import de.cerus.flatcraft.game.event.listener.FlatEventListener;

public interface FlatEventDistributor {

    void distributeEvent(FlatEvent event);

    void registerListener(FlatEventListener listener);

}
