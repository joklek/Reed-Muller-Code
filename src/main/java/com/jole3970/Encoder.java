package com.jole3970;

import com.jole3970.datastructure.BooleanUtils;
import com.jole3970.datastructure.Matrix;

public class Encoder {

    private final ReedMullerCodeGenerator generator;

    public Encoder(ReedMullerCodeGenerator generator) {
        this.generator = generator;
    }

    public boolean[] encode(int[] vector, int m) {
        Matrix multiplied = generator.generateGenerativeMatrixForM(m).transpose()
                .multiply(new Matrix(new int[][]{vector}).transpose()).transpose();
        return BooleanUtils.boolArrayFromIntArray(multiplied.getData()[0]);
    }

    public boolean[] encode(boolean[] vector, int m) {
        int[] converted = BooleanUtils.intArrayFromBoolArray(vector);
        return encode(converted, m);
    }
}
