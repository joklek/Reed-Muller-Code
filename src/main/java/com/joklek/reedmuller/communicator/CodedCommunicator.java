package com.joklek.reedmuller.communicator;

import com.joklek.reedmuller.BooleanUtils;
import com.joklek.reedmuller.communicator.elements.Channel;
import com.joklek.reedmuller.communicator.elements.Decoder;
import com.joklek.reedmuller.communicator.elements.Encoder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CodedCommunicator implements Communicator {

    private final Channel channel;
    private final Encoder encoder;
    private final Decoder decoder;
    private final int m;

    public CodedCommunicator(Channel channel, Encoder encoder, Decoder decoder, int m) {
        this.channel = channel;
        this.encoder = encoder;
        this.decoder = decoder;
        this.m = m;
    }

    /**
     * Transmits coded bytes through a channel with given error rate and then returns the received uncoded data
     *
     * @param bytes     byte array of data that is to be transmitted
     * @param errorRate error rate of channel
     * @return received data
     */
    @Override
    public byte[] transmitAndReceiveCodedBytes(byte[] bytes, double errorRate) {
        boolean[] listOfBools = BooleanUtils.getAsBits(bytes);
        boolean[] decodedBools = transmitAndReceiveCodedBits(listOfBools, errorRate);
        return BooleanUtils.getBytes(decodedBools);
    }

    /**
     * Transmits coded bits through a channel with given error rate and then returns the received uncoded data
     *
     * @param bits      bit array of data that is to be transmitted
     * @param errorRate error rate of channel
     * @return received data
     */
    @Override
    public boolean[] transmitAndReceiveCodedBits(boolean[] bits, double errorRate) {
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
        for (int i = 0; i < bits.length; i += m + 1) {
            boolean[] bools = new boolean[m + 1];
            int length = i + m + 1 > bits.length ? bits.length - i : m + 1;
            System.arraycopy(bits, i, bools, 0, length);

            streamBuilder.add(bools);
        }

        return streamBuilder.build();
    }
}
