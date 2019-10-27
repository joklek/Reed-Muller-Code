package com.joklek.reedmuller.communicator;

import com.joklek.reedmuller.communicator.elements.Channel;
import com.joklek.reedmuller.communicator.elements.Decoder;
import com.joklek.reedmuller.communicator.elements.Encoder;
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

public class CodedCommunicatorTest {

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
        int m = 2;
        CodedCommunicator communicator = new CodedCommunicator(channel, encoder, decoder, m);

        boolean[] bools = new boolean[]{false, true, false, true};
        boolean[] bools1 = new boolean[]{false, true, false};
        boolean[] bools2 = new boolean[]{true, false, false};

        List<boolean[]> collected = communicator.splitBitsForEncoding(bools, m);
        assertThat((collected.get(0)).length, is(m + 1));
        assertThat(collected.get(0), is(bools1));
        assertThat(collected.get(1), is(bools2));
    }

    @Test
    void shouldReturnEmptyBytes() {
        int m = 6;
        CodedCommunicator communicator = new CodedCommunicator(channel, encoder, decoder, m);
        byte[] payload = new byte[5];
        boolean[] booleanArr = new boolean[5];
        boolean[] payloadInBinary = new boolean[5*8];


        double errorRate = 0;
        when(encoder.encode((boolean[]) any(), eq(m)))
                .thenReturn(booleanArr);
        when(channel.sendThroughChannel(any(), eq(errorRate)))
                .thenReturn(booleanArr);
        when(decoder.decode(any(), eq(m)))
                .thenReturn(payloadInBinary);

        byte[] result = communicator.transmitAndReceiveCodedBytes(payload, errorRate);
        assertThat(result.length, is(payload.length));
        assertThat(result, is(payload));
    }

    @Test
    void shouldReturnEmptyBits() {
        int m = 3;
        CodedCommunicator communicator = new CodedCommunicator(channel, encoder, decoder, m);
        boolean[] payload = new boolean[5];
        boolean[] booleanArr = new boolean[7];

        double errorRate = 0;
        when(encoder.encode((boolean[]) any(), eq(m)))
                .thenReturn(booleanArr);
        when(channel.sendThroughChannel(any(), eq(errorRate)))
                .thenReturn(booleanArr);
        when(decoder.decode(any(), eq(m)))
                .thenReturn(payload);

        boolean[] result = communicator.transmitAndReceiveCodedBits(payload, errorRate);
        assertThat(result.length, is(payload.length));
        assertThat(result, is(payload));
    }
}
