package game;

import city.cs.engine.*;

public class Portal extends StaticBody {

    private Sensor portalSensor;

    public Portal(World world) {
        super(world);

        Shape shape = new BoxShape(1f, 2f);
        this.addImage(new BodyImage("data/assets/lvl2_portal_left.gif", 3.0f));

        portalSensor = new Sensor(this, shape);
    }

    public Sensor getSensor() {
        return portalSensor;
    }
}