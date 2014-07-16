package tiltadv.entity.components.behavior;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import dhcoder.support.time.Duration;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.MotionComponent;
import tiltadv.entity.components.data.TiltComponent;
import tiltadv.entity.components.sprite.SpriteComponent;
import tiltadv.immutable.ImmutableAngle;
import tiltadv.immutable.ImmutableVector2;

/**
 * Component that maintains the state and logic of the main player's avatar.
 */
public class PlayerBehaviorComponent extends AbstractComponent {


    private static final float TILT_THRESHOLD = 4f;
    private static final float TILT_MULTIPLIER = 30f;
    private static final float MAX_VELOCITY = 60f;

    private SpriteComponent spriteComponent;
    private TiltComponent tiltComponent;
    private MotionComponent motionComponent;

    private boolean isMoving;
    private final Sprite playerUp;
    private final Sprite playerDown;
    private final Sprite playerLeft;
    private final Sprite playerRight;
    private static final float DAMPING_TIME = .5f;

    public PlayerBehaviorComponent(final Sprite playerUp, final Sprite playerDown, final Sprite playerLeft,
        final Sprite playerRight) {
        this.playerUp = playerUp;
        this.playerDown = playerDown;
        this.playerLeft = playerLeft;
        this.playerRight = playerRight;
    }

    @Override
    public void initialize(final Entity owner) {
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        tiltComponent = owner.requireComponent(TiltComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);

        motionComponent.maxVelocityOpt.set(MAX_VELOCITY);
        motionComponent.setAccelerationTime(Duration.fromSeconds(1f));
        spriteComponent.sprite.set(playerDown);
    }

    @Override
    public void update(final Duration elapsedTime) {
        ImmutableVector2 tiltVector = tiltComponent.getTiltVector();

        if (tiltVector.isZero(TILT_THRESHOLD)) {
            if (isMoving) {
                isMoving = false;
                motionComponent.setTargetVelocity(Vector2.Zero);
            }

            return;
        }

        motionComponent.setTargetVelocity(tiltVector.getX() * TILT_MULTIPLIER, tiltVector.getY() * TILT_MULTIPLIER);
        float tiltDegrees = tiltVector.getAngle();

        if (0f <= tiltDegrees && tiltDegrees < 45f) {
            spriteComponent.sprite.set(playerRight);
        } else if (45f <= tiltDegrees && tiltDegrees < 135f) {
            spriteComponent.sprite.set(playerUp);
        } else if (135f <= tiltDegrees && tiltDegrees < 225f) {
            spriteComponent.sprite.set(playerLeft);
        } else if (225f <= tiltDegrees && tiltDegrees < 315f) {
            spriteComponent.sprite.set(playerDown);
        } else { // tiltDegrees >= 315 && tiltDegrees < 360f
            spriteComponent.sprite.set(playerRight);
        }

        isMoving = true;
    }
}
