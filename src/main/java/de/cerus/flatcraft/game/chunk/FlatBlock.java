package de.cerus.flatcraft.game.chunk;

import de.cerus.flatcraft.util.Vec3;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a single block in the world
 */
public class FlatBlock {

    // All implemented blocks
    public static final FlatBlock BLOCK_AIR = new FlatBlock("air", "Air", '▒', 0, 0, 0);
    public static final FlatBlock BLOCK_STONE = new FlatBlock("stone", "Stone", '▓', 148, 148, 148);
    public static final FlatBlock BLOCK_GRASS = new FlatBlock("grass", "Grass", '▓', 67, 117, 32);
    public static final FlatBlock BLOCK_WOOD = new FlatBlock("wood", "Wood", '▓', 240, 229, 149);
    public static final FlatBlock BLOCK_GLASS = new FlatBlock("glass", "Glass", '▞', 220, 220, 220);
    public static final FlatBlock BLOCK_STONE_SLAB_UP = new FlatBlock("stone_slab_up", "Stone slab (Up)", 148, 148, 148, '▔', false);
    public static final FlatBlock BLOCK_STONE_SLAB_DOWN = new FlatBlock("stone_slab_down", "Stone slab (Down)", 148, 148, 148, '▁', false);
    public static final FlatBlock BLOCK_STONE2 = new FlatBlock("stone2", "Stone 2", '▒', 148, 148, 148);
    public static final FlatBlock BLOCK_LEAVES = new FlatBlock("leaves", "Leaves", '▒', 30, 145, 10);
    public static final Map<Integer, FlatBlock> BLOCK_MAP = new LinkedHashMap<>() {
        {
            this.put(0, BLOCK_AIR);
            this.put(1, BLOCK_STONE);
            this.put(2, BLOCK_GRASS);
            this.put(3, BLOCK_WOOD);
            this.put(4, BLOCK_GLASS);
            this.put(5, BLOCK_STONE_SLAB_UP);
            this.put(6, BLOCK_STONE_SLAB_DOWN);
            this.put(7, BLOCK_STONE2);
            this.put(8, BLOCK_LEAVES);
        }
    };

    private final String name;
    private final String formattedName;
    private final int r;
    private final int g;
    private final int b;
    private final char displayChar;
    private final boolean collider;

    public FlatBlock(final String name, final String formattedName, final int r, final int g, final int b, final char displayChar, final boolean collider) {
        this.name = name;
        this.formattedName = formattedName;
        this.r = r;
        this.g = g;
        this.b = b;
        this.displayChar = displayChar;
        this.collider = collider;
    }

    public FlatBlock(final String name, final String formattedName, final char displayChar, final int r, final int g, final int b) {
        this(name, formattedName, r, g, b, displayChar, true);
    }

    public String getName() {
        return this.name;
    }

    public String getFormattedName() {
        return this.formattedName;
    }

    public char getDisplayChar() {
        return this.displayChar;
    }

    public Vec3 getColor() {
        return new Vec3(this.r, this.g, this.b);
    }

    public int getR() {
        return this.r;
    }

    public int getG() {
        return this.g;
    }

    public int getB() {
        return this.b;
    }

    public boolean hasCollider() {
        return this.collider;
    }

}
