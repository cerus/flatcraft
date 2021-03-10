package de.cerus.flatcraft.game;

import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.game.chunk.FlatChunk;
import de.cerus.flatcraft.game.chunk.generator.DefaultFlatChunkGenerator;
import de.cerus.flatcraft.game.chunk.generator.FlatChunkGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the whole 2d world
 */
public class FlatWorld {

    private final FlatChunkGenerator chunkGenerator;
    private final Map<Integer, FlatChunk> cachedChunks;
    private final List<FlatGameObject> gameObjects;
    private final int seed;
    private final FlatPlayer player;
    private int maxSize;

    public FlatWorld(final int seed) {
        this(seed, new DefaultFlatChunkGenerator());
    }

    public FlatWorld(final int seed, final FlatChunkGenerator chunkGenerator) {
        this(seed, chunkGenerator, new FlatPlayer());
    }

    public FlatWorld(final int seed, final FlatChunkGenerator chunkGenerator, final FlatPlayer player) {
        this.seed = seed;
        this.chunkGenerator = chunkGenerator;
        this.cachedChunks = new TreeMap<>();
        this.player = player;
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

    public void putChunk(final FlatChunk chunk) {
        this.cachedChunks.put(chunk.getX(), chunk);
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
