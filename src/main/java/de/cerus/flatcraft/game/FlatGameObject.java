package de.cerus.flatcraft.game;

import de.cerus.flatcraft.game.storage.Storable;
import de.cerus.flatcraft.util.Vec3;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents everything that is not a block
 *
 * @see FlatPlayer
 */
public abstract class FlatGameObject implements Storable<FlatGameObject> {

    protected UUID uuid;
    protected int x;
    protected int y;


    public FlatGameObject(final UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Allows to overwrite the block rendered at the given coordinates
     *
     * @param x        The x coordinate
     * @param y        The y coordinate
     * @param charRef  The overrideable block
     * @param colorRef The overrideable color
     */
    public abstract void render(int x, int y, AtomicReference<Character> charRef, AtomicReference<Vec3> colorRef);

    @Override
    public void store(final OutputStream stream, final Object... extra) throws IOException {
        stream.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(this.uuid.getMostSignificantBits()).array());
        stream.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(this.uuid.getLeastSignificantBits()).array());
        stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.x).array());
        stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.y).array());
        this.storeData(stream);
    }

    protected abstract void storeData(OutputStream stream) throws IOException;

    @Override
    public FlatGameObject read(final InputStream stream, final Object... extra) throws IOException {
        final byte[] arr = new byte[8];
        //stream.read(arr, 0, 8);
        //final long mostSig = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getLong();
        //stream.read(arr, 0, 8);
        //final long leastSig = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getLong();
        //this.uuid = new UUID(mostSig, leastSig);

        stream.read(arr, 0, 4);
        this.x = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();
        stream.read(arr, 0, 4);
        this.y = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();

        this.readData(stream);
        return this;
    }

    protected abstract void readData(InputStream stream) throws IOException;

    public int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public UUID getUuid() {
        return this.uuid;
    }

}
