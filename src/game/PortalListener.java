package game;

import city.cs.engine.*;

public class PortalListener implements SensorListener {

    private Game game;

    public PortalListener(Game game) {
        this.game = game;
    }

    @Override
    public void beginContact(SensorEvent e) {
        if (e.getContactBody() instanceof Character) {
            System.out.println("Portal entered! Loading next level...");
            game.loadNextLevel();
        }
    }

    @Override
    public void endContact(SensorEvent e) {
    }
}