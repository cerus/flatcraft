package de.cerus.flatcraft.game;

import de.cerus.flatcraft.util.Vec3;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents everything that is not a block
 *
 * @see FlatPlayer
 */
public abstract class FlatGameObject {

    protected UUID uuid;
    protected int x;
    protected int y;


    public FlatGameObject(final UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Allows to overwrite the block rendered at the given coordinates
     *
     * @param x        The x coordinate
     * @param y        The y coordinate
     * @param charRef  The overrideable block
     * @param colorRef The overrideable color
     */
    public abstract void render(int x, int y, AtomicReference<Character> charRef, AtomicReference<Vec3> colorRef);

    public int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public UUID getUuid() {
        return this.uuid;
    }

}
