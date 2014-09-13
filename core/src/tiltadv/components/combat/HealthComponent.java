package tiltadv.components.combat;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.opt.Opt;
import dhcoder.support.time.Duration;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.model.MotionComponent;
import tiltadv.memory.Pools;

/**
 * Component that maintains logic for how an entity responds to taking damage.
 */
public final class HealthComponent extends AbstractComponent {

    public interface Listener {
        void onHurt();

        void onDied();
    }

    private static final float KNOCKBACK_MULTIPLIER = 150f;
    private static final Duration STOP_DURATION = Duration.fromSeconds(.3f);
    private static final Duration INVINCIBLE_DURATION = Duration.fromSeconds(3f);

    private final Duration invincibleDuration = Duration.zero();
    private final Opt<Listener> listenerOpt = Opt.withNoValue();
    public int health;
    private boolean isInvincible;
    private DefenseComponent defenseComponent;
    private MotionComponent motionComponent;
    private SpriteComponent spriteComponent;

    public HealthComponent() { reset(); }

    public Listener getListener() {
        return listenerOpt.getValue();
    }

    public HealthComponent setListener(final Listener listener) {
        listenerOpt.set(listener);
        return this;
    }

    public int getHealth() {
        return health;
    }

    public HealthComponent setHealth(final int health) {
        this.health = health;
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        defenseComponent = owner.requireComponent(DefenseComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);
        spriteComponent = owner.requireComponent(SpriteComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        if (isInvincible) {
            invincibleDuration.add(elapsedTime);
            if (invincibleDuration.getSeconds() >= INVINCIBLE_DURATION.getSeconds()) {
                invincibleDuration.setZero();
                setInvincible(false);
            }
        }
    }

    @Override
    public void reset() {
        health = 1;
        invincibleDuration.setZero();
        isInvincible = false;
        defenseComponent = null;
    }

    public boolean canTakeDamage() {
        return !isInvincible;
    }

    public boolean takeDamage(final Vector2 damageVector, final int damage) {
        if (!canTakeDamage()) {
            return false;
        }

        knockback(damageVector);
        health = Math.max(0, health - defenseComponent.reduceDamage(damage));

        Listener listener = listenerOpt.getValue();
        if (health > 0) {
            listener.onHurt();
        }
        else {
            listener.onDied();
        }

        return true;
    }

    private void setInvincible(final boolean isInvincible) {
        spriteComponent.setAlpha(isInvincible ? .5f : 1f);
        this.isInvincible = isInvincible;
    }

    private void knockback(final Vector2 direction) {
        Vector2 impulse = Pools.vector2s.grabNew().set(direction).scl(KNOCKBACK_MULTIPLIER);
        motionComponent.setImpulse(impulse, STOP_DURATION);
        Pools.vector2s.free(impulse);

        setInvincible(true);
    }
}