package de.cerus.flatcraft.game.chunk;

/**
 * Represents a 16x64 slice of the world
 */
public class FlatChunk {

    private final FlatBlock[][] blocks = new FlatBlock[16][64];
    private final int x;
    private boolean generated;
    private boolean decorated;

    public FlatChunk(final int x, final boolean generated, final boolean decorated) {
        this.x = x;
        this.generated = generated;
        this.decorated = decorated;
    }

    public void setBlockSafe(final int x, final int y, final FlatBlock block) {
        if (x >= this.blocks.length || x < 0 || y >= this.blocks[x].length || y < 0) {
            return;
        }
        this.setBlock(x, y, block);
    }

    public void setBlock(final int x, final int y, final FlatBlock block) {
        this.blocks[x][y] = block;
    }

    public FlatBlock getBlock(final int x, final int y) {
        return this.blocks[x][y];
    }

    /**
     * Gets the highest block pos of this x coordinate
     *
     * @param x The x coordinate
     *
     * @return The highest y coordinate
     */
    public int getHighestBlockPos(final int x) {
        for (int y = 63; y >= 0; y--) {
            if (this.blocks[x][y] != FlatBlock.BLOCK_AIR) {
                return y;
            }
        }
        return 64;
    }

    public int getX() {
        return this.x;
    }

    public boolean isGenerated() {
        return this.generated;
    }

    public void setGenerated(final boolean generated) {
        this.generated = generated;
    }

    public boolean isDecorated() {
        return this.decorated;
    }

    public void setDecorated(final boolean decorated) {
        this.decorated = decorated;
    }

}
