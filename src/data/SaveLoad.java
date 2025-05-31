package data;

import main.GamePanel;
import character.Player; // Giả sử bạn có lớp Player

import java.io.*;

public class SaveLoad {
    GamePanel gp;
    private static final String SAVE_FILE_NAME = "save.dat"; // Đặt tên file lưu làm hằng số

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    public void saveGame() { // Đổi tên phương thức cho rõ ràng hơn
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_NAME))) {
            DataStorage data = new DataStorage();
            Player player = gp.getPlayer(); // Lấy đối tượng Player một lần

            // --- Lưu trữ dữ liệu từ Player ---
            // Giả sử Player có các getter/setter hoặc bạn truy cập trực tiếp thuộc tính
            // Bạn cần đảm bảo lớp Player có các thuộc tính này hoặc phương thức tương ứng
            // data.level = player.getLevel(); // Ví dụ nếu có phương thức getLevel()
            data.maxHealth = player.getMaxHealth();
            data.currentHealth = player.getCurrentHealth();
            // data.maxMana = player.getMaxMana();
            // data.currentMana = player.getCurrentMana();
            // data.strength = player.getStrength();
            // data.dexterity = player.getDexterity();
            // data.exp = player.getExp();

            data.playerWorldX = player.worldX;
            data.playerWorldY = player.worldY;
            data.playerDirection = player.direction;

            data.hasKey = player.getHasKey();

            // --- Lưu trữ dữ liệu từ GamePanel hoặc các hệ thống khác (ví dụ UI) ---
            // data.playtime = gp.getUi().getPlaytime(); // Cần có getPlaytime() trong lớp UI
            // data.currentMapName = gp.getCurrentMapName(); // Nếu có

            oos.writeObject(data);
            System.out.println("Game saved successfully to " + SAVE_FILE_NAME);

        } catch (IOException e) {
            System.err.println("Save Exception: Could not write save data to file '" + SAVE_FILE_NAME + "'.");
            e.printStackTrace(); // In ra chi tiết lỗi để dễ debug
        }
    }

    public void loadGame() { // Đổi tên phương thức
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE_NAME))) {
            DataStorage data = (DataStorage) ois.readObject();
            Player player = gp.getPlayer();

            // --- Khôi phục dữ liệu cho Player ---
            // Bạn cần đảm bảo lớp Player có các setter hoặc bạn gán trực tiếp thuộc tính
            // player.setLevel(data.level);
            player.setMaxHealth(data.maxHealth); // Có thể cần khởi tạo lại thanh máu UI sau khi load
            player.setCurrentHealth(data.currentHealth);
            // player.setMaxMana(data.maxMana);
            // player.setCurrentMana(data.currentMana);
            // player.setStrength(data.strength);
            // player.setDexterity(data.dexterity);
            // player.setExp(data.exp);

            player.worldX = data.playerWorldX;
            player.worldY = data.playerWorldY;
            player.direction = data.playerDirection; // Quan trọng để sprite hiển thị đúng sau khi load

            player.setHasKey(data.hasKey); // Giả sử có phương thức setHasKey()

            // --- Khôi phục dữ liệu cho GamePanel hoặc các hệ thống khác ---
            // gp.getUi().setPlaytime(data.playtime); // Cần có setPlaytime() trong lớp UI
            // gp.loadMap(data.currentMapName); // Nếu có

            System.out.println("Game loaded successfully from " + SAVE_FILE_NAME);
            // Sau khi load, có thể cần cập nhật lại UI hoặc các trạng thái khác của game
            gp.getUi().showMessage("Game Loaded!");
            gp.gameState = gp.playState;

        } catch (FileNotFoundException e) {
            System.out.println("Load Warning: No save file found: '" + SAVE_FILE_NAME + "'. Starting new game or default state.");
            // Tại đây, bạn có thể quyết định bắt đầu game mới hoặc không làm gì cả, để game tự khởi tạo mặc định
        } catch (IOException e) {
            System.err.println("Load Exception: Could not read save data from file '" + SAVE_FILE_NAME + "'.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Load Exception: DataStorage class not found. Save file may be corrupted or from an incompatible game version.");
            e.printStackTrace();
        }
    }
}