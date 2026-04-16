package game;

public class SoundFX extends Audio {

    public SoundFX(String filePath) {
        super(filePath);
    }

    @Override
    public void play() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }
}
