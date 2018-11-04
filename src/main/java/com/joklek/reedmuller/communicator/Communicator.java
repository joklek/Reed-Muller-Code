package com.joklek.reedmuller.communicator;

public interface Communicator {

    /**
     * Transmits bytes through a channel with given error rate and then returns the received data
     * @param bytes byte array of data that is to be transmitted
     * @param errorRate error rate of channel
     * @return received data
     */
    byte[] transmitAndReceiveCodedBytes(byte[] bytes, double errorRate);

    /**
     * Transmits bits through a channel with given error rate and then returns the received data
     * @param bits bit array of data that is to be transmitted
     * @param errorRate error rate of channel
     * @return received data
     */
    boolean[] transmitAndReceiveCodedBits(boolean[] bits, double errorRate);
}
