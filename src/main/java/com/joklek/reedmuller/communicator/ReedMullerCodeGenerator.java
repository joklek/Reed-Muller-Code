package com.joklek.reedmuller.communicator;

import com.joklek.reedmuller.datastructure.Matrix;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.pow;

public class ReedMullerCodeGenerator {

    private final Map<Integer, Matrix> matrices;

    public ReedMullerCodeGenerator() {
        matrices = new ConcurrentHashMap<>();
    }

    /**
     * Generates generative Reed-Muller code matrix for given m
     * @return Generative matrix
     */
    public Matrix generateGenerativeMatrixForM(int m) {
        return matrices.computeIfAbsent(m, key -> {
            int length = m + 1;
            int height =  (int) pow(2, m);
            Matrix matrix = new Matrix(height, length);
            for(int i = 0; i < length; i++) {
                for(int j = 0; j < height; j++) {
                    int number;
                    if (i == 0) {
                        number = 1;
                    }
                    else {
                        number = (j / ((int) pow(2, i - 1.0))) % 2;
                    }
                    matrix.getData()[j][i] = number;
                }
            }
            return matrix;
        });
    }
}
