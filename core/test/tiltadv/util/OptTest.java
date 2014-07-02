package tiltadv.util;

import org.junit.Test;
import tiltadv.TestUtils;
import tiltadv.util.lambda.Action0;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static tiltadv.TestUtils.assertException;

public class OptTest {

    private static final String DUMMY_VALUE = "dummy";

    @Test
    public void defaultOptionalHasNoValue() {
        Opt<String> emptyValue = new Opt<String>();
        assertThat(emptyValue.hasValue(), equalTo(false));
    }

    @Test
    public void createOptionalWithValueWorks() {
        Opt<String> stringValue = new Opt<String>(DUMMY_VALUE);
        assertThat(stringValue.hasValue(), equalTo(true));
        assertThat(stringValue.getValue(), equalTo(DUMMY_VALUE));
    }

    @Test
    public void createOptionalByCopyWorks() {
        Opt<String> emptyValue = new Opt<String>();
        Opt<String> stringValue = new Opt<String>(DUMMY_VALUE);

        Opt<String> emptyValueCopy = new Opt<String>(emptyValue);
        Opt<String> stringValueCopy = new Opt<String>(stringValue);

        assertThat(emptyValueCopy.hasValue(), equalTo(false));
        assertThat(stringValueCopy.hasValue(), equalTo(true));
        assertThat(stringValueCopy.getValue(), equalTo(DUMMY_VALUE));
    }

    @Test
    public void clearOptionalWorks() {
        Opt<String> stringValue = new Opt<String>(DUMMY_VALUE);
        assertThat(stringValue.hasValue(), equalTo(true));
        stringValue.clearValue();
        assertThat(stringValue.hasValue(), equalTo(false));
    }

    @Test
    public void testOptionalEquality() {
        Opt<String> stringValue = new Opt<String>(DUMMY_VALUE);
        Opt<String> stringValueDuplicate = new Opt<String>(DUMMY_VALUE);
        Opt<String> emptyValue = new Opt<String>();

        assertThat(stringValue.equals(stringValueDuplicate), equalTo(true));
        assertThat(stringValue.equals(emptyValue), equalTo(false));

        assertThat(stringValue.hashCode(), equalTo(stringValueDuplicate.hashCode()));
    }

    @Test
    public void creatingOptionalWithNullThrowsException() {
        assertException("Creating an optional with null isn't allowed", IllegalArgumentException.class, new Action0() {
            @Override
            public void run() {
                String nullString = null;
                Opt<String> stringValue = new Opt<String>(nullString);
            }
        });
    }

    @Test
    public void settingOptionalToNullThrowsException() {
        final Opt<String> stringValue = new Opt<String>(DUMMY_VALUE);
        assertException("Setting an optional to null isn't allowed", IllegalArgumentException.class, new Action0() {
            @Override
            public void run() {
                stringValue.setValue(null);
            }
        });
    }

    @Test
    public void getValueWithoutValueThrowsException() {
        final Opt<String> emptyValue = new Opt<String>();
        assertException("Can't get a value from a valueless optional", IllegalStateException.class, new Action0() {
            @Override
            public void run() {
                String result = emptyValue.getValue();
            }
        });
    }

}