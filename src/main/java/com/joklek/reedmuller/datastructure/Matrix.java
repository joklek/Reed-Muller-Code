package com.joklek.reedmuller.datastructure;

import java.util.Arrays;
import java.util.Objects;

public class Matrix {
    private final int height;
    private final int length;
    private final int[][] data;

    /**
     * Creates a new matrix and fills it with zeroes
     * @param height wanted height of new matrix
     * @param length wanted length of new matrix
     */
    public Matrix(int height, int length) {
        this.height = height;
        this.length = length;
        data = new int[height][length];
    }

    /**
     * Generates new matrix from given 2d array
     * @param data a 2d array of data
     */
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

    /**
     * Multiplies current matrix with another and returns new matrix. Current matrix length should be equal to given matrix height. X×Y multiplied by Z×W, should have Y == Z
     * @param matrixToMultiplyBy the matrix that the current should be multiplied with
     * @return a new matrix that is the product of matrix multiplication. Dimensions of new matrix (where X×Y is multiplied by Z×W) is X×W
     */
    public Matrix multiply(Matrix matrixToMultiplyBy) {
        if (length != matrixToMultiplyBy.getHeight()) {
            throw new IllegalStateException("Matrix dimensions don't match. Length of multiplied matrix should be equal to the multiplier matrix height");
        }
        Matrix newMatrix = new Matrix(height, matrixToMultiplyBy.getLength());
        for (int i = 0; i < newMatrix.getHeight(); i++) {
            for (int j = 0; j < newMatrix.getLength(); j++) {
                for (int k = 0; k < length; k++) {
                    newMatrix.data[i][j] += (data[i][k] * matrixToMultiplyBy.getData()[k][j]);
                }
            }
        }
        return newMatrix;
    }

    /**
     * Matrix transposition. Rows become columns, and columns become rows
     * @return new transposed matrix
     */
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
