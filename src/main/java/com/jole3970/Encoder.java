package com.jole3970;

import com.jole3970.datastructure.Matrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Encoder {
    public boolean[] encode(int[] vector) {
        Matrix multiplied = ReedMullerCodeGenerator.generateGenerativeMatrixForM(3).transpose()
                .multiply(new Matrix(new int[][]{vector}).transpose()).transpose();
        return toBitArray(multiplied.getData()[0]);
    }

    private boolean[] toBitArray(int[] array) {
        Stream<Integer> boxed = Arrays.stream(array)
                .map(x -> x % 2).boxed();
        List<Boolean> boolList = boxed.map(x -> x == 1).collect(Collectors.toList());
        return toPrimitiveArray(boolList);
    }

    private boolean[] toPrimitiveArray(final List<Boolean> booleanList) {
        final boolean[] primitives = new boolean[booleanList.size()];
        int index = 0;
        for (Boolean object : booleanList) {
            primitives[index++] = object;
        }
        return primitives;
    }
}
