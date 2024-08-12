package org.capt.world.counters;

public class LineCounter implements Counter {
    private int count = 0;
    private boolean hasCarriageReturn = false;

    @Override
    public void update(int codepoint) {
        if (Character.isBmpCodePoint(codepoint)) {
            char ch = (char) codepoint;

            // source: https://stackoverflow.com/a/1761086
            if (ch == '\r') {
                hasCarriageReturn = true;
            } else if (ch == '\n') {
                if (hasCarriageReturn) {
                    hasCarriageReturn = false;
                }
                count++;
            } else if (hasCarriageReturn) {
                hasCarriageReturn = false;
                count++;
            }
        }
    }

    @Override
    public long count() {
        return count;
    }
}
