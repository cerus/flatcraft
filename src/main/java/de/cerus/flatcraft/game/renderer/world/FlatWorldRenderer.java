package de.cerus.flatcraft.game.renderer.world;

import de.cerus.flatcraft.game.FlatCraftGame;
import java.util.List;
import net.kyori.adventure.text.Component;

/**
 * Renderer interface for the world
 */
public interface FlatWorldRenderer {

    /**
     * Renders the world around the given coordinates into a list of chat components
     *
     * @param game The game object
     * @param xPos The x coordinate
     * @param yPos The y coordinate
     *
     * @return a list of components
     */
    List<Component> render(FlatCraftGame game, int xPos, int yPos);

}
