package game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public abstract class Audio {

    public static final String MUSIC_BACKGROUND = "data/audio/soundtrack/background_soundtrack_by_utkuortal.wav";
    public static final String SFX_DAMAGE = "data/audio/damage_sound_effect.wav";
    public static final String SFX_DEATH = "data/audio/death_sound.wav";
    public static final String SFX_SLASH = "data/audio/slash_sfx.wav";
    public static final String SFX_THROW = "data/audio/tunetank.com_whoosh-air-punch.wav";
    public static final String SFX_ENEMY_HIT = "data/audio/little_my_hurt.wav";

    protected Clip clip;
    protected FloatControl volumeControl;

    public Audio(String filePath) {
        try {
            File audioFile = new File(filePath);
            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile)) {
                clip = AudioSystem.getClip();
                clip.open(audioStream);
            }

            if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
        } catch (Exception e) {
            System.err.println("Failed to load audio: " + filePath);
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        if (volumeControl != null) {
            if (volume <= 0.0f) {
                volumeControl.setValue(volumeControl.getMinimum());
            } else {
                float dB = (float) (Math.log10(volume) * 20f);
                float clamped = Math.max(volumeControl.getMinimum(), Math.min(dB, volumeControl.getMaximum()));
                volumeControl.setValue(clamped);
            }
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public abstract void play();
}