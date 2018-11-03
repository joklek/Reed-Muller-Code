package com.joklek;

import com.joklek.communicator.Communicator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class Operator {
    public byte[] sendWithCommunicator(WorkingMode mode, String payloadInfo, Communicator communicator, double errorRate) throws IOException {
        switch (mode) {
            case FILE:
                File fi = new File(payloadInfo);
                byte[] fileContent = Files.readAllBytes(fi.toPath());
                return communicator.transmitAndReceiveCodedBytes(fileContent, errorRate);
            case BITMAP:
                return sendBitmap(payloadInfo, communicator, errorRate);
            case BINARY:
                return sendBinaryRepresentation(payloadInfo, communicator, errorRate);
            case TEXT:
                return communicator.transmitAndReceiveCodedBytes(payloadInfo.getBytes(), errorRate);
            default:
                System.out.println("this should not happen"); // TODO Throw exception
                return new byte[0];
        }
    }

    private byte[] sendBitmap(String sourcePath, Communicator communicator, double errorRate) throws IOException {
        File fi = new File(sourcePath);
        byte[] fileContent = Files.readAllBytes(fi.toPath());

        int lengthOfBmpHeader = 54;
        byte[] header = new byte[lengthOfBmpHeader];
        System.arraycopy(fileContent, 0, header, 0, lengthOfBmpHeader);

        byte[] bytesToSend = new byte[fileContent.length - lengthOfBmpHeader];
        System.arraycopy(fileContent, lengthOfBmpHeader, bytesToSend, 0, fileContent.length - lengthOfBmpHeader);

        byte[] receiveBytes = communicator.transmitAndReceiveCodedBytes(bytesToSend, errorRate);

        byte[] receivedImage = new byte[fileContent.length];
        System.arraycopy(header, 0, receivedImage, 0, lengthOfBmpHeader);
        System.arraycopy(receiveBytes, 0, receivedImage, lengthOfBmpHeader, fileContent.length - lengthOfBmpHeader);

        return receivedImage;
    }

    private byte[] sendBinaryRepresentation(String input, Communicator communicator, double errorRate) {
        if(Pattern.matches("^[0,1]+$", input)) {
            boolean[] vector = new boolean[input.length()];
            for(int i = 0; i < input.length(); i++) {
                vector[i] = input.charAt(i) == '1';
            }
            boolean[] decoded = communicator.transmitAndReceiveCodedBits(vector, errorRate);
            byte[] stringRepresentation = new byte[decoded.length];

            for(int i = 0; i < decoded.length; i++) {
                stringRepresentation[i] = decoded[i] ? (byte)'1' : (byte)'0';
            }
            return stringRepresentation; // TODO Decide if return in bytes the string representation, or not
        }
        return new byte[0]; // TODO throw error?
    }
}
