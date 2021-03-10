package de.cerus.flatcraft.game.renderer.combine;

import de.cerus.flatcraft.game.FlatCraftGame;
import de.cerus.flatcraft.game.renderer.menu.FlatMenuRenderer;
import de.cerus.flatcraft.game.renderer.world.FlatWorldRenderer;
import java.util.List;
import net.kyori.adventure.text.Component;

/**
 * Combines the output of a world renderer and a menu renderer into one single output
 */
public interface RenderCombiner {

    /**
     * Initialize this combiner
     *
     * @param worldRenderer The world renderer
     * @param menuRenderer  The menu renderer
     * @param horSpacing    The horizontal spacing
     * @param vertSpacing   The amount of vertical spacing lines
     */
    void setCombineWorldMenu(FlatWorldRenderer worldRenderer, FlatMenuRenderer menuRenderer, String horSpacing, int vertSpacing);

    /**
     * Combine the renderer outputs into a single list of chat components
     *
     * @param game The game
     *
     * @return The combined outputs
     */
    List<Component> render(FlatCraftGame game);

}
