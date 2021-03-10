package de.cerus.flatcraft.game.chunk.generator;

import de.cerus.flatcraft.game.FlatWorld;
import de.cerus.flatcraft.game.chunk.FlatChunk;

/**
 * Represents a chunk generator interface for the 2d world
 */
public interface FlatChunkGenerator {

    /**
     * Generates the specified chunk of the specified world
     *
     * @param world The world
     * @param chunk The chunk
     */
    void generate(FlatWorld world, FlatChunk chunk);

}
