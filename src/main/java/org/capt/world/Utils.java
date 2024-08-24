package org.capt.world;

import org.capt.world.counters.Counter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {

    public static List<WCInput> getInputStreams(List<String> files) {
        if (files.isEmpty()) {
            return List.of(new WCInput("", System.in));
        } else {
            return files.stream().map(file -> new WCInput(file, Utils.getInputStream(file))).toList();
        }
    }

    public static InputStream getInputStream(String fileName) {
        if (fileName.equals("-")) {
            return System.in;
        }

        Path filePath;
        try {
            filePath = Path.of(fileName).toRealPath();
        } catch (NoSuchFileException e) {
            throw new RuntimeException("Failed to find the file: " + fileName, e);
        } catch (IOException e) {
            throw new RuntimeException("IO failure while determining the validity of the path: " + fileName, e);
        }

        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from file: " + filePath, e);
        }
    }

    public static List<WCOutput> countMultipleInput(Set<CommandLineOption> options, List<WCInput> wcInputs) {
        List<WCOutput> wcOutputs = wcInputs.stream().map(wcInput -> Utils.countSingleInput(options, wcInput)).collect(Collectors.toList());
        if (wcOutputs.size() > 1) {
            long[] totalCount = new long[wcOutputs.getFirst().counts().length];
            for (WCOutput wcOutput : wcOutputs) {
                for (int i = 0; i < totalCount.length; i++) {
                    totalCount[i] += wcOutput.counts()[i];
                }
            }
            wcOutputs.add(new WCOutput("total", totalCount));
        }
        return wcOutputs;
    }

    /**
     * A single codepoint string literal can result in
     * char array with its size in the range [1, 2]
     * <p>
     * if (charArr.length == 1)
     * eg: "世"
     * name: Basic Multilingual Plane (BMP) character
     * </p>
     * <p>
     * if (charArr.length == 2)
     * eg: 🙐
     * name: supplementary character
     * charArr[0]: higher surrogate
     * charArr[1] -> lower surrogate
     * </p>
     * <p>
     * Depending on the chosen encoding (here, using default),
     * the charArr can be encoded to arbitrary size
     * <p>
     * UTF-8
     * BMP char example -> 3 bytes
     * Supplementary char example -> 4 bytes
     * </p>
     * <p>
     * UTF-16
     * BMP char example -> 4 bytes
     * Supplementary char example -> 6 bytes
     * </p>
     */
    private static WCOutput countSingleInput(Set<CommandLineOption> options, WCInput wcInput) {
        long[] counts = new long[options.size()];

        Counter[] counters = options.stream().map(CommandLineOption::counter).toArray(Counter[]::new);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(wcInput.inputStream()))) {
            char highSurrogate = 0;
            int intCh;
            while ((intCh = reader.read()) != -1) {
                int codepoint = -1;
                char ch = (char) intCh;
                if (Character.isHighSurrogate(ch)) {
                    highSurrogate = ch;
                } else if (Character.isLowSurrogate(ch)) {
                    codepoint = Character.toCodePoint(highSurrogate, ch);
                } else {
                    codepoint = intCh;
                }
                if (codepoint != -1) {
                    for (Counter counter : counters) {
                        counter.update(codepoint);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create BufferedReader from InputStream", e);
        }

        for (int i = 0; i < counters.length; i++) {
            counts[i] = counters[i].count();
        }

        Arrays.sort(counts);
        if (wcInput.inputStream() == System.in) {
            System.out.println("EOF is reached for Console Input");
        }
        return new WCOutput(wcInput.fileName(), counts);
    }
}
