package de.cerus.flatcraft.game.chunk.generator;

import de.cerus.flatcraft.game.FlatWorld;
import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.game.chunk.FlatChunk;
import hoten.perlin.Perlin1d;
import java.util.HashMap;
import java.util.Map;
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

        final Map<Integer, Integer> treeMap = new HashMap<>();
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

            if (random.nextInt(20) == 0) {
                treeMap.put(x, h + 1);
            }
        }

        treeMap.forEach((x, y) -> this.generateTree(chunk, x, y, random));
        treeMap.clear();
    }

    /**
     * Generates a tree at the given coordinates
     *
     * @param chunk  The chunk
     * @param x      The x coordinate
     * @param y      The y coordinate
     * @param random The seeded random
     */
    private void generateTree(final FlatChunk chunk, final int x, final int y, final Random random) {
        // TODO: Trees at the edge of a chunk generate with missing leaves, needs fixing
        // It would probably be a good idea to move tree generation out of the chunk generator and
        // into some sort of post-generation world decorator (would fix tree issue)

        // Generate trunk
        final int trunkLen = random.nextInt(4) + 3;
        for (int i = 0; i < trunkLen; i++) {
            chunk.setBlockSafe(x, y + i, FlatBlock.BLOCK_WOOD);
        }

        // Generate crown
        // Calculate initial width of the crown
        int crownWidth = random.nextInt(3) + 3 + (trunkLen - 3);
        while (crownWidth % 2 == 0) {
            crownWidth++;
        }

        // Stack layers on top of the crown until we can't
        int n = 0;
        while (crownWidth > 0) {
            final int middle = (crownWidth / 2);
            for (int i = 0; i < crownWidth; i++) {
                chunk.setBlockSafe(x - middle + i, y + trunkLen + n, FlatBlock.BLOCK_LEAVES);
            }
            n++;
            crownWidth -= 2;
        }
    }

}
