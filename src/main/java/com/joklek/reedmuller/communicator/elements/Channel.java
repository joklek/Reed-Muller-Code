package com.joklek.reedmuller.communicator.elements;

import com.joklek.reedmuller.BooleanUtils;

import java.util.Random;
import java.util.stream.IntStream;

public class Channel {

    private final Random rand = new Random();

    /**
     * Imitates a noisy channel
     * @param inputWord the input word that is sent through the channel
     * @param rateOfErrors the procental probability of an error occurring. Should be between 0.0 and 0.100
     * @return word that came out of the channel, possibly with errors
     */
    public boolean[] sendThroughChannel(boolean[] inputWord, double rateOfErrors) {
        if (rateOfErrors < 0 || rateOfErrors > 1) {
            throw new IllegalArgumentException(String.format("Rate of errors should be between [0, 1], but was %s", rateOfErrors));
        }

        return IntStream.range(0, inputWord.length)
                .mapToObj(x -> inputWord[x])
                .map(bit -> (rand.nextDouble() < rateOfErrors) != bit)
                .collect(BooleanUtils.TO_BOOLEAN_ARRAY);
    }
}
