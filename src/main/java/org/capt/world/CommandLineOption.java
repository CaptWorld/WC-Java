package org.capt.world;

import org.capt.world.counters.*;

import java.io.InputStream;
import java.util.*;

public enum CommandLineOption {

    BYTES(new ByteCounter(), "-c", "--bytes"),
    LINES(new LineCounter(), "-l", "--lines"),
    WORDS(new WordCounter(), "-w", "--words"),
    CHARACTERS(new CharacterCounter(), "-m", "--chars");

    private final static Map<String, CommandLineOption> OPTION_MAPPING = new HashMap<>();

    static {
        for (var option : CommandLineOption.values()) {
            for (String string : option.optionStrings) {
                if (OPTION_MAPPING.putIfAbsent(string, option) != null) {
                    throw new RuntimeException("Duplicate option string " + string);
                }
            }
        }
    }

    private final Counter counter;
    private final String[] optionStrings;

    CommandLineOption(Counter counter, String... optionStrings) {
        this.counter = counter;
        if (optionStrings == null) {
            throw new RuntimeException("No strings for the option is provided");
        } else if (optionStrings.length > 2) {
            throw new RuntimeException(
                    String.format(
                            "Too many strings: %s for the option is provided",
                            Arrays.toString(optionStrings
                            )
                    )
            );
        }
        this.optionStrings = optionStrings;
    }

    public Counter counter() {
        return counter;
    }

    public static LinkedHashSet<CommandLineOption> getEnums(String optionString) {
        LinkedHashSet<CommandLineOption> options = new LinkedHashSet<>();
        var option = CommandLineOption.OPTION_MAPPING.get(optionString);
        if (option == null) {
            if (optionString.startsWith("-") && !optionString.startsWith("--")) {
                List<String> optionStrings = parseMultipleOptions(optionString);
                for (var opString : optionStrings) {
                    option = CommandLineOption.OPTION_MAPPING.get(opString);
                    if (option == null) {
                        return new LinkedHashSet<>();
                    } else {
                        options.add(option);
                    }
                }
            }
        } else {
            options.add(option);
        }
        return options;
    }

    private static List<String> parseMultipleOptions(String optionString) {
        List<String> optionStrings = new ArrayList<>();
        for (int i = 1; i < optionString.length(); i++) {
            optionStrings.add("-" + optionString.charAt(i));
        }
        return optionStrings;
    }
}