package org.capt.world;

import org.capt.world.counters.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public enum CommandLineOption {

    BYTES(ByteCounter.class, "-c", "--bytes"),
    LINES(LineCounter.class, "-l", "--lines"),
    WORDS(WordCounter.class, "-w", "--words"),
    CHARACTERS(CharacterCounter.class, "-m", "--chars");

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

    private final Class<? extends Counter> counterClass;
    private final String[] optionStrings;

    CommandLineOption(Class<? extends Counter> counterClass, String... optionStrings) {
        this.counterClass = counterClass;
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
        try {
            return counterClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException("Failed to create instance of class: " + counterClass.getSimpleName(), e);
        }
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