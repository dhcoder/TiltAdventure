package tiltadv.entity.components.display;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.MotionComponent;

/**
 * Component that maintains the state and logic of the main player's avatar.
 */
public class PlayerDisplayComponent extends AbstractComponent {

    private final Animation animUp;
    private final Animation animDown;
    private final Animation animLeft;
    private final Animation animRight;
    private MotionComponent motionComponent;
    private SpriteComponent spriteComponent;
    private float elapsedSoFar;
    private Animation activeAnim;

    public PlayerDisplayComponent(final Animation animUp, final Animation animDown, final Animation animLeft,
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
            spriteComponent.setTextureRegion(activeAnim.getKeyFrame(0));
        }
    }
}
