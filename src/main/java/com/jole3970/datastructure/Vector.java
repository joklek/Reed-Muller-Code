package com.jole3970.datastructure;

public class Vector {
    private final int[] data;
    private final int length;

    public Vector(int[] data) {
        this.length = data.length;
        this.data = new int[length];
        System.arraycopy(data, 0, this.data, 0, length);
    }

    public int getHeight() {
        return 1;
    }

    public int getLength() {
        return length;
    }

    public int get(int x) {
        if (x >= length || x < 0) {
            throw new IndexOutOfBoundsException(String.format("Illegal X of '%d' when it should be [0, %d)", x, length));
        }
        return data[x];
    }
}
