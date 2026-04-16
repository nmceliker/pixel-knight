package game;

import city.cs.engine.*;

public class ShoePickupListener implements SensorListener {

    private Character player;

    public ShoePickupListener(Character player) {
        this.player = player;
    }

    @Override
    public void beginContact(SensorEvent e) {
        if (e.getContactBody() == player) {
            player.giveDoubleJump();         // Unlock the ability!
            e.getSensor().getBody().destroy(); // Destroy the shoe
        }
    }

    @Override
    public void endContact(SensorEvent e) {
    }
}