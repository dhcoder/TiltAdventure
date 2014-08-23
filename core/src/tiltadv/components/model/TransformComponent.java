package tiltadv.components.model;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.support.math.Angle;

/**
 * A component that encapsulates an entity's position, scale, and rotation values.
 */
public final class TransformComponent extends AbstractComponent {

    private final Vector2 translate = new Vector2();
    private final Vector2 scale = new Vector2();
    private final Angle rotation = Angle.fromDegrees(0f);

    public TransformComponent() { reset(); }

    public Vector2 getTranslate() {
        return translate;
    }

    public TransformComponent setTranslate(final Vector2 translate) {
        this.translate.set(translate);
        return this;
    }

    public Vector2 getScale() {
        return scale;
    }

    public TransformComponent setScale(final Vector2 scale) {
        this.scale.set(scale);
        return this;
    }

    public Angle getRotation() {
        return rotation;
    }

    public TransformComponent setRotation(final Angle rotation) {
        this.rotation.setFrom(rotation);
        return this;
    }

    @Override
    public void reset() {
        translate.set(0f, 0f);
        scale.set(1f, 1f);
        rotation.setDegrees(0f);
    }
}
