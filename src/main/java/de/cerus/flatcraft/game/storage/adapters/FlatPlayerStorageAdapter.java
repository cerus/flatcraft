package de.cerus.flatcraft.game.storage.adapters;

import de.cerus.flatcraft.game.FlatPlayer;
import de.cerus.flatcraft.game.storage.StorageAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class FlatPlayerStorageAdapter extends StorageAdapter<FlatPlayer> {

    @Override
    public FlatPlayer load(final InputStream inputStream, final Object... extras) throws IOException {
        final byte[] arr = new byte[8];
        if (extras.length > 0 && extras[0] instanceof Boolean && (boolean) extras[0]) {
            inputStream.read(arr, 0, 8);
            final long mostSig = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getLong();
            inputStream.read(arr, 0, 8);
            final long leastSig = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getLong();
            final UUID readUuid = new UUID(mostSig, leastSig);

            if (!readUuid.equals(FlatPlayer.PLAYER_UUID)) {
                throw new IllegalStateException("UUID does not match");
            }
        }

        inputStream.read(arr, 0, 4);
        final int x = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();
        inputStream.read(arr, 0, 4);
        final int y = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();

        final FlatPlayer flatPlayer = new FlatPlayer();
        flatPlayer.setX(x);
        flatPlayer.setY(y);
        return flatPlayer;
    }

    @Override
    public void store(final FlatPlayer flatPlayer, final OutputStream outputStream, final Object... extras) throws IOException {
        outputStream.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(FlatPlayer.PLAYER_UUID.getMostSignificantBits()).array());
        outputStream.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(FlatPlayer.PLAYER_UUID.getLeastSignificantBits()).array());
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(flatPlayer.getX()).array());
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(flatPlayer.getY()).array());
    }

}
