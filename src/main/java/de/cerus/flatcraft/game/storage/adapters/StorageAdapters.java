package de.cerus.flatcraft.game.storage.adapters;

import de.cerus.flatcraft.game.FlatGameObject;
import de.cerus.flatcraft.game.FlatPlayer;
import de.cerus.flatcraft.game.FlatWorld;
import de.cerus.flatcraft.game.chunk.FlatChunk;
import de.cerus.flatcraft.game.storage.BlockIndex;
import de.cerus.flatcraft.game.storage.StorageAdapter;
import java.util.UUID;

public class StorageAdapters {

    public static final StorageAdapter<BlockIndex> BLOCK_INDEX_STORAGE_ADAPTER = new BlockIndexStorageAdapter();
    public static final StorageAdapter<FlatPlayer> FLAT_PLAYER_STORAGE_ADAPTER = new FlatPlayerStorageAdapter();
    public static final StorageAdapter<FlatChunk> FLAT_CHUNK_STORAGE_ADAPTER = new FlatChunkStorageAdapter();
    public static final StorageAdapter<FlatWorld> FLAT_WORLD_STORAGE_ADAPTER = new FlatWorldStorageAdapter();

    private StorageAdapters() {
    }

    public static StorageAdapter<? extends FlatGameObject> getGameObjectStorageAdapter(final UUID uuid) {
        if (uuid.equals(FlatPlayer.PLAYER_UUID)) {
            return FLAT_PLAYER_STORAGE_ADAPTER;
        }
        return null;
    }

}
