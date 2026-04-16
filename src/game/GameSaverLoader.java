package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import java.io.*;
import java.util.*;

public class GameSaverLoader {

    public static void saveGame(Game game, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Level," + game.getCurrentLevel() + "\n");
            
            Character character = game.getCharacter();
            if (character != null) {
                writer.write("Character," + character.getPosition().x + "," + character.getPosition().y + ","
                        + character.getHealth() + "," + character.getCoinCount() + "," + character.hasDoubleJump() + "\n");
            }
            
            for (StaticBody body : game.getWorld().getStaticBodies()) {
                if (body instanceof Coin) {
                    writer.write("Coin," + body.getPosition().x + "," + body.getPosition().y + "\n");
                } else if (body instanceof DoubleJumpShoes) {
                    writer.write("DoubleJumpShoes," + body.getPosition().x + "," + body.getPosition().y + "\n");
                } else if (body instanceof HealthMask) {
                    writer.write("HealthMask," + body.getPosition().x + "," + body.getPosition().y + "\n");
                } else if (body instanceof Portal) {
                    writer.write("Portal," + body.getPosition().x + "," + body.getPosition().y + "\n");
                }
            }
            
            writer.write("PortalSpawned," + game.isPortalSpawned() + "\n");
            
            for (DynamicBody body : game.getWorld().getDynamicBodies()) {
                if (body instanceof Enemy) {
                    Enemy enemy = (Enemy) body;
                    writer.write("Enemy," + enemy.getPosition().x + "," + enemy.getPosition().y + "," 
                            + enemy.getHealth() + "," + enemy.isDropsLoot() + "," + enemy.isDropsCoins() + "\n");
                }
            }
            System.out.println("Game saved successfully to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGame(Game game, String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Save file does not exist.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            int level = 1;
            float charX = 0, charY = 0;
            int health = 3, coins = 0;
            boolean doubleJump = false;
            boolean portalSpawned = false;
            
            List<Vec2> savedCoins = new ArrayList<>();
            List<Vec2> savedShoes = new ArrayList<>();
            List<Vec2> savedMasks = new ArrayList<>();
            List<Vec2> savedPortals = new ArrayList<>();
            List<String> enemyData = new ArrayList<>();
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts[0].equals("Level")) {
                    level = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("Character")) {
                    charX = Float.parseFloat(parts[1]);
                    charY = Float.parseFloat(parts[2]);
                    health = Integer.parseInt(parts[3]);
                    coins = Integer.parseInt(parts[4]);
                    doubleJump = Boolean.parseBoolean(parts[5]);
                } else if (parts[0].equals("Coin")) {
                    savedCoins.add(new Vec2(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
                } else if (parts[0].equals("DoubleJumpShoes")) {
                    savedShoes.add(new Vec2(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
                } else if (parts[0].equals("HealthMask")) {
                    savedMasks.add(new Vec2(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
                } else if (parts[0].equals("Portal")) {
                    savedPortals.add(new Vec2(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
                } else if (parts[0].equals("PortalSpawned")) {
                    portalSpawned = Boolean.parseBoolean(parts[1]);
                } else if (parts[0].equals("Enemy")) {
                    enemyData.add(line);
                }
            }
            
            game.startLevel(level);
            
            Character character = game.getCharacter();
            if (character != null) {
                character.setPosition(new Vec2(charX, charY));
                character.setHealth(health);
                character.setCoins(coins);
                if (doubleJump) {
                    character.giveDoubleJump();
                }
            }
            
            // destroy current existing stuff (coins and masks)
            for (StaticBody body : game.getWorld().getStaticBodies()) {
                if (body instanceof Coin || body instanceof DoubleJumpShoes || body instanceof HealthMask || body instanceof Portal) {
                    body.destroy();
                }
            }
            
            for (DynamicBody body : game.getWorld().getDynamicBodies()) {
                if (body instanceof Enemy) {
                    body.destroy();
                }
            }
            
            // re-spawn coins that aren't picked up
            for (Vec2 pos : savedCoins) {
                Coin coin = new Coin(game.getWorld());
                coin.setPosition(pos);
                coin.getSensor().addSensorListener(new CoinListener(coin, game));
            }
            
            for (Vec2 pos : savedShoes) {
                DoubleJumpShoes shoe = new DoubleJumpShoes(game.getWorld());
                shoe.setPosition(pos);
                shoe.addCustomListener(new ShoePickupListener(character));
            }
            
            game.setMask(null);
            for (Vec2 pos : savedMasks) {
                HealthMask mask = new HealthMask(game.getWorld(), pos.x, pos.y);
                game.setMask(mask);
            }
            
            for (Vec2 pos : savedPortals) {
                Portal portal = new Portal(game.getWorld());
                portal.setPosition(pos);
                portal.getSensor().addSensorListener(new PortalListener(game));
            }
            
            for (String eLine : enemyData) {
                String[] parts = eLine.split(",");
                Enemy enemy = new Enemy(game.getWorld(), character);
                enemy.setPosition(new Vec2(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
                enemy.setHealth(Integer.parseInt(parts[3]));
                enemy.setDropsLoot(Boolean.parseBoolean(parts[4]));
                enemy.setDropsCoins(Boolean.parseBoolean(parts[5]));
            }
            
            game.setPortalSpawned(portalSpawned);
            
            System.out.println("Game loaded successfully from " + filename);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}