package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SlashAttack extends StaticBody {

    private static final BodyImage[] leftFrames = {
            new BodyImage("data/assets/slashattacks/slash-left/sl-frame1.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-left/sl-frame2.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-left/sl-frame3.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-left/sl-frame4.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-left/sl-frame5.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-left/sl-frame6.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-left/sl-frame7.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-left/sl-frame8.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-left/sl-frame9.png", 3f),

    };

    private static final BodyImage[] rightFrames = {
            new BodyImage("data/assets/slashattacks/slash-right/sr-frame1.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-right/sr-frame2.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-right/sr-frame3.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-right/sr-frame4.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-right/sr-frame5.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-right/sr-frame6.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-right/sr-frame7.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-right/sr-frame8.png", 3f),
            new BodyImage("data/assets/slashattacks/slash-right/sr-frame9.png", 3f),
    };

    private int currentFrame = 0;
    private BodyImage[] activeAnimation;

    public SlashAttack(World world, Character player) {
        super(world);

        Sensor hitBox = new Sensor(this, new BoxShape(1f, 1.5f));

        if (player.isFacingLeft()) {
            activeAnimation = leftFrames;
            this.setPosition(new Vec2(player.getPosition().x - 2.0f, player.getPosition().y));
        } else {
            activeAnimation = rightFrames;
            this.setPosition(new Vec2(player.getPosition().x + 2.0f, player.getPosition().y));
        }

        this.addImage(activeAnimation[0]);

        hitBox.addSensorListener(new SensorListener() {
            @Override
            public void beginContact(SensorEvent e) {
                if (e.getContactBody() instanceof Enemy) {
                    Enemy hitEnemy = (Enemy) e.getContactBody();
                    hitEnemy.takeDamage(1);
                }
            }

            @Override
            public void endContact(SensorEvent e) {}
        });

        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame++;

                if (currentFrame == 3) {
                    if (Menu.slashSound != null) {
                        Menu.slashSound.play();
                    }
                }

                if (currentFrame >= activeAnimation.length) {
                    ((Timer) e.getSource()).stop();
                    SlashAttack.this.destroy();
                } else {
                    SlashAttack.this.removeAllImages();
                    SlashAttack.this.addImage(activeAnimation[currentFrame]);
                }
            }
        });

        timer.start();


    }
}