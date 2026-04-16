package game;

import city.cs.engine.*;

public class Enemy extends Walker implements StepListener {
    private int health = 3;
    private Character player;
    private BodyImage imageLeft;
    private BodyImage imageRight;
    private AttachedImage currentImage;
    private boolean facingLeft = true;
    private boolean isStunned = false;
    private BodyImage imageHitLeft;
    private BodyImage imageHitRight;
    private BodyImage imageTopLeft;
    private BodyImage imageTopRight;
    private boolean lookingUp = false;
    private BodyImage fullHealth;
    private BodyImage halfHealth;
    private BodyImage lowHealth;
    private boolean dropsLoot;
    private boolean dropsCoins = false;

    public void setDropsCoins(boolean dropsCoins) {
        this.dropsCoins = dropsCoins;
    }

    public boolean isDropsCoins() {
        return dropsCoins;
    }

    public void setDropsLoot(boolean dropsLoot) {
        this.dropsLoot = dropsLoot;
    }

    public boolean isDropsLoot() {
        return dropsLoot;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    // enemy constructor
    public Enemy(World world, Character player) {
        super(world, new BoxShape(1f, 1.5f));
        this.player = player;

        imageLeft = new BodyImage("data/characters/little_my_left.png", 3f);
        imageRight = new BodyImage("data/characters/little_my_right.png", 3f);
        imageHitLeft = new BodyImage("data/characters/little_my_left_red.png", 3f);
        imageHitRight = new BodyImage("data/characters/little_my_right_red.png", 3f);
        imageTopLeft = new BodyImage("data/characters/little_my_top_left.png", 3f);
        imageTopRight = new BodyImage("data/characters/little_my_top_right.png", 3f);
        fullHealth = new BodyImage("data/assets/full_health.png", 1f);
        halfHealth = new BodyImage("data/assets/half_health.png", 1f);
        lowHealth = new BodyImage("data/assets/low_health.png", 1f);

        this.setFacing();
        world.addStepListener(this);
        this.addCollisionListener(new CollisionListener() {
            @Override
            public void collide(CollisionEvent e) {
                if (e.getOtherBody() instanceof Character character) {
                    character.minusHealth(1);
                    // knockback
                    float direction = Math.signum(character.getPosition().x - Enemy.this.getPosition().x);
                    if (direction == 0) direction = 1;
                    character.setLinearVelocity(new org.jbox2d.common.Vec2(direction * 8, 6));
                }
            }
        });
    }

    @Override
    public void destroy() {
        this.getWorld().removeStepListener(this);
        super.destroy();
    }

    // enemy AI
    @Override
    public void preStep(StepEvent e) {

        if (isStunned) {
            return;
        }

        float playerX = player.getPosition().x;
        float enemyX = this.getPosition().x;
        float playerY = player.getPosition().y;
        float enemyY = this.getPosition().y;

        boolean shouldFaceLeft = playerX < enemyX;

        boolean shouldLookUp = playerY > enemyY + 4.0f;

        if (this.facingLeft != shouldFaceLeft || this.lookingUp != shouldLookUp) {
            this.facingLeft = shouldFaceLeft;
            this.lookingUp = shouldLookUp;
            setFacing();
        }

        if (playerX < enemyX - 0.5f) {
            this.startWalking(-3); // Walk Left
        }
        else if (playerX > enemyX + 0.5f) {
            this.startWalking(3);  // Walk Right
        }
        else {
            this.stopWalking();
        }
    }

    @Override
    public void postStep(StepEvent e) {
        // leave this blank, it is required by the StepListener
    }

    // correct image orientation
    private void setFacing() {
        this.removeAllImages();

        if (facingLeft) {
            if (lookingUp) {
                currentImage = this.addImage(imageTopLeft);
            } else {
                currentImage = this.addImage(imageLeft);
            }
        } else {
            if (lookingUp) {
                currentImage = this.addImage(imageTopRight);
            } else {
                currentImage = this.addImage(imageRight);
            }
        }
        this.enemyHealthBar();
    }

    public void takeDamage(int damage) {
        if (isStunned) return;

        health = health - damage;
        System.out.println("Enemy hit! Remaining HP: " + health);

        if (player != null) {
            player.playEnemyHitSound();
        }

        // enemy dead
        if (health <= 0) {
            die();
            return;
        }

        // damage taken
        isStunned = true;
        this.stopWalking();

        float knockbackDirection = (player.getPosition().x < this.getPosition().x) ? 2.5f : -2.5f;
        this.setLinearVelocity(new org.jbox2d.common.Vec2(knockbackDirection, 3f));

        this.removeAllImages();
        if (facingLeft) {
            currentImage = this.addImage(imageHitLeft);
            currentImage.setRotation(0.4f); // tilt backwards
        } else {
            currentImage = this.addImage(imageHitRight);
            currentImage.setRotation(-0.4f); // tilt backwards
        }

        this.enemyHealthBar();

        // 1 sec time to recover
        javax.swing.Timer timer = new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // only recover when alive
                if (Enemy.this.health > 0) {
                    isStunned = false;
                    setFacing();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void die() {
        System.out.println("Little My Defeated!");

        if (dropsLoot) {
            org.jbox2d.common.Vec2 deathPos = this.getPosition();
            World world = this.getWorld();

            DoubleJumpShoes magicShoe = new DoubleJumpShoes(world);
            magicShoe.setPosition(new org.jbox2d.common.Vec2(deathPos.x, deathPos.y));

            ShoePickupListener shoeListener = new ShoePickupListener(player);
            magicShoe.addCustomListener(shoeListener);        }

        if (dropsCoins) {
            org.jbox2d.common.Vec2 deathPos = this.getPosition();
            World world = this.getWorld();

            Coin coin1 = new Coin(world);
            coin1.setPosition(new org.jbox2d.common.Vec2(deathPos.x - 1, deathPos.y + 1));
            coin1.getSensor().addSensorListener(new CoinListener(coin1, Game.currentGame));

            Coin coin2 = new Coin(world);
            coin2.setPosition(new org.jbox2d.common.Vec2(deathPos.x, deathPos.y + 2));
            coin2.getSensor().addSensorListener(new CoinListener(coin2, Game.currentGame));

            Coin coin3 = new Coin(world);
            coin3.setPosition(new org.jbox2d.common.Vec2(deathPos.x + 1, deathPos.y + 1));
            coin3.getSensor().addSensorListener(new CoinListener(coin3, Game.currentGame));
        }

        this.destroy();
    }

    private void enemyHealthBar() {
        AttachedImage currentEnemyHP = null;
        if (health >= 3) {
            currentEnemyHP = this.addImage(fullHealth);
        } else if (health == 2) {
            currentEnemyHP = this.addImage(halfHealth);
        } else {
            currentEnemyHP = this.addImage(lowHealth);
        }
        currentEnemyHP.setRotation(0f);
        if (currentEnemyHP != null) {
            currentEnemyHP.setOffset(new org.jbox2d.common.Vec2(0, 2f));
        }
    }

    public Enemy(World world, Character player, boolean dropsLoot) {
        super(world, new BoxShape(0.6f, 1.5f));
        this.player = player;
        this.dropsLoot = dropsLoot;
    }
}