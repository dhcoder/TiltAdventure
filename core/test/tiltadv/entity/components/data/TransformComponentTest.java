package tiltadv.entity.components.data;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import tiltadv.entity.components.data.TransformComponent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class TransformComponentTest {

    @Test
    public void defaultComponentHasExpectedValues() {
        TransformComponent transformComponent = new TransformComponent.Builder().build();

        assertThat(transformComponent.translate, equalTo(new Vector2(0f, 0f)));
        assertThat(transformComponent.scale, equalTo(new Vector2(1f, 1f)));
        assertThat(transformComponent.rotation.getDegrees(), equalTo(0f));
    }

    @Test
    public void componentConsumesInputVectorsDefensively() {
        Vector2 sneakyTranslate = new Vector2(0f, 0f);
        Vector2 sneakyScale = new Vector2(1f, 1f);

        TransformComponent transformComponent =
            new TransformComponent.Builder().setTranslate(sneakyTranslate).setScale(sneakyScale).build();

        sneakyTranslate.set(123f, -456f);
        assertThat(transformComponent.translate, equalTo(new Vector2(0f, 0f)));

        sneakyScale.set(123f, -456f);
        assertThat(transformComponent.scale, equalTo(new Vector2(1f, 1f)));
    }

    @Test
    public void settingComponentValuesWorks() {
        TransformComponent transformComponent = new TransformComponent.Builder().build();

        Vector2 newTranslate = new Vector2(123f, -456f);
        transformComponent.translate.set(newTranslate);
        assertThat(transformComponent.translate, equalTo(newTranslate));

        Vector2 newScale = new Vector2(-123f, 456f);
        transformComponent.scale.set(newScale);
        assertThat(transformComponent.scale, equalTo(newScale));

        float newRotation = 30f;
        transformComponent.rotation.setDegrees(newRotation);
        assertThat(transformComponent.rotation.getDegrees(), equalTo(newRotation));
    }

}