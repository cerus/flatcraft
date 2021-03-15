package de.cerus.flatcraft.game.security.context;

import de.cerus.flatcraft.game.FlatWorld;
import org.bukkit.entity.Player;

public class MoveSecurityContext extends FlatSecurityContext {

    private final int oldX;
    private final int oldY;
    private final int newX;
    private final int newY;
    private final FlatWorld flatWorld;

    public MoveSecurityContext(final Player player, final int oldX, final int oldY, final int newX, final int newY, final FlatWorld flatWorld) {
        super(player);
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.flatWorld = flatWorld;

        this.fillRawDataMap();
    }

    public int getOldX() {
        return this.oldX;
    }

    public int getOldY() {
        return this.oldY;
    }

    public int getNewX() {
        return this.newX;
    }

    public int getNewY() {
        return this.newY;
    }

    public FlatWorld getFlatWorld() {
        return this.flatWorld;
    }

}
