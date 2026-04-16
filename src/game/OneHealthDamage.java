package game;

import city.cs.engine.PolygonShape;
import city.cs.engine.StaticBody;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class OneHealthDamage extends StaticBody {
    private final int damageAmount = 1;

    public int getDamageAmount() {
        return damageAmount;
    }

    public OneHealthDamage(World world, float x, float y) {
        super(world, new PolygonShape(-0.5f, -1f, 0.5f, -1f, 0f, 1f));
        this.setPosition(new Vec2(x, y));
    }
}