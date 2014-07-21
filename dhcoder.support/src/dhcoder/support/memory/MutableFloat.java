package dhcoder.support.memory;

/**
 * Simple float wrapper, useful for pre-allocation and avoiding an auto-boxing penalty.
 */
public class MutableFloat extends Number {
    private float value;

    public MutableFloat() {}

    public MutableFloat(final float value) {
        set(value);
    }

    public void set(final float value) {
        this.value = value;
    }

    public float get() {
        return value;
    }

    @Override
    public int intValue() {
        return (int)value;
    }

    @Override
    public long longValue() {
        return (long)value;
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
        return Float.toString(value);
    }
}
