package dhcoder.support.math;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.IsCloseTo.closeTo;

public class AngleTest {

    private static final double ALLOWED_ERROR = 0.01; // Converting degrees <-> radians is imprecise

    @Test
    public void defaultAngleDefaultsTo0() {
        Angle angle = new Angle();
        assertThat(angle.getDegrees(), equalTo(0f));
        assertThat(angle.getRadians(), equalTo(0f));
    }

    @Test
    public void testSetAngleToDegrees() {
        Angle angle = Angle.fromDegrees(45f);
        assertThat(angle.getDegrees(), equalTo(45f));
        assertThat(angle.getRadians(), equalTo(45f * Angle.DEG_TO_RAD));
    }

    @Test
    public void testSetAngleToRadians() {
        Angle angle = Angle.fromRadians(Angle.PI / 3f);
        assertThat(angle.getRadians(), equalTo(Angle.PI / 3f));
        assertThat(angle.getDegrees(), equalTo(Angle.PI / 3f * Angle.RAD_TO_DEG));
    }

    @Test
    public void testSetAngleToDegreesThenRadians() {
        Angle angle = Angle.fromDegrees(180f);
        angle.setRadians(Angle.PI / 2f);
        assertClose(angle.getDegrees(), 90f);
    }

    @Test
    public void testSetAngleToRadiansThenDegrees() {
        Angle angle = Angle.fromRadians(Angle.PI);
        angle.setDegrees(90f);
        assertClose(angle.getRadians(), Angle.PI / 2);
    }

    @Test
    public void outOfBoundsDegreesAreBounded() {
        Angle angle = new Angle();

        angle.setDegrees(-300f);
        assertThat(angle.getDegrees(), equalTo(60f));

        angle.setDegrees(-3000f);
        assertThat(angle.getDegrees(), equalTo(240f));

        angle.setDegrees(400f);
        assertThat(angle.getDegrees(), equalTo(40f));

        angle.setDegrees(2000f);
        assertThat(angle.getDegrees(), equalTo(200f));
    }

    @Test
    public void outOfBoundsRadiansAreBounded() {
        Angle angle = new Angle();

        angle.setRadians(-Angle.PI / 2f);
        assertClose(angle.getRadians(), 3f * Angle.PI / 2f);

        angle.setRadians(-5f * Angle.PI);
        assertClose(angle.getRadians(), Angle.PI);

        angle.setRadians(5f * Angle.PI / 2f);
        assertClose(angle.getRadians(), Angle.PI / 2f);

        angle.setRadians(31f * Angle.PI);
        assertClose(angle.getRadians(), Angle.PI);
    }

    // Helper method for the assertThat/closeTo pattern, since closeTo uses doubles and Angle returns floats
    private void assertClose(float value, float closeToValue) {
        assertThat((double)value, closeTo((double)closeToValue, ALLOWED_ERROR));
    }

}