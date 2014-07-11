package tiltadv.entity.components;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TransformComponentTest {

    @Test
    public void defaultComponentHasExpectedValues() {
        TransformComponent transformComponent = new TransformComponent.Builder().build();

        assertThat(transformComponent.origin, equalTo(new Vector2(0f, 0f)));
        assertThat(transformComponent.scale, equalTo(new Vector2(1f, 1f)));
        assertThat(transformComponent.getRotation(), equalTo(0f));
    }

    @Test
    public void componentConsumesInputVectorsDefensively() {
        Vector2 sneakyOrigin = new Vector2(0f, 0f);
        Vector2 sneakyScale = new Vector2(1f, 1f);

        TransformComponent transformComponent =
            new TransformComponent.Builder().setOrigin(sneakyOrigin).setScale(sneakyScale).build();

        sneakyOrigin.set(123f, -456f);
        assertThat(transformComponent.origin, equalTo(new Vector2(0f, 0f)));

        sneakyScale.set(123f, -456f);
        assertThat(transformComponent.scale, equalTo(new Vector2(1f, 1f)));
    }

    @Test
    public void settingComponentValuesWorks() {
        TransformComponent transformComponent = new TransformComponent.Builder().build();

        Vector2 newOrigin = new Vector2(123f, -456f);
        transformComponent.origin.set(newOrigin);
        assertThat(transformComponent.origin, equalTo(newOrigin));

        Vector2 newScale = new Vector2(-123f, 456f);
        transformComponent.scale.set(newScale);
        assertThat(transformComponent.scale, equalTo(newScale));

        float newRotation = 30f;
        transformComponent.setRotation(newRotation);
        assertThat(transformComponent.getRotation(), equalTo(newRotation));
    }

}