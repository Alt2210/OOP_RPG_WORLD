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
            DataStorage data = new DataStorage(gp.maxMap);
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
            data.currentMap = gp.currentMap;

            // 2. Lưu trạng thái của tất cả WorldObject trên map hiện tại
            for (WorldObject wObject : gp.getwObjects()) {
                if (wObject != null) {
                    data.objectStates[gp.currentMap].add(new WorldObjectState(wObject.name, wObject.getWorldX(), wObject.getWorldY(), true));
                }
            }

            // 3. Lưu trạng thái của tất cả Monster trên map hiện tại
            saveMonsterStates(data.monsterStates[gp.currentMap]);


            oos.writeObject(data);
            gp.getUi().showMessage("Game Saved!");
            System.out.println("Game saved for map " + gp.currentMap);

        } catch (IOException e) {
            System.err.println("Save Exception: " + e.getMessage());
            e.printStackTrace();
            gp.getUi().showMessage("Error saving game!");
        }
    }

    private void saveMonsterStates(List<MonsterState> monsterStateList) {
        // Lưu trạng thái của Green Slimes
        for (Monster monster : gp.getMON_GreenSlime()) {
            if (monster != null) {
                monsterStateList.add(new MonsterState(monster.getName(), monster.getWorldX(), monster.getWorldY(), monster.getCurrentHealth(), monster.isOnPath()));
            }
        }
        // Lưu trạng thái của Bats
        for (Monster monster : gp.getMON_Bat()) {
            if (monster != null) {
                monsterStateList.add(new MonsterState(monster.getName(), monster.getWorldX(), monster.getWorldY(), monster.getCurrentHealth(), monster.isOnPath()));
            }
        }
        // Lưu trạng thái của Orcs
        for (Monster monster : gp.getMON_Orc()) {
            if (monster != null) {
                monsterStateList.add(new MonsterState(monster.getName(), monster.getWorldX(), monster.getWorldY(), monster.getCurrentHealth(), monster.isOnPath()));
            }
        }
        // Lưu trạng thái của Skeleton Lords
        for (Monster monster : gp.getSkeletonLord()) {
            if (monster != null) {
                monsterStateList.add(new MonsterState(monster.getName(), monster.getWorldX(), monster.getWorldY(), monster.getCurrentHealth(), monster.isOnPath()));
            }
        }
        // Lưu trạng thái của Golem Bosses
        for (Monster monster : gp.getMON_GolemBoss()) {
            if (monster != null) {
                monsterStateList.add(new MonsterState(monster.getName(), monster.getWorldX(), monster.getWorldY(), monster.getCurrentHealth(), monster.isOnPath()));
            }
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
            gp.currentMap = data.currentMap;

            // 2. Dọn dẹp thế giới cũ và tái tạo lại từ dữ liệu đã lưu
            gp.clearEntitiesForMapChange();

            // Tải trạng thái WorldObject
            if (data.objectStates[gp.currentMap] != null) {
                for (int i = 0; i < data.objectStates[gp.currentMap].size(); i++) {
                    WorldObjectState state = data.objectStates[gp.currentMap].get(i);
                    if (state.isExists()) {
                        gp.getwObjects()[i] = createObjectFromName(state.getName());
                        if (gp.getwObjects()[i] != null) {
                            gp.getwObjects()[i].setWorldX(state.getWorldX());
                            gp.getwObjects()[i].setWorldY(state.getWorldY());
                        }
                    }
                }
            }

            // Tải trạng thái Monster
            if (data.monsterStates[gp.currentMap] != null) {
                loadMonsterStates(data.monsterStates[gp.currentMap]);
            }

            gp.getUi().showMessage("Game Loaded!");
            gp.gameState = gp.playState;
            System.out.println("Game loaded for map " + gp.currentMap);

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

    private void loadMonsterStates(List<MonsterState> monsterStateList) {
        int greenSlimeIndex = 0;
        int batIndex = 0;
        int orcIndex = 0;
        int skeletonLordIndex = 0;
        int golemBossIndex = 0;

        for(MonsterState state : monsterStateList) {
            Monster monster = null;
            if (state.getName() == null) continue;

            switch(state.getName()) {
                case "Green Slime":
                    if(greenSlimeIndex < gp.getMON_GreenSlime().length) {
                        monster = new MON_GreenSlime(gp);
                        gp.getMON_GreenSlime()[greenSlimeIndex++] = (MON_GreenSlime) monster;
                    }
                    break;
                case "Bat":
                    if(batIndex < gp.getMON_Bat().length) {
                        monster = new MON_Bat(gp);
                        gp.getMON_Bat()[batIndex++] = (MON_Bat) monster;
                    }
                    break;
                case "Orc":
                    if(orcIndex < gp.getMON_Orc().length) {
                        monster = new MON_Orc(gp);
                        gp.getMON_Orc()[orcIndex++] = (MON_Orc) monster;
                    }
                    break;
                case "Skeleton Lord":
                    if(skeletonLordIndex < gp.getSkeletonLord().length) {
                        monster = new MON_SkeletonLord(gp);
                        gp.getSkeletonLord()[skeletonLordIndex++] = (MON_SkeletonLord) monster;
                    }
                    break;
                case "GolemBoss":
                    if(golemBossIndex < gp.getMON_GolemBoss().length) {
                        monster = new MON_GolemBoss(gp);
                        gp.getMON_GolemBoss()[golemBossIndex++] = (MON_GolemBoss) monster;
                    }
                    break;
            }

            if(monster != null) {
                monster.setWorldX(state.getWorldX());
                monster.setWorldY(state.getWorldY());
                monster.setCurrentHealth(state.getCurrentHealth());
                monster.setOnPath(state.isOnPath());
            }
        }
    }
}