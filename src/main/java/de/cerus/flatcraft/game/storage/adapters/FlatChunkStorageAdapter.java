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

        // Preserve backwards compatibility
        boolean extended = false;
        if (size != 16 * 64 && !(extended = size == 16 * 64 + 2)) {
            throw new IllegalStateException();
        }

        // Read blocks
        final FlatChunk chunk = new FlatChunk(chunkX, true, true);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 64; y++) {
                inputStream.read(arr, 0, 4);
                chunk.setBlock(x, y, blockIndex.getBlock(ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt()));
            }
        }

        if (extended) {
            inputStream.read(arr, 0, 4);
            chunk.setGenerated(ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt() == 1);
            inputStream.read(arr, 0, 4);
            chunk.setDecorated(ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt() == 1);
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
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(16 * 64 + 2).array());

        // Write blocks
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 64; y++) {
                outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(blockIndex.getIndex(chunk.getBlock(x, y))).array());
            }
        }

        // Sneaky way to store data without changing the format
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(chunk.isGenerated() ? 1 : 0).array());
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(chunk.isDecorated() ? 1 : 0).array());
    }

}
