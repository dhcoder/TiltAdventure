package tiltadv.util;

import org.junit.Test;
import tiltadv.util.lambda.Action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static tiltadv.util.StringUtils.format;
import static tiltadv.TestUtils.assertException;

public class StringUtilsTest {

    @Test
    public void formatHandlesBasicValues() {
        assertThat(format("Hello {0}!", "World"), equalTo("Hello World!"));
        assertThat(format("Easy as {0}, {1}, {2}...", 1, 2, 3), equalTo("Easy as 1, 2, 3..."));
    }

    @Test
    public void formatAllowsDuplicateIndices() {
        assertThat(format("{0}+{0}={1}", 2, 4), equalTo("2+2=4"));
    }

    @Test
    public void formatAllowsSkippingIndices() {
        assertThat(format("First: {0}, Last: {2}", "Edgar", "Allen", "Poe"), equalTo("First: Edgar, Last: Poe"));
    }

    @Test
    public void formatEscapesDoubleBraces() {
        assertThat(format("Argument {{0}} -> {0}", "3.14"), equalTo("Argument {0} -> 3.14"));
    }

    @Test
    public void formatHandlesRecursion() {
        assertThat(format("Recursion! {0}{1}{0}", "{0}", "{1}"), equalTo("Recursion! {0}{1}{0}"));
    }
}