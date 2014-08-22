package tiltadv.components.display;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.model.MotionComponent;

/**
 * Component that renders a character which can move in any of the cardinal directions.
 */
public final class CharacterDisplayComponent extends AbstractComponent {

    private final Animation animS;
    private final Animation animSE;
    private final Animation animE;
    private final Animation animNE;
    private final Animation animN;
    private final Animation animNW;
    private final Animation animW;
    private final Animation animSW;

    private MotionComponent motionComponent;
    private SpriteComponent spriteComponent;
    private float elapsedSoFar;
    private Animation activeAnim;

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

        activeAnim = animS;
    }

    @Override
    public void initialize(final Entity owner) {
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);

        spriteComponent.setTextureRegion(animS.getKeyFrame(0));
    }

    @Override
    public void update(final Duration elapsedTime) {
        Vector2 velocity = motionComponent.getVelocity();

        if (!velocity.isZero()) {
            elapsedSoFar += elapsedTime.getSeconds();

            float motionAngle = velocity.angle();

            if (0 <= motionAngle && motionAngle < 22.5f) {
                activeAnim = animE;
            }
            else if (motionAngle < 67.5f) {
                activeAnim = animNE;
            }
            else if (motionAngle < 112.5f) {
                activeAnim = animN;
            }
            else if (motionAngle < 157.5f) {
                activeAnim = animNW;
            }
            else if (motionAngle < 202.5f) {
                activeAnim = animW;
            }
            else if (motionAngle < 247.5f) {
                activeAnim = animSW;
            }
            else if (motionAngle < 292.5f) {
                activeAnim = animS;
            }
            else if (motionAngle < 337.5f) {
                activeAnim = animSE;
            }
            else { // tiltDegrees >= 315 && tiltDegrees < 45f
                activeAnim = animE;
            }

            spriteComponent.setTextureRegion(activeAnim.getKeyFrame(elapsedSoFar));
        }
        else {
            elapsedSoFar = 0f;
            spriteComponent.setTextureRegion(activeAnim.getKeyFrame(0));
        }
    }
}
