package tiltadv.components.collision;

import tiltadv.globals.Group;

/**
 * Component that maintains the collision logic for all obstacles.
 */
public final class ObstacleCollisionComponent extends CollisionComponent {

    public ObstacleCollisionComponent() {
        super(Group.OBSTACLES);
    }
}
