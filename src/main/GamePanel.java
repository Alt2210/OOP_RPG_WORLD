package main;

import character.Character;
import character.Player;
import character.monster.MON_GreenSlime;
import tile.TileManager;
import worldObject.WorldObject;
import dialogue.DialogueManager;

import javax.swing.*;
import java.awt.*;

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
    private final int maxWorldCol = 50;
    private final int maxWorldRow = 50;
    private final int worldWidth = tileSize * maxWorldCol;
    private final int worldHeight = tileSize * maxWorldRow;

    private UI ui = new UI(this);
    private DialogueManager dialogueManager = new DialogueManager(this);
    private int FPS = 60;
    private Character currentInteractingNPC = null;
    // Game State
    public int gameState;
    public final int titleState = 0;
    public final int playState=1;
    public final int pauseState=2;
    public final int dialogueState = 3;
    public final int endGameState = 4;


    public UI getUi(){return ui;}

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

    public DialogueManager getDialogueManager() {return dialogueManager;}

    public Character getInteractingNPC() { return currentInteractingNPC; }

    public void setInteractingNPC(Character npc) { this.currentInteractingNPC = npc; }

    public TileManager getTileM() {
        return tileM;
    }

    public KeyHandler getKeyH() {
        return keyH;
    }

    public Thread getGameThread() {
        return gameThread;
    }

    public void stopGameThread() { this.gameThread = null; }

    public CollisionChecker getcChecker() {
        return cChecker;
    }

    public AssetSetter getaSetter() {
        return aSetter;
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
    public Character[] getMON_GreenSlime() { return greenSlime;  }

    private TileManager tileM = new TileManager(this);;
    private KeyHandler keyH = new KeyHandler(this);
    private Thread gameThread;
    private CollisionChecker cChecker = new CollisionChecker(this);
    private AssetSetter aSetter = new AssetSetter(this);
    private Player player = new Player(this,keyH);
    private WorldObject wObjects[] = new WorldObject[10];
    private character.Character npc[] = new character.Character[10];
    private character.Character greenSlime[] = new character.Character[10]; // Nếu Monster kế thừa từ Character


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
        gameState= playState;
        aSetter.setGreenSlime();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override

    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta >= 1) {
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
        if(gameState == playState) {
            player.update();

            for (int i = 0; i < npc.length; i++) {
                if (npc[i] != null) {
                    npc[i].setAction(); // NPC quyết định hành động (ví dụ: đổi hướng)
                    npc[i].update();    // NPC thực hiện cập nhật (di chuyển, animation)
                }
            }

            for (int i = 0; i < greenSlime.length; i++) {
                if (greenSlime[i] != null) {
                    greenSlime[i].setAction();
                    greenSlime[i].update();
                }
            }
        }
        else if(gameState== pauseState){

        }
        else if(gameState== endGameState){


        }
    }
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        tileM.draw(g2);

        for (int i = 0; i < wObjects.length; i++) {
            if (wObjects[i] != null) {
                wObjects[i].draw(g2, this);
            }
        }
        for(int i=0; i< npc.length; i++){
            if(npc[i] != null){
                npc[i].draw(g2);
            }
        }

        for(int i = 0; i < greenSlime.length; i++){ // Sử dụng mảng 'monster' mới
            if(greenSlime[i] != null){
                greenSlime[i].draw(g2);
            }
        }
        player.draw(g2);

        ui.draw(g2);
        g2.dispose();
    }
}
