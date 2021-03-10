package de.cerus.flatcraft.game.chunk.decorator;

import de.cerus.flatcraft.game.FlatWorld;
import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.game.chunk.FlatChunk;
import java.util.Random;

public class DefaultFlatChunkDecorator implements FlatChunkDecorator {

    @Override
    public void decorate(final FlatChunk chunk, final FlatWorld world) {
        final Random random = new Random(world.getSeed() + chunk.getX());
        for (int x = 0; x < 16; x++) {
            if (random.nextInt(20) == 0) {
                this.generateTree(chunk, world, x, chunk.getHighestBlockPos(x) + 1, random);
            }
        }

        // Set decorated flag
        chunk.setDecorated(true);
    }

    /**
     * Generates a tree at the given coordinates
     *
     * @param chunk  The chunk
     * @param x      The x coordinate
     * @param y      The y coordinate
     * @param random The seeded random
     */
    private void generateTree(final FlatChunk chunk, final FlatWorld world, final int x, final int y, final Random random) {
        // Generate trunk
        final int trunkLen = random.nextInt(4) + 3;
        for (int i = 0; i < trunkLen; i++) {
            this.setBlock(chunk, world, x, y + i, FlatBlock.BLOCK_WOOD);
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
                this.setBlock(chunk, world, x - middle + i, y + trunkLen + n, FlatBlock.BLOCK_LEAVES);
            }
            n++;
            crownWidth -= 2;
        }
    }

    private void setBlock(final FlatChunk chunk, final FlatWorld world, final int x, final int y, final FlatBlock block) {
        if (y < 0 || y > 63) {
            return;
        }
        if (x < -15 || x > 31) {
            return;
        }

        final FlatChunk effectiveChunk = x < 0 ? chunk.getX() - 1 < 0 ? null : world.getChunkAt(chunk.getX() - 1)
                : x > 15 ? chunk.getX() + 1 >= world.getMaxSize() ? null : world.getChunkAt(chunk.getX() + 1) : chunk;
        if (effectiveChunk == null) {
            return;
        }

        final int effectiveX = x < 0 ? x + 16 : x > 15 ? x - 16 : x;
        effectiveChunk.setBlockSafe(effectiveX, y, block);
    }

}
