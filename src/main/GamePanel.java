package main;

import character.Character;
import character.Player;
import character.monster.MON_Bat;
import character.monster.MON_GreenSlime;
import character.monster.Monster;
import projectile.Projectile;
import tile.TileManager;
import worldObject.WorldObject;
import dialogue.DialogueManager;
import sound.Sound;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    // sreen settings
    private final int originalTileSize = 16; // 16x16 tile
    private final int scale = 3;
    private final int tileSize = originalTileSize * scale; // 48x48 tile
    private final int maxScreenCol = 16;
    private final int maxScreenRow = 12;
    private final int ScreenWidth = tileSize * maxScreenCol; // 768 pixel
    private final int ScreenHeight = tileSize * maxScreenRow; // 576 pixel

    // WORLD SETTINGS
    public int maxWorldCol;
    public int maxWorldRow;
    private final int maxMap = 3;
    private final int worldWidth = tileSize * maxWorldCol;
    private final int worldHeight = tileSize * maxWorldRow;
    private UI ui = new UI(this);
    private DialogueManager dialogueManager = new DialogueManager(this);
    // Khởi tạo CombatSystem
    private events_system.CombatSystem combatSystem = new events_system.CombatSystem(this);

    private int FPS = 60;
    private Character currentInteractingNPC = null;
    // Game State
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int victoryEndState = 4;
    public final int gameOverState = 5;
    // Sound
    public Sound music = new Sound();
    public Sound soundEffect = new Sound();

    public UI getUi() {
        return ui;
    }

    public int getOriginalTileSize() {
        return originalTileSize;
    }

    public int getScale() {
        return scale;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getMaxScreenCol() {
        return maxScreenCol;
    }

    public int getMaxScreenRow() {
        return maxScreenRow;
    }

    public int getScreenWidth() {
        return ScreenWidth;
    }

    public int getScreenHeight() {
        return ScreenHeight;
    }

    public int getMaxWorldCol() {
        return maxWorldCol;
    }

    public int getMaxWorldRow() {
        return maxWorldRow;
    }

    public int getMaxMap() { return maxMap;}

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getFPS() {
        return FPS;
    }

    public void setFPS(int FPS) {
        this.FPS = FPS;
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public Character getInteractingNPC() {
        return currentInteractingNPC;
    }

    public void setInteractingNPC(Character npc) {
        this.currentInteractingNPC = npc;
    }

    public TileManager getTileM() {
        return tileM;
    }

    public KeyHandler getKeyH() {
        return keyH;
    }

    public Thread getGameThread() {
        return gameThread;
    }

    public void stopGameThread() {
        this.gameThread = null;
    }

    public CollisionChecker getcChecker() {
        return cChecker;
    }

    public AssetSetter getaSetter() {
        return aSetter;
    }

    public events_system.CombatSystem getCombatSystem() { // Sửa lại kiểu cho đúng package
        return combatSystem;
    }

    public Player getPlayer() {
        return player;
    }

    public WorldObject[] getwObjects() {
        return wObjects;
    }

    public void setwObjects(WorldObject[] wObjects) {
        this.wObjects = wObjects;
    }

    public Character[] getNpc() {
        return npc;
    }

    public MON_GreenSlime[] getMON_GreenSlime() {
        return greenSlime;
    }

    public MON_Bat[] getMON_Bat() {
        return bat;
    }

    private TileManager tileM = new TileManager(this);
    ;
    private KeyHandler keyH = new KeyHandler(this);
    private Thread gameThread;
    private CollisionChecker cChecker = new CollisionChecker(this);
    private AssetSetter aSetter = new AssetSetter(this);
    private Player player = new Player(this, keyH);
    private WorldObject wObjects[] = new WorldObject[10];
    private character.Character npc[] = new character.Character[10];
    private MON_GreenSlime[] greenSlime = new MON_GreenSlime[10];
    private MON_Bat[] bat = new MON_Bat[10];

    public List<Projectile> projectiles = new ArrayList<>();
    public GamePanel() {

        this.setPreferredSize(new Dimension(ScreenWidth, ScreenHeight));
        this.setBackground(Color.white);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame() {
        aSetter.setWObjects();
        aSetter.setNPC();
        gameState = titleState;
        aSetter.setGreenSlime();
        aSetter.setBat();
        playMusic(Sound.MUSIC_BACKGROUND);
        if (player != null) {
            player.setDefaultValues(); // << ĐÂY LÀ NƠI QUAN TRỌNG ĐỂ RESET PLAYER
        }
        projectiles.clear(); // Xóa danh sách projectile để tránh rò rỉ bộ nhớ

    }

    public void playMusic(int soundIndex) {
        music.loop(soundIndex);
    }

    // Phương thức để dừng nhạc nền
    public void stopMusic() {
        music.stopMusic();
        // music.close(); // Có thể gọi khi thoát game hoàn toàn để giải phóng tài nguyên
    }

    // Phương thức để phát hiệu ứng âm thanh (SFX)
    public void playSoundEffect(int soundIndex) {
        soundEffect.play(soundIndex);
    }

    public void cleanUpBeforeExit() {
        music.closeAllClips();
        soundEffect.closeAllClips();
        // Dừng gameThread nếu nó đang chạy
        stopGameThread();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override

    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }
    // Trong GamePanel.java

    public void update() {
        if (gameState == playState) {
            // 1. Cập nhật trạng thái của Player
            player.update();

            // 2. Cập nhật trạng thái của NPCs
            for (int i = 0; i < npc.length; i++) {
                if (npc[i] != null) {
                    // NPC tự quyết định hành động (ví dụ setAction) trong update của nó
                    npc[i].update();
                }
            }

            // 3. Cập nhật trạng thái của Monsters (greenSlime)
            for (int i = 0; i < greenSlime.length; i++) {
                if (greenSlime[i] != null) {
                    if (greenSlime[i].getCurrentHealth() > 0) {
                        // MON_GreenSlime.update() sẽ gọi playerChasing() và sau đó super.update()
                        // để Monster di chuyển và cập nhật trạng thái cơ bản.
                        greenSlime[i].update();
                    } else {

                    }
                }
            }

            for (int i = 0; i < bat.length; i++) {
                if (bat[i] != null) {
                    if (bat[i].getCurrentHealth() > 0) {
                        // MON_bat.update() sẽ gọi playerChasing() và sau đó super.update()
                        // để Monster di chuyển và cập nhật trạng thái cơ bản.
                        bat[i].update();
                    } else {

                    }
                }
            }

            // Cập nhật projectiles
            for (int i = projectiles.size() - 1; i >= 0; i--) {
                Projectile p = projectiles.get(i);
                if (p.isAlive()) { // Sử dụng getter
                    p.update(); // Projectile tự cập nhật vị trí, animation, va chạm tile, tầm xa

                    if (p.isAlive()) { // Nếu projectile vẫn còn sống sau các kiểm tra riêng của nó
                        combatSystem.processProjectileImpacts(p); // CombatSystem kiểm tra va chạm với quái vật
                    }
                }

                if (!p.isAlive()) { // Nếu projectile không còn sống (do va chạm tile, tầm xa, hoặc trúng quái vật)
                    projectiles.remove(i);
                }
            }

            // 4. XỬ LÝ HỆ THỐNG CHIẾN ĐẤU
            // Chỉ xử lý nếu Player còn sống
            if (player != null && player.getCurrentHealth() > 0) {
                // 4a. Player chủ động nhấn nút tấn công Monster
                combatSystem.checkPlayerMonsterCombat(player, greenSlime);

                // 4b. Monster tự động tấn công Player khi có va chạm trực tiếp
                combatSystem.handleMonsterCollisionAttack(player, greenSlime);
            }

            if (player != null && player.getCurrentHealth() > 0) {
                // 4a. Player chủ động nhấn nút tấn công Monster
                combatSystem.checkPlayerMonsterCombat(player, bat);

                // 4b. Monster tự động tấn công Player khi có va chạm trực tiếp
                combatSystem.handleMonsterCollisionAttack(player, bat);
            }

        } else if (gameState == pauseState) {
            // Logic cho trạng thái Pause
        } else if (gameState == victoryEndState) {
            // Logic cho trạng thái Victory
        } else if (gameState == gameOverState) {
            // Logic cho trạng thái Game Over
        }
        // Bạn có thể thêm các else if cho các trạng thái game khác nếu có


    }

    public void resetGameForNewSession() {
        System.out.println("Resetting game for new session...");
        setupGame();
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // Title screen

        if (gameState == titleState) {
            ui.draw(g2);
        } else {
            tileM.draw(g2);

            for (int i = 0; i < wObjects.length; i++) {
                if (wObjects[i] != null) {
                    wObjects[i].draw(g2, this);
                }
            }
            for (int i = 0; i < npc.length; i++) {
                if (npc[i] != null) {
                    npc[i].draw(g2);
                }
            }

            for (int i = 0; i < greenSlime.length; i++) { // Sử dụng mảng 'monster' mới
                if (greenSlime[i] != null) {
                    greenSlime[i].draw(g2);
                }
            }

            for (int i = 0; i < bat.length; i++) { // Sử dụng mảng 'monster' mới
                if (bat[i] != null) {
                    bat[i].draw(g2);
                }
            }
            // Vẽ projectiles sau các đối tượng khác nhưng trước Player hoặc UI để nó bay phía trên
            for (Projectile p : projectiles) {
                if (p.alive) { // Chỉ vẽ projectile còn "sống"
                    p.draw(g2);
                }
            }
            player.draw(g2);

            ui.draw(g2);
            g2.dispose();
        }

    }
}
