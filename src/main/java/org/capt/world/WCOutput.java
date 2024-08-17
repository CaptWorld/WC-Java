package org.capt.world;

import java.util.Arrays;

public record WCOutput(String fileName, long[] counts) {
    public WCOutput {
        for (int i = 1; i < counts.length; i++) {
            if (counts[i - 1] > counts[i]) {
                throw new RuntimeException("counts: " + Arrays.toString(counts) + " is not sorted");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (long count : counts) {
            result.append(count).append(" ");
        }
        result.append(fileName);
        return result.toString();
    }
}
