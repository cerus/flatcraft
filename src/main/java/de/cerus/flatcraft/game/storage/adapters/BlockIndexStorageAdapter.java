package de.cerus.flatcraft.game.storage.adapters;

import de.cerus.flatcraft.game.storage.BlockIndex;
import de.cerus.flatcraft.game.storage.StorageAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BlockIndexStorageAdapter extends StorageAdapter<BlockIndex> {

    @Override
    public BlockIndex load(final InputStream inputStream, final Object... extras) throws IOException {
        byte[] arr = new byte[4];
        inputStream.read(arr, 0, 2);
        final short amt = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();
        final String[] elements = new String[amt];

        for (int i = 0; i < elements.length; i++) {
            arr = new byte[4];
            inputStream.read(arr, 0, 4);
            final int idx = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();

            inputStream.read(arr, 0, 2);
            final short len = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();

            arr = new byte[len];
            inputStream.read(arr, 0, len);
            elements[idx] = new String(arr);
        }
        return new BlockIndex(elements);
    }

    @Override
    public void store(final BlockIndex blockIndex, final OutputStream outputStream, final Object... extras) throws IOException {
        final String[] elements = blockIndex.getElements();
        outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) elements.length).array());
        for (int i = 0; i < elements.length; i++) {
            outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array());
            final String element = elements[i];
            outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) element.length()).array());
            for (final char c : element.toCharArray()) {
                outputStream.write((byte) c);
            }
        }
    }

}
