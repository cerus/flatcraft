package de.cerus.flatcraft.game;

import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.game.chunk.FlatChunk;
import de.cerus.flatcraft.game.chunk.generator.DefaultFlatChunkGenerator;
import de.cerus.flatcraft.game.chunk.generator.FlatChunkGenerator;
import de.cerus.flatcraft.game.storage.BlockIndex;
import de.cerus.flatcraft.game.storage.Storable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Represents the whole 2d world
 */
public class FlatWorld implements Storable<FlatWorld> {

    private final FlatChunkGenerator chunkGenerator;
    private final Map<Integer, FlatChunk> cachedChunks;
    private final List<FlatGameObject> gameObjects;
    private int seed;
    private int maxSize;
    private FlatPlayer player;

    public FlatWorld(final int seed) {
        this(seed, new DefaultFlatChunkGenerator());
    }

    public FlatWorld(final int seed, final FlatChunkGenerator chunkGenerator) {
        this.seed = seed;
        this.chunkGenerator = chunkGenerator;
        this.cachedChunks = new TreeMap<>();
        this.player = new FlatPlayer();
        this.gameObjects = new ArrayList<>();
        this.maxSize = 64; // max size in chunks

        // Init player
        this.player.setX(16 * (this.maxSize / 2));
        this.player.setY(this.getChunkAt(this.player.getX() / 16).getHighestBlockPos(this.player.getX() % 16) + 1);
        this.gameObjects.add(this.player);
    }

    /**
     * Sets the specified block at the specified coordinates
     * Will generate the chunk if it doesn't exist yet
     *
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param block The block
     */
    public void setBlockAt(final int x, final int y, final FlatBlock block) {
        this.getChunkAt(x / 16).setBlock(x % 16, y, block);
    }

    /**
     * Gets the block at the specified coordinates
     * Will generate the chunk if it doesn't exist yet
     *
     * @param x The x coordinate
     * @param y The y coordinate
     *
     * @return The block
     */
    public FlatBlock getBlockAt(final int x, final int y) {
        return this.getChunkAt(x / 16).getBlock(x % 16, y);
    }

    /**
     * Gets the chunk at the specified coordinates
     * Will generate the chunk if it doesn't exist yet
     * Will return null if the coordinate is out of bounds
     *
     * @param x The x coordinate
     *
     * @return The chunk or null
     */
    public FlatChunk getChunkAt(final int x) {
        if (x > this.maxSize || x < 0) {
            return null;
        }

        FlatChunk chunk = this.cachedChunks.get(x);
        if (chunk == null) {
            this.cachedChunks.put(x, chunk = this.loadChunk(x));
        }
        return chunk;
    }

    /**
     * Loads / generates the chunk at this coordinate
     *
     * @param x The x coordinate
     *
     * @return The generated chunk
     */
    private FlatChunk loadChunk(final int x) {
        final FlatChunk chunk = new FlatChunk(x);
        this.chunkGenerator.generate(this, chunk);
        return chunk;
    }

    @Override
    public void store(final OutputStream stream, final Object... extra) throws IOException {
        // Write header and version
        stream.write(new byte[] {'P', 'Y', 'R', 'S'});
        stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) 1).array());

        // Write block index
        final BlockIndex blockIndex = new BlockIndex(new ArrayList<>(FlatBlock.BLOCK_MAP.values()).stream()
                .map(FlatBlock::getName)
                .toArray(String[]::new));
        blockIndex.store(stream, extra);

        // Write game objects
        stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) this.gameObjects.size()).array());
        for (final FlatGameObject gameObject : this.gameObjects) {
            gameObject.store(stream, extra);
        }

        // Write seed
        stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.seed).array());

        // Write chunks
        stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) this.cachedChunks.size()).array());
        for (final FlatChunk chunk : this.cachedChunks.values()) {
            chunk.store(stream, blockIndex);
        }
    }

    @Override
    public FlatWorld read(final InputStream stream, final Object... extra) throws IOException {
        // Cleanup
        this.gameObjects.clear();
        this.cachedChunks.clear();

        // Read header
        byte[] arr = new byte[4];
        stream.read(arr, 0, 4);
        if (!Arrays.equals(arr, new byte[] {'P', 'Y', 'R', 'S'})) {
            throw new IllegalStateException();
        }

        // Read version
        stream.read(arr, 0, 2);
        final short ver = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();
        if (ver != 1) {
            throw new IllegalStateException();
        }

        // Read block index
        final BlockIndex blockIndex = new BlockIndex(0);
        blockIndex.read(stream, extra);

        // Read game objects
        stream.read(arr, 0, 2);
        short amt = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();
        for (int i = 0; i < amt; i++) {
            // Read uuid
            arr = new byte[8];
            stream.read(arr, 0, 8);
            final long mostSig = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getLong();
            stream.read(arr, 0, 8);
            final long leastSig = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getLong();

            // Get game object by uuid
            final UUID uuid = new UUID(mostSig, leastSig);
            if (uuid.equals(FlatPlayer.PLAYER_UUID)) {
                this.player = new FlatPlayer();
                this.player.read(stream, extra);
                this.gameObjects.add(this.player);
            } else {
                throw new IllegalStateException();
            }
        }

        // Read seed
        stream.read(arr, 0, 4);
        this.seed = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();

        // Read chunks
        stream.read(arr, 0, 2);
        amt = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getShort();
        for (int i = 0; i < amt; i++) {
            final FlatChunk chunk = new FlatChunk(0);
            chunk.read(stream, blockIndex);
            this.cachedChunks.put(chunk.getX(), chunk);
        }
        return this;
    }

    public int getSeed() {
        return this.seed;
    }

    public FlatChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

    public List<FlatGameObject> getGameObjects() {
        return this.gameObjects;
    }

    public FlatPlayer getPlayer() {
        return this.player;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }
}
