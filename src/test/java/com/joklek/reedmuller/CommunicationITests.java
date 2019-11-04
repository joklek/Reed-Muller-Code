package com.joklek.reedmuller;

import com.joklek.reedmuller.communicator.CodedCommunicator;
import com.joklek.reedmuller.communicator.ReedMullerCodeGenerator;
import com.joklek.reedmuller.communicator.elements.Channel;
import com.joklek.reedmuller.communicator.elements.Decoder;
import com.joklek.reedmuller.communicator.elements.Encoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CommunicationITests {
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void shouldDecodeCorrectly(int m) {
        CodedCommunicator communicator = new CodedCommunicator(new Channel(), new Encoder(new ReedMullerCodeGenerator()), new Decoder(), m);
        String original = "This is a generally nice string";
        byte[] bytes = communicator.transmitAndReceiveCodedBytes(original.getBytes(), 0);
        String received = new String(bytes);
        assertThat(received, is(original));
    }

    @Disabled
    @Test
    void capabilityTest() {
        int m = 7;
        CodedCommunicator communicator = new CodedCommunicator(new Channel(), new Encoder(new ReedMullerCodeGenerator()), new Decoder(), m);
        String original = "abcdefghijklmnoprstuvz1234567890";
        int originallen = original.length();
        byte[] originalBytes = original.getBytes();

        for(int j = 1; j <= 100; j++) {
            int count = 0;
            int charDiff = 0;
            for(int i = 0; i < 500; i++) {
                byte[] bytes = communicator.transmitAndReceiveCodedBytes(originalBytes, ((double)j)/100);
                String received = new String(bytes);
                if(!received.equals(original)) {
                    count++;
                    charDiff += getDifferentPlaces(original, received);
                }
            }
            charDiff = count != 0 ? charDiff/count : 0;
            System.out.println(j + ";" + count + ";" + charDiff + ";" + (originallen-charDiff));
        }
    }

    private int getDifferentPlaces(String s1, String s2) {
        int count = 0;
        int minLength = Math.min(s1.length(), s2.length());
        int lengthDiff = Math.abs(s1.length() - s2.length());
        for(int i = 0; i< minLength; i++) {
            if(s1.charAt(i) != s2.charAt(i)) {
                count++;
            }
        }
        return count+lengthDiff;
    }
}
