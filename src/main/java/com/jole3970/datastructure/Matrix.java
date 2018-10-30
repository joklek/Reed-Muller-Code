package com.jole3970.datastructure;

import java.util.Arrays;
import java.util.Objects;

public class Matrix {
    private final int height;
    private final int length;
    private final int[][] data;

    public Matrix(int height, int length) {
        this.height = height;
        this.length = length;
        data = new int[height][length];
    }

    public Matrix(int[][] data) {
        this.height = data.length;
        this.length = data[0].length;

        this.data = new int[height][length];
        for(int i = 0; i < height; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, length);
        }
    }

    public int[][] getData() {
        return data;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public int get(int line, int column) {
        if (line >= height || line < 0) {
            throw new IndexOutOfBoundsException(String.format("Illegal X of '%d' when it should be [0, %d)", line, height));
        }
        if (column >= length || column < 0) {
            throw new IndexOutOfBoundsException(String.format("Illegal Y of '%d' when it should be [0, %d)", column, length));
        }
        return data[line][column];
    }

    public void set(int line, int column, int value) {
        if (line >= height || line < 0) {
            throw new IndexOutOfBoundsException(String.format("Illegal X of '%d' when it should be [0, %d)", line, height));
        }
        if (column >= length || column < 0) {
            throw new IndexOutOfBoundsException(String.format("Illegal Y of '%d' when it should be [0, %d)", column, length));
        }
        data[line][column] = value;
    }

    public Matrix multiply(Matrix matrixToMultiplyBy) {
        if (length != matrixToMultiplyBy.getHeight()) {
            throw new IllegalStateException("Matrix dimensions don't match. Length of multiplied matrix should be equal to the multiplier matrix height");
        }
        Matrix newMatrix = new Matrix(height, matrixToMultiplyBy.getLength());
        for (int i = 0; i < newMatrix.getHeight(); i++)
            for (int j = 0; j < newMatrix.getLength(); j++)
                for (int k = 0; k < length; k++)
                    newMatrix.data[i][j] += (data[i][k] * matrixToMultiplyBy.get(k,j));
        return newMatrix;
    }

    public Matrix add(Matrix matrixToAdd) {
        if (length != matrixToAdd.getLength() ||
                height != matrixToAdd.getHeight()) {
            throw new IllegalStateException("Matrix dimensions should match");
        }
        Matrix newMatrix = new Matrix(height, length);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                int value = get(i, j) + matrixToAdd.get(i, j);
                newMatrix.set(i, j, value);
            }
        }
        return newMatrix;
    }

    public Matrix transpose() {
        Matrix transposed = new Matrix(length, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                transposed.data[j][i] = this.data[i][j];
            }
        }
        return transposed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Matrix)) {
            return false;
        }
        Matrix matrix = (Matrix) o;
        return getHeight() == matrix.getHeight() &&
                getLength() == matrix.getLength() &&
                Arrays.deepEquals(getData(), matrix.getData());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getHeight(), getLength());
        result = 31 * result + Arrays.deepHashCode(getData());
        return result;
    }
}
