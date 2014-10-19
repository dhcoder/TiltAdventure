package tiltadv.globals;

import dhcoder.libgdx.entity.EntityManager;

/**
 * Collection of entity template IDs. See also {@link EntityManager#registerTemplate(Enum, EntityManager.EntityCreator)}
 */
public enum EntityId {
    // Game entities
    BOUNDARY,
    BOULDER,
    GRAVITY_WELL,
    PLAYER,
    PLAYER_SWORD,
    PLAYER_SENSOR,
    OCTO,
    OCTO_ROCK,
    TARGET_INDICATOR,

    // UI entities
    FPS_INDICATOR,
    TILT_INDICATOR,
}
