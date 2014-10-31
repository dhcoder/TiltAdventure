package tiltadv.components.hierarchy.children;

import dhcoder.libgdx.entity.Entity;
import tiltadv.components.hierarchy.ChildrenComponent;

/**
 * Class that contains a list of children entities for this {@link Entity}.
 */
public final class PlayerChildrenComponent extends ChildrenComponent {

    private Entity sensorEntity;
    private Entity swordEntity = null;

//    public PlayerChildrenComponent setSensorEntity(final Entity sensorEntity) {
//        this.sensorEntity = sensorEntity;

    public Entity getSwordEntity() {
        return swordEntity;
    }

    @Override
    protected void handleInitialize(final Entity owner) {
//        swordEntity = owner.getManager().newEntityFromTemplate(EntityId.PLAYER_SWORD);
//        add(swordEntity);
//
//        sensorEntity = owner.getManager().newEntityFromTemplate(EntityId.PLAYER_SENSOR);
//        add(sensorEntity);

//        Entity testEntity = owner.getManager().newEntity();
//        testEntity.addComponent(ParentComponent.class);
//        testEntity.addComponent(TransformComponent.class);
//        testEntity.addComponent(SpriteComponent.class).setTextureRegion(Tiles.ROCK);
//        testEntity.addComponent(OffsetComponent.class).setOffset(Pools.vector2s.grabNew());
//        testEntity.addComponent(TestCollisionComponent.class).setShape(new Circle(4f));
//        Pools.vector2s.freeCount(1);
//        add(testEntity);

//        Entity testEntity2 = owner.getManager().newEntity();
//        testEntity2.addComponent(ParentComponent.class);
//        testEntity2.addComponent(TransformComponent.class);
//        testEntity2.addComponent(SpriteComponent.class).setTextureRegion(Tiles.ROCK);
//        testEntity2.addComponent(SizeComponent.class).setSizeFrom(Tiles.ROCK);
//        testEntity2.addComponent(OffsetComponent.class).setOffset(Pools.vector2s.grabNew().set(-40f, 40f));
//        testEntity2.addComponent(TestCollisionComponent.class).setShape(new Circle(4f));
//        Pools.vector2s.freeCount(1);
//        add(testEntity2);
    }

    @Override
    protected void handleReset() {
//        sensorEntity = null;
        swordEntity = null;
    }
}
