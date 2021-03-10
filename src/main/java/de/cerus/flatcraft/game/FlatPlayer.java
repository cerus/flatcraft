package de.cerus.flatcraft.game;

import de.cerus.flatcraft.util.Vec3;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents the player character
 */
public class FlatPlayer extends FlatGameObject {

    public static final UUID PLAYER_UUID = new UUID(0, 0);

    public FlatPlayer() {
        super(PLAYER_UUID);
    }

    @Override
    public void render(final int x, final int y, final AtomicReference<Character> charRef, final AtomicReference<Vec3> colorRef) {
        if (x == this.x) {
            if (y == this.y) {
                // Render lower half
                charRef.set('█');
                colorRef.set(new Vec3(22, 110, 178));
            } else if (y == this.y + 1) {
                // Render upper half
                charRef.set('█');
                colorRef.set(new Vec3(99, 99, 99));
            }
        }
    }

}
