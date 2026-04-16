package game;

import city.cs.engine.*;

public class ThrowingKnife extends Projectile {

    private static final Shape SHAPE = new BoxShape(0.3f, 0.6f);

    public ThrowingKnife(World world, boolean facingLeft) {
        super(world, SHAPE, facingLeft ? "data/assets/throwing_knife_left.png" : "data/assets/throwing_knife_right.png", 1.2f);
    }
}