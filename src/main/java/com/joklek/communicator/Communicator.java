package com.joklek.communicator;

public interface Communicator {

    byte[] transmitAndReceiveCodedBytes(byte[] bytes, int m, double errorRate);
    boolean[] transmitAndReceiveCodedBits(boolean[] bits, int m, double errorRate);
}
