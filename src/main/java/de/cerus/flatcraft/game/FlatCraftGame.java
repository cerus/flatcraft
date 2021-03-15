package de.cerus.flatcraft.game;

import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.game.event.DefaultEventDistributor;
import de.cerus.flatcraft.game.event.FlatEventDistributor;
import de.cerus.flatcraft.game.event.events.FlatGameInitEvent;
import de.cerus.flatcraft.game.renderer.combine.DefaultRenderCombiner;
import de.cerus.flatcraft.game.renderer.combine.RenderCombiner;
import de.cerus.flatcraft.game.renderer.menu.DefaultFlatMenuRenderer;
import de.cerus.flatcraft.game.renderer.menu.FlatMenuRenderer;
import de.cerus.flatcraft.game.renderer.world.DefaultFlatWorldRenderer;
import de.cerus.flatcraft.game.renderer.world.FlatWorldRenderer;
import de.cerus.flatcraft.game.security.DefaultFlatSecurityProvider;
import de.cerus.flatcraft.game.security.FlatSecurityProvider;
import de.cerus.flatcraft.game.security.context.BlockSecurityContext;
import de.cerus.flatcraft.game.security.context.MoveSecurityContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.MagicConstant;

public class FlatCraftGame {

    public static final int MODE_WALK = 0;
    public static final int MODE_PLACE = 1;
    public static final int MODE_BREAK = 2;

    private final Player bukkitPlayer;
    private final FlatWorld world;
    private final Set<FlatSecurityProvider> securityProviderSet;
    private FlatWorldRenderer worldRenderer;
    private FlatMenuRenderer menuRenderer;
    private RenderCombiner renderCombiner;
    private FlatEventDistributor eventDistributor;

    // 0 = Walk, 1 = Place, 2 = Break
    private int mode = 0;
    private FlatBlock selectedBlock = FlatBlock.BLOCK_STONE;

    public FlatCraftGame(final FlatWorld world, final Player bukkitPlayer) {
        this(bukkitPlayer, world, new DefaultFlatWorldRenderer(), new DefaultFlatMenuRenderer(), new DefaultRenderCombiner());
    }

    public FlatCraftGame(final Player bukkitPlayer, final FlatWorld world, final FlatWorldRenderer worldRenderer, final FlatMenuRenderer menuRenderer, final RenderCombiner renderCombiner) {
        this.bukkitPlayer = bukkitPlayer;
        this.world = world;
        this.worldRenderer = worldRenderer;
        this.menuRenderer = menuRenderer;
        this.renderCombiner = renderCombiner;
        this.eventDistributor = new DefaultEventDistributor();
        this.securityProviderSet = new HashSet<>();

        this.securityProviderSet.add(new DefaultFlatSecurityProvider());
        this.eventDistributor.distributeEvent(new FlatGameInitEvent(this));
    }

