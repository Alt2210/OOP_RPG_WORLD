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
            data.setCurrentMapIndex(gp.getCurrentMapIndex());
            data.playerClassIdentifier = player.getCharacterClassIdentifier();
            // Lưu thông tin mô tả và timestamp
            data.setTimestamp(System.currentTimeMillis());
            data.setDescription(description);

            // 2. Lưu trạng thái của tất cả WorldObject trên map hiện tại
            for (WorldObject wObject : gp.getCurrentMap().getwObjects()) { //
                if (wObject != null) {
                    data.objectStates[gp.getCurrentMapIndex()].add(new WorldObjectState(wObject.getName(), wObject.getWorldX(), wObject.getWorldY(), true)); //
                }
            }

            // 3. Lưu trạng thái Monster
            for (Monster monster : gp.getCurrentMap().getMonster()) { //
                if (monster != null) {
                    data.monsterStates[gp.getCurrentMapIndex()].add(new MonsterState(monster.getName(), monster.getWorldX(), monster.getWorldY(), monster.getCurrentHealth(), monster.isOnPath())); //
                }
            }

            // Thêm điểm save mới vào lịch sử và ghi lại toàn bộ
            history.addSavePoint(data);
            oos.writeObject(history);

            gp.getUi().showMessage("Game Saved: " + description);
            System.out.println("Game saved for map " + gp.getCurrentMapIndex());

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

            // 1. Tải trạng thái Player
            // ... (Giữ nguyên logic tạo Player và thiết lập Player stats)
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

            // 2. Thiết lập bản đồ mới và xóa các thực thể
            gp.setCurrentMapIndex(data.getCurrentMapIndex()); // Đảm bảo map index đúng
            gp.clearEntitiesForMapChange(); // Xóa các thực thể của bản đồ cũ (nếu có) HOẶC map mới sau khi set currentMap

            // QUAN TRỌNG: Gọi setupMapAssets để khởi tạo bản đồ MỚI với các thực thể mặc định
            gp.getaSetter().setupMapAssets(gp.getCurrentMapIndex()); //
            gp.setCurrentMap(gp.getaSetter().getMap(gp.getCurrentMapIndex())); // Cập nhật currentMap reference


            // 3. Ghi đè trạng thái của WorldObject từ file save
            // Sau khi `setupMapAssets` đã tạo ra các đối tượng mặc định cho map hiện tại,
            // chúng ta sẽ xóa chúng và chỉ thêm lại những gì có trong save.
            // Điều này là cần thiết nếu bạn có các đối tượng có trạng thái (như rương đã mở, hoặc đã bị nhặt).

            // Xóa tất cả WorldObject và Monster mặc định của bản đồ vừa được setupMapAssets tạo
            gp.getCurrentMap().getwObjects().clear(); //
            gp.getCurrentMap().getMonster().clear(); //
            gp.getCurrentMap().getNpc().clear(); // Cũng cần xóa NPC nếu trạng thái NPC cũng được lưu

            if (data.objectStates[gp.getCurrentMapIndex()] != null) { //
                for (WorldObjectState state : data.objectStates[gp.getCurrentMapIndex()]) { //
                    if (state.isExists()) { //
                        WorldObject loadedObject = createObjectFromName(state.getName()); //
                        if (loadedObject != null) { //
                            loadedObject.setWorldX(state.getWorldX()); //
                            loadedObject.setWorldY(state.getWorldY()); //
                            // Xử lý các trạng thái đặc biệt của object (ví dụ: isOpened cho Chest)
                            if (loadedObject instanceof OBJ_Chest) {
                                // Nếu bạn lưu trạng thái isOpened của Chest trong WorldObjectState, hãy tải nó ở đây
                                // Hiện tại WorldObjectState chỉ có name, worldX, worldY, exists
                                // Bạn cần mở rộng WorldObjectState và OBJ_Chest để lưu và tải trạng thái này.
                                // Ví dụ: ((OBJ_Chest) loadedObject).setOpened(state.isChestOpened());
                            }
                            gp.getCurrentMap().getwObjects().add(loadedObject); // Thêm vào danh sách của currentMap
                        }
                    }
                }
            }

            // Tải trạng thái Monster từ file save
            if (data.monsterStates[gp.getCurrentMapIndex()] != null) { //
                for (MonsterState state : data.monsterStates[gp.getCurrentMapIndex()]) { //
                    Monster monster = createMonsterFromName(state.getName()); //
                    if (monster != null) { //
                        monster.setWorldX(state.getWorldX()); //
                        monster.setWorldY(state.getWorldY()); //
                        monster.setCurrentHealth(state.getCurrentHealth()); //
                        monster.setOnPath(state.isOnPath()); //
                        gp.getCurrentMap().getMonster().add(monster); //
                    }
                }
            }

            // Tải trạng thái NPC (nếu bạn lưu NPC)
            // Tương tự cho NPC, bạn sẽ cần một NpcState và thêm logic tải vào đây.
            // Nếu NPC luôn có mặt và không thay đổi vị trí/trạng thái (trừ dialogue), có thể không cần lưu.

            gp.getUi().showMessage("Game Loaded: " + data.getDescription()); //
            gp.gameState = GamePanel.playState; //
            gp.getUi().setUI(gp.gameState); //

        } catch (Exception e) {
            System.err.println("Load Exception: " + e.getMessage()); //
            e.printStackTrace(); //
            gp.getUi().showMessage("Error loading game: File corrupted or incompatible."); //
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