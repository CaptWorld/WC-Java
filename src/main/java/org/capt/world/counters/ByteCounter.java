package org.capt.world.counters;

import java.nio.charset.Charset;

public class ByteCounter implements Counter {
    private long count = 0;

    @Override
    public void update(int codepoint) {
        count += getByteCount(codepoint);
    }

    private static int getByteCount(int codepoint) {
        char[] chars = Character.toChars(codepoint);
        String s = new String(chars);
        return s.getBytes(Charset.defaultCharset()).length;
    }

    @Override
    public long count() {
        return count;
    }
}
