package tiltadv.components.behavior;

import dhcoder.libgdx.entity.Entity;
import dhcoder.libgdx.physics.PhysicsSystem;
import dhcoder.libgdx.physics.PhysicsUpdateListener;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.dynamics.box2d.BodyComponent;
import tiltadv.components.dynamics.box2d.OffsetComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.components.math.LerpComponent;
import tiltadv.globals.Services;
import tiltadv.memory.Pools;

/**
 * Class that oscillates an entity between two locations, both easing out of and into each location.
 */
public final class SwordBehaviorComponent extends LerpComponent implements PhysicsUpdateListener {

    private final static Angle ARC = Angle.fromDegrees(100f);
    private final static Angle ARC_START = Angle.fromDegrees(-ARC.getDegrees() / 2f);
    private final static Duration SWING_DURATION = Duration.fromMilliseconds(150f);

    private Duration restTimeRemaining = Duration.zero();

    private BodyComponent parentBodyComponent;
    private SpriteComponent parentSpriteComponent;

    private BodyComponent bodyComponent;
    private OffsetComponent offsetComponent;
    private SpriteComponent spriteComponent;

    public SwordBehaviorComponent() {
        setDuration(SWING_DURATION);
    }

    public void swing() {
        if (isActive() || !restTimeRemaining.isZero()) {
            return; // Ignore call to swing while swinging
        }

        lerpFromStart();
    }

    @Override
    public void onPhysicsUpdate() {
        Angle swordAngle = Pools.angles.grabNew();
        swordAngle.setRadians(bodyComponent.getBody().getAngle());
        spriteComponent.setRotation(swordAngle);
        Pools.angles.freeCount(1);
    }

    @Override
    protected void handleLerp(final float percent) {
        float deltaDegrees = ARC.getDegrees() * percent;
        Angle currAngle = Pools.angles.grabNew().setFrom(ARC_START).addDegrees(deltaDegrees);

        offsetComponent.setAngle(currAngle);
        Pools.angles.freeCount(1);
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

        restTimeRemaining.setFrom(BodyComponent.IMPULSE_RECOVERY_TIME);
    }

    @Override
    public void handleInitialize(final Entity owner) {

        Entity parentEntity = owner.requireComponent(ParentComponent.class).getParent();
        parentBodyComponent = parentEntity.requireComponent(BodyComponent.class);
        parentSpriteComponent = parentEntity.requireComponent(SpriteComponent.class);

        bodyComponent = owner.requireComponent(BodyComponent.class);
        offsetComponent = owner.requireComponent(OffsetComponent.class);
        spriteComponent = owner.requireComponent(SpriteComponent.class);
//        offsetComponent = owner.requireComponent(OffsetComponent.class);
//        collisionComponent = owner.requireComponent(SwordCollisionComponent.class);

        Services.get(PhysicsSystem.class).addUpdateListener(this);

        enableCollision(false);
    }

    @Override
    public void handleReset() {
        Services.get(PhysicsSystem.class).removeUpdateListener(this);

        restTimeRemaining.setZero();

        parentBodyComponent = null;
        parentSpriteComponent = null;

        bodyComponent = null;
        offsetComponent = null;
        spriteComponent = null;
    }

    @Override
    protected void handleUpdate(final Duration elapsedTime) {
        if (!restTimeRemaining.isZero()) {
            restTimeRemaining.subtract(elapsedTime);
        }

        // Sword should ALWAYS be below the player.
        spriteComponent.setZ(parentSpriteComponent.getZ() - .1f);
    }

    private void enableCollision(final boolean enabled) {
        offsetComponent.setAngle(ARC_START);
        Services.get(PhysicsSystem.class).setActive(bodyComponent.getBody(), enabled);
        spriteComponent.setHidden(!enabled);
    }
}
