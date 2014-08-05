package tiltadv.components.model;

import com.badlogic.gdx.math.Vector2;
import dhcoder.support.math.Angle;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class TransformComponentTest {

    @Test
    public void defaultComponentHasExpectedValues() {
        TransformComponent transformComponent = new TransformComponent.Builder().build();

        assertThat(transformComponent.getTranslate(), equalTo(new Vector2(0f, 0f)));
        assertThat(transformComponent.getScale(), equalTo(new Vector2(1f, 1f)));
        assertThat(transformComponent.getRotation().getDegrees(), equalTo(0f));
    }

    @Test
    public void componentConsumesInputVectorsDefensively() {
        Vector2 sneakyTranslate = new Vector2(0f, 0f);
        Vector2 sneakyScale = new Vector2(1f, 1f);

        TransformComponent transformComponent =
            new TransformComponent.Builder().setTranslate(sneakyTranslate).setScale(sneakyScale).build();

        sneakyTranslate.set(123f, -456f);
        assertThat(transformComponent.getTranslate(), equalTo(new Vector2(0f, 0f)));

        sneakyScale.set(123f, -456f);
        assertThat(transformComponent.getScale(), equalTo(new Vector2(1f, 1f)));
    }

    @Test
    public void settingComponentValuesWorks() {
        TransformComponent transformComponent = new TransformComponent.Builder().build();

        Vector2 newTranslate = new Vector2(123f, -456f);
        transformComponent.setTranslate(newTranslate);
        assertThat(transformComponent.getTranslate(), equalTo(newTranslate));

        Vector2 newScale = new Vector2(-123f, 456f);
        transformComponent.setScale(newScale);
        assertThat(transformComponent.getScale(), equalTo(newScale));

        transformComponent.setRotation(Angle.fromDegrees(30f));
        assertThat(transformComponent.getRotation().getDegrees(), equalTo(30f));
    }

}