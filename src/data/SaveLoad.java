package data;

import character.monster.*;
import main.GamePanel;
import character.role.Player;
import worldObject.WorldObject;
import worldObject.pickableObject.OBJ_HealthPotion;
import worldObject.pickableObject.OBJ_Key;
import worldObject.unpickableObject.OBJ_Chest;
import worldObject.unpickableObject.OBJ_Door;
import worldObject.unpickableObject.OBJ_Portal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveLoad {
    GamePanel gp;
    private static final String SAVE_FILE_NAME = "save.dat";

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    public void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_NAME))) {
            DataStorage data = new DataStorage(gp.getMaxMap());
            Player player = gp.getPlayer();

            if (player == null) {
                System.err.println("Save Error: Player object is null.");
                return;
            }

            // 1. Lưu trạng thái Player
            data.maxHealth = player.getMaxHealth();
            data.currentHealth = player.getCurrentHealth();
            data.maxMana = player.getMaxMana();
            data.currentMana = player.getCurrentMana();
            data.hasKey = player.getHasKey();
            data.playerWorldX = player.getWorldX();
            data.playerWorldY = player.getWorldY();
            data.playerDirection = player.getDirection();
            data.setCurrentMap(gp.getCurrentMap());

            // 2. Lưu trạng thái của tất cả WorldObject trên map hiện tại
            for (WorldObject wObject : gp.getwObjects()) {
                if (wObject != null) {
                    data.objectStates[gp.getCurrentMap()].add(new WorldObjectState(wObject.getName(), wObject.getWorldX(), wObject.getWorldY(), true));
                }
            }

            for (Monster monster : gp.getMonster()) {
                if (monster != null) {
                    data.monsterStates[gp.getCurrentMap()].add(new MonsterState(monster.getName(), monster.getWorldX(), monster.getWorldY(), monster.getCurrentHealth(), monster.isOnPath()));
                }
            }

            oos.writeObject(data);
            gp.getUi().showMessage("Game Saved!");
            System.out.println("Game saved for map " + gp.getCurrentMap());

        } catch (IOException e) {
            System.err.println("Save Exception: " + e.getMessage());
            e.printStackTrace();
            gp.getUi().showMessage("Error saving game!");
        }
    }


    public void loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE_NAME))) {
            DataStorage data = (DataStorage) ois.readObject();
            Player player = gp.getPlayer();

            if (player == null) {
                System.err.println("Load Error: Player is null.");
                return;
            }

            // 1. Tải trạng thái Player
            player.setMaxHealth(data.maxHealth);
            player.setCurrentHealth(data.currentHealth);
            player.setCurrentMana(data.currentMana);
            player.setHasKey(data.hasKey);
            player.setWorldX(data.playerWorldX);
            player.setWorldY(data.playerWorldY);
            player.setDirection(data.playerDirection);
            gp.setCurrentMap(data.getCurrentMap());

            // 2. Dọn dẹp thế giới cũ và tái tạo lại từ dữ liệu đã lưu
            gp.clearEntitiesForMapChange();

            // Tải trạng thái WorldObject
            if (data.objectStates[gp.getCurrentMap()] != null) {
                for (int i = 0; i < data.objectStates[gp.getCurrentMap()].size(); i++) {
                    WorldObjectState state = data.objectStates[gp.getCurrentMap()].get(i);
                    if (state.isExists()) {
                        gp.getwObjects()[i] = createObjectFromName(state.getName());
                        if (gp.getwObjects()[i] != null) {
                            gp.getwObjects()[i].setWorldX(state.getWorldX());
                            gp.getwObjects()[i].setWorldY(state.getWorldY());
                        }
                    }
                }
            }

            if(data.monsterStates[gp.getCurrentMap()] != null){
                for (int i = 0; i < data.monsterStates[gp.getCurrentMap()].size(); i++) {
                    MonsterState state = data.monsterStates[gp.getCurrentMap()].get(i);
                    Monster monster = createMonsterFromName(state.getName());
                    if (monster != null) {
                        monster.setWorldX(state.getWorldX());
                        monster.setWorldY(state.getWorldY());
                        monster.setCurrentHealth(state.getCurrentHealth());
                        monster.setOnPath(state.isOnPath());
                        gp.getMonster().add(monster); // Thêm vào ArrayList
                    }
                }
            }


            gp.getUi().showMessage("Game Loaded!");
            gp.gameState = gp.playState;
            System.out.println("Game loaded for map " + gp.getCurrentMap());

        } catch (FileNotFoundException e) {
            gp.getUi().showMessage("No save file found.");
        } catch (Exception e) {
            System.err.println("Load Exception: " + e.getMessage());
            e.printStackTrace();
            gp.getUi().showMessage("Error loading game: File corrupted or incompatible.");
        }
    }

    private WorldObject createObjectFromName(String name) {
        switch (name) {
            case "Key":
                return new OBJ_Key(gp);
            case "Door":
                return new OBJ_Door();
            case "Chest":
                return new OBJ_Chest(gp);
            case "Health Potion":
                return new OBJ_HealthPotion(gp);
            // Thêm các case cho portal và các vật phẩm khác nếu cần
            default:
                return null;
        }
    }

    private Monster createMonsterFromName(String name) {
        if (name == null) return null;
        switch (name) {
            case "Green Slime":
                return new MON_GreenSlime(gp);
            case "Bat":
                return new MON_Bat(gp);
            case "Orc":
                return new MON_Orc(gp);
            case "Skeleton Lord":
                return new MON_SkeletonLord(gp);
            case "GolemBoss":
                return new MON_GolemBoss(gp);
            default:
                System.err.println("SaveLoad: Unknown monster name '" + name + "' in save file.");
                return null;
        }
    }
}