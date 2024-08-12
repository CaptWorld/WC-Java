package org.capt.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

// TODO: Multiple files so different counters
// TODO: Modularize better
// TODO: Improve README.md
// TODO: Add Tests using JUnit
// TODO: (optional) Binary Executable creation steps

public class Main {

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

    public static void main(String[] args) {
        LinkedHashSet<CommandLineOption> options = new LinkedHashSet<>();
        List<String> files = new LinkedList<>();
        for (int i = 0; i < args.length; ) {
            var parsedOptions = CommandLineOption.getEnums(args[i]);
            if (parsedOptions.isEmpty()) {
                if (args[i].startsWith("-")) {
                    throw new RuntimeException("Unknown option: " + args[i]);
                } else {
                    files.add(args[i]);
                }
            } else {
                options.addAll(parsedOptions);
            }
            i++;
        }

        List<InputStream> inputStreams = files.stream().map(Main::getInputStream).toList();
        if (inputStreams.isEmpty()) {
            inputStreams = List.of(System.in);
        }

        if (options.isEmpty()) {
            options = new LinkedHashSet<>(List.of(CommandLineOption.LINES, CommandLineOption.WORDS, CommandLineOption.BYTES));
        }

        for (var in : inputStreams) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
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
                        for (var option : options) {
                            option.counter().update(codepoint);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create BufferedReader from InputStream", e);
            }
        }

        for (var option : options) {
            System.out.print(option.counter().count() + " ");
        }
        System.out.println(files.size() == 1 ? files.getFirst() : "");
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
}