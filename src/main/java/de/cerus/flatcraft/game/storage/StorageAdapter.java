package de.cerus.flatcraft.game.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class StorageAdapter<T> {

    public abstract T load(InputStream inputStream, Object... extras) throws IOException;

    public abstract void store(T t, OutputStream outputStream, Object... extras) throws IOException;

}
