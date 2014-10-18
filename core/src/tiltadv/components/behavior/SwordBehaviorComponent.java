package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.body.HeadingComponent;
import tiltadv.components.body.LerpComponent;
import tiltadv.components.collision.SwordCollisionComponent;
import tiltadv.components.combat.KnockbackComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.hierarchy.OffsetComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.memory.Pools;

/**
 * Class that oscillates an entity between two locations, both easing out of and into each location.
 */
public final class SwordBehaviorComponent extends LerpComponent {

    private final static Angle ARC = Angle.fromDegrees(100f);
    private final static Angle HALF_ARC = Angle.fromDegrees(ARC.getDegrees() / 2f);
    private final static Duration DURATION = Duration.fromMilliseconds(200f);
    private final static Vector2 SWORD_POS = new Vector2(12f, 0f);
    private final static Vector2 SWORD_TIP_POS = new Vector2(-10f, 0f);

    private Angle from;
    private Duration restTimeRemaining;
    private HeadingComponent headingComponent;
    private OffsetComponent offsetComponent;
    private SwordCollisionComponent collisionComponent;
    private SpriteComponent spriteComponent;

    public SwordBehaviorComponent() {
        super();
        reset();
        setDuration(DURATION);
    }

    public void swing() {
        if (isActive() || !restTimeRemaining.isZero()) {
            return; // Ignore call to swing while swinging
        }

        from.setFrom(headingComponent.getHeading()).sub(HALF_ARC);
        lerpFromStart();
    }

    @Override
    protected void handleLerp(final float percent) {
        Angle currAngle = Pools.angles.grabNew().setFrom(from);
        float deltaDegrees = ARC.getDegrees() * percent;
        currAngle.addDegrees(deltaDegrees);

        int vectorMark = Pools.vector2s.mark();
        Vector2 currTranslate = Pools.vector2s.grabNew().set(SWORD_POS);
        currTranslate.rotate(currAngle.getDegrees());
        offsetComponent.setOffset(currTranslate);
        spriteComponent.setRotation(currAngle);

        Vector2 swordTip = Pools.vector2s.grabNew().set(SWORD_POS).add(SWORD_TIP_POS);
        swordTip.rotate(currAngle.getDegrees());
        collisionComponent.setOffset(swordTip);

        Pools.angles.freeCount(1);
        Pools.vector2s.freeToMark(vectorMark);
    }

    @Override
    protected void handleLerpActivated() {
        headingComponent.setLocked(true);
        enableCollision(true);
    }

    @Override
    protected void handleLerpDeactivated() {
        headingComponent.setLocked(false);
        enableCollision(false);

        // Using the sword incurs a knockback, so let's reuse that duration here as well, so we don't swing while
        // being knocked back.
        restTimeRemaining.setFrom(KnockbackComponent.DURATION);
    }

    @Override
    protected void handleConstruction() {
        restTimeRemaining = Duration.zero();
        from = Angle.fromDegrees(0);
    }

    @Override
    public void handleInitialize(final Entity owner) {

        Entity parentEntity = owner.requireComponent(ParentComponent.class).getParent();
        headingComponent = parentEntity.requireComponent(HeadingComponent.class);

        spriteComponent = owner.requireComponent(SpriteComponent.class);
        offsetComponent = owner.requireComponent(OffsetComponent.class);
        collisionComponent = owner.requireComponent(SwordCollisionComponent.class);

        enableCollision(false);
    }

    @Override
    protected void handleUpdate(final Duration elapsedTime) {
        if (!restTimeRemaining.isZero()) {
            restTimeRemaining.subtract(elapsedTime);
        }
    }

    @Override
    public void handleReset() {
        from.reset();
        restTimeRemaining.setZero();

        headingComponent = null;
        spriteComponent = null;
        offsetComponent = null;
        collisionComponent = null;
    }

    private void enableCollision(final boolean enabled) {
        collisionComponent.getCollider().setEnabled(enabled);
        spriteComponent.setHidden(!enabled);
    }
}
