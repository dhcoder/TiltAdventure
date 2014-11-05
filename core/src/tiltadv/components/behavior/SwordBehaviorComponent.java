package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.dynamics.box2d.BodyComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.components.math.LerpComponent;

/**
* Class that oscillates an entity between two locations, both easing out of and into each location.
*/
public final class SwordBehaviorComponent extends LerpComponent {

    private final static Angle ARC = Angle.fromDegrees(100f);
    private final static Angle HALF_ARC = Angle.fromDegrees(ARC.getDegrees() / 2f);
    private final static Duration SWING_DURATION = Duration.fromMilliseconds(150f);
    private final static Duration SWING_RECOVERY = Duration.fromMilliseconds(350f);
    private final static Vector2 SWORD_POS = new Vector2(12f, 0f);
    private final static Vector2 SWORD_TIP_POS = new Vector2(-10f, 0f);

    private Angle from = Angle.fromRadians(0f);
    private Duration restTimeRemaining = Duration.zero();

    private BodyComponent parentBodyComponent;
    private SpriteComponent parentSpriteComponent;

    private SpriteComponent spriteComponent;

    public SwordBehaviorComponent() {
        setDuration(SWING_DURATION);
    }

    public void swing() {
        if (isActive() || !restTimeRemaining.isZero()) {
            return; // Ignore call to swing while swinging
        }

        from.setFrom(parentBodyComponent.getHeading()).sub(HALF_ARC);
        lerpFromStart();
    }

    @Override
    protected void handleLerp(final float percent) {
//        Angle currAngle = Pools.angles.grabNew().setFrom(from);
//        float deltaDegrees = ARC.getDegrees() * percent;
//        currAngle.addDegrees(deltaDegrees);
//
//        int vectorMark = Pools.vector2s.mark();
//        Vector2 currTranslate = Pools.vector2s.grabNew().set(SWORD_POS);
//        currTranslate.rotate(currAngle.getDegrees());
////        offsetComponent.setOffset(currTranslate);
//        spriteComponent.setRotation(currAngle);
//
//        Vector2 swordTip = Pools.vector2s.grabNew().set(SWORD_POS).add(SWORD_TIP_POS);
//        swordTip.rotate(currAngle.getDegrees());
//        collisionComponent.setOffset(swordTip);
//
//        Pools.angles.freeCount(1);
//        Pools.vector2s.freeToMark(vectorMark);
    }

    @Override
    protected void handleLerpActivated() {
        parentBodyComponent.lockHeading(true);
        enableCollision(true);
    }

    @Override
    protected void handleLerpDeactivated() {
        parentBodyComponent.lockHeading(false);
        enableCollision(false);

        // Using the sword incurs a knockback, so let's reuse that duration here as well, so we don't swing while
        // being knocked back.
        restTimeRemaining.setFrom(SWING_RECOVERY);
    }

    @Override
    public void handleInitialize(final Entity owner) {

        Entity parentEntity = owner.requireComponent(ParentComponent.class).getParent();
        parentBodyComponent = parentEntity.requireComponent(BodyComponent.class);
        parentSpriteComponent = parentEntity.requireComponent(SpriteComponent.class);

        spriteComponent = owner.requireComponent(SpriteComponent.class);
//        offsetComponent = owner.requireComponent(OffsetComponent.class);
//        collisionComponent = owner.requireComponent(SwordCollisionComponent.class);

        enableCollision(false);
    }

    @Override
    protected void handleUpdate(final Duration elapsedTime) {
        if (!restTimeRemaining.isZero()) {
            restTimeRemaining.subtract(elapsedTime);
        }

        // Sword should ALWAYS be below the player.
        spriteComponent.setZ(parentSpriteComponent.getZ() - .1f);
    }

    @Override
    public void handleReset() {
        from.setRadians(0f);
        restTimeRemaining.setZero();

        parentBodyComponent = null;
        parentSpriteComponent = null;

        spriteComponent = null;
    }

    private void enableCollision(final boolean enabled) {
//        collisionComponent.getCollider().setEnabled(enabled);
        spriteComponent.setHidden(!enabled);
    }
}
