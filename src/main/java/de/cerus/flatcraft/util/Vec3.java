package de.cerus.flatcraft.util;

import net.kyori.adventure.util.RGBLike;
import org.checkerframework.common.value.qual.IntRange;

public class Vec3 implements RGBLike {

    private int x;
    private int y;
    private int z;

    public Vec3(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

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

    public int getZ() {
        return this.z;
    }

    public void setZ(final int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return this.x + "," + this.y + "," + this.z;
    }

    @Override
    public @IntRange(from = 0L, to = 255L) int red() {
        return this.x;
    }

    @Override
    public @IntRange(from = 0L, to = 255L) int green() {
        return this.y;
    }

    @Override
    public @IntRange(from = 0L, to = 255L) int blue() {
        return this.z;
    }

}
