package com.jole3970;

import com.jole3970.datastructure.BooleanUtils;
import com.jole3970.datastructure.Matrix;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.pow;

public class Decoder {

    private final Matrix H_Matrix;
    private final Map<Integer, Matrix> I_Matrices;
    private final Map<Integer, Matrix> H_Matrices;

    private final int m;
    public Decoder(int m) {
        this.m = m;

        H_Matrix = new Matrix(new int[][]{{1, 1}, {1, -1}});
        int iMatrixCount =  (int) pow(2, m-1);
        I_Matrices = new HashMap<>(iMatrixCount);
        H_Matrices = new HashMap<>(3);
    }

    public boolean[] decode(boolean[] vector) {
        int[] integers = BooleanUtils.intArrayFromBoolArray(vector);
        int[] vectorSwitched = switchZeroToMinusOne(integers);
        Matrix previousW = new Matrix(new int[][]{vectorSwitched});
        for(int i = 1; i <= m; i++) {
            previousW = generateH_Matrix(i).multiply(previousW.transpose()).transpose();
        }
        BooleanUtils.boolArrayFromIntArray(previousW.getData()[0]);
        return new boolean[0];
    }

    private int[] switchZeroToMinusOne(int[] integers) {
        int[] switched = new int[integers.length];
        for(int i = 0; i < integers.length; i++) {
            switched[i] = integers[i] == 0 ? -1 : 1;
        }
        return switched;
    }

    private Matrix generateH_Matrix(int i) {
        return H_Matrices.computeIfAbsent(i, key -> {
            int i_power =  (int) pow(2, m-i);
            int i_power2 =  (int) pow(2, i-1);
            return generateI_Matrix(i_power)
                    .cartesian(H_Matrix)
                    .cartesian(generateI_Matrix(i_power2));
        });
    }

    private Matrix generateI_Matrix(int n) {
        return I_Matrices.computeIfAbsent(n , key -> {
            int[][] i_array = new int[n][n];
            for(int i = 0; i < n; i++) {
                i_array[i][i] = 1;
            }
            return new Matrix(i_array);
        });
    }
}
