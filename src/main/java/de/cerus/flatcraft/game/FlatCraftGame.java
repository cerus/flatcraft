package de.cerus.flatcraft.game;

import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.game.renderer.combine.DefaultRenderCombiner;
import de.cerus.flatcraft.game.renderer.combine.RenderCombiner;
import de.cerus.flatcraft.game.renderer.menu.DefaultFlatMenuRenderer;
import de.cerus.flatcraft.game.renderer.menu.FlatMenuRenderer;
import de.cerus.flatcraft.game.renderer.world.DefaultFlatWorldRenderer;
import de.cerus.flatcraft.game.renderer.world.FlatWorldRenderer;
import java.util.List;
import net.kyori.adventure.text.Component;

public class FlatCraftGame {

    public static final int MODE_WALK = 0;
    public static final int MODE_PLACE = 1;
    public static final int MODE_BREAK = 2;

    private final FlatWorld world;
    private final FlatWorldRenderer worldRenderer;
    private final FlatMenuRenderer menuRenderer;
    private final RenderCombiner renderCombiner;

    // 0 = Walk, 1 = Place, 2 = Break
    private int mode = 0;
    private FlatBlock selectedBlock = FlatBlock.BLOCK_STONE;

    public FlatCraftGame(final FlatWorld world) {
        this(world, new DefaultFlatWorldRenderer(), new DefaultFlatMenuRenderer(), new DefaultRenderCombiner());
    }

    public FlatCraftGame(final FlatWorld world, final FlatWorldRenderer worldRenderer, final FlatMenuRenderer menuRenderer, final RenderCombiner renderCombiner) {
        this.world = world;
        this.worldRenderer = worldRenderer;
        this.menuRenderer = menuRenderer;
        this.renderCombiner = renderCombiner;
    }

    public boolean handleInteract(final int x, final int y) {
        switch (this.mode) {
            case MODE_WALK:
                if (x == this.world.getPlayer().x) {
                    return false;
                }

                final int newX;
                if (x < this.world.getPlayer().x) {
                    // Left
                    newX = this.world.getPlayer().x - 1;
                } else {
                    // Right
                    newX = this.world.getPlayer().x + 1;
                }

                if (newX < 0 || newX / 16 > this.world.getMaxSize()) {
                    return false;
                }

                FlatBlock block;
                if ((block = this.world.getBlockAt(newX, this.world.getPlayer().y)) != FlatBlock.BLOCK_AIR && block.hasCollider()
                        || (block = this.world.getBlockAt(newX, this.world.getPlayer().y + 1)) != FlatBlock.BLOCK_AIR && block.hasCollider()) {
                    if ((block = this.world.getBlockAt(newX, this.world.getPlayer().y + 1)) != FlatBlock.BLOCK_AIR && block.hasCollider()
                            || (block = this.world.getBlockAt(newX, this.world.getPlayer().y + 2)) != FlatBlock.BLOCK_AIR && block.hasCollider()) {
                        return false;
                    }

                    this.world.getPlayer().x = newX;
                    this.world.getPlayer().y += 1;
                } else {
                    this.world.getPlayer().x = newX;
                }

                while (this.world.getPlayer().y > 0 && (block = this.world.getBlockAt(newX, this.world.getPlayer().y - 1)) == FlatBlock.BLOCK_AIR || !block.hasCollider()) {
                    this.world.getPlayer().y -= 1;
                }
                return true;
            case MODE_PLACE:
                if (x < 0 || x / 16 > this.world.getMaxSize() || y < 1 || y > 60) {
                    return false;
                }

                this.world.setBlockAt(x, y, this.selectedBlock);
                if (this.world.getPlayer().y == y && this.world.getPlayer().x == x && this.selectedBlock.hasCollider()) {
                    this.world.getPlayer().y += 1;
                }
                return true;
            case MODE_BREAK:
                // Prevent out of bounds breaking
                if (x < 0 || x / 16 > this.world.getMaxSize() || y < 1 || y > 63) {
                    return false;
                }
                if (this.world.getBlockAt(x, y) == FlatBlock.BLOCK_AIR) {
                    return false;
                }

                this.world.setBlockAt(x, y, FlatBlock.BLOCK_AIR);
                if (this.world.getPlayer().y > 0) {
                    while (this.world.getBlockAt(this.world.getPlayer().x, this.world.getPlayer().y - 1) == FlatBlock.BLOCK_AIR) {
                        this.world.getPlayer().y -= 1;
                    }
                }
                return true;
            default:
                return false;
        }
    }

    public boolean selectBlock(final FlatBlock block) {
        if (block != null) {
            this.selectedBlock = block;
            return true;
        }
        return false;
    }

    public List<Component> render() {
        this.renderCombiner.setCombineWorldMenu(this.worldRenderer, this.menuRenderer, "  ", 2);
        return this.renderCombiner.render(this);
    }

    public FlatWorld getWorld() {
        return this.world;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(final int mode) {
        this.mode = mode;
    }

    public FlatBlock getSelectedBlock() {
        return this.selectedBlock;
    }

}
