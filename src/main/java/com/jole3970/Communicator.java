package com.jole3970;

import com.jole3970.datastructure.BooleanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Communicator {

    private Channel channel = new Channel();

    public byte[] transmitAndReceiveBytes(byte[] bytes, int m, double errorRate) {
        Encoder encoder = new Encoder(m);
        Decoder decoder = new Decoder(m);

        List<Boolean> listOfBools = getAsBits(bytes);

        Stream<Object> bitStream = getBitStream(listOfBools, m);
        List<Boolean> decodedBools = bitStream
                .map(vector -> encoder.encode((boolean[]) vector))
                .map(encoded -> channel.sendThroughChannel(encoded, errorRate))
                .map(channelized -> decoder.decode(channelized))
                .map(decoded -> BooleanUtils.boxArray(decoded))
                .flatMap(decoded -> Arrays.stream(decoded))
                .collect(Collectors.toList());

        return getBytes(decodedBools);
    }

    private Stream<Object> getBitStream(List<Boolean> bits, int m) {
        Stream.Builder<Object> streamBuilder = Stream.builder();
        for (int i = 0; i < bits.size(); i+= m+1) {
            List<Boolean> sublist;
            if ( i + m + 1 < bits.size() ) {
                sublist = bits.subList(i, i + m + 1);
            }
            else {
                sublist = bits.subList(i, bits.size());
                while (sublist.size() <= m) {
                    sublist.add(false);
                }
            }
            streamBuilder.add(BooleanUtils.listToArray(sublist));
        }


        return streamBuilder.build();
    }

    private List<Boolean> getAsBits(byte[] bytes) {
        List<Boolean> listOfBools = new ArrayList<>();

        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                listOfBools.add((val & 128) != 0);
                val <<= 1;
            }
        }
        return listOfBools;
    }

    private byte[] getBytes(List<Boolean> decodedBools) {
        int usableSize = decodedBools.size() - decodedBools.size() % 8;
        byte[] returnedBytes = new byte[usableSize/8];
        for(int i = 0; i*8 < usableSize; i++) {
            byte val = 0;
            for(int j = 0; j < 8; j++) {
                val <<= 1;
                val += decodedBools.get(i*8 + j) ? 1 : 0;
            }
            returnedBytes[i] = val;
        }
        return returnedBytes;
    }
}
