package de.cerus.flatcraft.game.storage.adapters;

import de.cerus.flatcraft.game.FlatGameObject;
import de.cerus.flatcraft.game.FlatPlayer;
import de.cerus.flatcraft.game.FlatWorld;
import de.cerus.flatcraft.game.chunk.FlatChunk;
import de.cerus.flatcraft.game.chunk.generator.DefaultFlatChunkGenerator;
import de.cerus.flatcraft.game.storage.BlockIndex;
import de.cerus.flatcraft.game.storage.StorageAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FlatWorldStorageAdapter extends StorageAdapter<FlatWorld> {

    @Override
    public FlatWorld load(final InputStream inputStream, final Object... extras) throws IOException {
        // Read header
        byte[] arr = new byte[4];
        inputStream.read(arr, 0, 4);
        if (!Arrays.equals(arr, new byte[] {'P', 'Y', 'R', 'S'})) {
            throw new IllegalStateException();
        }

        // Read version
        inputStream.read(arr, 0, 2);
        final short ver = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();
        if (ver != 1) {
            throw new IllegalStateException();
        }

        // Read block index
        final BlockIndex blockIndex = StorageAdapters.BLOCK_INDEX_STORAGE_ADAPTER.load(inputStream);

        // Read game objects
        final List<FlatGameObject> gameObjects = new ArrayList<>();
        FlatPlayer player = null;

        inputStream.read(arr, 0, 2);
        short amt = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();
        for (int i = 0; i < amt; i++) {
            // Read uuid
            arr = new byte[8];
            inputStream.read(arr, 0, 8);
            final long mostSig = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getLong();
            inputStream.read(arr, 0, 8);
            final long leastSig = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getLong();

            // Get game object by uuid
            final UUID uuid = new UUID(mostSig, leastSig);
            final StorageAdapter<? extends FlatGameObject> adapter = StorageAdapters.getGameObjectStorageAdapter(uuid);
            if (adapter == null) {
                throw new IllegalStateException();
            }

            // Load game object
            final FlatGameObject gameObject = adapter.load(inputStream);
            gameObjects.add(gameObject);
            if (gameObject instanceof FlatPlayer) {
                player = (FlatPlayer) gameObject;
            }
        }

        // Read seed
        inputStream.read(arr, 0, 4);
        final int seed = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();

        // Create world
        final FlatWorld flatWorld = new FlatWorld(seed, new DefaultFlatChunkGenerator(), player);
        flatWorld.getGameObjects().addAll(gameObjects);

        // Read chunks
        inputStream.read(arr, 0, 2);
        amt = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();
        for (int i = 0; i < amt; i++) {
            final FlatChunk chunk = StorageAdapters.FLAT_CHUNK_STORAGE_ADAPTER.load(inputStream, blockIndex);
            flatWorld.putChunk(chunk);
        }
        return flatWorld;
    }

    @Override
    public void store(final FlatWorld world, final OutputStream outputStream, final Object... extras) throws IOException {

    }

}
