package com.joklek.reedmuller.communicator.elements;

import com.joklek.reedmuller.BooleanUtils;
import com.joklek.reedmuller.communicator.ReedMullerCodeGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EncoderTest {

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
        ReedMullerCodeGenerator generator = new ReedMullerCodeGenerator(); // No idea how and why to mock this, I guess this is an integration sendWithCommunicator then

        Encoder encoder = new Encoder(generator);
        boolean[] encoded = encoder.encode(word, m);
        int[] encodedUnboolified = BooleanUtils.intArrayFromBoolArray(encoded);
        assertThat(encodedUnboolified, is(expected));
    }
}
