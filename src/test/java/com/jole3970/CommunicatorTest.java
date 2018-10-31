package com.jole3970;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommunicatorTest {

    @Test
    void name() {
        Channel channel = mock(Channel.class);
        Encoder encoder = mock(Encoder.class);
        Decoder decoder = mock(Decoder.class);
        Communicator communicator = new Communicator(channel, encoder, decoder);
        byte[] payload = new byte[5];
        boolean[] booleanArr = new boolean[5];

        int m = 6;
        double errorRate = 0;
        when(encoder.encode((boolean[]) any(), eq(m)))
                .thenReturn(booleanArr);
        when(channel.sendThroughChannel(any(), eq(errorRate)))
                .thenReturn(booleanArr);
        when(decoder.decode(any(), eq(m)))
                .thenReturn(booleanArr);

        communicator.transmitAndReceiveBytes(payload,m, errorRate);
    }
}
