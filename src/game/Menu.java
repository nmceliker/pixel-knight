package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Menu {

    private final JFrame mainGameWindow;
    private JSlider masterSlider;
    private JSlider musicSlider;
    private JSlider fxSlider;

    private float masterVol = 0.5f;
    private float musicVol = 0.5f;
    private float fxVol = 0.5f;

    private GameMusic gameMusic;
    private SoundFX damageSound;
    private SoundFX deathSound;
    public static SoundFX slashSound;
    public static SoundFX throwSound;
    public SoundFX enemyHitSound;

    private JDialog settingsDialog;

    public Menu(JFrame frame) {
        this.mainGameWindow = frame;

        gameMusic = new GameMusic(Audio.MUSIC_BACKGROUND);
        damageSound = new SoundFX(Audio.SFX_DAMAGE);
        deathSound = new SoundFX(Audio.SFX_DEATH);
        slashSound = new SoundFX(Audio.SFX_SLASH);
        throwSound = new SoundFX(Audio.SFX_THROW);
        enemyHitSound = new SoundFX(Audio.SFX_ENEMY_HIT);


        if (gameMusic != null) {
            gameMusic.play();
        }

        masterSlider = new JSlider(0, 100, 50);
        musicSlider = new JSlider(0, 100, 50);
        fxSlider = new JSlider(0, 100, 50);

        masterSlider.addChangeListener(e -> {
            masterVol = masterSlider.getValue() / 100f;
            updateVolume();
        });

        musicSlider.addChangeListener(e -> {
            musicVol = musicSlider.getValue() / 100f;
            updateVolume();
        });

        fxSlider.addChangeListener(e -> {
            fxVol = fxSlider.getValue() / 100f;
            updateVolume();
        });

        updateVolume();

        settingsDialog = new JDialog(mainGameWindow, "Audio Settings", true);
        settingsDialog.setSize(350, 250);
        settingsDialog.setLocationRelativeTo(mainGameWindow);
        settingsDialog.setLayout(new GridLayout(4, 1));

        JPanel masterPanel = new JPanel();
        masterPanel.add(new JLabel("Master Volume:"));
        masterPanel.add(masterSlider);

        JPanel musicPanel = new JPanel();
        musicPanel.add(new JLabel("Music Volume:"));
        musicPanel.add(musicSlider);

        JPanel fxPanel = new JPanel();
        fxPanel.add(new JLabel("Sound FX Volume:"));
        fxPanel.add(fxSlider);

        JButton closeButton = new JButton("Close Settings");
        closeButton.addActionListener(e -> settingsDialog.setVisible(false));

        settingsDialog.add(masterPanel);
        settingsDialog.add(musicPanel);
        settingsDialog.add(fxPanel);
        settingsDialog.add(closeButton);
    }

    public void showSettingsMenu() {
        if (settingsDialog != null) {
            settingsDialog.setVisible(true);
        }
    }

    public void showPauseMenu(Runnable onResume, Runnable onSave, Runnable onLoad, Runnable onSettings, Runnable onMainMenu, Runnable onQuit, JPanel overlay) {
        if (overlay == null) {
            return;
        }

        overlay.removeAll();
        overlay.setVisible(true);
        overlay.setFocusable(true);
        overlay.setLayout(new GridBagLayout());

        JPanel pauseMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pauseMenuPanel.setLayout(new BoxLayout(pauseMenuPanel, BoxLayout.Y_AXIS));
        pauseMenuPanel.setOpaque(false);
        pauseMenuPanel.setBorder(BorderFactory.createEmptyBorder(120, 90, 120, 90));

        JLabel title = new JLabel("Paused");
        title.setFont(new Font("Serif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton resumeBtn = new JButton("Resume");
        resumeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumeBtn.addActionListener(e -> {
            if (onResume != null) onResume.run();
        });

        JButton saveBtn = new JButton("Save Game");
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            if (onSave != null) onSave.run();
        });

        JButton loadBtn = new JButton("Load Game");
        loadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadBtn.addActionListener(e -> {
            if (onLoad != null) onLoad.run();
        });

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsBtn.addActionListener(e -> {
            if (onSettings != null) onSettings.run();
        });

        JButton menuBtn = new JButton("Main Menu");
        menuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuBtn.addActionListener(e -> {
            if (onMainMenu != null) onMainMenu.run();
        });

        JButton quitBtn = new JButton("Quit");
        quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitBtn.addActionListener(e -> {
            if (onQuit != null) onQuit.run();
        });

        pauseMenuPanel.add(title);
        pauseMenuPanel.add(Box.createVerticalStrut(40));
        pauseMenuPanel.add(resumeBtn);
        pauseMenuPanel.add(Box.createVerticalStrut(20));
        pauseMenuPanel.add(saveBtn);
        pauseMenuPanel.add(Box.createVerticalStrut(20));
        pauseMenuPanel.add(loadBtn);
        pauseMenuPanel.add(Box.createVerticalStrut(20));
        pauseMenuPanel.add(settingsBtn);
        pauseMenuPanel.add(Box.createVerticalStrut(20));
        pauseMenuPanel.add(menuBtn);
        pauseMenuPanel.add(Box.createVerticalStrut(20));
        pauseMenuPanel.add(quitBtn);

        overlay.add(pauseMenuPanel);
        overlay.revalidate();
        overlay.repaint();
        overlay.requestFocusInWindow();

        InputMap inputMap = overlay.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = overlay.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resumeGame");
        actionMap.put("resumeGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onResume != null) onResume.run();
            }
        });
    }

    public void showDeathMenu(Runnable onRestart, Runnable onMainMenu, Runnable onQuit) {


        mainGameWindow.getContentPane().removeAll();

        JPanel deathMenuPanel = new JPanel();
        deathMenuPanel.setLayout(new BoxLayout(deathMenuPanel, BoxLayout.Y_AXIS));
        deathMenuPanel.setBackground(Color.BLACK);

        JLabel title = new JLabel("You Died");
        title.setFont(new Font("Serif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton restartBtn = new JButton("Restart");
        restartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartBtn.addActionListener(e -> {
            if (onRestart != null) onRestart.run();
        });

        JButton menuBtn = new JButton("Main Menu");
        menuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuBtn.addActionListener(e -> {
            if (onMainMenu != null) onMainMenu.run();
        });

        JButton quitBtn = new JButton("Quit");
        quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitBtn.addActionListener(e -> {
            if (onQuit != null) onQuit.run();
        });

        deathMenuPanel.add(Box.createVerticalStrut(100));
        deathMenuPanel.add(title);
        deathMenuPanel.add(Box.createVerticalStrut(50));
        deathMenuPanel.add(restartBtn);
        deathMenuPanel.add(Box.createVerticalStrut(20));
        deathMenuPanel.add(menuBtn);
        deathMenuPanel.add(Box.createVerticalStrut(20));
        deathMenuPanel.add(quitBtn);

        mainGameWindow.add(deathMenuPanel);
        mainGameWindow.revalidate();
        mainGameWindow.repaint();
    }

    public void updateVolume() {
        float finalMusicVol = masterVol * musicVol;
        float finalFxVol = masterVol * fxVol;

        if (gameMusic != null) gameMusic.setVolume(finalMusicVol);
        if (damageSound != null) damageSound.setVolume(finalFxVol);
        if (deathSound != null) deathSound.setVolume(finalFxVol);
        if (slashSound != null) slashSound.setVolume(finalFxVol);
        if (enemyHitSound != null) enemyHitSound.setVolume(finalFxVol);
        if (throwSound != null) throwSound.setVolume(finalFxVol);
    }

    public void stopMusic() {
        if (gameMusic != null) {
            gameMusic.stop();
        }
    }

    public void changeMusic(String path) {
        if (gameMusic != null) {
            gameMusic.stop();
        }
        gameMusic = new GameMusic(path);
        updateVolume();
        gameMusic.play();
    }

    public void playMusic() {
        if (gameMusic != null) {
            gameMusic.resume();
        }
    }

    public SoundFX getDamageSound() { return damageSound; }
    public SoundFX getDeathSound() { return deathSound; }
    public SoundFX getSlashSound() { return slashSound; }
    public SoundFX getThrowSound() { return throwSound; }
    public SoundFX getEnemyHitSound() { return enemyHitSound; }
}