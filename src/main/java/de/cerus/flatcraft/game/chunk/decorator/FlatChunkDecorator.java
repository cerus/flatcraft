package de.cerus.flatcraft.game.chunk.decorator;

import de.cerus.flatcraft.game.FlatWorld;
import de.cerus.flatcraft.game.chunk.FlatChunk;

public interface FlatChunkDecorator {

    void decorate(FlatChunk chunk, FlatWorld world);

}
