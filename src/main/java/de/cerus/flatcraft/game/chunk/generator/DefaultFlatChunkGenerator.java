package de.cerus.flatcraft.game.chunk.generator;

import de.cerus.flatcraft.game.FlatWorld;
import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.game.chunk.FlatChunk;
import hoten.perlin.Perlin1d;
import java.util.Random;

/**
 * The default chunk generator
 */
public class DefaultFlatChunkGenerator implements FlatChunkGenerator {

    private double[] tile;

    @Override
    public void generate(final FlatWorld world, final FlatChunk chunk) {
        if (this.tile == null) {
            // Generate 1d noise
            // Could be moved to constructor
            this.tile = new Perlin1d(0.65, 8, world.getSeed()).createTiledArray(16 * 16);
        }

        final Random random = new Random(world.getSeed() + chunk.getX());
        for (int x = 0; x < 16; x++) {
            // Calculate tile access (index)
            int ta = (chunk.getX() + 1) * 16 + x;
            if (ta >= this.tile.length) {
                ta %= this.tile.length;
            }

            // Get tile and transform into a usable number
            final double v = this.tile[ta];
            final int h = ((int) (v * 24d)) + 16;

            for (int y = 0; y < 64; y++) {
                // Everything under our tile is stone
                // Everything at the tile is grass
                // Everything above is air
                // TODO: Block population, trees, stuff like that

                if (y == 0) {
                    // Set stone 2
                    chunk.setBlock(x, y, FlatBlock.BLOCK_STONE2);
                } else if (y < h) {
                    // Set stone
                    chunk.setBlock(x, y, random.nextInt(6) == 0 ? FlatBlock.BLOCK_STONE2 : FlatBlock.BLOCK_STONE);
                } else if (y == h) {
                    // Set grass
                    chunk.setBlock(x, y, FlatBlock.BLOCK_GRASS);
                } else {
                    // Set air
                    chunk.setBlock(x, y, FlatBlock.BLOCK_AIR);
                }
            }
        }

        // Set generated flag
        chunk.setGenerated(true);
    }

}
