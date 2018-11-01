package com.joklek;

import com.joklek.communicator.CodedCommunicator;
import com.joklek.communicator.Communicator;
import com.joklek.communicator.UncodedCommunicator;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings("squid:S106")
public class Main {

    private static List<String> argumentFlags = Arrays.asList("-f", "-b", "-m", "-e", "-u");

    public static void main(String[] args) throws IOException {
        double errorRate;
        Communicator communicator;
        Map<String, String> arguments = collectArgumentMap(args);

        if (arguments == null) { return; }

        if(arguments.containsKey("-u")) {
            communicator = new UncodedCommunicator(new Channel());
        }
        else {
            int m = parseM(arguments);
            communicator = new CodedCommunicator(new Channel(), new Encoder(new ReedMullerCodeGenerator()), new Decoder(), m);
        }
        errorRate = parseErrorRate(arguments);

        if(arguments.containsKey("-f")) {
            String sourcePath = arguments.get("-f");
            File fi = new File(sourcePath);
            byte[] fileContent = Files.readAllBytes(fi.toPath());
            byte[] receiveBytes = communicator.transmitAndReceiveCodedBytes(fileContent, errorRate);
            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(sourcePath + ".out"))) {
                out.write(receiveBytes);
            }
        }
        else if(arguments.containsKey("-b")) {
            String input = arguments.get("-b");
            if(Pattern.matches("^[0,1]+$", input)) {
                boolean[] vector = new boolean[input.length()];
                for(int i = 0; i < input.length(); i++) {
                    vector[i] = input.charAt(i) == '1';
                }
                boolean[] decoded = communicator.transmitAndReceiveCodedBits(vector, errorRate);
                for (Boolean aDecoded : decoded) {
                    System.out.print(aDecoded ? 1 : 0);
                }
                System.out.println();
            }
        }
        else {
            byte[] decoded = communicator.transmitAndReceiveCodedBytes(arguments.get("args").getBytes(), errorRate);
            System.out.println(new String(decoded));
        }
    }

    private static double parseErrorRate(Map<String, String> arguments) {
        try {
            return Double.parseDouble(arguments.get("-e"));
        }
        catch (NumberFormatException e) {
            System.err.println(String.format("\"-e\" should be decimal number between 0.0 and 0.100, but is %s%n", arguments.get("-e")));
            return -1;
        }
    }

    private static int parseM(Map<String, String> arguments) {
        try {
            return Integer.parseInt(arguments.get("-m"));
        }
        catch (NumberFormatException e) {
            System.err.println(String.format("Incorrect flags, \"-m\" should be integer, but is %s%n", arguments.get("-m")));
            return -1;
        }
    }

    private static Map<String, String> collectArgumentMap(String[] args) {
        if(args.length < 4) {
            System.err.println("Not enough arguments. Example arguments '-m 4 -e 0.1 hello' or '-e 0.1 -u this is an uncoded message'");
            return null;
        }
        Map<String, String> arguments = parseArgs(args);

        if(arguments.size() < 3) {
            System.err.println("Not enough arguments, should contain -m and -e, or -u flags and something to encode");
            return null;
        }
        if((!arguments.containsKey("-m") || !arguments.containsKey("-e")) && !arguments.containsKey("-u")) {
            System.err.println("Should contain -m and -e, or -u flags");
            return null;
        }
        if(arguments.containsKey("-m") && arguments.containsKey("-e") && arguments.containsKey("-u")) {
            System.err.println("Should contain -m and -e, or -u flags, now contains all three");
            return null;
        }
        return arguments;
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();

        List<String> argList = new ArrayList<>(Arrays.asList(args));
        for (String argFlag: argumentFlags) {
            int index = argList.indexOf(argFlag);
            if (index >= 0) {
                String argValue = !argFlag.equals("-u") && argList.size() > index + 1 ? argList.get(index + 1) : null;
                map.put(argFlag, argValue);
                argList.remove(argFlag);
                argList.remove(argValue);
            }
        }
        map.put("args", StringUtils.join(argList, " "));
        return map;
    }
}
