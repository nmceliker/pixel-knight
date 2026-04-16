package game;

import city.cs.engine.*;

public class KnifePickupListener implements SensorListener {

    private Character player;

    public KnifePickupListener(Character player) {
        this.player = player;
    }

    @Override
    public void beginContact(SensorEvent e) {
        if (e.getContactBody() == player) {
            player.addKnives(1);
            e.getSensor().getBody().destroy();
        }
    }

    @Override
    public void endContact(SensorEvent e) { }
}