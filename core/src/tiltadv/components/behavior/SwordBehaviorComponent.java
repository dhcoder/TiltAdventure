package tiltadv.components.behavior;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.math.Angle;
import dhcoder.support.time.Duration;
import tiltadv.components.model.HeadingComponent;
import tiltadv.components.model.SizeComponent;
import tiltadv.components.model.TransformComponent;
import tiltadv.memory.Pools;

/**
 * Class that oscillates an entity between two locations, both easing out of and into each location.
 */
public final class SwordBehaviorComponent extends AbstractComponent {

    private final static Angle ARC = Angle.fromDegrees(130f);
    private final static Duration DURATION = Duration.fromMilliseconds(250f);
    private final Angle from = Angle.fromDegrees(0);
    private final Duration elapsedSoFar = Duration.zero();
    private boolean onReturnTrip;

    private Entity owner;
    private TransformComponent transformComponent;
    private SizeComponent sizeComponent;

    private Entity parent;
    private TransformComponent parentTransformComponent;
    private SizeComponent parentSizeComponent;
    private HeadingComponent parentHeadingComponent;

    public SwordBehaviorComponent() { reset(); }

    public void setParent(final Entity parent) {
        this.parent = parent;
        parentTransformComponent = parent.requireComponent(TransformComponent.class);
        parentHeadingComponent = parent.requireComponent(HeadingComponent.class);
        parentSizeComponent = parent.requireComponent(SizeComponent.class);
    }

    public void swing() {
        if (owner.isActive()) {
            return; // Ignore call to swing while swinging
        }

        if (!onReturnTrip) {
            from.setDegrees(parentHeadingComponent.getHeading().getDegrees() - ARC.getDegrees() / 2f);
        }
        else {
            from.setDegrees(parentHeadingComponent.getHeading().getDegrees() + ARC.getDegrees() / 2f);
        }

        owner.setActive(true);
    }

    @Override
    public void initialize(final Entity owner) {

        if (parent == null) {
            throw new IllegalStateException("parent must be set");
        }

        this.owner = owner;
        transformComponent = owner.requireComponent(TransformComponent.class);
        sizeComponent = owner.requireComponent(SizeComponent.class);
        owner.setActive(false);
    }

    @Override
    public void update(final Duration elapsedTime) {
        elapsedSoFar.add(elapsedTime);
        if (elapsedSoFar.getSeconds() > DURATION.getSeconds()) {
            elapsedSoFar.subtract(DURATION);
            onReturnTrip = !onReturnTrip;
            owner.setActive(false);
        }

        float percent = elapsedSoFar.getSeconds() / DURATION.getSeconds();

        Angle currAngle = Pools.angles.grabNew();

        currAngle.setFrom(from);
        float deltaDegrees = ARC.getDegrees() * percent;
        if (!onReturnTrip) {
            currAngle.addDegrees(deltaDegrees);
        }
        else {
            currAngle.subDegrees(deltaDegrees);
        }

        Vector2 currTranslate = Pools.vector2s.grabNew();
        currTranslate.set((parentSizeComponent.getSize().x * .8f + sizeComponent.getSize().x) / 2f, 0f);
        currTranslate.rotate(currAngle.getDegrees());
        currTranslate.add(parentTransformComponent.getTranslate());
        transformComponent.setTranslate(currTranslate);
        transformComponent.setRotation(currAngle);
        Pools.angles.freeCount(1);
        Pools.vector2s.freeCount(1);
    }

    @Override
    public void reset() {
        from.reset();
        elapsedSoFar.setZero();
        onReturnTrip = false;

        owner = null;
        transformComponent = null;
        sizeComponent = null;
        parent = null;
        parentTransformComponent = null;
        parentHeadingComponent = null;
        parentSizeComponent = null;
    }
}
