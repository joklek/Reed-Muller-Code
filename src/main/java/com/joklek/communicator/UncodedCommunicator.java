package com.joklek.communicator;

import com.joklek.Channel;
import com.joklek.datastructure.BooleanUtils;

public class UncodedCommunicator implements Communicator {

    private final Channel channel;

    public UncodedCommunicator(Channel channel) {
        this.channel = channel;
    }

    @Override
    public byte[] transmitAndReceiveCodedBytes(byte[] bytes, double errorRate) {
        boolean[] listOfBools = BooleanUtils.getAsBits(bytes);
        boolean[] decodedBools = transmitAndReceiveCodedBits(listOfBools, errorRate);
        return BooleanUtils.getBytes(decodedBools);
    }

    @Override
    public boolean[] transmitAndReceiveCodedBits(boolean[] bits, double errorRate) {
        return channel.sendThroughChannel(bits, errorRate);
    }
}
