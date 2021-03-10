package de.cerus.flatcraft.game.renderer.menu;

import de.cerus.flatcraft.game.FlatCraftGame;
import java.util.List;
import net.kyori.adventure.text.Component;

/**
 * Rendering interface for the control menu
 */
public interface FlatMenuRenderer {

    /**
     * Renders the menu into a list of chat components
     *
     * @param game      The game object
     * @param maxHeight The max height of the menu
     * @param maxWidth  The max width of the menu
     *
     * @return a list of chat components
     */
    List<Component> render(FlatCraftGame game, int maxHeight, int maxWidth);

}
