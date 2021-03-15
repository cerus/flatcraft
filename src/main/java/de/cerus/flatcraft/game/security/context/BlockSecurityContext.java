package de.cerus.flatcraft.game.security.context;

import de.cerus.flatcraft.game.FlatWorld;
import de.cerus.flatcraft.game.chunk.FlatBlock;
import org.bukkit.entity.Player;

public class BlockSecurityContext extends FlatSecurityContext {

    private final int x;
    private final int y;
    private final FlatBlock flatBlock;
    private final FlatWorld flatWorld;

    public BlockSecurityContext(final Player player, final int x, final int y, final FlatBlock flatBlock, final FlatWorld flatWorld) {
        super(player);
        this.x = x;
        this.y = y;
        this.flatBlock = flatBlock;
        this.flatWorld = flatWorld;

        this.fillRawDataMap();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public FlatBlock getFlatBlock() {
        return this.flatBlock;
    }

    public FlatWorld getFlatWorld() {
        return this.flatWorld;
    }

}
