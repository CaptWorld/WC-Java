package org.capt.world;

import java.util.Arrays;
import java.util.Objects;

public record WCOutput(String fileName, long[] counts) {
    public WCOutput {
        for (int i = 1; i < counts.length; i++) {
            if (counts[i - 1] > counts[i]) {
                throw new RuntimeException("counts: " + Arrays.toString(counts) + " is not sorted");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WCOutput wcOutput = (WCOutput) o;
        return Objects.deepEquals(counts, wcOutput.counts) && Objects.equals(fileName, wcOutput.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, Arrays.hashCode(counts));
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
