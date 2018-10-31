package com.jole3970;

import com.jole3970.datastructure.BooleanUtils;

import java.util.Random;
import java.util.stream.IntStream;

public class Channel {

    private Random rand = new Random();

    public boolean[] sendThroughChannel(boolean[] inputStream, double rateOfErrors) {
        if (rateOfErrors < 0 || rateOfErrors > 1) {
            throw new IllegalArgumentException(String.format("Rate of errors should be between [0, 1], but was %s", rateOfErrors));
        }

        return IntStream.range(0, inputStream.length)
                .mapToObj(x -> inputStream[x])
                .map(bit -> rand.nextDouble() < rateOfErrors ? !bit : bit)
                .collect(BooleanUtils.TO_BOOLEAN_ARRAY);
    }
}
