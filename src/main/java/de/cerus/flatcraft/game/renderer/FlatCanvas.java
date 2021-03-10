package de.cerus.flatcraft.game.renderer;

import de.cerus.flatcraft.util.Vec3;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

/**
 * A simple utility for drawing blocks and rendering them to chat components
 */
public class FlatCanvas {

    private final char[][] blocks;
    private final Vec3[][] colors;
    private final ClickEvent[][] clickEvents;
    private final HoverEvent<?>[][] hoverEvents;
    private final int width;
    private final int height;

    public FlatCanvas(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.blocks = new char[height][width];
        this.colors = new Vec3[height][width];
        this.clickEvents = new ClickEvent[height][width];
        this.hoverEvents = new HoverEvent[height][width];

        this.clear();
    }

    /**
     * Clear canvas
     */
    public void clear() {
        // Fill with dark gray mesh blocks
        this.fill('▒', new Vec3(64, 64, 64));
        // Reset events
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.clickEvents[y][x] = null;
                this.hoverEvents[y][x] = null;
            }
        }
    }

    /**
     * Set a click action for a coordinate
     *
     * @param event The click action
     * @param x     The x coordinate
     * @param y     The y coordinate
     */
    public void event(final ClickEvent event, final int x, final int y) {
        this.clickEvents[y][x] = event;
    }

    /**
     * Set a hover action for a coordinate
     *
     * @param event The hover action
     * @param x     The x coordinate
     * @param y     The y coordinate
     */
    public void event(final HoverEvent<?> event, final int x, final int y) {
        this.hoverEvents[y][x] = event;
    }

    /**
     * Draw text onto the canvas
     * Warning - The formatting will look terrible
     *
     * @param text  The text
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param color The color
     */
    public void text(final String text, final int x, final int y, final Vec3 color) {
        final char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            this.setBlock(x + i, y, chars[i], color);
        }
    }

    /**
     * Fills the canvas with the same colored block
     *
     * @param block The block
     * @param color The color
     */
    public void fill(final char block, final Vec3 color) {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.blocks[y][x] = block;
                this.colors[y][x] = color;
            }
        }
    }

    /**
     * Sets a colored block at the given coordinates
     *
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param block The block
     * @param r     Red value of color
     * @param g     Green value of color
     * @param b     Blue value of color
     */
    public void setBlock(final int x, final int y, final char block, final int r, final int g, final int b) {
        this.setBlock(x, y, block, new Vec3(r, g, b));
    }

    /**
     * Sets a colored block at the given coordinates
     *
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param block The block
     * @param color The rgb color
     */
    public void setBlock(final int x, final int y, final char block, final Vec3 color) {
        this.blocks[y][x] = block;
        this.colors[y][x] = color;
    }

    /**
     * Transforms the canvas to a list of chat components
     *
     * @return a list of chat components
     */
    public List<Component> toComponentList() {
        final List<Component> components = new ArrayList<>();
        for (int y = 0; y < this.height; y++) {
            TextComponent component = Component.empty();
            for (int x = 0; x < this.width; x++) {
                final Vec3 color = this.colors[y][x];
                component = component.append(Component.text(this.blocks[y][x], TextColor.color(color))
                        .clickEvent(this.clickEvents[y][x]).hoverEvent(this.hoverEvents[y][x]));
            }
            components.add(component);
        }
        return components;
    }

}
