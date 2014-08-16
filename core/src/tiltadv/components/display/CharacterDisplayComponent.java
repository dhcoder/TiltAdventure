package tiltadv.components.display;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.model.MotionComponent;

/**
 * Component that renders the main player's avatar depending on its state.
 */
public final class CharacterDisplayComponent extends AbstractComponent {

    private final Animation animUp;
    private final Animation animDown;
    private final Animation animLeft;
    private final Animation animRight;
    private MotionComponent motionComponent;
    private SpriteComponent spriteComponent;
    private float elapsedSoFar;
    private Animation activeAnim;

    public CharacterDisplayComponent(final Animation animUp, final Animation animDown, final Animation animLeft,
        final Animation animRight) {
        this.animUp = animUp;
        this.animDown = animDown;
        this.animLeft = animLeft;
        this.animRight = animRight;

        activeAnim = animDown;
    }

    @Override
    public void initialize(final Entity owner) {
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);

        spriteComponent.setTextureRegion(animDown.getKeyFrame(0));
    }

    @Override
    public void update(final Duration elapsedTime) {
        Vector2 velocity = motionComponent.getVelocity();

        if (!velocity.isZero()) {
            elapsedSoFar += elapsedTime.getSeconds();

            float motionAngle = velocity.angle();

            if (45f <= motionAngle && motionAngle < 135f) {
                activeAnim = animUp;
            }
            else if (135f <= motionAngle && motionAngle < 225f) {
                activeAnim = animLeft;
            }
            else if (225f <= motionAngle && motionAngle < 315f) {
                activeAnim = animDown;
            }
            else { // tiltDegrees >= 315 && tiltDegrees < 45f
                activeAnim = animRight;
            }

            spriteComponent.setTextureRegion(activeAnim.getKeyFrame(elapsedSoFar));
        }
        else {
            elapsedSoFar = 0f;
            spriteComponent.setTextureRegion(activeAnim.getKeyFrame(0));
        }
    }
}
