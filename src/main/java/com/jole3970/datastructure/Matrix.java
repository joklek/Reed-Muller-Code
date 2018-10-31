package com.jole3970.datastructure;

import java.util.Arrays;
import java.util.Objects;

public class Matrix {
    protected final int height;
    protected final int length;
    protected final int[][] data;

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
     * Multiplies current matrix with another. Current matrix length should be equal to given matrix height. X×Y multiplied by Z×W, should have Y == Z
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
     * Scalar matrix multiplication
     * @param multiplier by which each cell should be multiplied
     * @return new matrix that is multiplied by given multiplier
     */
    public Matrix multiply(int multiplier) {
        Matrix newMatrix = new Matrix(height, length);
        for (int i = 0; i < newMatrix.getHeight(); i++) {
            for (int j = 0; j < newMatrix.getLength(); j++) {
                newMatrix.getData()[i][j] *= multiplier;
            }
        }
        return newMatrix;
    }

    /**
     * Cartesian matrix multiplication
     * @param matrix to be multiplied by
     * @return new matrix that is the product of cartesian multiplication
     */
    public Matrix cartesian(Matrix matrix) {
        int hostHeight = matrix.getHeight();
        int hostLength = matrix.getLength();

        int newHeight = this.height * hostHeight;
        int newLength = length * hostLength;
        int[][] newArray = new int[newHeight][newLength];
        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newLength; j++) {
                int valueOfCell = data[i/hostHeight][j/hostLength];
                newArray[i][j] = matrix.getData()[i%hostHeight][j%hostLength] * valueOfCell;
            }
        }
        return new Matrix(newArray);
    }

    /**
     * Matrix addition, where matrix dimensions should be identical
     * @param matrixToAdd the term matrix, which is to be added to the current one
     * @return a new matrix that is the product of matrix addition
     */
    public Matrix add(Matrix matrixToAdd) {
        if (length != matrixToAdd.getLength() ||
                height != matrixToAdd.getHeight()) {
            throw new IllegalStateException("Matrix dimensions should match");
        }
        Matrix newMatrix = new Matrix(height, length);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                newMatrix.getData()[i][j] += data[i][j] + matrixToAdd.getData()[i][j];
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
