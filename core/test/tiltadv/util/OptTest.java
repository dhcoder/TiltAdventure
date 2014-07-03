package tiltadv.util;

import org.junit.Test;
import tiltadv.util.lambda.Action0;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static tiltadv.TestUtils.assertException;

public final class OptTest {

    private static final String DUMMY_VALUE = "dummy";

    @Test
    public void defaultOptionalHasNoValue() {
        Opt<String> stringOpt = new Opt<String>();
        assertThat(stringOpt.hasValue(), equalTo(false));
    }

    @Test
    public void createOptionalWithValueWorks() {
        Opt<String> stringOpt = new Opt<String>(DUMMY_VALUE);
        assertThat(stringOpt.hasValue(), equalTo(true));
        assertThat(stringOpt.value(), equalTo(DUMMY_VALUE));

        String nullString = null;
        Opt<String> emptyStringOpt = new Opt<String>((String)null);
        assertThat(emptyStringOpt.hasValue(), equalTo(false));
    }

    @Test
    public void createOptionalByCopyWorks() {
        Opt<String> emptyStringOpt = new Opt<String>();
        Opt<String> stringOpt = new Opt<String>(DUMMY_VALUE);
        Opt<String> emtpyStringOptCopy = new Opt<String>(emptyStringOpt);
        Opt<String> stringOptCopy = new Opt<String>(stringOpt);

        assertThat(emtpyStringOptCopy.hasValue(), equalTo(false));
        assertThat(stringOptCopy.hasValue(), equalTo(true));
        assertThat(stringOptCopy.value(), equalTo(DUMMY_VALUE));
    }

    @Test
    public void settingOptionalValueWorks() {
        Opt<String> stringOpt = new Opt<String>();
        assertThat(stringOpt.hasValue(), equalTo(false));

        stringOpt.set(DUMMY_VALUE);
        assertThat(stringOpt.hasValue(), equalTo(true));
        assertThat(stringOpt.value(), equalTo(DUMMY_VALUE));

        stringOpt.set(null);
        assertThat(stringOpt.hasValue(), equalTo(false));
    }

    @Test
    public void clearOptionalWorks() {
        Opt<String> stringOpt = new Opt<String>(DUMMY_VALUE);
        assertThat(stringOpt.hasValue(), equalTo(true));

        stringOpt.clear();
        assertThat(stringOpt.hasValue(), equalTo(false));
    }

    @Test
    public void testOptionalEquality() {
        Opt<String> stringOpt = new Opt<String>(DUMMY_VALUE);
        Opt<String> stringOptDuplicate = new Opt<String>(DUMMY_VALUE);
        Opt<String> emptyValue = new Opt<String>();
        assertThat(stringOpt.equals(stringOptDuplicate), equalTo(true));
        assertThat(stringOpt.equals(emptyValue), equalTo(false));
        assertThat(stringOpt.hashCode(), equalTo(stringOptDuplicate.hashCode()));
    }

    @Test
    public void getValueWithoutValueThrowsException() {
        final Opt<String> emptyStringOpt = new Opt<String>();
        assertException("Can't value a value from a valueless optional", IllegalStateException.class, new Action0() {
            @Override
            public void run() {
                String result = emptyStringOpt.value();
            }
        });
    }
}