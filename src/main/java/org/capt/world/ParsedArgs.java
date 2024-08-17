package org.capt.world;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public record ParsedArgs(CommandLineOption[] options, String[] files) {

    public static ParsedArgs parse(String[] args) {
        LinkedHashSet<CommandLineOption> optionsSet = new LinkedHashSet<>();
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
                optionsSet.addAll(parsedOptions);
            }
            i++;
        }

        CommandLineOption[] options;

        if (optionsSet.isEmpty()) {
            options = new CommandLineOption[]{
                    CommandLineOption.LINES,
                    CommandLineOption.WORDS,
                    CommandLineOption.BYTES
            };
        } else {
            options = optionsSet.toArray(CommandLineOption[]::new);
        }

        return new ParsedArgs(options, files.toArray(String[]::new));
    }

}