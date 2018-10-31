package com.jole3970;

public class Main {

    public static void main(String[] args) {
        Communicator communicator = new Communicator(new Channel(), new Encoder(new ReedMullerCodeGenerator()), new Decoder());

        String text = "Test Text";
        System.out.println(text);
        byte[] bytes = text.getBytes();
        byte[] receiveBytes = communicator.transmitAndReceiveBytes(bytes, 5, 0.1);
        System.out.println(new String(receiveBytes));
    }
}
