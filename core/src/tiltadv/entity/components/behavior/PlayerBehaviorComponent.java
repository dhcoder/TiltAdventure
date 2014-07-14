package tiltadv.entity.components.behavior;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import tiltadv.entity.AbstractComponent;
import tiltadv.entity.Entity;
import tiltadv.entity.components.data.MotionComponent;
import tiltadv.entity.components.data.TiltComponent;
import tiltadv.entity.components.sprite.SpriteComponent;

/**
 * Component that maintains the state and logic of the main player's avatar.
 */
public class PlayerBehaviorComponent extends AbstractComponent {


    private static final float TILT_THRESHOLD = 4f;
    private static final float VELOCITY_SCALER = 15f;

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

        spriteComponent.sprite.set(playerDown);
    }

    @Override
    public void update(final float elapsedTime) {
        Vector2 tiltVector = tiltComponent.getTiltVector();

        if (tiltVector.len2() < TILT_THRESHOLD) {
            if (isMoving) {
                isMoving = false;
                motionComponent.setDampingTime(DAMPING_TIME);
            }

            return;
        }

        motionComponent.setVelocity(tiltVector.scl(VELOCITY_SCALER));
        float angle = tiltVector.angle();

        if (0f <= angle && angle < 45f) {
            spriteComponent.sprite.set(playerRight);
        } else if (45f <= angle && angle < 135f) {
            spriteComponent.sprite.set(playerUp);
        } else if (135f <= angle && angle < 225f) {
            spriteComponent.sprite.set(playerLeft);
        } else if (225f <= angle && angle < 315f) {
            spriteComponent.sprite.set(playerDown);
        } else { // angle >= 315 && angle < 360f
            spriteComponent.sprite.set(playerRight);
        }

        isMoving = true;
    }
}
