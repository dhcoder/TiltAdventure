package tiltadv.components.body;

import com.badlogic.gdx.math.Interpolation;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;

import static dhcoder.support.contract.ContractUtils.requireTrue;

/**
 * A base class for any component that needs to move between two positions.
 */
public abstract class LerpComponent extends AbstractComponent {

    private final Duration duration = Duration.zero();
    private final Duration accumulated = Duration.zero();
    private Interpolation interpolator;
    private boolean onReturnTrip;
    private boolean shouldLoop;

    public LerpComponent() {
        handleConstruction();
        reset(); }

    public final void setInterpolator(final Interpolation interpolator) {
        this.interpolator = interpolator;
    }

    public final void setShouldLoop(final boolean shouldLoop) {
        this.shouldLoop = shouldLoop;
    }

    public void setDuration(final Duration duration) {
        this.duration.setFrom(duration);
    }

    @Override
    public final void initialize(final Entity owner) {
        requireTrue(!duration.isZero(), "Duration should be set!");
        handleInitialize(owner);
    }

    @Override
    public final void update(final Duration elapsedTime) {
        accumulated.add(elapsedTime);
        if (accumulated.getSeconds() > duration.getSeconds()) {
            if (!shouldLoop) {
                return;
            }
            accumulated.subtract(duration);
            onReturnTrip = !onReturnTrip;
        }

        float percent = accumulated.getSeconds() / duration.getSeconds();
        percent = interpolator.apply(percent);
        if (onReturnTrip) {
            percent = 1f - percent;
        }
        handleLerp(percent);
    }

    @Override
    public final void reset() {
        duration.setZero();
        accumulated.setZero();
        onReturnTrip = false;
        shouldLoop = false;
        interpolator = Interpolation.sine;
        handleReset();
    }

    /**
     * The child class should maintain two values, a start and end point, and by handling this function, they should
     * set their current position based on the percent passed in.
     */
    protected abstract void handleLerp(final float percent);

    // Hack needed because constructor calls reset -> handleReset, which calls into base class before base class
    // fields are initialized...
    protected abstract void handleConstruction();
    protected void handleInitialize(final Entity owner) {}
    protected void handleReset() {}
}
