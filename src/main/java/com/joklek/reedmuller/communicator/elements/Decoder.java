package com.joklek.reedmuller.communicator.elements;

import com.joklek.reedmuller.BooleanUtils;
import com.joklek.reedmuller.datastructure.Matrix;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.pow;

public class Decoder {

    protected final Matrix hMatrix;
    private final Map<Pair<Integer, Integer>, Matrix> hMatrices;

    public Decoder() {
        hMatrix = new Matrix(new int[][]{{1, 1}, {1, -1}});
        hMatrices = new ConcurrentHashMap<>();
    }

    /**
     * Decodes message encoded in Reed-Muller code
     *
     * @param vector unknown coded vector
     * @return decoded vector
     */
    public boolean[] decode(boolean[] vector, int m) {
        int[] integers = BooleanUtils.intArrayFromBoolArray(vector);

        int[] vectorSwitched = switchZeroToMinusOne(integers);
        Matrix previousW = new Matrix(new int[][]{vectorSwitched});
        for (int i = 1; i <= m; i++) {
            previousW = generateHadamardMatrix(i, m).multiply(previousW.transpose()).transpose();
        }

        int[] multipliedResult = previousW.getData()[0];

        int min = multipliedResult[0];
        int max = multipliedResult[0];
        for (int i : multipliedResult) {
            if(i > max) max = i;
            if(i < min) min = i;
        }

        int number = Math.abs(max) > Math.abs(min) ? max : min;
        int sign = number > 0 ? 1 : 0;
        int length = m + 1;
        int pos = findPositionOfElementInArray(multipliedResult, number);

        int[] binaryFormReversed = getBinaryFormReversed(pos, m);
        int[] result = new int[length];
        result[0] = sign;
        System.arraycopy(binaryFormReversed, 0, result, 1, binaryFormReversed.length);

        return BooleanUtils.boolArrayFromIntArray(result);
    }

    /**
     * Transforms number to binary representation and reverses it for given length.
     * For example with m = 4, transformation for 3 goes like this: 3 -> 11 -> 0011 -> 1100
     *
     * @param number number to get the binary reversed form of
     * @param length length of wanted array
     * @return a reversed binary representation of a number
     */
    private int[] getBinaryFormReversed(int number, int length) {
        StringBuilder binaryRepresentation = new StringBuilder(Integer.toBinaryString(number));
        int[] array = new int[length];

        while (binaryRepresentation.length() != length) {
            binaryRepresentation.insert(0, "0");
        }
        for (int i = 0; i < binaryRepresentation.length(); i++) {
            array[length - 1 - i] = Character.getNumericValue(binaryRepresentation.charAt(i));
        }
        return array;
    }

    private int findPositionOfElementInArray(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    private int[] switchZeroToMinusOne(int[] integers) {
        int[] switched = new int[integers.length];
        for (int i = 0; i < integers.length; i++) {
            switched[i] = integers[i] == 0 ? -1 : 1;
        }
        return switched;
    }

    /**
     * Generates i sized Hadamard matrix
     *
     * @return 2^m×2^m sized Hadamard matrix for i
     */
    private Matrix generateHadamardMatrix(int i, int m) {
        Pair<Integer, Integer> pair = Pair.of(m, i);
        return hMatrices.computeIfAbsent(pair, key -> {
            int powerOfI = (int) pow(2, m - i);
            int powerOfI2 = (int) pow(2, i - 1);

            Matrix multiplied = kroeneckerProductIdentityWithMatrix(hMatrix, powerOfI);
            return kroneckerProductMatrixWithIdentity(multiplied, powerOfI2);
        });
    }

    protected Matrix kroeneckerProductIdentityWithMatrix(Matrix matrix, int sizeOfIdentity) {
        int hostHeight = matrix.getHeight();
        int hostLength = matrix.getLength();

        int newHeight = sizeOfIdentity * hostHeight;
        int newLength = sizeOfIdentity * hostLength;
        int[][] newArray = new int[newHeight][newLength];
        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newLength; j++) {
                int valueOfCell = i/hostHeight == j/hostLength ? 1 : 0;
                if(valueOfCell == 0) {
                    continue;            // this is a optimisation, probably unnecessary
                }
                newArray[i][j] = matrix.getData()[i%hostHeight][j%hostLength] * valueOfCell;
            }
        }
        return new Matrix(newArray);
    }

    protected Matrix kroneckerProductMatrixWithIdentity(Matrix matrix, int identitySize) {

        int newHeight = matrix.getHeight() * identitySize;
        int newLength = matrix.getLength() * identitySize;
        int[][] newArray = new int[newHeight][newLength];
        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newLength; j++) {
                int valueOfCell = matrix.getData()[i/identitySize][j/identitySize];
                if(valueOfCell == 0) {
                    continue;            // this is a optimisation, probably unnecessary
                }
                newArray[i][j] = i%identitySize == j%identitySize ? valueOfCell : 0;
            }
        }
        return new Matrix(newArray);
    }
}
