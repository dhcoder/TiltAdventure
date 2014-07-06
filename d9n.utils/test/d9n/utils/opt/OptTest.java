package d9n.utils.opt;

import org.junit.Test;
import d9n.utils.lambda.Action;

import static d9n.utils.TestUtils.assertException;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public final class OptTest {

    private static final String DUMMY_VALUE = "dummy";

    @Test
    public void createOptionalWithNoValueWorks() {
        Opt<String> stringOpt = Opt.withNoValue();
        assertThat(stringOpt.hasValue(), equalTo(false));
    }

    @Test
    public void createOptionalWithValueWorks() {
        Opt<String> stringOpt = Opt.of(DUMMY_VALUE);
        assertThat(stringOpt.hasValue(), equalTo(true));
        assertThat(stringOpt.value(), equalTo(DUMMY_VALUE));
    }

    @Test
    public void createOptionalWithNullValueWorks() {
        String nullString = null;
        Opt<String> emptyStringOpt = Opt.ofNullable(nullString);
        assertThat(emptyStringOpt.hasValue(), equalTo(false));
    }

    @Test
    public void settingOptionalValueWorks() {
        Opt<String> stringOpt = Opt.withNoValue();
        assertThat(stringOpt.hasValue(), equalTo(false));

        stringOpt.set(DUMMY_VALUE);
        assertThat(stringOpt.hasValue(), equalTo(true));
        assertThat(stringOpt.value(), equalTo(DUMMY_VALUE));

        stringOpt.set(null);
        assertThat(stringOpt.hasValue(), equalTo(false));
    }

    @Test
    public void clearOptionalWorks() {
        Opt<String> stringOpt = Opt.of(DUMMY_VALUE);
        assertThat(stringOpt.hasValue(), equalTo(true));

        stringOpt.clear();
        assertThat(stringOpt.hasValue(), equalTo(false));
    }

    @Test
    public void testOptionalEquality() {
        Opt<String> stringOpt = Opt.of(DUMMY_VALUE);
        Opt<String> stringOptDuplicate = Opt.of(DUMMY_VALUE);
        Opt<String> emptyValue = Opt.withNoValue();

        assertThat(stringOpt.equals(stringOptDuplicate), equalTo(true));
        assertThat(stringOpt.equals(emptyValue), equalTo(false));
        assertThat(stringOpt.hashCode(), equalTo(stringOptDuplicate.hashCode()));
    }

    @Test
    public void getValueWithoutValueThrowsException() {
        final Opt<String> emptyStringOpt = Opt.withNoValue();
        assertException("Can't get a value from a valueless optional", IllegalStateException.class, new Action() {
            @Override
            public void run() {
                String result = emptyStringOpt.value();
            }
        });
    }

    @Test
    public void optOfWithNullValueThrowsException() {

        assertException("Can't pass null into Opt.of", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                String nullValue = null;
                Opt.of(nullValue);
            }
        });
    }

}