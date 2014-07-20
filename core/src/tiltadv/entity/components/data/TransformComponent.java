package tiltadv.entity.components.data;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.math.Angle;
import dhcoder.support.opt.Opt;
import tiltadv.entity.AbstractComponent;

/**
 * A component that encapsulates an entity's position, scale, and rotation values.
 */
public final class TransformComponent extends AbstractComponent {

    public static class Builder {

        private final Opt<Vector2> translateOpt = Opt.withNoValue();
        private final Opt<Vector2> scaleOpt = Opt.withNoValue();
        private final Opt<Angle> rotationOpt = Opt.withNoValue();

        public Builder setTranslate(final Vector2 translate) {
            translateOpt.set(translate);
            return this;
        }

        public Builder setTranslate(final float x, final float y) {
            translateOpt.set(new Vector2(x, y));
            return this;
        }

        public Builder setScale(final Vector2 scale) {
            scaleOpt.set(scale);
            return this;
        }

        public Builder setScale(final float x, final float y) {
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

    private final Vector2 translate = new Vector2(0f, 0f);
    private final Vector2 scale = new Vector2(1f, 1f);
    private final Angle rotation = new Angle();

    public TransformComponent() {}

    private TransformComponent(final Opt<Vector2> translateOpt, final Opt<Vector2> scaleOpt,
        final Opt<Angle> rotationOpt) {
        if (translateOpt.hasValue()) {
            translate.set(translateOpt.getValue());
        }

        if (scaleOpt.hasValue()) {
            scale.set(scaleOpt.getValue());
        }

        if (rotationOpt.hasValue()) {
            rotation.setFrom(rotationOpt.getValue());
        }
    }

    public Vector2 getTranslate() {
        return translate;
    }

    public void setTranslate(final Vector2 translate) {
        this.translate.set(translate);
    }

    public Vector2 getScale() {
        return scale;
    }

    public void setScale(final Vector2 scale) {
        this.scale.set(scale);
    }

    public Angle getRotation() {
        return rotation;
    }

    public void setRotation(final Angle rotation) {
        this.rotation.setFrom(rotation);
    }

    public void setRotation(final float degrees) {
        rotation.setDegrees(degrees);
    }
}
