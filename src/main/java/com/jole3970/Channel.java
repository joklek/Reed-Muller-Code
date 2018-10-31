package com.jole3970;

import java.util.Random;
import java.util.stream.Stream;

public class Channel {

    private Random rand = new Random();

    public Stream<Boolean> sendThroughChannel(Stream<Boolean> inputStream, double rateOfErrors) {
        if (rateOfErrors < 0 || rateOfErrors > 1) {
            throw new IllegalArgumentException(String.format("Rate of errors should be between [0, 1], but was %s", rateOfErrors));
        }
        return inputStream.map(bit -> rand.nextDouble() < rateOfErrors ? !bit : bit);
    }
}
