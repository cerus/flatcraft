package de.cerus.flatcraft.game.renderer.world;

import de.cerus.flatcraft.game.FlatCraftGame;
import de.cerus.flatcraft.game.FlatGameObject;
import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.util.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;

/**
 * Default world renderer
 */
public class DefaultFlatWorldRenderer implements FlatWorldRenderer {

    public static final int HEIGHT = 16;
    public static final int WIDTH = 20;

    @Override
    public List<Component> render(final FlatCraftGame game, int xPos, int yPos) {
        // Make sure coordinates are not out of bounds
        while (yPos - (HEIGHT / 2) < 0) {
            yPos++;
        }
        while (yPos + (HEIGHT / 2) > 63) {
            yPos--;
        }
        while (xPos - (WIDTH / 2) < 0) {
            xPos++;
        }

        // TODO: make use of the canvas
        final List<Component> components = new ArrayList<>();
        for (int y = HEIGHT - 1; y >= 0; y--) {
            final List<Component> localComponents = new ArrayList<>();
            for (int x = 0; x < WIDTH; x++) {
                // Calculate the world's x and y coordinates for this pixel
                // This math was a real pain to figure out. I suck at maths btw
                final int X = x < WIDTH / 2 ? (xPos - this.reverseNumber(x, 0, WIDTH)) + (WIDTH / 2) : (xPos + x) - (WIDTH / 2);
                final int Y = y < HEIGHT / 2 ? (yPos - this.reverseNumber(y, 0, HEIGHT)) + (HEIGHT / 2) : (yPos + y) - (HEIGHT / 2);

                // Render the block at the coordinates
                final AtomicReference<Character> blockRef = new AtomicReference<>();
                final AtomicReference<Vec3> colorRef = new AtomicReference<>();

                final FlatBlock block = game.getWorld().getBlockAt(X, Y);
                blockRef.set(block.getDisplayChar());
                colorRef.set(new Vec3(block.getR(), block.getG(), block.getB()));

                // Loop through game objects and let them have a chance at rendering
                for (final FlatGameObject flatGameObject : game.getWorld().getGameObjects()) {
                    flatGameObject.render(X, Y, blockRef, colorRef);
                }

                // Finally create component
                final Vec3 color = colorRef.get();
                localComponents.add(Component.text(blockRef.get(), TextColor.color(color.getX(), color.getY(), color.getZ()))
                        .clickEvent(ClickEvent.runCommand("/flatcraft interact " + X + " " + Y)));
            }
            components.add(Component.join(Component.empty(), localComponents));
        }
        return components;
    }

    /**
     * Reverses the input
     *
     * @param num The input number
     * @param min The lowest possible value
     * @param max The highest possible value
     *
     * @return The reversed number
     * <p>
     * Source: StackOverflow
     */
    public int reverseNumber(final int num, final int min, final int max) {
        return (max + min) - num;
    }

}
