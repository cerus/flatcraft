package de.cerus.flatcraft.game.storage;

import de.cerus.flatcraft.game.chunk.FlatBlock;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents the Papyrus block index
 */
public class BlockIndex implements Storable<BlockIndex> {

    private String[] elements;

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

    @Override
    public void store(final OutputStream stream, final Object... extra) throws IOException {
        stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) this.elements.length).array());
        for (int i = 0; i < this.elements.length; i++) {
            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array());
            final String element = this.elements[i];
            stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) element.length()).array());
            for (final char c : element.toCharArray()) {
                stream.write((byte) c);
            }
        }
    }

    @Override
    public BlockIndex read(final InputStream stream, final Object... extra) throws IOException {
        byte[] arr = new byte[4];
        stream.read(arr, 0, 2);
        final short amt = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();
        this.elements = new String[amt];

        for (int i = 0; i < this.elements.length; i++) {
            arr = new byte[4];
            stream.read(arr, 0, 4);
            final int idx = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();

            stream.read(arr, 0, 2);
            final short len = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();

            arr = new byte[len];
            stream.read(arr, 0, len);
            this.elements[idx] = new String(arr);
        }
        return this;
    }

    public String[] getElements() {
        return this.elements;
    }

}
