package tiltadv.entity.components;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.math.Angle;
import dhcoder.support.opt.Opt;
import tiltadv.entity.AbstractComponent;

/**
 * A component that encapsulates an entity's position, scale, and rotation values.
 */
public final class TransformComponent extends AbstractComponent {

    public static class Builder {

        private Opt<Vector2> translateOpt = Opt.withNoValue();
        private Opt<Vector2> scaleOpt = Opt.withNoValue();
        private Opt<Angle> rotationOpt = Opt.withNoValue();

        public Builder setTranslate(final Vector2 translate) {
            translateOpt.set(translate);
            return this;
        }

        public Builder setTranslate(float x, float y) {
            translateOpt.set(new Vector2(x, y));
            return this;
        }

        public Builder setScale(final Vector2 scale) {
            scaleOpt.set(scale);
            return this;
        }

        public Builder setScale(float x, float y) {
            scaleOpt.set(new Vector2(x, y));
            return this;
        }

        public Builder setRotation(final Angle rotation) {
            rotationOpt.set(rotation);
            return this;
        }

        public TransformComponent build() {
            return new TransformComponent(translateOpt, scaleOpt, rotationOpt);
        }
    }

    public final Vector2 translate = new Vector2(0f, 0f);
    public final Vector2 scale = new Vector2(1f, 1f);
    public final Angle rotation = new Angle();

    public TransformComponent() {}

    private TransformComponent(final Opt<Vector2> translateOpt, final Opt<Vector2> scaleOpt,
        final Opt<Angle> rotationOpt) {
        if (translateOpt.hasValue()) {
            translate.set(translateOpt.value());
        }

        if (scaleOpt.hasValue()) {
            scale.set(scaleOpt.value());
        }

        if (rotationOpt.hasValue()) {
            rotation.set(rotationOpt.value());
        }
    }
}
