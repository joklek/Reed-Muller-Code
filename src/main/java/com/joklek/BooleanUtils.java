package com.joklek;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class BooleanUtils {

    private BooleanUtils() {}

    public static boolean[] listToArray(List<Boolean> list) {
        int length = list.size();
        boolean[] arr = new boolean[length];
        for (int i = 0; i < length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static boolean[] boolArrayFromIntArray(int[] intArray) {
        int length = intArray.length;
        boolean[] arr = new boolean[length];
        for (int i = 0; i < length; i++) {
            arr[i] = intArray[i] % 2 != 0;
        }
        return arr;
    }

    public static int[] intArrayFromBoolArray(boolean[] intArray) {
        int length = intArray.length;
        int[] arr = new int[length];
        for (int i = 0; i < length; i++) {
            arr[i] = intArray[i] ? 1 : 0;
        }
        return arr;
    }

    public static Boolean[] boxArray(boolean[] unboxed) {
        Boolean[] boxed = new Boolean[unboxed.length];
        for(int i = 0; i< unboxed.length; i++) {
            boxed[i] = unboxed[i];
        }
        return boxed;
    }

    public static final Collector<Boolean, ?, boolean[]> TO_BOOLEAN_ARRAY
            = Collectors.collectingAndThen(Collectors.toList(), BooleanUtils::listToArray);

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

    public static byte[] getBytes(boolean[] decodedBools) {
        int usableSize = decodedBools.length - decodedBools.length % 8;
        byte[] returnedBytes = new byte[usableSize/8];
        for(int i = 0; i*8 < usableSize; i++) {
            byte val = 0;
            for(int j = 0; j < 8; j++) {
                val <<= 1;
                val += decodedBools[i*8 + j] ? 1 : 0;
            }
            returnedBytes[i] = val;
        }
        return returnedBytes;
    }
}