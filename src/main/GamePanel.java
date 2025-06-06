package main;

import character.Character;
import character.Role.Player;
import character.Role.*;
import character.monster.MON_Bat;
import character.monster.MON_GolemBoss;
import character.monster.MON_GreenSlime;
import character.monster.MON_Orc;
import data.SaveLoad;
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

    // SCREEN SETTINGS
    private final int originalTileSize = 16;
    private final int scale = 3;
    private final int tileSize = originalTileSize * scale;
    private final int maxScreenCol = 16;
    private final int maxScreenRow = 12;
    private final int ScreenWidth = tileSize * maxScreenCol;
    private final int ScreenHeight = tileSize * maxScreenRow;

    // WORLD SETTINGS
    public int maxWorldCol = 100; // Kích thước cố định cho tất cả map
    public int maxWorldRow = 100; // Kích thước cố định cho tất cả map
    public final int maxMap = 2; // SỐ LƯỢNG MAP
    public int currentMap = 0;   // Map hiện tại, bắt đầu từ map 0

    // SYSTEM
    private TileManager tileM = new TileManager(this);
    private KeyHandler keyH = new KeyHandler(this);
    public Sound music = new Sound();
    public Sound soundEffect = new Sound();
    private CollisionChecker cChecker = new CollisionChecker(this);
    private AssetSetter aSetter = new AssetSetter(this);
    private UI ui = new UI(this);
    private DialogueManager dialogueManager = new DialogueManager(this);
    private events_system.CombatSystem combatSystem = new events_system.CombatSystem(this);
    private SaveLoad saveLoadManager = new SaveLoad(this);
    private Thread gameThread;


    // ENTITY AND OBJECT
    private Player player;
    public WorldObject wObjects[] = new WorldObject[20];
    public Character npc[] = new Character[10];
    public MON_GreenSlime[] greenSlime = new MON_GreenSlime[20];
    public MON_Bat[] bat = new MON_Bat[10];
    public MON_GolemBoss[] golemBoss = new MON_GolemBoss[5];
    public MON_Orc[] orc = new MON_Orc[10];
    public List<Projectile> projectiles = new ArrayList<>();


    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int victoryEndState = 4;
    public final int gameOverState = 5;
    public final int characterSelectState = 6;

    private Character currentInteractingNPC = null;
    private int FPS = 60;
    public int getTileSize() { return tileSize; }
    public int getMaxScreenCol() { return maxScreenCol; }
    public int getMaxScreenRow() { return maxScreenRow; }
    public int getScreenWidth() { return ScreenWidth; }
    public int getScreenHeight() { return ScreenHeight; }
    public int getMaxWorldCol() { return maxWorldCol; }
    public int getMaxWorldRow() { return maxWorldRow; }
    public Player getPlayer() { return player; }
    public int getScale() {
        return this.scale;
    }
    public TileManager getTileM() { return tileM; }
    public CollisionChecker getcChecker() { return cChecker; }
    public AssetSetter getaSetter() { return aSetter; }
    public UI getUi() { return ui; }
    public KeyHandler getKeyH() { return keyH; }
    public DialogueManager getDialogueManager() { return dialogueManager; }
    public int getFPS() { return FPS; }
    public SaveLoad getSaveLoadManager() { return saveLoadManager; }
    public Character getInteractingNPC() { return currentInteractingNPC; }
    public void setInteractingNPC(Character npc) { this.currentInteractingNPC = npc; }
    public WorldObject[] getwObjects() { return wObjects; }
    public events_system.CombatSystem getCombatSystem() {return combatSystem;}
    public Character[] getNpc() { return npc; }
    public MON_GreenSlime[] getMON_GreenSlime() { return greenSlime; }
    public MON_Bat[] getMON_Bat() { return bat; }
    public MON_GolemBoss[] getMON_GolemBoss() { return golemBoss; }
    public MON_Orc[] getMON_Orc() { return orc; }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public void playMusic(int soundIndex) { //
        music.loop(soundIndex); //
    }
    public void stopMusic() { //
        music.stopMusic(); //
    }
    public void playSoundEffect(int soundIndex) { //
        soundEffect.play(soundIndex); //
    }
    public void cleanUpBeforeExit() { //
        music.closeAllClips(); //
        soundEffect.closeAllClips(); //
        stopGameThread(); //
    }

    public void stopGameThread() { //
        this.gameThread = null; //
    }


    public GamePanel() {
        this.setPreferredSize(new Dimension(ScreenWidth, ScreenHeight));
        this.setBackground(Color.black); // Màu nền mặc định
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame() {
        // Đặt các thực thể cho map hiện tại (ban đầu là currentMap = 0)
        aSetter.setupMapAssets(currentMap);

        gameState = titleState;
        playMusic(Sound.MUSIC_BACKGROUND);
        if (player != null) {
            player.setDefaultValues();
            // Ví dụ đặt vị trí khởi đầu cho Player
            if(currentMap == 0) {
                player.worldX = getTileSize() * 7; //
                player.worldY = getTileSize() * 92; //
            } else if (currentMap == 1) {
                player.worldX = getTileSize() * 5; //
                player.worldY = getTileSize() * 10; //
            }
        }
        projectiles.clear();
    }
    public void resetGameForNewSession() { //
        System.out.println("Resetting game for new session...");
        currentMap = 0; // Luôn bắt đầu từ map 0 khi game mới
        player.setDefaultValues(); // Reset trạng thái Player về mặc định
        // Đặt lại vị trí Player cho map 0
        player.worldX = getTileSize() * 7;
        player.worldY = getTileSize() * 92;
        player.direction = "down";

        clearEntitiesForMapChange(); // Xóa tất cả entities cũ
        aSetter.setupMapAssets(currentMap); // Nạp entities cho map 0
        projectiles.clear();
        dialogueManager.reset(); // Reset DialogueManager
        ui = new UI(this); // Tạo lại UI để reset playtime và các trạng thái khác của UI

        // gameState sẽ được đặt lại thành titleState bởi KeyHandler khi chọn New Game hoặc sau Game Over/Victory
        // playMusic(Sound.MUSIC_BACKGROUND); // Có thể không cần thiết nếu title screen đã có nhạc
    }


    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
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

    public void update() {
        if (gameState == playState) {
            player.update();

            for (int i = 0; i < npc.length; i++) {
                if (npc[i] != null) {
                    npc[i].update();
                }
            }

            for (int i = 0; i < greenSlime.length; i++) {
                if (greenSlime[i] != null) {
                    if (greenSlime[i].getCurrentHealth() > 0) {
                        greenSlime[i].update();
                    } else {
                        aSetter.removeDeadMonster(greenSlime, i, currentMap); //
                    }
                }
            }
            for (int i = 0; i < bat.length; i++) {
                if (bat[i] != null) {
                    if (bat[i].getCurrentHealth() > 0) {
                        bat[i].update();
                    } else {
                        aSetter.removeDeadMonster(bat, i, currentMap); //
                    }
                }
            }
            for (int i = 0; i < orc.length; i++) {
                if (orc[i] != null) {
                    if (orc[i].getCurrentHealth() > 0) {
                        orc[i].update();
                    } else {
                        aSetter.removeDeadMonster(orc, i, currentMap); // Tạo phương thức removeDeadMonster cho MON_Orc trong AssetSetter
                    }
                }
            }
            for (int i = 0; i < golemBoss.length; i++) {
                if (golemBoss[i] != null) {
                    if (golemBoss[i].getCurrentHealth() > 0) {
                        golemBoss[i].update();
                    } else {
                        aSetter.removeDeadMonster(golemBoss, i, currentMap); //
                    }
                }
            }


            for (int i = projectiles.size() - 1; i >= 0; i--) {
                Projectile p = projectiles.get(i);
                if (p.isAlive()) {
                    p.update();
                    if (p.isAlive()) {
                        combatSystem.processProjectileImpacts(p);
                    }
                }
                if (!p.isAlive()) {
                    projectiles.remove(i);
                }
            }

            if (player != null && player.getCurrentHealth() > 0) {
                combatSystem.checkPlayerMonsterCombat(player, greenSlime);
                combatSystem.checkPlayerMonsterCombat(player, bat);
                combatSystem.checkPlayerMonsterCombat(player, golemBoss);
                combatSystem.checkPlayerMonsterCombat(player, orc);
            }

        } else if (gameState == pauseState) {
            // Logic cho trạng thái Pause
        }
    }


     // Xóa các thực thể (NPC, Monster, WorldObject) khỏi map hiện tại.
     // Player và projectiles sẽ được xử lý riêng nếu cần.
    public void clearEntitiesForMapChange() {
        for (int i = 0; i < wObjects.length; i++) {
            wObjects[i] = null;
        }
        for (int i = 0; i < npc.length; i++) {
            npc[i] = null;
        }
        for (int i = 0; i < greenSlime.length; i++) {
            greenSlime[i] = null;
        }
        for (int i = 0; i < orc.length; i++) {
            orc[i] = null;
        }
        for (int i = 0; i < bat.length; i++) {
            bat[i] = null;
        }
        for (int i = 0; i < golemBoss.length; i++) {
            golemBoss[i] = null;
        }
        projectiles.clear(); // Xóa tất cả projectiles khi chuyển map
        System.out.println("GamePanel: Entities and projectiles cleared for map change.");
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Vẽ màn hình tiêu đề
        if (gameState == titleState) {
            ui.draw(g2);
        }
        // THÊM MỚI: Vẽ màn hình chọn nhân vật
        else if (gameState == characterSelectState) {
            ui.draw(g2); // Chỉ cần vẽ UI cho màn hình này
        }
        // Các trạng thái còn lại (play, pause, dialogue, v.v.)
        else {
            // Vẽ thế giới game
            tileM.draw(g2);

            // Vẽ các đối tượng
            for (int i = 0; i < wObjects.length; i++) {
                if (wObjects[i] != null) {
                    wObjects[i].draw(g2, this);
                }
            }

            // Vẽ NPC
            for (int i = 0; i < npc.length; i++) {
                if (npc[i] != null) {
                    npc[i].draw(g2);
                }
            }
            // Vẽ quái vật...
            for (MON_GreenSlime slime : greenSlime) {
                if (slime != null) slime.draw(g2);
            }
            for (MON_Orc currentOrc : orc) {
                if (currentOrc != null) currentOrc.draw(g2);
            }
            for (MON_Bat currentBat : bat) {
                if (currentBat != null) currentBat.draw(g2);
            }
            for (MON_GolemBoss boss : golemBoss) {
                if (boss != null) boss.draw(g2);
            }
            // Vẽ projectiles...
            for (Projectile p : projectiles) {
                if (p.isAlive()) {
                    p.draw(g2);
                }
            }

            // Vẽ người chơi
            if (player != null) {
                player.draw(g2);
            }

            // Vẽ UI trên cùng
            ui.draw(g2);
        }
        g2.dispose();
    }

}