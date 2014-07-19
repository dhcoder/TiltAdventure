package dhcoder.support.immutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class that wraps an Opt, offering read-only access to it.
 */
public final class ImmutableList<T> extends Immutable<List<T>> implements Iterable<T> {

    public ImmutableList(final List<T> list) {
        super(list);
    }

    public List<T> toMutable() {
        return new ArrayList<T>(wrappedMutable);
    }

    @Override
    public void copyInto(final List<T> target) {
        target.clear();
        target.addAll(wrappedMutable);
    }

    @Override
    public Iterator<T> iterator() {
        return wrappedMutable.iterator();
    }

    @Override
    public void forEach(final Consumer<? super T> action) {
        wrappedMutable.forEach(action);
    }

    public int size() { return wrappedMutable.size(); }

    public boolean isEmpty() { return wrappedMutable.isEmpty(); }

    public boolean contains(final T item) { return wrappedMutable.contains(item); }

    public T[] toArray() { return (T[])wrappedMutable.toArray(); }

    public T[] toArray(final T[] a) { return wrappedMutable.toArray(a); }

    public boolean containsAll(final Collection<? extends T> c) { return wrappedMutable.containsAll(c); }

    public T get(final int index) { return wrappedMutable.get(index); }

    public int indexOf(final T item) { return wrappedMutable.indexOf(item); }

    public int lastIndexOf(final T item) { return wrappedMutable.lastIndexOf(item); }

    public ImmutableList<T> subList(final int fromIndex, final int toIndex) {
        return new ImmutableList<T>(wrappedMutable.subList(fromIndex, toIndex));
    }

    @Override
    public int hashCode() {
        return wrappedMutable.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        ImmutableList that = (ImmutableList)o;

        if (!wrappedMutable.equals(that.wrappedMutable)) { return false; }

        return true;
    }

}
