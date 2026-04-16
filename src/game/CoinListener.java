package game;

import city.cs.engine.*;

public class CoinListener implements SensorListener {

    private Coin coin;
    private Game game;

    public CoinListener(Coin coin, Game game) {
        this.coin = coin;
        this.game = game;
    }

    @Override
    public void beginContact(SensorEvent e) {
        if (e.getContactBody() instanceof Character) {
            Character player = (Character) e.getContactBody();

            player.addCoin();
            coin.destroy();

            if (player.getCoinCount() >= 3) {
                game.portalSpawner();
            }
        }
    }

    @Override
    public void endContact(SensorEvent e) {}
}