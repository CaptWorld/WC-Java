package org.capt.world;

import java.io.InputStream;

public class WC {
    public static void main(String[] args) {
        ParsedArgs parsedArgs = ParsedArgs.parse(args);

        InputStream[] inputStreams = Utils.getInputStreams(parsedArgs.files());

        long[] counts = Utils.count(parsedArgs.options(), inputStreams);

        for (var count : counts) {
            System.out.print(count + " ");
        }
        System.out.println(parsedArgs.files().length == 1 ? parsedArgs.files()[0] : "");
    }
}