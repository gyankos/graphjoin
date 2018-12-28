package it.giacomobergami.usergenerator.utils;

public class RandomWithJump {

    private long initialSeed;

    public RandomWithJump(long initialSeed) {
        this.initialSeed = initialSeed;
    }

    public RandomWithJump() {
        this(0L);
    }

    public long int64(long jump) {
        long v = (jump + initialSeed) * 3935559000370003845L + 2691343689449507681L;
        v ^= v >> 21;
        v ^= v << 37;
        v ^= v >> 4;
        v *= 4768777513237032717L;
        v ^= v << 20;
        v ^= v >> 41;
        v ^= v << 5;
        return v;
    }

    public double nextDouble(long jump) {
        return 5.42101086242752217E-20 * Double.valueOf(Long.toUnsignedString(int64(jump)));
    }

    public int nextInt(long jump, int maxIntExcluded) {
        return (int)Math.floor(nextDouble(jump)*((double)(maxIntExcluded-1)));
    }

    public int nextInt(int maxIntExcluded) {
        return (int)Math.floor(nextDouble(initialSeed++)*((double)(maxIntExcluded-1)));
    }

    public boolean nextBoolean() {
        return Long.remainderUnsigned(int64(initialSeed++), 2) == 0;
    }

    public boolean nextBoolean(long jump) {
        return Long.remainderUnsigned(int64(jump), 2) == 0;
    }

}
