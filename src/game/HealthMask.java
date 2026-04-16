package game;

import city.cs.engine.BodyImage;
import city.cs.engine.CircleShape;
import city.cs.engine.GhostlyFixture;
import city.cs.engine.Shape;
import city.cs.engine.StaticBody;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class HealthMask extends StaticBody {
    public HealthMask(World world, float x, float y) {
        super(world);

        Shape healthMaskShape = new CircleShape(2f);

        // ghostlyfixture to make sure the character can pass through the object (mask/health)
        new GhostlyFixture(this, healthMaskShape);

        this.setPosition(new Vec2(x, y));
        this.addImage(new BodyImage("data/assets/health-icon.png", 2f));
    }
}