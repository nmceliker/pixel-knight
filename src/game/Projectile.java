package game;

import city.cs.engine.*;

public abstract class Projectile extends DynamicBody {

    protected Projectile(World world, Shape shape, String imagePath, float imageHeight) {
        super(world, shape);
        this.addImage(new BodyImage(imagePath, imageHeight));
        this.setGravityScale(0);
    }
}