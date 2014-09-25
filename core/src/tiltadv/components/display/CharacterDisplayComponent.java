package tiltadv.components.display;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.math.CardinalDirection;
import dhcoder.support.math.CompassDirection;
import dhcoder.support.time.Duration;
import tiltadv.components.body.HeadingComponent;
import tiltadv.components.body.MotionComponent;

/**
 * Component that renders a character which can move in any of the cardinal directions.
 */
public final class CharacterDisplayComponent extends AbstractComponent {

    // In order not to have a character pop between two animations when moving at just the right angle (say, at 45°
    // right between NE and E), we add a bit of margin of error so that, once you're facing a direction, it sticks a bit
    // and you actually have to changing your heading a bit more than usual to change direction.
    private static final Angle HEADING_MARGIN = Angle.fromDegrees(2.5f);

    private Animation animS;
    private Animation animSE;
    private Animation animE;
    private Animation animNE;
    private Animation animN;
    private Animation animNW;
    private Animation animW;
    private Animation animSW;

    private float elapsedSoFar;
    private Animation activeAnim;

    private CompassDirection activeCompassDirection;
    private CardinalDirection activeCardinalDirection;
    private boolean isCardinal;

    private MotionComponent motionComponent;
    private HeadingComponent headingComponent;
    private SpriteComponent spriteComponent;

    CharacterDisplayComponent() {
        reset();
    }

    public CharacterDisplayComponent set(final Animation animS, final Animation animE, final Animation animN,
        final Animation animW) {
        return set(animS, null, animE, null, animN, null, animW, null, true);
    }

    public CharacterDisplayComponent set(final Animation animS, final Animation animSE, final Animation animE,
        final Animation animNE, final Animation animN, final Animation animNW, final Animation animW,
        final Animation animSW) {
        return set(animS, animSE, animE, animNE, animN, animNW, animW, animSW, false);
    }

    @Override
    public void initialize(final Entity owner) {
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);
        headingComponent = owner.requireComponent(HeadingComponent.class);

        spriteComponent.setTextureRegion(activeAnim.getKeyFrame(0));
    }

    @Override
    public void update(final Duration elapsedTime) {
        Vector2 velocity = motionComponent.getVelocity();

        if (!velocity.isZero()) {
            elapsedSoFar += elapsedTime.getSeconds();
        }
        else {
            elapsedSoFar = 0f;
        }

        final Angle angle = headingComponent.getHeading();
        if (isCardinal) {
            if (!activeCardinalDirection.isFacing(angle, HEADING_MARGIN)) {
                activeCardinalDirection = CardinalDirection.getForAngle(angle);
                activeAnim = getAnimationForDirection(activeCardinalDirection);
            }
        }
        else {
            if (!activeCompassDirection.isFacing(angle, HEADING_MARGIN)) {
                activeCompassDirection = CompassDirection.getForAngle(angle);
                activeAnim = getAnimationForDirection(activeCompassDirection);
            }
        }

        spriteComponent.setTextureRegion(activeAnim.getKeyFrame(elapsedSoFar));
    }

    @Override
    public void reset() {
        animS = null;
        animSE = null;
        animE = null;
        animNE = null;
        animN = null;
        animNW = null;
        animW = null;
        animSW = null;

        elapsedSoFar = 0f;
        activeAnim = null;

        isCardinal = false;
        activeCardinalDirection = CardinalDirection.S;
        activeCompassDirection = CompassDirection.S;

        motionComponent = null;
        headingComponent = null;
        spriteComponent = null;
    }

    private CharacterDisplayComponent set(final Animation animS, final Animation animSE, final Animation animE,
        final Animation animNE, final Animation animN, final Animation animNW, final Animation animW,
        final Animation animSW, final boolean isCardinal) {

        this.animS = animS;
        this.animSE = animSE;
        this.animE = animE;
        this.animNE = animNE;
        this.animN = animN;
        this.animNW = animNW;
        this.animW = animW;
        this.animSW = animSW;
        this.isCardinal = isCardinal;

        activeAnim = isCardinal ? getAnimationForDirection(activeCardinalDirection) :
            getAnimationForDirection(activeCompassDirection);

        return this;
    }

    private Animation getAnimationForDirection(final CompassDirection direction) {
        switch (direction) {
            case E:
                return animE;
            case NE:
                return animNE;
            case N:
                return animN;
            case NW:
                return animNW;
            case W:
                return animW;
            case SW:
                return animSW;
            case S:
                return animS;
            case SE:
                return animSE;
            default:
                return animS;
        }
    }

    private Animation getAnimationForDirection(final CardinalDirection direction) {
        switch (direction) {
            case E:
                return animE;
            case N:
                return animN;
            case W:
                return animW;
            case S:
                return animS;
            default:
                return animS;
        }
    }

}
