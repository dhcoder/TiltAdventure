package dhcoder.support.collection;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class ArrayMapTest {

    @Test
    public void createMapWithIntegerKeys() {
        ArrayMap<Integer, String> numericStringMap = new ArrayMap<Integer, String>();

        numericStringMap.put(1, "one");
        numericStringMap.put(2, "two");
        numericStringMap.put(3, "three");
        numericStringMap.put(4, "four");
        numericStringMap.put(5, "five");
        numericStringMap.put(6, "six");
        numericStringMap.put(7, "seven");
        numericStringMap.put(8, "eight");
        numericStringMap.put(9, "nine");

        assertThat(numericStringMap.getSize(), equalTo(9));

        assertThat(numericStringMap.get(1), equalTo("one"));
        assertThat(numericStringMap.get(2), equalTo("two"));
        assertThat(numericStringMap.get(3), equalTo("three"));
        assertThat(numericStringMap.get(4), equalTo("four"));
        assertThat(numericStringMap.get(5), equalTo("five"));
        assertThat(numericStringMap.get(6), equalTo("six"));
        assertThat(numericStringMap.get(7), equalTo("seven"));
        assertThat(numericStringMap.get(8), equalTo("eight"));
        assertThat(numericStringMap.get(9), equalTo("nine"));

        assertThat(numericStringMap.remove(1), equalTo("one"));
        assertThat(numericStringMap.remove(2), equalTo("two"));
        assertThat(numericStringMap.remove(3), equalTo("three"));
        assertThat(numericStringMap.remove(4), equalTo("four"));
        assertThat(numericStringMap.remove(5), equalTo("five"));
        assertThat(numericStringMap.remove(6), equalTo("six"));
        assertThat(numericStringMap.remove(7), equalTo("seven"));
        assertThat(numericStringMap.remove(8), equalTo("eight"));
        assertThat(numericStringMap.remove(9), equalTo("nine"));

        assertThat(numericStringMap.getSize(), equalTo(0));
    }

    @Test
    public void createMapWithStringKeys() {
        ArrayMap<String, Integer> stringNumericMap = new ArrayMap<String, Integer>();

        stringNumericMap.put("one", 1);
        stringNumericMap.put("two", 2);
        stringNumericMap.put("three", 3);
        stringNumericMap.put("four", 4);
        stringNumericMap.put("five", 5);
        stringNumericMap.put("six", 6);
        stringNumericMap.put("seven", 7);
        stringNumericMap.put("eight", 8);
        stringNumericMap.put("nine", 9);

        assertThat(stringNumericMap.getSize(), equalTo(9));

        assertThat(stringNumericMap.get("one"), equalTo(1));
        assertThat(stringNumericMap.get("two"), equalTo(2));
        assertThat(stringNumericMap.get("three"), equalTo(3));
        assertThat(stringNumericMap.get("four"), equalTo(4));
        assertThat(stringNumericMap.get("five"), equalTo(5));
        assertThat(stringNumericMap.get("six"), equalTo(6));
        assertThat(stringNumericMap.get("seven"), equalTo(7));
        assertThat(stringNumericMap.get("eight"), equalTo(8));
        assertThat(stringNumericMap.get("nine"), equalTo(9));

        assertThat(stringNumericMap.remove("one"), equalTo(1));
        assertThat(stringNumericMap.remove("two"), equalTo(2));
        assertThat(stringNumericMap.remove("three"), equalTo(3));
        assertThat(stringNumericMap.remove("four"), equalTo(4));
        assertThat(stringNumericMap.remove("five"), equalTo(5));
        assertThat(stringNumericMap.remove("six"), equalTo(6));
        assertThat(stringNumericMap.remove("seven"), equalTo(7));
        assertThat(stringNumericMap.remove("eight"), equalTo(8));
        assertThat(stringNumericMap.remove("nine"), equalTo(9));

        assertThat(stringNumericMap.getSize(), equalTo(0));
    }
}