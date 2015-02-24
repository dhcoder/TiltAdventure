package tiltadv.components.body;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import tiltadv.components.dynamics.PositionComponent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class PositionComponentTest {

    @Test
    public void defaultComponentHasExpectedValues() {
        PositionComponent positionComponent = new PositionComponent();

        assertThat(positionComponent.getPosition(), equalTo(new Vector2(0f, 0f)));
//        assertThat(positionComponent.getScale(), equalTo(new Vector2(1f, 1f)));
//        assertThat(positionComponent.getRotation().getDegrees(), equalTo(0f));
    }

//    @Test
//    public void componentConsumesInputVectorsDefensively() {
//        Vector2 sneakyTranslate = new Vector2(0f, 0f);
//        Vector2 sneakyScale = new Vector2(1f, 1f);
//
//        PositionComponent positionComponent =
//            new PositionComponent().setPosition(sneakyTranslate).setScale(sneakyScale);
//
//        sneakyTranslate.set(123f, -456f);
//        assertThat(positionComponent.getPosition(), equalTo(new Vector2(0f, 0f)));
//
//        sneakyScale.set(123f, -456f);
//        assertThat(positionComponent.getScale(), equalTo(new Vector2(1f, 1f)));
//    }
//
//    @Test
//    public void settingComponentValuesWorks() {
//        PositionComponent positionComponent = new PositionComponent();
//
//        Vector2 newTranslate = new Vector2(123f, -456f);
//        positionComponent.setPosition(newTranslate);
//        assertThat(positionComponent.getPosition(), equalTo(newTranslate));
//
//        Vector2 newScale = new Vector2(-123f, 456f);
//        positionComponent.setScale(newScale);
//        assertThat(positionComponent.getScale(), equalTo(newScale));
//
//        positionComponent.setRotation(Angle.fromDegrees(30f));
//        assertThat(positionComponent.getRotation().getDegrees(), equalTo(30f));
//    }

}