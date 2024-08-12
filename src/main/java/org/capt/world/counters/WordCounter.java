package org.capt.world.counters;

import java.io.InputStream;
import java.util.Scanner;

public class WordCounter implements Counter {
    private long count = 0;
    private boolean hasWhitespaceCharacter = true;

    @Override
    public void update(int codepoint) {
        if (Character.isWhitespace(codepoint)) {
            hasWhitespaceCharacter = true;
        } else {
            if (hasWhitespaceCharacter) {
                hasWhitespaceCharacter = false;
                count++;
            }
        }
    }

    @Override
    public long count() {
        return count;
    }
}
