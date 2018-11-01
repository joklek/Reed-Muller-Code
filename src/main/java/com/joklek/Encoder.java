package com.joklek;

import com.joklek.datastructure.BooleanUtils;
import com.joklek.datastructure.Matrix;

public class Encoder {

    private final ReedMullerCodeGenerator generator;

    public Encoder(ReedMullerCodeGenerator generator) {
        this.generator = generator;
    }

    /**
     * Encodes a vector, where each member represents a bit value
     * @param vector to-be encoded vector
     * @param m
     * @return boolean array of the encoded vector
     */
    public boolean[] encode(int[] vector, int m) {
        Matrix multiplied = generator.generateGenerativeMatrixForM(m).transpose()
                .multiply(new Matrix(new int[][]{vector}).transpose()).transpose();
        return BooleanUtils.boolArrayFromIntArray(multiplied.getData()[0]);
    }

    /**
     * Encodes a vector, where each boolean represents a bit value
     * @param vector to-be encoded vector
     * @param m
     * @return boolean array of the encoded vector
     */
    public boolean[] encode(boolean[] vector, int m) {
        int[] converted = BooleanUtils.intArrayFromBoolArray(vector);
        return encode(converted, m);
    }
}
