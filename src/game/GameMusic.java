package game;

import javax.sound.sampled.Clip;

public class GameMusic extends Audio {

    public GameMusic(String filePath) {
        super(filePath);
    }

    @Override
    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loops
        }
    }

    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }
}
