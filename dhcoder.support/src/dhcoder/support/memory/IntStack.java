package dhcoder.support.memory;

import java.util.EmptyStackException;

/**
 * A stack of primitive integers. Use this to avoid the cost of boxing / unboxing ints to Integers.
 */
public final class IntStack {

    private static final int DEFAULT_CAPACITY = 4;

    private int[] values;
    private int size;

    public IntStack() {
        this(DEFAULT_CAPACITY);
    }

    public IntStack(final int capacity) {
        values = new int[capacity];
        size = 0;
    }

    public void push(final int value) {
        if (size == values.length) {
            int[] oldValues = values;
            values = new int[values.length * 2];
            System.arraycopy(oldValues, 0, values, 0, oldValues.length);
        }

        values[size] = value;
        ++size;
    }

    public int peek() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        return values[size - 1];
    }

    public int pop() {
        int value = peek();
        size--;
        return value;
    }
}
