package de.cerus.flatcraft.game.chunk;

import de.cerus.flatcraft.game.storage.BlockIndex;
import de.cerus.flatcraft.game.storage.Storable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a 16x64 slice of the world
 */
public class FlatChunk implements Storable<FlatChunk> {

    private final FlatBlock[][] blocks = new FlatBlock[16][64];
    private int x;

    public FlatChunk(final int x) {
        this.x = x;
    }

    public void setBlockSafe(final int x, final int y, final FlatBlock block) {
        if (x >= this.blocks.length || x < 0 || y >= this.blocks[x].length || y < 0) {
            return;
        }
        this.setBlock(x, y, block);
    }

    public void setBlock(final int x, final int y, final FlatBlock block) {
        this.blocks[x][y] = block;
    }

    public FlatBlock getBlock(final int x, final int y) {
        return this.blocks[x][y];
    }

    /**
     * Gets the highest block pos of this x coordinate
     *
     * @param x The x coordinate
     *
     * @return The highest y coordinate
     */
    public int getHighestBlockPos(final int x) {
        for (int y = 63; y >= 0; y--) {
            if (this.blocks[x][y] != FlatBlock.BLOCK_AIR) {
                return y;
            }
        }
        return 64;
    }

    @Override
    public void store(final OutputStream stream, final Object... extra) throws IOException {
        final BlockIndex blockIndex = (BlockIndex) extra[0];

        // Write coordinate and block amount
        stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.x).array());
        stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(16 * 64).array());

        // Write blocks
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 64; y++) {
                stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(blockIndex.getIndex(this.blocks[x][y])).array());
            }
        }
    }

    @Override
    public FlatChunk read(final InputStream stream, final Object... extra) throws IOException {
        final BlockIndex blockIndex = (BlockIndex) extra[0];

        // Read coordinate and amount of blocks
        final byte[] arr = new byte[4];
        stream.read(arr, 0, 4);
        this.x = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();
        stream.read(arr, 0, 4);
        final int size = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (size != 16 * 64) {
            throw new IllegalStateException();
        }

        // Read blocks
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 64; y++) {
                stream.read(arr, 0, 4);
                this.blocks[x][y] = blockIndex.getBlock(ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt());
            }
        }
        return this;
    }

    public int getX() {
        return this.x;
    }

}
