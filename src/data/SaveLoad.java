package data;

import character.monster.*;
import character.role.*;
import main.GamePanel;
import worldObject.WorldObject;
import worldObject.pickableObject.*;

import worldObject.unpickableObject.*;

import java.io.*;
import java.util.List;

public class SaveLoad {
    GamePanel gp;
    // Tên file mới để chứa toàn bộ lịch sử save
    private static final String SAVE_FILE_NAME = "game_history.dat";

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Đọc file game_history.dat và trả về đối tượng GameHistory.
     * Nếu file không tồn tại hoặc lỗi, trả về một đối tượng rỗng.
     * @return Đối tượng GameHistory chứa danh sách các điểm save.
     */
    public GameHistory getSaveHistory() {
        GameHistory history = new GameHistory();
        File saveFile = new File(SAVE_FILE_NAME);
        if (saveFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
                history = (GameHistory) ois.readObject();
            } catch (Exception e) {
                System.err.println("Could not load save history: " + e.getMessage());
                // Trả về history rỗng nếu file bị lỗi
            }
        }
        return history;
    }

    /**
     * Lưu trạng thái game hiện tại vào log.
     * Phương thức này đọc lịch sử cũ, thêm điểm save mới, và ghi đè lại toàn bộ.
     * @param description Mô tả cho điểm save (ví dụ: "Vào hầm ngục").
     */
    public void saveGame(String description) {
        GameHistory history = getSaveHistory(); // Đọc lịch sử cũ

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

            data.level = player.getLevel();
            data.currentExp = player.getCurrentExp();
            data.expToNextLevel = player.getExpToNextLevel();
            data.attack = player.getAttack();
            data.defense = player.getDefense();

            data.playerWorldX = player.getWorldX();
            data.playerWorldY = player.getWorldY();
            data.playerDirection = player.getDirection();
            data.setCurrentMap(gp.getCurrentMap());
            data.playerClassIdentifier = player.getCharacterClassIdentifier();
            // Lưu thông tin mô tả và timestamp
            data.setTimestamp(System.currentTimeMillis());
            data.setDescription(description);

            // 2. Lưu trạng thái của tất cả WorldObject trên map hiện tại
            for (WorldObject wObject : gp.getwObjects()) { //
                if (wObject != null) {
                    data.objectStates[gp.getCurrentMap()].add(new WorldObjectState(wObject.getName(), wObject.getWorldX(), wObject.getWorldY(), true)); //
                }
            }

            // 3. Lưu trạng thái Monster
            for (Monster monster : gp.getMonster()) { //
                if (monster != null) {
                    data.monsterStates[gp.getCurrentMap()].add(new MonsterState(monster.getName(), monster.getWorldX(), monster.getWorldY(), monster.getCurrentHealth(), monster.isOnPath())); //
                }
            }

            // Thêm điểm save mới vào lịch sử và ghi lại toàn bộ
            history.addSavePoint(data);
            oos.writeObject(history);

            gp.getUi().showMessage("Game Saved: " + description);
            System.out.println("Game saved for map " + gp.getCurrentMap());

        } catch (IOException e) {
            System.err.println("Save Exception: " + e.getMessage());
            e.printStackTrace();
            gp.getUi().showMessage("Error saving game!");
        }
    }

    /**
     * Tải lại game từ một điểm save cụ thể trong log.
     * @param slotIndex Chỉ số của điểm save trong danh sách (bắt đầu từ 0).
     */
    public void loadGame(int slotIndex) {
        GameHistory history = getSaveHistory();
        if (slotIndex < 0 || slotIndex >= history.getSavePoints().size()) {
            gp.getUi().showMessage("Invalid save slot selected.");
            return;
        }

        try {
            DataStorage data = history.getSavePoints().get(slotIndex);

            if ("sodier".equals(data.playerClassIdentifier)) {
                gp.setPlayer(new Soldier(gp, gp.getKeyH()));
            } else if ("astrologist".equals(data.playerClassIdentifier)) {
                gp.setPlayer(new Astrologer(gp, gp.getKeyH()));
            } else {
                System.err.println("Load Error: Unknown player class identifier: " + data.playerClassIdentifier);
                gp.getUi().showMessage("Error: Save file is corrupted (unknown class)!");
                return;
            }

            Player player = gp.getPlayer();

            // 1. Tải trạng thái Player
            player.setMaxHealth(data.maxHealth);
            player.setCurrentHealth(data.currentHealth);
            player.setMaxMana(data.maxMana);
            player.setCurrentMana(data.currentMana);
            player.setHasKey(data.hasKey);

            player.setLevel(data.level);
            player.setCurrentExp(data.currentExp);
            player.setExpToNextLevel(data.expToNextLevel);
            player.setAttack(data.attack);
            player.setDefense(data.defense);

            player.setWorldX(data.playerWorldX);
            player.setWorldY(data.playerWorldY);
            player.setDirection(data.playerDirection);
            gp.setCurrentMap(data.getCurrentMap());

            // 2. Dọn dẹp thế giới cũ
            gp.clearEntitiesForMapChange();
            // BỎ LỆNH gp.getaSetter().setupMapAssets(gp.getCurrentMap());
            // Chúng ta sẽ tái tạo thế giới hoàn toàn từ file save.

            // Tải trạng thái WorldObject từ file save
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

            // Tải trạng thái Monster từ file save
            if (data.monsterStates[gp.getCurrentMap()] != null) {
                for (MonsterState state : data.monsterStates[gp.getCurrentMap()]) {
                    Monster monster = createMonsterFromName(state.getName());
                    if (monster != null) {
                        monster.setWorldX(state.getWorldX());
                        monster.setWorldY(state.getWorldY());
                        monster.setCurrentHealth(state.getCurrentHealth());
                        monster.setOnPath(state.isOnPath());
                        gp.getMonster().add(monster);
                    }
                }
            }

            gp.getUi().showMessage("Game Loaded: " + data.getDescription());
            gp.gameState = GamePanel.playState;
            gp.getUi().setUI(gp.gameState);

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
            case "Health Potion":
                return new OBJ_HealthPotion(gp);
            case "Mana Potion":
                return new OBJ_ManaPotion(gp);
            case "Normal Sword":
                return new OBJ_Sword(gp);
            case "Normal Book":
                return new OBJ_Book(gp);

            // Các đối tượng không thể nhặt
            case "Door":
                return new OBJ_Door();
            case "Chest":
                return new OBJ_Chest(gp);
            case "Portal":
                return new OBJ_Portal(gp, 0, 0, 0); // Tọa độ đích sẽ được ghi đè nếu cần
            case "Revive Statue":
                return new OBJ_ReviveStatue(gp);
            case "Spike Trap":
                return new OBJ_Spike(gp);default:
                return null;
        }
    }

    private Monster createMonsterFromName(String name) {
        if (name == null) return null;
        switch (name) {
            case "Green Slime":
                return new MON_GreenSlime(gp); //
            case "Bat":
                return new MON_Bat(gp); //
            case "Orc":
                return new MON_Orc(gp); //
            case "Skeleton Lord":
                return new MON_SkeletonLord(gp); //
            case "GolemBoss":
                return new MON_GolemBoss(gp); //
            default:
                System.err.println("SaveLoad: Unknown monster name '" + name + "' in save file.");
                return null;
        }
    }
}