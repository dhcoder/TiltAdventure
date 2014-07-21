package dhcoder.support.memory;

/**
 * Simple int wrapper, useful for pre-allocation and avoiding an auto-boxing penalty.
 */
public class MutableInt extends Number {
    private int value;

    public MutableInt() {}

    public MutableInt(final int value) {
        set(value);
    }

    public void set(final int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
