package org.capt.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ParsedArgs(Set<CommandLineOption> options, List<String> files) {

    public static ParsedArgs parse(String[] args) {
        Set<CommandLineOption> options = new HashSet<>();
        List<String> files = new ArrayList<>();
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