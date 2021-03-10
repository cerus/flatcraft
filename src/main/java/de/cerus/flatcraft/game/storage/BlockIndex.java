package de.cerus.flatcraft.game.storage;

import de.cerus.flatcraft.game.chunk.FlatBlock;
import java.lang.reflect.Field;

/**
 * Represents the Papyrus block index
 */
public class BlockIndex {

    private final String[] elements;

    public BlockIndex(final int size) {
        this.elements = new String[size];
    }

    public BlockIndex(final String[] elements) {
        this.elements = elements;
    }

    /**
     * Get the block at the specified index
     *
     * @param index The index
     *
     * @return The block
     */
    public FlatBlock getBlock(final int index) {
        final String element = this.elements[index];
        try {
            final Field blockField = FlatBlock.class.getDeclaredField("BLOCK_" + element.toUpperCase());
            blockField.setAccessible(true);
            return (FlatBlock) blockField.get(null);
        } catch (final IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the index for the specified block
     *
     * @param block The block
     *
     * @return The index
     */
    public int getIndex(final FlatBlock block) {
        return this.getIndex(block.getName());
    }

    /**
     * Get the index for the specified block name
     *
     * @param block The block name
     *
     * @return The index
     */
    public int getIndex(final String block) {
        for (int i = 0; i < this.elements.length; i++) {
            if (this.elements[i].equals(block)) {
                return i;
            }
        }
        return -1;
    }

    public String[] getElements() {
        return this.elements;
    }

}
