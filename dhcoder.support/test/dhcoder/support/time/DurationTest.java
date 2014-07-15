package dhcoder.support.time;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class DurationTest {

    @Test
    public void testFromSeconds() throws Exception {
        Duration duration = Duration.fromSeconds(90f);

        assertThat(duration.inMinutes(), equalTo(1.5f));
        assertThat(duration.inSeconds(), equalTo(90f));
        assertThat(duration.inMilliseconds(), equalTo(90000f));
    }

    @Test
    public void testFromMinutes() throws Exception {
        Duration duration = Duration.fromMinutes(1.5f);

        assertThat(duration.inMinutes(), equalTo(1.5f));
        assertThat(duration.inSeconds(), equalTo(90f));
        assertThat(duration.inMilliseconds(), equalTo(90000f));
    }

    @Test
    public void testFromMilliseconds() throws Exception {
        Duration duration = Duration.fromMilliseconds(90000f);

        assertThat(duration.inMinutes(), equalTo(1.5f));
        assertThat(duration.inSeconds(), equalTo(90f));
        assertThat(duration.inMilliseconds(), equalTo(90000f));
    }
}