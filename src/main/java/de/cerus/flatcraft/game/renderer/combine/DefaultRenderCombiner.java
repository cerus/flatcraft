package de.cerus.flatcraft.game.renderer.combine;

import de.cerus.flatcraft.game.FlatCraftGame;
import de.cerus.flatcraft.game.renderer.menu.FlatMenuRenderer;
import de.cerus.flatcraft.game.renderer.world.DefaultFlatWorldRenderer;
import de.cerus.flatcraft.game.renderer.world.FlatWorldRenderer;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;

/**
 * Default renderer combiner
 */
public class DefaultRenderCombiner implements RenderCombiner {

    private static final int WORLD_RENDER_WIDTH = DefaultFlatWorldRenderer.WIDTH;
    private static final int WORLD_RENDER_HEIGHT = DefaultFlatWorldRenderer.HEIGHT;
    private static final int TOTAL_MAX_WIDTH = 35;
    private static final int TOTAL_MAX_HEIGHT = 20;

    private FlatWorldRenderer worldRenderer;
    private FlatMenuRenderer menuRenderer;
    private String horSpacing;
    private int vertSpacing;

    @Override
    public void setCombineWorldMenu(final FlatWorldRenderer worldRenderer,
                                    final FlatMenuRenderer menuRenderer,
                                    final String horSpacing,
                                    final int vertSpacing) {
        this.worldRenderer = worldRenderer;
        this.menuRenderer = menuRenderer;
        this.horSpacing = horSpacing;
        this.vertSpacing = vertSpacing;
    }

    @Override
    public List<Component> render(final FlatCraftGame game) {
        // Get the two outputs
        final List<Component> worldRenderOutput = this.worldRenderer.render(game, game.getWorld().getPlayer().getX(), game.getWorld().getPlayer().getY());
        final List<Component> menuRenderOutput = this.menuRenderer.render(game, WORLD_RENDER_HEIGHT,
                TOTAL_MAX_WIDTH - WORLD_RENDER_WIDTH - (this.horSpacing.length() * 3));

        // Add leading spacing
        final List<Component> output = new ArrayList<>();
        for (int i = 0; i < this.vertSpacing; i++) {
            output.add(Component.empty());
        }
        // Combine
        for (int i = 0; i < menuRenderOutput.size(); i++) {
            final Component menuComponent = menuRenderOutput.get(i);
            final Component worldComponent = worldRenderOutput.get(i);

            // World + Menu + Some spacing = <3
            output.add(Component.text(this.horSpacing).append(worldComponent).append(Component.text(this.horSpacing))
                    .append(menuComponent).append(Component.text(this.horSpacing)));
        }
        // Add trailing spacing
        for (int i = 0; i < this.vertSpacing; i++) {
            output.add(Component.empty());
        }
        return output;
    }

}
