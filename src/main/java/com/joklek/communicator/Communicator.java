package com.joklek.communicator;

public interface Communicator {

    byte[] transmitAndReceiveCodedBytes(byte[] bytes, double errorRate);
    boolean[] transmitAndReceiveCodedBits(boolean[] bits, double errorRate);
}
