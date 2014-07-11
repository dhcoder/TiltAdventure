package tiltadv.entity.components;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.opt.Opt;
import tiltadv.entity.AbstractComponent;

/**
 * A component that encapsulates an entity's position, scale, and rotation values.
 */
public final class TransformComponent extends AbstractComponent {

    public static class Builder {

        private Opt<Vector2> originOpt = Opt.withNoValue();
        private Opt<Vector2> scaleOpt = Opt.withNoValue();
        private Opt<Float> rotationOpt = Opt.withNoValue();

        public Builder setOrigin(final Vector2 origin) {
            originOpt.set(origin);
            return this;
        }

        public Builder setScale(final Vector2 scale) {
            scaleOpt.set(scale);
            return this;
        }

        public Builder setRotation(final float rotation) {
            rotationOpt.set(rotation);
            return this;
        }

        public TransformComponent build() {
            return new TransformComponent(originOpt, scaleOpt, rotationOpt);
        }
    }

    public final Vector2 origin = new Vector2(0f, 0f);
    public final Vector2 scale = new Vector2(1f, 1f);
    private float rotation = 0f;

    private TransformComponent(final Opt<Vector2> originOpt, final Opt<Vector2> scaleOpt,
        final Opt<Float> rotationOpt) {
        if (originOpt.hasValue()) {
            origin.set(originOpt.value());
        }

        if (scaleOpt.hasValue()) {
            scale.set(scaleOpt.value());
        }

        if (rotationOpt.hasValue()) {
            rotation = rotationOpt.value();
        }
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(final float rotation) {
        this.rotation = rotation;
    }
}
