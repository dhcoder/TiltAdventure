package tiltadv.globals;

import dhcoder.libgdx.entity.EntityManager;

/**
 * Collection of entity template IDs. See also {@link EntityManager#registerTemplate(Enum, EntityManager.EntityCreator)}
 */
public enum EntityId {
    // Game entities
    BOULDER,
    BOUNDARY,
    CAMERA_ENTITY,
    GRAVITY_WELL,
    OCTO,
    OCTO_ROCK,
    PLAYER,
    PLAYER_SENSOR,
    PLAYER_SWORD,
    TARGET_INDICATOR,

    // UI entities
    FPS_INDICATOR,
    TILT_INDICATOR,
}
