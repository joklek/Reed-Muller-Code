package com.joklek;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommunicatorTest {

    @Mock
    Channel channel;
    @Mock
    Encoder encoder;
    @Mock
    Decoder decoder;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void shouldGetCorrectBitStream() {

        Communicator communicator = new Communicator(channel, encoder, decoder);

        boolean[] bools = new boolean[]{false, true, false, true};
        boolean[] bools1 = new boolean[]{false, true, false};
        boolean[] bools2 = new boolean[]{true, false, false};
        int m = 2;
        List<Object> collected = communicator.getBitStream(bools, m)
                .collect(Collectors.toList());
        assertThat(((boolean[])collected.get(0)).length, is(m + 1));
        assertThat(collected.get(0), is(bools1));
        assertThat(collected.get(1), is(bools2));
    }

    @Test
    void shouldGetCorrectBits() {
        Communicator communicator = new Communicator(channel, encoder, decoder);

        byte[] bytes = new byte[]{1};
        boolean[] bools = new boolean[]{false, false, false, false, false, false, false, true};
        boolean[] collected = communicator.getAsBits(bytes);
        assertThat(collected, is(bools));
    }

    @Test
    void shouldGetCorrectBytes() {
        Communicator communicator = new Communicator(channel, encoder, decoder);

        byte[] bytes = new byte[]{3};
        boolean[] bools = new boolean[]{false, false, false, false, false, false, true, true};
        byte[] collected = communicator.getBytes(bools);
        assertThat(collected, is(bytes));
    }

    @Test
    void shouldReturnEmptyBytes() {
        Communicator communicator = new Communicator(channel, encoder, decoder);
        byte[] payload = new byte[5];
        boolean[] booleanArr = new boolean[5];
        boolean[] payloadInBinary = new boolean[5*8];

        int m = 6;
        double errorRate = 0;
        when(encoder.encode((boolean[]) any(), eq(m)))
                .thenReturn(booleanArr);
        when(channel.sendThroughChannel(any(), eq(errorRate)))
                .thenReturn(booleanArr);
        when(decoder.decode(any(), eq(m)))
                .thenReturn(payloadInBinary);

        byte[] result = communicator.transmitAndReceiveBytes(payload, m, errorRate);
        assertThat(result.length, is(payload.length));
        assertThat(result, is(payload));
    }

    @Test
    void shouldReturnEmptyBits() {
        Communicator communicator = new Communicator(channel, encoder, decoder);
        boolean[] payload = new boolean[5];
        boolean[] booleanArr = new boolean[7];

        int m = 3;
        double errorRate = 0;
        when(encoder.encode((boolean[]) any(), eq(m)))
                .thenReturn(booleanArr);
        when(channel.sendThroughChannel(any(), eq(errorRate)))
                .thenReturn(booleanArr);
        when(decoder.decode(any(), eq(m)))
                .thenReturn(payload);

        boolean[] result = communicator.transmitAndReceiveBits(payload, m, errorRate);
        assertThat(result.length, is(payload.length));
        assertThat(result, is(payload));
    }
}
