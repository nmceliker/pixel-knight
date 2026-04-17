package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Game {

    public static Game currentGame;

    private boolean portalSpawned = false;
    private boolean deathMenuShown = false;
    private boolean paused = false;
    private World world;
    private final JFrame frame;
    private final Menu menu;
    private final JPanel pauseOverlay;
    private Character character;
    private HealthMask mask;
    private UserView view;
    private Image currentBackground = new ImageIcon("data/assets/lvl1/cot_background.png").getImage();

    public Game() {
        currentGame = this;

        frame = new JFrame("Pixel Knight");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);

        pauseOverlay = new JPanel(new GridBagLayout());
        pauseOverlay.setOpaque(false);
        pauseOverlay.setVisible(false);
        frame.setGlassPane(pauseOverlay);

        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "togglePause");
        actionMap.put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });

        menu = new Menu(frame);
        showMainMenu();

        frame.setVisible(true);
    }

    public void portalSpawner() {
        if (!portalSpawned) {
            Portal levelPortal = new Portal(world);

            levelPortal.setPosition(new Vec2(-11, 6.5f));

            levelPortal.getSensor().addSensorListener(new PortalListener(this));

            portalSpawned = true;
        }
    }

    private void showMainMenu() {
        paused = false;
        if (pauseOverlay != null) {
            pauseOverlay.removeAll();
            pauseOverlay.setVisible(false);
        }

        if (world != null) {
            try { world.stop(); } catch (Exception ignored) {}
            world = null;
        }
        character = null;
        view = null;
        portalSpawned = false;

        frame.getContentPane().removeAll();

        JPanel mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new BoxLayout(mainMenuPanel, BoxLayout.Y_AXIS));
        mainMenuPanel.setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("Pixel Knight");
        title.setFont(new Font("Serif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startBtn = new JButton("Start Game");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.addActionListener(e -> showLevelSelectionMenu());

        JButton loadBtn = new JButton("Load Game");
        loadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadBtn.addActionListener(e -> loadGameWithDialog());

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsBtn.addActionListener(e -> menu.showSettingsMenu());

        JButton quitBtn = new JButton("Quit");
        quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitBtn.addActionListener(e -> System.exit(0));

        mainMenuPanel.add(Box.createVerticalStrut(100));
        mainMenuPanel.add(title);
        mainMenuPanel.add(Box.createVerticalStrut(50));
        mainMenuPanel.add(startBtn);
        mainMenuPanel.add(Box.createVerticalStrut(20));
        mainMenuPanel.add(loadBtn);
        mainMenuPanel.add(Box.createVerticalStrut(20));
        mainMenuPanel.add(settingsBtn);
        mainMenuPanel.add(Box.createVerticalStrut(20));
        mainMenuPanel.add(quitBtn);

        frame.add(mainMenuPanel);
        frame.revalidate();
        frame.repaint();
    }

    private void showLevelSelectionMenu() {
        paused = false;
        if (pauseOverlay != null) {
            pauseOverlay.removeAll();
            pauseOverlay.setVisible(false);
        }
        frame.getContentPane().removeAll();

        JPanel levelMenuPanel = new JPanel();
        levelMenuPanel.setLayout(new BoxLayout(levelMenuPanel, BoxLayout.Y_AXIS));
        levelMenuPanel.setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("Select Level");
        title.setFont(new Font("Serif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton level1Btn = new JButton("Level 1");
        level1Btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        level1Btn.addActionListener(e -> startLevel(1));

        JButton level2Btn = new JButton("Level 2");
        level2Btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        level2Btn.addActionListener(e -> startLevel(2));

        JButton level3Btn = new JButton("Level 3");
        level3Btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        level3Btn.addActionListener(e -> startLevel(3));

        JButton backBtn = new JButton("Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> showMainMenu());

        levelMenuPanel.add(Box.createVerticalStrut(100));
        levelMenuPanel.add(title);
        levelMenuPanel.add(Box.createVerticalStrut(50));
        levelMenuPanel.add(level1Btn);
        levelMenuPanel.add(Box.createVerticalStrut(20));
        levelMenuPanel.add(level2Btn);
        levelMenuPanel.add(Box.createVerticalStrut(20));
        levelMenuPanel.add(level3Btn);
        levelMenuPanel.add(Box.createVerticalStrut(20));
        levelMenuPanel.add(backBtn);

        frame.add(levelMenuPanel);
        frame.revalidate();
        frame.repaint();
    }

    private int currentLevel = 1;

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    public void setPortalSpawned(boolean spawned) {
        this.portalSpawned = spawned;
    }

    public void setMask(HealthMask mask) {
        this.mask = mask;
    }

    public boolean isPortalSpawned() {
        return portalSpawned;
    }

    public Character getCharacter() {
        return character;
    }

    public World getWorld() {
        return world;
    }

    private void saveGameWithDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            GameSaverLoader.saveGame(this, fileToSave.getAbsolutePath());
        }
        resumeGame();
    }

    private void loadGameWithDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");
        int userSelection = fileChooser.showOpenDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToLoad = fileChooser.getSelectedFile();
            GameSaverLoader.loadGame(this, fileToLoad.getAbsolutePath());
        }
    }

    public void startLevel(int level) {
        this.currentLevel = level;
        if (level == 1) {
            startGame();
            // Add portal to level 2 after all other level 1 setup
            Portal lvl2Portal = new Portal(world);
            lvl2Portal.setPosition(new Vec2(14, -9));
            lvl2Portal.getSensor().addSensorListener(new PortalListener(this));
        } else if (level == 2) {
            startGame();

            world.stop();
            for (StaticBody body : world.getStaticBodies()) {
                body.destroy();
            }
            character.resetCoins();
            portalSpawned = false;
            character.setLinearVelocity(new Vec2(0,0));
            character.setPosition(new Vec2(-10, -9));
            currentBackground = new ImageIcon("data/assets/lvl2/lvl2_background.png").getImage();
            menu.changeMusic("data/audio/soundtrack/lvl2_soundtrack.wav");
            StaticBody ground = new Platform(world, 15, 0.5f, 0, -11.5f);
            StaticBody newPlatform = new Platform(world, 3f, 0.5f, 0, -1f);
            Enemy littleMy = new Enemy(world, character);
            littleMy.setPosition(new Vec2(10, -9));
            littleMy.setDropsCoins(true);
            littleMy.setDropsLoot(true);

            Portal lvl3Portal = new Portal(world);
            lvl3Portal.setPosition(new Vec2(14, -9));
            lvl3Portal.getSensor().addSensorListener(new PortalListener(this));
            ThrowingKnifePickup knifePickup = new ThrowingKnifePickup(world);
            knifePickup.setPosition(new Vec2(8, 2));
            knifePickup.addCustomListener(new KnifePickupListener(character));
            world.start();
        } else if (level == 3) {
            startGame();

            world.stop();
            for (StaticBody body : world.getStaticBodies()) {
                body.destroy();
            }
            character.resetCoins();
            portalSpawned = false;
            character.setLinearVelocity(new Vec2(0,0));
            character.setPosition(new Vec2(-10, -9));
            currentBackground = new ImageIcon("data/assets/lvl3/lvl3_background.png").getImage();
            menu.changeMusic("data/audio/soundtrack/lvl3_soundtrack.wav");
            StaticBody ground = new Platform(world, 18, 0.5f, 0, -11.5f);
            StaticBody plat1 = new Platform(world, 4f, 0.5f, -8, 2f);
            StaticBody plat2 = new Platform(world, 4f, 0.5f, 8, 4f);
            StaticBody plat3 = new Platform(world, 2f, 0.5f, 0, -2f);
            Enemy boss = new Enemy(world, character);
            boss.setPosition(new Vec2(12, -9));
            boss.setDropsCoins(true);
            boss.setDropsLoot(true);
            mask = new HealthMask(world, 3f, 2f);
            mask.setPosition(new Vec2(0, 5));
            world.start();
        }
    }

    private void resumeGame() {
        paused = false;
        pauseOverlay.removeAll();
        pauseOverlay.setVisible(false);
        menu.playMusic();
        if (view != null) {
            view.requestFocusInWindow();
        }
        if (world != null) {
            world.start();
        }
    }

    public void stopGameMusic() {
        menu.stopMusic();
    }

    private void showDeathMenu() {
        paused = false;
        pauseOverlay.removeAll();
        pauseOverlay.setVisible(false);

        menu.showDeathMenu(
                this::startGame,
                this::showMainMenu,
                () -> System.exit(0)
        );
    }

    public static void onPlayerDeath() {
        if (currentGame != null && !currentGame.deathMenuShown) {
            currentGame.deathMenuShown = true;
            SwingUtilities.invokeLater(currentGame::showDeathMenu);
        }
    }

    public void startGame() {
        deathMenuShown = false;
        paused = false;
        if (pauseOverlay != null) {
            pauseOverlay.removeAll();
            pauseOverlay.setVisible(false);
        }

        frame.getContentPane().removeAll();

        world = new World(144);

        new Platform(world, 11, 0.5f, 0f, -11.5f);
        new Platform(world, 3.5f, 0.5f, -8, 4.5f);
        new Platform(world, 3.5f, 0.5f, 6, -6f);
        new Platform(world, 2f, 0.5f, 3, 0f);

        new OneHealthDamage(world, -8f, -10.5f);
        mask = new HealthMask(world, 3f, 2f);

        character = new Character(world, menu);
        character.setPosition(new Vec2(7, -9));

        // create projectile
        Shuriken shuriken = new Shuriken(world);
        shuriken.setPosition(new Vec2(-15, -2));
        shuriken.setLinearVelocity(new Vec2(10, 0));
        shuriken.addCollisionListener(new ProjectileCollision(shuriken, null));

        portalSpawned = false;

        // create coins
        Coin coin1 = new Coin(world);
        coin1.setPosition(new Vec2(-10, -10));

        Coin coin2 = new Coin(world);
        coin2.setPosition(new Vec2(8, -4));

        Coin coin3 = new Coin(world);
        coin3.setPosition(new Vec2(-2, 0));

        // coin listeners
        coin1.getSensor().addSensorListener(new CoinListener(coin1, this));
        coin2.getSensor().addSensorListener(new CoinListener(coin2, this));
        coin3.getSensor().addSensorListener(new CoinListener(coin3, this));


        world.addStepListener(new StepListener() {
            @Override
            public void preStep(StepEvent stepEvent) {
            }

            @Override
            public void postStep(StepEvent stepEvent) {
                if (shuriken.getWorld() == null) {
                    return;
                }

                float x = shuriken.getPosition().x;

                if (x >= 15) {
                    shuriken.setLinearVelocity(new Vec2(-10, 0));
                } else if (x <= -15) {
                    shuriken.setLinearVelocity(new Vec2(10, 0));
                }
            }
        });

        character.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof OneHealthDamage trap) {
                character.minusHealth(trap.getDamageAmount());
                character.setLinearVelocity(new Vec2(-5, 5));
            }
        });

        world.addStepListener(new StepListener() {
            public void preStep(StepEvent stepEvent) {}

            public void postStep(StepEvent stepEvent) {
                if (mask == null) return;

                Vec2 d = character.getPosition().sub(mask.getPosition());
                float distance = d.length();

                if (distance <= 2.2f && character.getHealth() < character.getMaxHealth()) {
                    character.plusHealth();
                    mask.destroy();
                    mask = null;
                }
            }
        });

        view = new UserView(world, 500, 500) {
            private final Image existingHealth = new ImageIcon("data/assets/health-icon.png").getImage();
            private final Image missingHealth = new ImageIcon("data/assets/missing-health-icon.png").getImage();
            private final Image uiCoinIcon = new ImageIcon("data/assets/coin.png").getImage();
            private final Image uiKnifeIcon = new ImageIcon("data/assets/throwing_knife_right.png").getImage();

            @Override
            protected void paintBackground(Graphics2D g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());

                Composite originalComposite = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.88f));
                g.drawImage(currentBackground, 0, 0, getWidth(), getHeight(), this);
                g.setComposite(originalComposite);
            }

            @Override
            protected void paintForeground(Graphics2D g) {
                super.paintForeground(g);

                int currentHealth = character.getHealth();
                int maxHealth = character.getMaxHealth();
                int iconSize = 32;
                float ratio = (float) existingHealth.getHeight(null) / existingHealth.getWidth(null);
                int iconHeight = (int) (iconSize * ratio);

                for (int i = 0; i < maxHealth; i++) {
                    int xPosition = 10 + (i * 30);
                    if (i < currentHealth) {
                        g.drawImage(existingHealth, xPosition, 20, iconSize, iconHeight, this);
                    } else {
                        g.drawImage(missingHealth, xPosition, 20, iconSize, iconHeight, this);
                    }
                }

                g.drawImage(uiCoinIcon, 115, 30, 24, 24, this);
                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 20));
                g.drawString("x " + character.getCoinCount(), 145, 50);

                // draw throwing knife count under health masks (top-left)
                try {
                    int knifeIconSize = 24;
                    int knifeX = 10;
                    int knifeY = 60;
                    if (uiKnifeIcon != null) g.drawImage(uiKnifeIcon, knifeX, knifeY, knifeIconSize, knifeIconSize, this);
                    g.setFont(new Font("SansSerif", Font.BOLD, 18));
                    g.drawString("x " + character.getKnifeCount(), knifeX + 34, knifeY + 18);
                } catch (Exception ignored) {}

                    String[] controls = {
                            "A/D - Move Left/Right",
                            "W/SPACE - Jump",
                            "SHIFT - Sprint",
                            "LMB - Attack",
                            "F - Throwing Knife",
                            "ESC - Pause Menu"
                    };

                    g.setFont(new Font("SansSerif", Font.BOLD, 12));
                    FontMetrics fm = g.getFontMetrics();

                    int padding = 12;
                    int lineHeight = fm.getHeight();
                    int boxWidth = 0;
                    for (String control : controls) {
                        boxWidth = Math.max(boxWidth, fm.stringWidth(control));
                    }

                    int boxHeight = (controls.length * lineHeight) + (padding * 2) - 4;
                    int boxX = getWidth() - boxWidth - (padding * 2) - 10;
                    int boxY = 10;

                    g.setColor(new Color(0, 0, 0, 160));
                    g.fillRoundRect(boxX, boxY, boxWidth + (padding * 2), boxHeight, 12, 12);

                    g.setColor(Color.WHITE);
                    int textY = boxY + padding + fm.getAscent();
                    for (String control : controls) {
                        g.drawString(control, boxX + padding, textY);
                        textY += lineHeight;
                    }

                if (character.isDead()) {
                    g.setColor(new Color(30, 30, 30, 180));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Serif", Font.BOLD, 40));
                    g.drawString("YOU ARE DEAD", 95, 200);
                }
            }
        };

        final Controller controller = new Controller(
                character,
                character,
                this::togglePause,
                this::showDeathMenu
        );

        view.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    character.attack();
                }
            }
        });

        view.addKeyListener(controller);
        view.setFocusable(true);
        view.setFocusTraversalKeysEnabled(false);

        InputMap inputMap = view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = view.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "togglePause");
        actionMap.put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });

        menu.playMusic();

        frame.add(view);
        frame.revalidate();
        frame.repaint();

        SwingUtilities.invokeLater(() -> view.requestFocusInWindow());
        world.start();
    }

    private void togglePause() {
        if (character == null || character.isDead()) {
            return;
        }

        if (paused) {
            resumeGame();
            return;
        }

        paused = true;
        if (world != null) {
            world.stop();
        }
        menu.stopMusic();

        menu.showPauseMenu(
                this::resumeGame,
                this::saveGameWithDialog,
                this::loadGameWithDialog,
                menu::showSettingsMenu,
                this::showMainMenu,
                () -> System.exit(0),
                pauseOverlay
        );

        pauseOverlay.setVisible(true);
        pauseOverlay.revalidate();
        pauseOverlay.repaint();
        pauseOverlay.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }

    public void loadNextLevel() {
        if (currentLevel == 1) {
            startLevel(2);
        } else if (currentLevel == 2) {
            startLevel(3);
        } else if (currentLevel == 3) {
            menu.showVictoryMenu(
                    () ->         showMainMenu(),
                    () -> System.exit(0)
            );
            }
        }
    }
