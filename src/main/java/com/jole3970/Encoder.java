package com.jole3970;

import com.jole3970.datastructure.BooleanUtils;
import com.jole3970.datastructure.Matrix;

public class Encoder {

    private final int m;
    public Encoder(int m) {
        this.m = m;
    }

    public boolean[] encode(int[] vector) {
        Matrix multiplied = ReedMullerCodeGenerator.generateGenerativeMatrixForM(m).transpose()
                .multiply(new Matrix(new int[][]{vector}).transpose()).transpose();
        return BooleanUtils.boolArrayFromIntArray(multiplied.getData()[0]);
    }
}
