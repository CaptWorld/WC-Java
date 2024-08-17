package org.capt.world;

import java.util.List;

public class WC {
    public static void main(String[] args) {
        ParsedArgs parsedArgs = ParsedArgs.parse(args);

        List<WCInput> wcInputs = Utils.getInputStreams(parsedArgs.files());

        List<WCOutput> wcOutputs = Utils.countMultipleInput(parsedArgs.options(), wcInputs);

        for (WCOutput wcOutput : wcOutputs) {
            System.out.println(wcOutput);
        }
    }
}