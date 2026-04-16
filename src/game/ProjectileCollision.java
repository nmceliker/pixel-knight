package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import java.util.HashSet;
import java.util.Set;

public class ProjectileCollision implements CollisionListener {

    private Projectile projectile;
    private Body owner;
    private Set<Body> hitBodies = new HashSet<>();
    private boolean destroyed = false;

    public ProjectileCollision(Projectile projectile, Body owner) {
        this.projectile = projectile;
        this.owner = owner;
        if (owner != null) hitBodies.add(owner); // avoid friendly-fire on spawn overlap
    }

    @Override
    public void collide(CollisionEvent e) {
        if (destroyed) return;
        Body other = e.getOtherBody();

        if (other == owner) {
            destroyed = true;
            projectile.destroy();
            return;
        }

        if (other instanceof Character) {
            if (!hitBodies.contains(other)) {
                Character player = (Character) other;
                player.minusHealth(1);

                Vec2 playerPos = player.getPosition();
                Vec2 projectilePos = projectile.getPosition();

                float knockbackX = playerPos.x < projectilePos.x ? -4f : 4f;
                player.setLinearVelocity(new Vec2(knockbackX, 3f));

                hitBodies.add(other);
            }
            destroyed = true;
            projectile.destroy();
            return;
        }

        if (other instanceof Enemy) {
            if (!hitBodies.contains(other)) {
                Enemy enemy = (Enemy) other;
                enemy.takeDamage(1);

                Vec2 enemyPos = enemy.getPosition();
                Vec2 projectilePos = projectile.getPosition();

                float knockbackX = enemyPos.x < projectilePos.x ? -4f : 4f;
                enemy.setLinearVelocity(new Vec2(knockbackX, 3f));

                hitBodies.add(other);
            }
            destroyed = true;
            projectile.destroy();
            return;
        }

        destroyed = true;
        projectile.destroy();
    }
}