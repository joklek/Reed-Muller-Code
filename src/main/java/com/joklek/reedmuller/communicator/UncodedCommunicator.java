package com.joklek.reedmuller.communicator;

import com.joklek.reedmuller.BooleanUtils;
import com.joklek.reedmuller.communicator.elements.Channel;

public class UncodedCommunicator implements Communicator {

    private final Channel channel;

    public UncodedCommunicator(Channel channel) {
        this.channel = channel;
    }

    /**
     * Transmits bytes through a channel with given error rate and then returns the received data
     * @param bytes byte array of data that is to be transmitted
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
     * Transmits bits through a channel with given error rate and then returns the received data
     * @param bits bit array of data that is to be transmitted
     * @param errorRate error rate of channel
     * @return received data
     */
    @Override
    public boolean[] transmitAndReceiveCodedBits(boolean[] bits, double errorRate) {
        return channel.sendThroughChannel(bits, errorRate);
    }
}
