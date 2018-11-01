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

    private static List<String> argumentFlags = Arrays.asList("-f", "-b", "-m", "-e");

    public static void main(String[] args) throws IOException {
        Communicator codedCommunicator = new CodedCommunicator(new Channel(), new Encoder(new ReedMullerCodeGenerator()), new Decoder());
        Communicator uncodedCommunicator = new UncodedCommunicator(new Channel());

        Map<String, String> arguments = collectArgumentMap(args);
        if (arguments == null) {
            return;
        }

        int m;
        double errorRate;
        try {
            m = Integer.parseInt(arguments.get("-m"));
            errorRate = Double.parseDouble(arguments.get("-e"));
        }
        catch (NumberFormatException e) {
            System.err.println(String.format("Incorrect flags, \"-m\" should be integer, but is %s%n" +
                    "\"-e\" should be decimal number between 0 and 100, but is %s%n",arguments.get("-m"), arguments.get("-e")));
            return;
        }

        if(arguments.containsKey("-f")) {
            String sourcePath = arguments.get("-f");
            File fi = new File(sourcePath);
            byte[] fileContent = Files.readAllBytes(fi.toPath());
            byte[] receiveBytes = codedCommunicator.transmitAndReceiveCodedBytes(fileContent, m, errorRate);
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
                boolean[] decoded = codedCommunicator.transmitAndReceiveCodedBits(vector, m, errorRate);
                for (Boolean aDecoded : decoded) {
                    System.out.print(aDecoded ? 1 : 0);
                }
                System.out.println();
            }
        }
        else {
            byte[] decoded = codedCommunicator.transmitAndReceiveCodedBytes(arguments.get("args").getBytes(), m, errorRate);
            System.out.println(new String(decoded));
        }
    }

    private static Map<String, String> collectArgumentMap(String[] args) {
        if(args.length < 5) {
            System.err.println("Not enough arguments. Example arguments '-m 4 -e 0.1 hello'");
            return null;
        }
        Map<String, String> arguments = parseArgs(args);

        if(arguments.size() < 3) {
            System.err.println("Not enough arguments, should contain -m and -e flags and something to encode");
            return null;
        }
        if(!arguments.containsKey("-m") || !arguments.containsKey("-e")) {
            System.err.println("Should contain -m and -e flags");
            return null;
        }
        return arguments;
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();

        List<String> argList = new ArrayList<>(Arrays.asList(args));
        for (String argFlag: argumentFlags) {
            int index = argList.indexOf(argFlag);
            if (index >= 0 && argList.size() > index + 1) {
                String argValue = argList.get(index + 1);
                map.put(argFlag, argValue);
                argList.remove(argFlag);
                argList.remove(argValue);
            }
        }
        map.put("args", StringUtils.join(argList, " "));
        return map;
    }
}
