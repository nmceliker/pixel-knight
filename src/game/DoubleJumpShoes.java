package game;

import city.cs.engine.*;

public class DoubleJumpShoes extends StaticBody {

    private static final BodyImage shoeImage = new BodyImage("data/assets/dj_boots.png", 1.5f);

    private Sensor shoeSensor;

    public DoubleJumpShoes(World world) {
        super(world);

        Shape shape = new BoxShape(0.5f, 0.5f);
        shoeSensor = new Sensor(this, shape);

        this.addImage(shoeImage);
    }
    public void addCustomListener(SensorListener listener) {
        shoeSensor.addSensorListener(listener);
    }
}