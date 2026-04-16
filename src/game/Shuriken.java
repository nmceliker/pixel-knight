package game;

import city.cs.engine.*;

public class Shuriken extends Projectile {

    private static final Shape SHAPE = new CircleShape(0.5f);

    public Shuriken(World world) {
        super(world, SHAPE, "data/assets/shiruken.png", 2.0f);
    }
}