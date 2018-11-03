package com.joklek;

import com.joklek.communicator.CodedCommunicator;
import com.joklek.communicator.Communicator;
import com.joklek.communicator.ReedMullerCodeGenerator;
import com.joklek.communicator.UncodedCommunicator;
import com.joklek.communicator.elements.Channel;
import com.joklek.communicator.elements.Decoder;
import com.joklek.communicator.elements.Encoder;
import com.joklek.fxgui.GuiLauncher;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

import static com.joklek.errors.ErrorMessages.ERROR_RATE_NOT_DECIMAL;
import static com.joklek.errors.ErrorMessages.M_NOT_INTEGER;

@SuppressWarnings("squid:S106")
public class Main {

    private static List<String> argumentFlags = Arrays.asList("-f", "-b", "-m", "-e", "-u", "-i");
    private static Map<String, WorkingMode> workingModeFlags;
    static {
        Map<String, WorkingMode> aMap = new HashMap<>();
        aMap.put("-f", WorkingMode.FILE);
        aMap.put("-i", WorkingMode.BITMAP);
        aMap.put("-b", WorkingMode.BINARY);
        workingModeFlags = Collections.unmodifiableMap(aMap);
    }

    public static void main(String[] args) throws IOException {

        if(args[0].equals("-gui")) {
            GuiLauncher.main(new String[0]);
            return;
        }

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

        Operator operator = new Operator();


        WorkingMode workingMode = null;
        String payload = "";

        for(Map.Entry<String, WorkingMode> entry: workingModeFlags.entrySet()) {
            if (arguments.containsKey(entry.getKey())) {
                workingMode = entry.getValue();
                payload = arguments.get(entry.getKey());
            }
        }
        if (payload == null) {
            workingMode = WorkingMode.TEXT;
            payload = arguments.get("args");
        }

        operator.sendWithCommunicator(workingMode, payload, communicator, errorRate);
    }

    private static double parseErrorRate(Map<String, String> arguments) {
        String errorRateString = arguments.get("-e");
        try {
            return Double.parseDouble(errorRateString);
        }
        catch (NumberFormatException e) {
            System.err.println(ERROR_RATE_NOT_DECIMAL(errorRateString));
            return -1;
        }
    }

    private static int parseM(Map<String, String> arguments) {
        String mString = arguments.get("-m");
        try {
            return Integer.parseInt(mString);
        }
        catch (NumberFormatException e) {
            System.err.println(M_NOT_INTEGER(mString));
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
