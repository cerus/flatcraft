package de.cerus.flatcraft.game.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents something that can be stored in the Papyrus format
 *
 * @param <T> The type that this storable stores - This is kinda a design flaw
 */
public interface Storable<T> {

    /**
     * Writes this object into storage
     *
     * @param stream The output stream
     * @param extra  Any extra objects that might be needed to store this object
     *
     * @throws IOException when something goes wrong with I/O
     */
    void store(OutputStream stream, Object... extra) throws IOException;

    /**
     * Reads this object from storage
     *
     * @param stream The input stream
     * @param extra  Any extra objects that might be needed to read this object
     *
     * @return The read object
     *
     * @throws IOException when something goes wrong with I/O
     */
    T read(InputStream stream, Object... extra) throws IOException;

}
