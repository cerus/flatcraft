package de.cerus.flatcraft.game.event.events;

import de.cerus.flatcraft.game.FlatCraftGame;
import de.cerus.flatcraft.game.event.FlatEvent;

public class FlatGameInitEvent implements FlatEvent {

    private final FlatCraftGame game;

    public FlatGameInitEvent(final FlatCraftGame game) {
        this.game = game;
    }

    @Override
    public FlatCraftGame getGame() {
        return this.game;
    }

}