    public boolean handleInteract(final int x, final int y) {
        switch (this.mode) {
            case MODE_WALK:
                if (x == this.world.player.x) {
                    return false;
                }

                // Save current coordinates
                final int prevX = this.world.player.x;
                final int prevY = this.world.player.y;

                final int newX;
                if (x < this.world.player.x) {
                    // Left
                    newX = this.world.player.x - 1;
                } else {
                    // Right
                    newX = this.world.player.x + 1;
                }

                if (newX < 0 || newX / 16 > this.world.getMaxSize()) {
                    return false;
                }

                FlatBlock block;
                if ((block = this.world.getBlockAt(newX, this.world.player.y)) != FlatBlock.BLOCK_AIR && block.hasCollider()
                        || (block = this.world.getBlockAt(newX, this.world.player.y + 1)) != FlatBlock.BLOCK_AIR && block.hasCollider()) {
                    if ((block = this.world.getBlockAt(newX, this.world.player.y + 1)) != FlatBlock.BLOCK_AIR && block.hasCollider()
                            || (block = this.world.getBlockAt(newX, this.world.player.y + 2)) != FlatBlock.BLOCK_AIR && block.hasCollider()) {
                        return false;
                    }

                    this.world.player.x = newX;
                    this.world.player.y += 1;
                } else {
                    this.world.player.x = newX;
                }

                while (this.world.player.y > 0 && (block = this.world.getBlockAt(newX, this.world.player.y - 1)) == FlatBlock.BLOCK_AIR || !block.hasCollider()) {
                    this.world.player.y -= 1;
                }

                // Load nearby 4 chunks
                final FlatPlayer player = this.world.player;
                if ((player.x / 16) + 1 < this.world.getMaxSize()) {
                    this.world.getChunkAt((this.world.player.x / 16) + 1);
                }
                if ((player.x / 16) + 2 < this.world.getMaxSize()) {
                    this.world.getChunkAt((this.world.player.x / 16) + 2);
                }
                if ((player.x / 16) - 1 >= 0) {
                    this.world.getChunkAt((this.world.player.x / 16) - 1);
                }
                if ((player.x / 16) - 2 >= 0) {
                    this.world.getChunkAt((this.world.player.x / 16) - 2);
                }

                // Check if security providers are okay with this
                if (!this.securityProviderSet.stream()
                        .allMatch(flatSecurityProvider ->
                                flatSecurityProvider.checkAction(FlatSecurityProvider.FlatAction.MOVE, new MoveSecurityContext(
                                        this.bukkitPlayer,
                                        prevX,
                                        prevY,
                                        this.world.player.x,
                                        this.world.player.y,
                                        this.world
                                )))) {
                    // Reset coords
                    this.world.player.x = prevX;
                    this.world.player.y = prevY;
                    return false;
                }
                return true;
            case MODE_PLACE:
                if (x < 0 || x / 16 > this.world.getMaxSize() || y < 1 || y > 60) {
                    return false;
                }

                // Check if security providers are okay with this
                if (!this.securityProviderSet.stream()
                        .allMatch(flatSecurityProvider ->
                                flatSecurityProvider.checkAction(FlatSecurityProvider.FlatAction.PLACE_BLOCK, new BlockSecurityContext(
                                        this.bukkitPlayer,
                                        x,
                                        y,
                                        this.selectedBlock,
                                        this.world
                                )))) {
                    return false;
                }

                this.world.setBlockAt(x, y, this.selectedBlock);
                if (this.world.player.y == y && this.world.player.x == x && this.selectedBlock.hasCollider()) {
                    this.world.player.y += 1;
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

                // Check if security providers are okay with this
                if (!this.securityProviderSet.stream()
                        .allMatch(flatSecurityProvider ->
                                flatSecurityProvider.checkAction(FlatSecurityProvider.FlatAction.BREAK_BLOCK, new BlockSecurityContext(
                                        this.bukkitPlayer,
                                        x,
                                        y,
                                        this.selectedBlock,
                                        this.world
                                )))) {
                    return false;
                }

                this.world.setBlockAt(x, y, FlatBlock.BLOCK_AIR);
                if (this.world.player.y > 0) {
                    while (this.world.getBlockAt(this.world.player.x, this.world.player.y - 1) == FlatBlock.BLOCK_AIR) {
                        this.world.player.y -= 1;
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

    public void setMode(@MagicConstant(intValues = {MODE_WALK, MODE_PLACE, MODE_BREAK}) final int mode) {
        this.mode = mode;
    }

    public FlatBlock getSelectedBlock() {
        return this.selectedBlock;
    }

    public Player getBukkitPlayer() {
        return this.bukkitPlayer;
    }

    public Set<FlatSecurityProvider> getSecurityProviderSet() {
        return this.securityProviderSet;
    }

    public FlatWorldRenderer getWorldRenderer() {
        return this.worldRenderer;
    }

    public void setWorldRenderer(final FlatWorldRenderer worldRenderer) {
        this.worldRenderer = worldRenderer;
    }

    public FlatMenuRenderer getMenuRenderer() {
        return this.menuRenderer;
    }

    public void setMenuRenderer(final FlatMenuRenderer menuRenderer) {
        this.menuRenderer = menuRenderer;
    }

    public RenderCombiner getRenderCombiner() {
        return this.renderCombiner;
    }

    public void setRenderCombiner(final RenderCombiner renderCombiner) {
        this.renderCombiner = renderCombiner;
    }

    public FlatEventDistributor getEventDistributor() {
        return this.eventDistributor;
    }

    public void setEventDistributor(final FlatEventDistributor eventDistributor) {
        this.eventDistributor = eventDistributor;
    }
}
