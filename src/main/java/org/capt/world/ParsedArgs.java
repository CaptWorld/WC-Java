package org.capt.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ParsedArgs(Set<CommandLineOption> options, List<String> files) {

    public static ParsedArgs parse(String[] args) {
        Set<CommandLineOption> options = new HashSet<>();
        List<String> files = new ArrayList<>();

        for (String arg : args) {
            Set<CommandLineOption> parsedOptions = CommandLineOption.getEnums(arg);
            if (parsedOptions.isEmpty()) {
                if (arg.startsWith("-") && !arg.equals("-")) {
                    throw new RuntimeException("Unknown option: " + arg);
                } else {
                    files.add(arg);
                }
            } else {
                options.addAll(parsedOptions);
            }
        }

        if (options.isEmpty()) {
            options = Set.of(
                    CommandLineOption.LINES,
                    CommandLineOption.WORDS,
                    CommandLineOption.BYTES
            );
        }

        return new ParsedArgs(options, files);
    }

}