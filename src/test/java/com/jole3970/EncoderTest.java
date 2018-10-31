package com.jole3970;

import com.jole3970.datastructure.BooleanUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class EncoderTest {

    private static Stream<Arguments> wordEndExpectedEncoded() {
        return Stream.of(
                arguments(new int[]{0, 0, 0, 1}, new int[]{0, 0, 0, 0, 1, 1, 1, 1}, 3),
                arguments(new int[]{1, 1, 0, 0}, new int[]{1, 0, 1, 0, 1, 0, 1, 0}, 3),
                arguments(new int[]{0, 0}, new int[]{0, 0}, 1)
        );
    }
    @ParameterizedTest
    @MethodSource("wordEndExpectedEncoded")
    void shouldDecodeCorrectly(int[] word, int[] expected, int m) {
        Encoder encoder = new Encoder(m);
        boolean[] encoded = encoder.encode(word);
        int[] encodedUnboolified = BooleanUtils.intArrayFromBoolArray(encoded);
        assertThat(encodedUnboolified, is(expected));
    }
}
