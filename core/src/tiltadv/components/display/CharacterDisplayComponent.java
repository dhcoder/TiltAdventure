package tiltadv.components.display;

import com.badlogic.gdx.graphics.g2d.Animation;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.math.CardinalDirection;
import dhcoder.support.math.CompassDirection;
import dhcoder.support.time.Duration;
import tiltadv.components.dynamics.box2d.BodyComponent;

/**
 * Component that renders a character which can move in any of the cardinal directions.
 */
public final class CharacterDisplayComponent extends AbstractComponent {

    // In order not to have a character pop between two animations when moving at just the right angle (say, at 45°
    // right between NE and E), we add a bit of margin of error so that, once you're facing a direction, it sticks a bit
    // and you actually have to changing your heading a bit more than usual to change direction.
    private static final Angle HEADING_MARGIN = Angle.fromDegrees(2.5f);

    private Animation animS;
    private Animation animE;
    private Animation animN;
    private Animation animSE;
    private Animation animNW;

    private float elapsedSoFar;
    private Animation activeAnim;

    private CompassDirection activeCompassDirection;
    private CardinalDirection activeCardinalDirection;
    private boolean activeFlipX;
    private boolean isCardinal;

    private BodyComponent bodyComponent;
    private SpriteComponent spriteComponent;

    CharacterDisplayComponent() {
        reset();
    }

    public CharacterDisplayComponent set(final Animation animS, final Animation animE, final Animation animN) {
        return set(animS, animE, animN, null, null, true);
    }

    public CharacterDisplayComponent set(final Animation animS, final Animation animE,
        final Animation animN, final Animation animSE, final Animation animNW) {
        return set(animS, animE, animN, animSE, animNW, false);
    }

    @Override
    public void initialize(final Entity owner) {
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        bodyComponent = owner.requireComponent(BodyComponent.class);

        spriteComponent.setTextureRegion(activeAnim.getKeyFrame(0));
    }

    @Override
    public void update(final Duration elapsedTime) {
        if (!bodyComponent.getBody().getLinearVelocity().isZero()) {
            elapsedSoFar += elapsedTime.getSeconds();
        }
        else {
            elapsedSoFar = 0f;
        }

        final Angle angle = bodyComponent.getHeading();
        if (isCardinal) {
            if (!activeCardinalDirection.isFacing(angle, HEADING_MARGIN)) {
                activeCardinalDirection = CardinalDirection.getForAngle(angle);
                activeAnim = getAnimationForDirection(activeCardinalDirection);

                activeFlipX = useFlippedSprite(activeCardinalDirection);
            }
        }
        else {
            if (!activeCompassDirection.isFacing(angle, HEADING_MARGIN)) {
                activeCompassDirection = CompassDirection.getForAngle(angle);
                activeAnim = getAnimationForDirection(activeCompassDirection);

                activeFlipX = useFlippedSprite(activeCompassDirection);
            }
        }

        spriteComponent.setTextureRegion(activeAnim.getKeyFrame(elapsedSoFar));
        spriteComponent.setFlip(activeFlipX, false);
    }

    @Override
    public void reset() {
        animS = null;
        animE = null;
        animNW = null;
        animN = null;
        animSE = null;

        elapsedSoFar = 0f;
        activeAnim = null;

        isCardinal = false;
        activeCardinalDirection = CardinalDirection.S;
        activeCompassDirection = CompassDirection.S;
        activeFlipX = false;

        bodyComponent = null;
        spriteComponent = null;
    }

    private CharacterDisplayComponent set(final Animation animS, final Animation animE,
        final Animation animN, final Animation animSE, final Animation animNW, final boolean isCardinal) {

        this.animS = animS;
        this.animE = animE;
        this.animN = animN;
        this.animSE = animSE;
        this.animNW = animNW;
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
                return animNW;
            case N:
                return animN;
            case NW:
                return animNW;
            case W:
                return animE;
            case SW:
                return animSE;
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
                return animE;
            case S:
                return animS;
            default:
                return animS;
        }
    }

    private boolean useFlippedSprite(final CompassDirection direction) {
        switch (direction) {
            case NE:
            case W:
            case SW:
                return true;
            default:
                return false;
        }
    }

    private boolean useFlippedSprite(final CardinalDirection direction) {
        switch (direction) {
            case W:
                return true;
            default:
                return false;
        }
    }
}
