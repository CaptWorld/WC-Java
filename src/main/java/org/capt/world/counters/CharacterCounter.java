package org.capt.world.counters;

public class CharacterCounter implements Counter {
    private long count = 0;

    @Override
    public void update(int codepoint) {
        count++;
    }

    @Override
    public long count() {
        return count;
    }
}
