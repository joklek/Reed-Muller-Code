package com.jole3970;

import com.jole3970.datastructure.Matrix;

public class Encoder {
    public int[] encode(int[] vector) {
        Matrix multiplied = ReedMullerCodeGenerator.generateGenerativeMatrixForM(3).transpose()
                .multiply(new Matrix(new int[][]{vector}).transpose()).transpose();
        return sanitizeData(multiplied, 2).getData()[0];
    }

    private Matrix sanitizeData(Matrix matrix, int base) {
        Matrix newMatrix = new Matrix(matrix.getHeight(), matrix.getLength());
        for(int i = 0; i < matrix.getHeight(); i++) {
            for(int j = 0; j < matrix.getLength(); j++) {
                newMatrix.getData()[i][j] = matrix.getData()[i][j] % base;
            }
        }
        return newMatrix;
    }
}
