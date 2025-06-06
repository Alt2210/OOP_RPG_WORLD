package data;

import main.GamePanel;
import character.Role.Player;

import java.io.*;

public class SaveLoad {
    GamePanel gp;
    private static final String SAVE_FILE_NAME = "save.dat"; //

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    public void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_NAME))) {
            DataStorage data = new DataStorage();
            Player player = gp.getPlayer();

            if (player == null) {
                System.err.println("Save Error: Player object is null. Cannot save game.");
                return;
            }
            // Player Stats
            // data.level = player.getLevel(); // Ví dụ
            data.maxHealth = player.getMaxHealth(); //
            data.currentHealth = player.getCurrentHealth(); //
            data.maxMana = player.getMaxMana(); //
            data.currentMana = player.getCurrentMana(); //
            // data.strength = player.getStrength();
            // data.dexterity = player.getDexterity();
            // data.exp = player.getExp();

            // Player Position and Direction
            data.playerWorldX = player.worldX; //
            data.playerWorldY = player.worldY; //
            data.playerDirection = player.direction; //

            // Player Inventory/Quest Items
            data.hasKey = player.getHasKey(); //

            // Game World State
            // data.playtime = gp.getUi().getPlaytime(); // Cần getter cho playtime trong UI

            // THÊM MỚI: Lưu map hiện tại
            data.currentMap = gp.currentMap;

            oos.writeObject(data);
            System.out.println("Game saved successfully to " + SAVE_FILE_NAME + " (Map: " + data.currentMap + ")");
            gp.getUi().showMessage("Game Saved!");


        } catch (IOException e) {
            System.err.println("Save Exception: Could not write save data to file '" + SAVE_FILE_NAME + "'."); //
            e.printStackTrace(); //
            gp.getUi().showMessage("Error saving game!");
        }
    }

    public void loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE_NAME))) {
            DataStorage data = (DataStorage) ois.readObject();
            Player player = gp.getPlayer();

            if (player == null) {
                System.err.println("Load Error: Player object is null. Cannot load game.");
                return;
            }

            // THÊM MỚI: Tải map hiện tại TRƯỚC khi tải vị trí Player
            // và TRƯỚC KHI setup assets
            gp.currentMap = data.currentMap;

            // Khôi phục dữ liệu cho Player
            // player.setLevel(data.level);
            player.setMaxHealth(data.maxHealth); //
            player.setCurrentHealth(data.currentHealth); //
            player.setCurrentMana(data.currentMana); // Giả sử currentMana có trong DataStorage và Player setter
            // player.setStrength(data.strength);
            // player.setDexterity(data.dexterity);
            // player.setExp(data.exp);

            player.worldX = data.playerWorldX; //
            player.worldY = data.playerWorldY; //
            player.direction = data.playerDirection; //
            player.setHasKey(data.hasKey); //

            // QUAN TRỌNG: Sau khi tải currentMap và vị trí player,
            // cần dọn dẹp và thiết lập lại các thực thể cho map đã tải.
            gp.clearEntitiesForMapChange(); // Xóa entities của map có thể đang active (nếu có từ lần chạy trước)
            gp.getaSetter().setupMapAssets(gp.currentMap); // Tải assets cho map vừa load

            // Khôi phục dữ liệu GamePanel hoặc UI khác (nếu có)
            // gp.getUi().setPlaytime(data.playtime);

            System.out.println("Game loaded successfully from " + SAVE_FILE_NAME + ". Current map: " + gp.currentMap);
            gp.getUi().showMessage("Game Loaded!"); //
            gp.gameState = gp.playState; //

        } catch (FileNotFoundException e) {
            System.out.println("Load Warning: No save file found: '" + SAVE_FILE_NAME + "'. Starting new game or default state."); //
            gp.getUi().showMessage("No save file found.");
            // Không chuyển gameState, để title screen hoặc logic khởi tạo game mới xử lý
        } catch (IOException e) {
            System.err.println("Load Exception: Could not read save data from file '" + SAVE_FILE_NAME + "'."); //
            e.printStackTrace(); //
            gp.getUi().showMessage("Error loading game: IO Exception.");
        } catch (ClassNotFoundException e) {
            System.err.println("Load Exception: DataStorage class not found. Save file may be corrupted or from an incompatible game version."); //
            e.printStackTrace(); //
            gp.getUi().showMessage("Error loading game: File corrupted/incompatible.");
        }  catch (Exception e) { // Bắt các lỗi không mong muốn khác
            System.err.println("Load Exception: An unexpected error occurred during loading.");
            e.printStackTrace();
            gp.getUi().showMessage("Error loading game: Unexpected error.");
        }
    }
}