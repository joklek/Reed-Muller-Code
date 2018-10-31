package com.jole3970;

import com.jole3970.datastructure.BooleanUtils;
import com.jole3970.datastructure.Matrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.pow;

public class Decoder {

    private final Matrix hMatrix;
    private final Map<Integer, Matrix> iMatrices;
    private final Map<Integer, Matrix> hMatrices;

    private final int m;
    public Decoder(int m) {
        this.m = m;

        hMatrix = new Matrix(new int[][]{{1, 1}, {1, -1}});
        int iMatrixCount =  (int) pow(2, m-1);
        iMatrices = new HashMap<>(iMatrixCount);
        hMatrices = new HashMap<>(3);
    }

    /**
     * Decodes message encoded in Reed-Muller code
     * @param vector unknown coded vector
     * @return decoded vector
     */
    public boolean[] decode(boolean[] vector) {
        int[] integers = BooleanUtils.intArrayFromBoolArray(vector);

        int[] vectorSwitched = switchZeroToMinusOne(integers);
        Matrix previousW = new Matrix(new int[][]{vectorSwitched});
        for(int i = 1; i <= m; i++) {
            previousW = generateHMatrix(i).multiply(previousW.transpose()).transpose();
        }

        int[] multipliedResult = previousW.getData()[0];
        int max = Arrays.stream(multipliedResult).min().getAsInt();
        int min = Arrays.stream(multipliedResult).max().getAsInt();

        int number = Math.abs(max) > Math.abs(min) ? max : min;
        int sign = number > 0 ? 1 : 0;
        int length =  m + 1;
        int pos = find(multipliedResult, number);

        int[] binaryFormReversed = getBinaryFormReversed(pos);
        int[] result = new int[length];
        result[0] = sign;
        System.arraycopy(binaryFormReversed, 0, result, 1, binaryFormReversed.length);

        return BooleanUtils.boolArrayFromIntArray(result);
    }

    private int[] getBinaryFormReversed(int pos) {
        StringBuilder binaryRepresentation = new StringBuilder(Integer.toBinaryString(pos));
        int[] array = new int[m];

        while (binaryRepresentation.length() != m) {
            binaryRepresentation.insert(0, "0");
        }
        for(int i = 0; i < binaryRepresentation.length(); i++) {
            array[m-1-i] = Character.getNumericValue(binaryRepresentation.charAt(i));
        }
        return array;
    }

    private int find(int[] array, int value) {
        for(int i=0; i<array.length; i++) {
            if(array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    private int[] switchZeroToMinusOne(int[] integers) {
        int[] switched = new int[integers.length];
        for(int i = 0; i < integers.length; i++) {
            switched[i] = integers[i] == 0 ? -1 : 1;
        }
        return switched;
    }

    private Matrix generateHMatrix(int i) {
        return hMatrices.computeIfAbsent(i, key -> {
            int powerOfI =  (int) pow(2, m-i);
            int powerOfI2 =  (int) pow(2, i-1);
            return generateIMatrix(powerOfI)
                    .kroneckerProduct(hMatrix)
                    .kroneckerProduct(generateIMatrix(powerOfI2));
        });
    }

    private Matrix generateIMatrix(int n) {
        return iMatrices.computeIfAbsent(n , key -> {
            int[][] iArray = new int[n][n];
            for(int i = 0; i < n; i++) {
                iArray[i][i] = 1;
            }
            return new Matrix(iArray);
        });
    }
}
