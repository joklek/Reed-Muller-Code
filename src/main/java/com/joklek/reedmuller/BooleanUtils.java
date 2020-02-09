package com.joklek.reedmuller;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Utility class for boolean and byte operations
 */
public final class BooleanUtils {

    private BooleanUtils() {}

    /**
     * Converts list of Boolean to boolean[]
     * @param list of boxed booleans
     * @return unboxed boolean array
     */
    public static boolean[] listToArray(List<Boolean> list) {
        int length = list.size();
        boolean[] arr = new boolean[length];
        for (int i = 0; i < length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    /**
     * Converts integer array, to a boolean array. For example this could be a transformation [1, 1, 0] -> [true, true false].
     * This method works with base two, so operations like this [3, 4, 5] -> [true, false, true] ar possible.
     * @param intArray integer array that is to be converted
     * @return converted boolean array
     */
    public static boolean[] boolArrayFromIntArray(int[] intArray) {
        int length = intArray.length;
        boolean[] arr = new boolean[length];
        for (int i = 0; i < length; i++) {
            arr[i] = intArray[i] % 2 != 0;
        }
        return arr;
    }

    /**
     * Converts a boolean array, to an int array. For example this could be a transformation [true, true false] -> [1, 1, 0]
     * @param boolArray boolean array that is to be converted
     * @return converted integer array
     */
    public static int[] intArrayFromBoolArray(boolean[] boolArray) {
        return intArrayFromBoolArray(boolArray, 1, 0);
    }

    /**
     * Converts a boolean array, to an int array. For example this could be a transformation [true, true false] -> [trueVal, trueVal, falseVal].
     * If trueVal = 1 and falseVal = 0. then [true, true false] -> [1, 1, 0]
     * @param boolArray boolean array that is to be converted
     * @return converted integer array
     */
    public static int[] intArrayFromBoolArray(boolean[] boolArray, int trueVal, int falseVal) {
        int length = boolArray.length;
        int[] arr = new int[length];
        for (int i = 0; i < length; i++) {
            arr[i] = boolArray[i] ? trueVal : falseVal;
        }
        return arr;
    }

    /**
     * Boxing a regular boolean array to a boxed boolean
     * @param unboxed unboxed boolean array
     * @return boxed boolean array
     */
    public static Boolean[] boxArray(boolean[] unboxed) {
        Boolean[] boxed = new Boolean[unboxed.length];
        for(int i = 0; i< unboxed.length; i++) {
            boxed[i] = unboxed[i];
        }
        return boxed;
    }

    /**
     * Collector to convert stream to list of Booleans
     */
    public static final Collector<Boolean, ?, boolean[]> TO_BOOLEAN_ARRAY
            = Collectors.collectingAndThen(Collectors.toList(), BooleanUtils::listToArray);

    /**
     * Converts byte array to bits, booleans in this case. Every byte gets divided into 8 bits, so the resulting array is 8 times bigger than the provided byte array.
     * @param bytes byte array of data
     * @return boolean array of converted bytes. Size is 8 times longer than byte array size
     */
    public static boolean[] getAsBits(byte[] bytes) {
        boolean[] bools = new boolean[bytes.length*8];

        for (int i = 0; i < bytes.length; i++) {
            int val = bytes[i];
            for (int j = 0; j < 8; j++) {
                bools[i*8 + j] = ((val & 128) != 0);
                val <<= 1;
            }
        }
        return bools;
    }

    /**
     * Converts bit(boolean) array to byte representation. Adds zeroes if boolean array size does not divide by 8.
     * @param bits boolean array of data to convert
     * @return byte array of given bits, it's size is 8 times smaller than provided bit array.
     */
    public static byte[] getBytes(boolean[] bits) {
        int usableSize = bits.length - bits.length % 8;
        byte[] returnedBytes = new byte[usableSize/8];
        for(int i = 0; i*8 < usableSize; i++) {
            byte val = 0;
            for(int j = 0; j < 8; j++) {
                val <<= 1;
                val += bits[i*8 + j] ? 1 : 0;
            }
            returnedBytes[i] = val;
        }
        return returnedBytes;
    }
}