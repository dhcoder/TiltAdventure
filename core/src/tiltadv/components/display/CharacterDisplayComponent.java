package tiltadv.components.display;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.math.CompassDirection;
import dhcoder.support.time.Duration;
import tiltadv.components.model.MotionComponent;
import tiltadv.memory.Pools;

/**
 * Component that renders a character which can move in any of the cardinal directions.
 */
public final class CharacterDisplayComponent extends AbstractComponent {

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

    private MotionComponent motionComponent;
    private SpriteComponent spriteComponent;
    private CompassDirection activeDirection;

    public CharacterDisplayComponent(final Animation animS, final Animation animE, final Animation animN,
        final Animation animW) {
        this(animS, animS, animE, animE, animN, animN, animW, animW);
    }

    public CharacterDisplayComponent(final Animation animS, final Animation animSE, final Animation animE,
        final Animation animNE, final Animation animN, final Animation animNW, final Animation animW,
        final Animation animSW) {
        this.animS = animS;
        this.animSE = animSE;
        this.animE = animE;
        this.animNE = animNE;
        this.animN = animN;
        this.animNW = animNW;
        this.animW = animW;
        this.animSW = animSW;

        activeDirection = CompassDirection.S;
        activeAnim = getAnimationForDirection(activeDirection);
    }

    @Override
    public void initialize(final Entity owner) {
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);

        spriteComponent.setTextureRegion(activeAnim.getKeyFrame(0));
    }

    @Override
    public void update(final Duration elapsedTime) {
        Vector2 velocity = motionComponent.getVelocity();

        if (!velocity.isZero()) {
            elapsedSoFar += elapsedTime.getSeconds();

            final Angle angle = Pools.angles.grabNew();
            angle.setDegrees(velocity.angle());
            if (!activeDirection.isFacing(angle)) {
                activeDirection = CompassDirection.getForAngle(angle);
                activeAnim = getAnimationForDirection(activeDirection);
            }

            Pools.angles.free(angle);

            spriteComponent.setTextureRegion(activeAnim.getKeyFrame(elapsedSoFar));
        }
        else {
            elapsedSoFar = 0f;
            spriteComponent.setTextureRegion(activeAnim.getKeyFrame(0));
        }
    }

    @Override
    protected void resetComponent() {
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
}
