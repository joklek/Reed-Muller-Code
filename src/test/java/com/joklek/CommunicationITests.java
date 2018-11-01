package com.joklek;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CommunicationITests {
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void shouldDecodeCorrectly(int m) {
        Communicator communicator = new Communicator(new Channel(), new Encoder(new ReedMullerCodeGenerator()), new Decoder());
        String original = "This is a generally nice string";
        byte[] bytes = communicator.transmitAndReceiveBytes(original.getBytes(), m, 0);
        String received = new String(bytes);
        assertThat(received, is(original));
    }
}
