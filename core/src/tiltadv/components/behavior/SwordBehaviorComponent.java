package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.body.PositionComponent;
import tiltadv.components.collision.SwordCollisionComponent;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.hierarchy.ParentComponent;
import tiltadv.components.body.HeadingComponent;
import tiltadv.memory.Pools;

/**
 * Class that oscillates an entity between two locations, both easing out of and into each location.
 */
public final class SwordBehaviorComponent extends AbstractComponent {

    private final static Angle ARC = Angle.fromDegrees(100f);
    private final static Duration DURATION = Duration.fromMilliseconds(200f);

    private final Angle from = Angle.fromDegrees(0);
    private final Duration elapsedSoFar = Duration.zero();
    private boolean gotSwingRequest;
    private boolean isActive;
    private boolean onReturnTrip;

    private PositionComponent positionComponent;
    private SwordCollisionComponent collisionComponent;
    private SpriteComponent spriteComponent;

    private PositionComponent parentPositionComponent;
    private HeadingComponent parentHeadingComponent;

    public SwordBehaviorComponent() { reset(); }

    public void swing() {
        if (isActive) {
            return; // Ignore call to swing while swinging
        }

        gotSwingRequest = true; // Handle
    }

    @Override
    public void initialize(final Entity owner) {
        Entity parent = owner.requireComponent(ParentComponent.class).getParent();
        parentPositionComponent = parent.requireComponent(PositionComponent.class);
        parentHeadingComponent = parent.requireComponent(HeadingComponent.class);

        spriteComponent = owner.requireComponent(SpriteComponent.class);
        positionComponent = owner.requireComponent(PositionComponent.class);
        collisionComponent = owner.requireComponent(SwordCollisionComponent.class);

        setActive(false);
    }

    @Override
    public void update(final Duration elapsedTime) {
        if (gotSwingRequest) {
            elapsedSoFar.setZero();
            if (!onReturnTrip) {
                from.setDegrees(parentHeadingComponent.getHeading().getDegrees() - ARC.getDegrees() / 2f);
            }
            else {
                from.setDegrees(parentHeadingComponent.getHeading().getDegrees() + ARC.getDegrees() / 2f);
            }

            setActive(true);
            gotSwingRequest = false;
        }

        if (!isActive) {
            return;
        }

        elapsedSoFar.add(elapsedTime);
        if (elapsedSoFar.getSeconds() > DURATION.getSeconds()) {
            elapsedSoFar.setFrom(DURATION);
//            onReturnTrip = !onReturnTrip;
            setActive(false);
            return;
        }

        float percent = elapsedSoFar.getSeconds() / DURATION.getSeconds();

        Angle currAngle = Pools.angles.grabNew().setFrom(from);

        float deltaDegrees = ARC.getDegrees() * percent;
        if (!onReturnTrip) {
            currAngle.addDegrees(deltaDegrees);
        }
        else {
            currAngle.subDegrees(deltaDegrees);
        }

        int vectorMark = Pools.vector2s.mark();
        Vector2 currTranslate = Pools.vector2s.grabNew();
        currTranslate.rotate(currAngle.getDegrees());
        currTranslate.add(parentPositionComponent.getPosition());
        positionComponent.setPosition(currTranslate);
        spriteComponent.setRotation(currAngle);

        Vector2 swordTip = Pools.vector2s.grabNew();
        swordTip.rotate(currAngle.getDegrees());
        collisionComponent.setOffset(swordTip);

        Pools.angles.freeCount(1);
        Pools.vector2s.freeToMark(vectorMark);
    }

    @Override
    public void reset() {
        from.reset();
        elapsedSoFar.setZero();
        onReturnTrip = false;
        gotSwingRequest = false;
        isActive = false;

        spriteComponent = null;
        positionComponent = null;
        collisionComponent = null;

        parentPositionComponent = null;
        parentHeadingComponent = null;
    }

    private void setActive(final boolean active) {
        isActive = active;
        collisionComponent.getCollider().setEnabled(active);
        spriteComponent.setHidden(!active);
    }
}
