package main;

import character.Character;
import character.role.Player;
import character.monster.*;
import data.SaveLoad;
import main.ui.*;
import skillEffect.SkillEffect;
import tile.TileManager;
import worldObject.WorldObject;
import dialogue.DialogueManager;
import sound.Sound;
import worldObject.unpickableObject.OBJ_Chest;

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
    private int maxWorldCol = 100; // Kích thước cố định cho tất cả map
    private int maxWorldRow = 100; // Kích thước cố định cho tất cả map
    private final int maxMap = 2; // SỐ LƯỢNG MAP
    private int currentMap = 0;   // Map hiện tại, bắt đầu từ map 0

    // SYSTEM
    private TileManager tileM = new TileManager(this);
    private KeyHandler keyH = new KeyHandler(this);
    public Sound music = new Sound();
    public Sound soundEffect = new Sound();
    private CollisionChecker cChecker = new CollisionChecker(this);
    private AssetSetter aSetter = new AssetSetter(this);
    private UiManager ui = new UiManager(this);
    private DialogueManager dialogueManager = new DialogueManager(this);
    private events_system.CombatSystem combatSystem = new events_system.CombatSystem(this);
    private SaveLoad saveLoadManager = new SaveLoad(this);
    private Thread gameThread;


    // ENTITY AND OBJECT
    private Player player;
    public WorldObject wObjects[] = new WorldObject[50];
    public Character npc[] = new Character[10];
    private ArrayList<Monster> monsters = new ArrayList<>();
    public List<SkillEffect> skillEffects = new ArrayList<>();
    public OBJ_Chest currentChest = null;


    // GAME STATE
    public int gameState;
    public static final int titleState = 0;
    public static final int playState = 1;
    public static final int pauseState = 2;
    public static final int dialogueState = 3;
    public static final int victoryEndState = 4;
    public static final int gameOverState = 5;
    public static final int characterSelectState = 6;
    public static final int InventoryState = 7;
    public static final int chestState = 8;
    public static final int loadGameState = 9;

    //GETTERS AND SETTERS

    public ArrayList<Monster> getMonster() {
        return monsters;
    }

    public int getMaxMap() {
        return maxMap;
    }

    public int getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(int currentMap) {
        this.currentMap = currentMap;
    }

    public int getOriginalTileSize() {
        return originalTileSize;
    }

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

    public UiManager getUi() { return ui; }


    public TileManager getTileM() { return tileM; }
    public CollisionChecker getcChecker() { return cChecker; }
    public AssetSetter getaSetter() { return aSetter; }
    public KeyHandler getKeyH() { return keyH; }
    public DialogueManager getDialogueManager() { return dialogueManager; }
    public int getFPS() { return FPS; }
    public SaveLoad getSaveLoadManager() { return saveLoadManager; }
    public Character getInteractingNPC() { return currentInteractingNPC; }
    public void setInteractingNPC(Character npc) { this.currentInteractingNPC = npc; }
    public WorldObject[] getwObjects() { return wObjects; }
    public events_system.CombatSystem getCombatSystem() {return combatSystem;}
    public Character[] getNpc() { return npc; }
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

        playMusic(Sound.MUSIC_BACKGROUND);
        if (player != null) {
            player.setDefaultValues();
            // Ví dụ đặt vị trí khởi đầu cho Player
            if(currentMap == 0) {
                player.setWorldX(getTileSize() * 7);
                player.setWorldY(getTileSize() * 92);
            } else if (currentMap == 1) {
                player.setWorldX(getTileSize() * 5);
                player.setWorldY( getTileSize() * 10);
            }
        }
        skillEffects.clear();
    }

    public void resetGameForNewSession() {
        System.out.println("Resetting game for new session...");
        currentMap = 0;

        // Quan trọng: Đặt player về null.
        // Player mới sẽ được tạo khi người dùng chọn "New Game".
        this.player = null;

        clearEntitiesForMapChange();
        aSetter.setupMapAssets(currentMap);

        dialogueManager.reset();

        // Tạo lại UI để reset các trạng thái như playtime
        ui = new UiManager(this);
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

            for (int i = monsters.size() - 1; i >= 0; i--) {
                Monster monster = monsters.get(i);
                if (monster != null) {
                    if (monster.getCurrentHealth() > 0) {
                        monster.update();
                    } else {
                        monsters.remove(i);
                    }
                } else {
                    monsters.remove(i);
                }
            }

            for (int i = skillEffects.size() - 1; i >= 0; i--) {
                SkillEffect p = skillEffects.get(i);
                if (p.isAlive()) {
                    p.update();
                    if (p.isAlive()) {
                        combatSystem.processSkillEffectImpacts(p);
                    }
                }
                if (!p.isAlive()) {
                    skillEffects.remove(i);
                }
            }

            if (player != null && player.getCurrentHealth() > 0) {
                combatSystem.checkPlayerMonsterCombat(player, monsters);
            }

        } else if (gameState == pauseState) {
            // Logic cho trạng thái Pause
        }
    }


     // Xóa các thực thể (NPC, Monster, WorldObject) khỏi map hiện tại.
     // Player và skillEffects sẽ được xử lý riêng nếu cần.
    public void clearEntitiesForMapChange() {
        for (int i = 0; i < wObjects.length; i++) {
            wObjects[i] = null;
        }
        for (int i = 0; i < npc.length; i++) {
            npc[i] = null;
        }

        monsters.clear();
        skillEffects.clear(); // Xóa tất cả skillEffects khi chuyển map
        System.out.println("GamePanel: Entities and skillEffects cleared for map change.");
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // CÁC TRẠNG THÁI CHỈ VẼ UI (Menu, Lựa chọn, Game Over, v.v.)
        if (gameState == titleState || gameState == characterSelectState || gameState == loadGameState || gameState == gameOverState || gameState == victoryEndState) {
            ui.draw(g2);
        }
        // CÁC TRẠNG THÁI CẦN VẼ THẾ GIỚI GAME
        else {
            if (tileM != null) tileM.draw(g2);

            for (WorldObject wObject : wObjects) {
                if (wObject != null) wObject.draw(g2, this);
            }
            for (SkillEffect p : skillEffects) {
                if (p != null && p.isAlive()) p.draw(g2);
            }
            for (Character character : npc) {
                if (character != null) character.draw(g2);
            }
            for (Monster monster : monsters) {
                if (monster != null) monster.draw(g2);
            }
            if (player != null) {
                player.draw(g2);
            }
            if (ui != null) ui.draw(g2);
        }
        g2.dispose();
    }
    public void GameOver() {
        stopMusic();
        gameState = gameOverState;
        ui.setUI(gameOverState); // CỰC KỲ QUAN TRỌNG: Báo cho UiManager thay đổi giao diện
    }
    public void Victory() {
        stopMusic();
        gameState = victoryEndState;
        ui.setUI(victoryEndState); // CỰC KỲ QUAN TRỌNG: Báo cho UiManager thay đổi giao diện
    }

}