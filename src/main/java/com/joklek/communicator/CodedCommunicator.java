package com.joklek.communicator;

import com.joklek.Channel;
import com.joklek.Decoder;
import com.joklek.Encoder;
import com.joklek.datastructure.BooleanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CodedCommunicator implements Communicator {

    private final Channel channel;
    private final Encoder encoder;
    private final Decoder decoder;

    public CodedCommunicator(Channel channel, Encoder encoder, Decoder decoder) {
        this.channel = channel;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public byte[] transmitAndReceiveCodedBytes(byte[] bytes, int m, double errorRate) {
        boolean[] listOfBools = BooleanUtils.getAsBits(bytes);
        boolean[] decodedBools = transmitAndReceiveCodedBits(listOfBools, m, errorRate);
        return BooleanUtils.getBytes(decodedBools);
    }

    public boolean[] transmitAndReceiveCodedBits(boolean[] bits, int m, double errorRate) {
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
}
