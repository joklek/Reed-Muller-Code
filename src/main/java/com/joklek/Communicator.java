package com.joklek;

import com.joklek.datastructure.BooleanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Communicator {

    private final Channel channel;
    private final Encoder encoder;
    private final Decoder decoder;

    public Communicator(Channel channel, Encoder encoder, Decoder decoder) {
        this.channel = channel;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public byte[] transmitAndReceiveBytes(byte[] bytes, int m, double errorRate) {
        boolean[] listOfBools = getAsBits(bytes);
        boolean[] decodedBools = transmitAndReceiveBits(listOfBools, m, errorRate);
        return getBytes(decodedBools);
    }

    public boolean[] transmitAndReceiveBits(boolean[] bits, int m, double errorRate) {
        int originalSize = bits.length;
        Stream<Object> bitStream = getBitStream(bits, m);
        List<Boolean> decodedBools = bitStream
                .map(vector -> encoder.encode((boolean[]) vector, m))
                .map(encoded -> channel.sendThroughChannel(encoded, errorRate))
                .map(channelized -> decoder.decode(channelized, m))
                .map(decoded -> BooleanUtils.boxArray(decoded))
                .flatMap(decoded -> Arrays.stream(decoded))
                .collect(Collectors.toList());
        return BooleanUtils.listToArray(decodedBools.subList(0, originalSize));
    }

    protected Stream<Object> getBitStream(boolean[] bits, int m) {
        Stream.Builder<Object> streamBuilder = Stream.builder();
        for (int i = 0; i < bits.length; i+= m+1) {
            boolean[] bools = new boolean[m+1];
            int length = i + m + 1 > bits.length ? bits.length - i : m+1;
            System.arraycopy(bits, i, bools, 0, length);

            streamBuilder.add(bools);
        }

        return streamBuilder.build();
    }

    protected boolean[] getAsBits(byte[] bytes) {
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

    protected byte[] getBytes(boolean[] decodedBools) {
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
