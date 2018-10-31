package com.jole3970;

import com.jole3970.datastructure.Matrix;

import static java.lang.Math.pow;

public class ReedMullerCodeGenerator {

    private ReedMullerCodeGenerator() {
    }

    /**
     * Generates generative Reed-Muller code matrix for given m
     * @return Generative matrix
     */
    public static Matrix generateGenerativeMatrixForM(int m) {
        int height = m + 1;
        int length =  (int) pow(2, m);
        Matrix matrix = new Matrix(height, length);
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < length; j++) {
                int number;
                if (i == 0) {
                    number = 1;
                }
                else {
                    number = (j / ((int) pow(2, i - 1))) % 2;
                }
                matrix.getData()[i][j] = number;
            }
        }
        return matrix;
    }
}
