package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;



public class Character extends Walker {

    private Menu myMenu;
    private int health = 3;
    private final int maxHealth = 3;
    private boolean facingLeft = true;
    private BodyImage imageLeft;
    private BodyImage imageRight;
    private boolean hasDoubleJump = false;
    private int jumpCount = 0;
    private int coinCount = 0;
    private int knifeCount = 0; // throwing knives available

    public Character(World world, Menu gameMenu) {
        super(world, new BoxShape(0.6f, 1.6f));
        this.myMenu = gameMenu;
        imageLeft = new BodyImage("data/characters/theknight-left.png", 4.0f);
        imageRight = new BodyImage("data/characters/theknight-right.png", 4.0f);
        this.facingLeft = true;
        this.setFacing();

        // start with 2 knives
        this.knifeCount = 2;

        this.addCollisionListener(new CollisionListener() {
            @Override
            public void collide(CollisionEvent e) {
                if (e.getOtherBody() instanceof Platform) {
                    resetJumps();
                }
            }
        });
    }



    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean hasDoubleJump() {
        return hasDoubleJump;
    }

    public void setCoins(int coins) {
        this.coinCount = coins;
    }

    public int getMaxHealth() {
        return maxHealth;
    }


    public void playDamageSound() {
        if (this.getHealth() > 0 && myMenu != null && myMenu.getDamageSound() != null) {
            myMenu.getDamageSound().play();
        }
    }

    public void minusHealth(int damageAmount) {
        health -= damageAmount;
        myMenu.getDamageSound().play();
        if (health <= 0) {
            myMenu.getDeathSound().play();
            if (Game.currentGame != null) {
                Game.currentGame.stopGameMusic();
            }
            die();
            Game.onPlayerDeath();
        }
    }

    //function to increase health
    public void plusHealth() {
        if (health < maxHealth) {
            health++;
        }
    }

    private boolean deathSoundPlayed = false;
    private boolean deathMenuTriggered = false;

    public void die() {
        setLinearVelocity(new Vec2(0, 0));
        stopWalking();
    }

    // function to determine if the character is dead
    public boolean isDead() {
        boolean dead = health <= 0 || this.getPosition().y < -20;

        if (dead) {
            if (!deathSoundPlayed) {
                if (myMenu != null && myMenu.getDeathSound() != null) myMenu.getDeathSound().play();
                deathSoundPlayed = true;
                if (Game.currentGame != null) {
                    Game.currentGame.stopGameMusic();
                }
            }
            if (this.getPosition().y < -20 && !deathMenuTriggered) {
                deathMenuTriggered = true;
                Game.onPlayerDeath();
            }
            return true;
        }

        return false;
    }

    public void setFacing() {
        this.removeAllImages();

        if (facingLeft) {
            this.addImage(imageLeft);
        } else {
            this.addImage(imageRight);
        }
    }

    public boolean isFacingLeft() {
        return facingLeft;
    }

    public void setFacingLeft(boolean isLeft) {
        this.facingLeft = isLeft;
        this.setFacing();
    }
    public void attack() {
        new SlashAttack(this.getWorld(), this);

        if (myMenu.getSlashSound() != null) {
            myMenu.getSlashSound().play();
        }
    }

    public void throwKnife() {
        if (knifeCount <= 0) return;
        knifeCount--;

        ThrowingKnife knife = new ThrowingKnife(this.getWorld(), this.isFacingLeft());
        // spawn the knife in front of the character
        org.jbox2d.common.Vec2 spawnOffset = new org.jbox2d.common.Vec2(this.isFacingLeft() ? -1.5f : 1.5f, 0f);
        org.jbox2d.common.Vec2 spawnPos = this.getPosition().add(spawnOffset);
        knife.setPosition(spawnPos);

        System.out.println("Throw method reached!"); // Debug 1

        if (myMenu.getThrowSound() != null) {
            myMenu.getThrowSound().play();
        }
        float speed = 12f;
        knife.setLinearVelocity(new org.jbox2d.common.Vec2(this.isFacingLeft() ? -speed : speed, 0f));
        // ignore collisions with the character
        knife.addCollisionListener(new ProjectileCollision(knife, this));

    }

    public void giveDoubleJump() {
        hasDoubleJump = true;
        System.out.println("Double Jump Unlocked!");
    }

    public void doDoubleJump() {
        if (jumpCount == 0) {
            this.jump(11.5f);
            jumpCount++;
        }
        else if (jumpCount == 1 && hasDoubleJump) {
            this.setLinearVelocity(new org.jbox2d.common.Vec2(this.getLinearVelocity().x, 10f));
            jumpCount++;
        }
    }

    public void resetJumps() {
        jumpCount = 0;
    };

    public void addCoin() {
        coinCount++;
        System.out.println("Got a coin! Total coins: " + coinCount);

    }

    public int getCoinCount() {
        return coinCount;
    }

    public void resetCoins() {
        coinCount = 0;
    }

    public int getKnifeCount() {
        return knifeCount;
    }

    public void addKnives(int n) {
        knifeCount += n;
        if (knifeCount > 2) knifeCount = 2; // cap to 2
    }

    public void playEnemyHitSound() {
        if (myMenu.getEnemyHitSound() != null) {
            myMenu.getEnemyHitSound().play();
        }
    }



}