package de.cerus.flatcraft.game.storage.adapters;

import de.cerus.flatcraft.game.chunk.FlatChunk;
import de.cerus.flatcraft.game.storage.BlockIndex;
import de.cerus.flatcraft.game.storage.StorageAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FlatChunkStorageAdapter extends StorageAdapter<FlatChunk> {

    @Override
    public FlatChunk load(final InputStream inputStream, final Object... extras) throws IOException {
        if (extras.length < 1 || !(extras[0] instanceof BlockIndex)) {
            throw new IllegalStateException();
        }
        final BlockIndex blockIndex = (BlockIndex) extras[0];

        // Read coordinate and amount of blocks
        final byte[] arr = new byte[4];
        inputStream.read(arr, 0, 4);
        final int chunkX = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();
        inputStream.read(arr, 0, 4);
        final int size = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (size != 16 * 64) {
            throw new IllegalStateException();
        }

        // Read blocks
        final FlatChunk chunk = new FlatChunk(chunkX);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 64; y++) {
                inputStream.read(arr, 0, 4);
                chunk.setBlock(x, y, blockIndex.getBlock(ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt()));
            }
        }
        return chunk;
    }

    @Override
    public void store(final FlatChunk chunk, final OutputStream outputStream, final Object... extras) throws IOException {
        if (extras.length < 1 || !(extras[0] instanceof BlockIndex)) {
            throw new IllegalStateException();
        }
        final BlockIndex blockIndex = (BlockIndex) extras[0];

        // Write coordinate and block amount
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(chunk.getX()).array());
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(16 * 64).array());

        // Write blocks
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 64; y++) {
                outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(blockIndex.getIndex(chunk.getBlock(x, y))).array());
            }
        }
    }

}