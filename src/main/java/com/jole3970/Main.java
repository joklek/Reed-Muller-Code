package com.jole3970;

public class Main {

    public static void main(String[] args) {
        Communicator communicator = new Communicator();

        String text = "Test Text";
        System.out.println(text);
        byte[] bytes = text.getBytes();
        byte[] receiveBytes = communicator.transmitAndReceiveBytes(bytes, 5, 0.2);
        System.out.println(new String(receiveBytes));
    }
}
