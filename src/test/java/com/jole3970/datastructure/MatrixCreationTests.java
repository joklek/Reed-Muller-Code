package com.jole3970.datastructure;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MatrixCreationTests {

    private static Stream<Arguments> arraysAndExpectedSizes() {
        return Stream.of(
                arguments(new int[][]{{0}}, 1, 1),
                arguments(new int[][]{{0}, {0}}, 2, 1),
                arguments(new int[][]{{0, 0}}, 1, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("arraysAndExpectedSizes")
    void shouldCreateCorrectMatrixSizeFromArray(int[][] array, int height, int length) {
        Matrix matrix = new Matrix(array);
        assertThat(matrix.getHeight(), is(height));
        assertThat(matrix.getLength(), is(length));
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < length; j++) {
                assertThat(matrix.get(i, j), is(0));
            }
        }
    }

    private static Stream<Arguments> arraysAndValueRanges() {
        return Stream.of(
                arguments(new int[][]{{1}}, 1, 1),
                arguments(new int[][]{{1}, {2}}, 1, 2),
                arguments(new int[][]{{3, 4}}, 3, 4),
                arguments(new int[][]{{1, 2}, {3, 4}}, 1, 4)
        );
    }
    @ParameterizedTest
    @MethodSource("arraysAndValueRanges")
    void shouldSetCellsWithCorrectValues(int[][] array, int from, int to) {
        Matrix matrix = new Matrix(array);
        int[] ints = IntStream.range(from, to + 1).toArray();
        assertThat(matrix.getHeight() * matrix.getLength(), is(ints.length));
        for(int i = 0; i < matrix.getHeight(); i++) {
            for(int j = 0; j < matrix.getLength(); j++) {
                assertThat(matrix.get(i, j), is(ints[i*matrix.getLength() + j]));
            }
        }
    }


    private static Stream<Arguments> matrixAndTransposed() {
        return Stream.of(
                arguments(new int[][]{{1}}, new int[][]{{1}}),
                arguments(new int[][]{{1}, {2}}, new int[][]{{1, 2}}),
                arguments(new int[][]{{1, 2}, {3, 4}}, new int[][]{{1, 3}, {2, 4}})
        );
    }
    @ParameterizedTest
    @MethodSource("matrixAndTransposed")
    void shouldTransposeCorrectly(int[][] array, int[][] transposed) {
        Matrix matrix = new Matrix(array);
        Matrix transposedExpected = new Matrix(transposed);

        assertThat(matrix.transpose(), Matchers.equalTo(transposedExpected));
    }

    private static Stream<Arguments> matrixTermAndSum() {
        return Stream.of(
                arguments(new int[][]{{1}}, new int[][]{{2}}, new int[][]{{3}}),
                arguments(new int[][]{{1}, {2}}, new int[][]{{3}, {2}}, new int[][]{{4}, {4}}),
                arguments(new int[][]{{1, 2}, {3, 4}}, new int[][]{{3, 4}, {5, 6}}, new int[][]{{4, 6}, {8, 10}})
        );
    }
    @ParameterizedTest
    @MethodSource("matrixTermAndSum")
    void shouldAddCorrectly(int[][] array, int[][] term, int[][] sum) {
        Matrix matrix = new Matrix(array);
        Matrix termMatrix = new Matrix(term);
        Matrix sumMatrix = new Matrix(sum);

        assertThat(matrix.add(termMatrix), is(sumMatrix));
    }


    private static Stream<Arguments> matrixMultiplierAndResult() {
        return Stream.of(
                arguments(new int[][]{{2, 3, 4}, {1, 0, 0}}, new int[][]{{0, 1000}, {1, 100}, {0, 10}}, new int[][]{{3, 2340}, {0, 1000}}),
                arguments(new int[][]{{2}}, new int[][]{{3}}, new int[][]{{6}})
        );
    }
    @ParameterizedTest
    @MethodSource("matrixMultiplierAndResult")
    void shouldMultiplyCorrectly(int[][] array, int[][] term, int[][] result) {
        Matrix matrix = new Matrix(array);
        Matrix termMatrix = new Matrix(term);
        Matrix sumMatrix = new Matrix(result);

        assertThat(matrix.multiply(termMatrix), is(sumMatrix));
    }


    @Test
    void shouldFillNewMatrixWithZeroes() {
        int length = 3;
        int height = 4;
        Matrix matrix = new Matrix(height, length);
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < length; j++) {
                assertThat(matrix.get(i, j), is(0));
            }
        }
    }
}
