package game;

import city.cs.engine.*;

public class Coin extends StaticBody {

    private Sensor coinSensor;

    private static final Shape shape = new CircleShape(0.5f);
    private static final BodyImage image = new BodyImage("data/assets/coin.png", 1.0f);

    public Coin(World world) {
        super(world);
        addImage(image);

        coinSensor = new Sensor(this, shape);
    }

    public Sensor getSensor() {
        return coinSensor;
    }
}