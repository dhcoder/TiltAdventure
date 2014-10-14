package tiltadv.components.combat;

import com.badlogic.gdx.math.Vector2;
import dhcoder.libgdx.entity.AbstractComponent;
import dhcoder.libgdx.entity.Entity;
import dhcoder.support.time.Duration;
import tiltadv.components.display.SpriteComponent;
import tiltadv.components.body.MotionComponent;
import tiltadv.memory.Pools;

import static dhcoder.support.contract.ContractUtils.requireNonNull;

/**
 * Component that maintains logic for how an entity responds to taking damage.
 */
public final class HealthComponent extends AbstractComponent {

    public interface Listener {
        void onHurt();

        void onDied();
    }

    private static final float KNOCKBACK_MAGNITUTDE = 150f;
    private static final Duration STOP_DURATION = Duration.fromSeconds(.3f);
    private static final Duration DEFAULT_INVINCIBLE_DURATION = Duration.fromSeconds(2f);

    private final Duration invincibleDuration = Duration.zero();
    private final Duration invincibleElapsed = Duration.zero();
    public int health;
    private boolean isInvincible;
    private Listener listener;
    private DefenseComponent defenseComponent;
    private MotionComponent motionComponent;
    private SpriteComponent spriteComponent;
    private KnockbackComponent knockbackComponent;

    public HealthComponent() { reset(); }

    public HealthComponent setListener(final Listener listener) {
        this.listener = listener;
        return this;
    }

    public HealthComponent setHealth(final int health) {
        this.health = health;
        return this;
    }

    public HealthComponent setInvicibilityDuration(final Duration duration) {
        invincibleDuration.setFrom(duration);
        return this;
    }

    @Override
    public void initialize(final Entity owner) {
        requireNonNull(listener, "HealthComponent listener must be set");

        defenseComponent = owner.requireComponent(DefenseComponent.class);
        motionComponent = owner.requireComponent(MotionComponent.class);
        spriteComponent = owner.requireComponent(SpriteComponent.class);
        knockbackComponent = owner.requireComponent(KnockbackComponent.class);
    }

    @Override
    public void update(final Duration elapsedTime) {
        if (isInvincible) {
            invincibleElapsed.add(elapsedTime);
            if (invincibleElapsed.getSeconds() >= invincibleDuration.getSeconds()) {
                invincibleElapsed.setZero();
                setInvincible(false);
            }
        }
    }

    @Override
    public void reset() {
        health = 1;
        invincibleElapsed.setZero();
        invincibleDuration.setFrom(DEFAULT_INVINCIBLE_DURATION);
        isInvincible = false;
        listener = null;
        defenseComponent = null;
        motionComponent = null;
        spriteComponent = null;
        knockbackComponent = null;
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

        if (health > 0) {
            listener.onHurt();
        }
        else {
            listener.onDied();
        }

        return true;
    }

    private void setInvincible(final boolean isInvincible) {

        if (invincibleDuration.isZero()) {
            return; // Invincibility disabled for this component
            // TODO: Move invinciblity logic to player behavior component?
        }

        spriteComponent.setAlpha(isInvincible ? .5f : 1f);
        this.isInvincible = isInvincible;
    }

    private void knockback(final Vector2 direction) {
        Vector2 impulse =
            Pools.vector2s.grabNew().set(direction).scl(KNOCKBACK_MAGNITUTDE * knockbackComponent.getMultiplier());
        motionComponent.setImpulse(impulse, STOP_DURATION);
        Pools.vector2s.free(impulse);

        setInvincible(true);
    }
}
