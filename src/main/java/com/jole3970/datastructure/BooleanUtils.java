package com.jole3970.datastructure;

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

    public static final Collector<Boolean, ?, boolean[]> TO_BOOLEAN_ARRAY
            = Collectors.collectingAndThen(Collectors.toList(), BooleanUtils::listToArray);

    public static Boolean[] boxArray(boolean[] unboxed) {
        Boolean[] boxed = new Boolean[unboxed.length];
        for(int i = 0; i< unboxed.length; i++) {
            boxed[i] = unboxed[i];
        }
        return boxed;
    }
}