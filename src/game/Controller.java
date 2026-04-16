package game;

import city.cs.engine.AttachedImage;
import city.cs.engine.Walker;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
    private Walker body;
    private final float walkingSpeed = 4f;
    private final float sprintSpeed = 8f;
    private Character character;
    private final Runnable onTogglePause;
    private final Runnable onDeathMenu;
    private boolean facingLeft;
    private boolean isSprinting;
    private boolean movingLeft;
    private boolean movingRight;
    private AttachedImage currentImage;

    public Controller(Walker body, Character character, Runnable onTogglePause, Runnable onDeathMenu) {
        this.body = body;
        this.character = character;
        this.onTogglePause = onTogglePause;
        this.onDeathMenu = onDeathMenu;

    }

    public void setCharacter(Character newCharacter) {
        this.body = newCharacter;
        this.character = newCharacter;
    }

    private float currentSpeed() {
        return isSprinting ? sprintSpeed : walkingSpeed;
    }

    private void updateWalking() {
        float speed = currentSpeed();

        if (movingLeft && !movingRight) {
            character.setFacingLeft(true);
            body.startWalking(-speed);

        } else if (movingRight && !movingLeft) {
            character.setFacingLeft(false);

            body.startWalking(speed);

        } else {
            body.stopWalking();
        }
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (character != null && character.isDead()) {
            if (onDeathMenu != null) {
                onDeathMenu.run();
            }
            return;
        }

        switch (code) {
            case KeyEvent.VK_SHIFT:
                isSprinting = true;
                updateWalking();
                break;
            case KeyEvent.VK_A:
                movingLeft = true;
                updateWalking();
                break;
            case KeyEvent.VK_D:
                movingRight = true;
                updateWalking();
                break;
            case KeyEvent.VK_W:
            case KeyEvent.VK_SPACE:
                character.doDoubleJump();
                break;
            case KeyEvent.VK_F:
                if (character != null) character.throwKnife();
                break;
            default:
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_SHIFT:
                isSprinting = false;
                updateWalking();
                break;
            case KeyEvent.VK_A:
                movingLeft = false;
                character.setFacing();
                updateWalking();
                break;
            case KeyEvent.VK_D:
                movingRight = false;
                character.setFacing();
                updateWalking();
                break;
            default:
                break;
        }
    }
}