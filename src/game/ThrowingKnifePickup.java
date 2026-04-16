package game;

import city.cs.engine.*;

public class ThrowingKnifePickup extends StaticBody {

    private Sensor sensor;
    private static final BodyImage image = new BodyImage("data/assets/throwing_knife_right.png", 1.6f);

    public ThrowingKnifePickup(World world) {
        super(world);
        Shape shape = new BoxShape(0.4f, 0.2f);
        sensor = new Sensor(this, shape);
        this.addImage(image);
    }

    public void addCustomListener(SensorListener listener) {
        sensor.addSensorListener(listener);
    }
}