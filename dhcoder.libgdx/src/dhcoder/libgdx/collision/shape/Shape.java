package dhcoder.libgdx.collision.shape;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dhcoder.support.memory.Poolable;

/**
 * Base class for shapes. Subclasses should encapsulate the size and bounds of a shape but not its location - that way,
 * the same shape can be reused for collision tests in many different locations.
 */
public interface Shape extends Poolable {
    boolean containsPoint(float x, float y);

    float getLeft(float xOrigin);

    float getBottom(float yOrigin);

    float getRight(float xOrigin);

    float getTop(float yOrigin);

    float getHalfWidth();

    float getHalfHeight();

    void render(ShapeRenderer renderer, float x, float y);
}
