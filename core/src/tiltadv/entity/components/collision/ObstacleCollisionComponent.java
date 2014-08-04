package tiltadv.entity.components.collision;

import dhcoder.support.collision.shape.Shape;
import tiltadv.globals.Group;

/**
 * Component that maintains the collision logic for all obstacles.
 */
public final class ObstacleCollisionComponent extends CollisionComponent {

    public ObstacleCollisionComponent(final Shape shape) {
        super(Group.OBSTACLES, shape);
    }
}
