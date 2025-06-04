package worldObject.unpickableObject;

import character.Player;
import main.GamePanel; // Quan trọng: import GamePanel
import worldObject.WorldObject;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Chest extends WorldObject {

    private boolean hasBeenUsed = false; // Thêm cờ để chest chỉ save game một lần (tùy chọn)

    public OBJ_Chest() {
        name = "Chest";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/chest.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true; // Chest là vật cản, Player không đi xuyên qua được
    }

    @Override // Đảm bảo ghi đè phương thức từ lớp cha WorldObject
    public void interactPlayer(Player player, int i, GamePanel gp) { // Signature phải khớp với WorldObject
        // Hiện tại, tương tác xảy ra khi Player cố gắng "đi vào" Chest
        // do Player.pickUpItem() được gọi từ CollisionChecker.checkItem()

        if (hasBeenUsed) {
            gp.getUi().showMessage("This chest has already been used to save.");
            return;
        }

        if (gp.getSaveLoadManager() != null) {
            gp.getSaveLoadManager().saveGame(); // Gọi phương thức saveGame()
            // Bạn có thể thay đổi sprite của chest ở đây để biểu thị nó đã được sử dụng
            // ví dụ: try { image = ImageIO.read(getClass().getResourceAsStream("/objects/chest_opened.png")); } catch (IOException e) { e.printStackTrace(); }
            hasBeenUsed = true; // Đánh dấu chest đã được sử dụng
        } else {
            // Dòng này chỉ để debug, trong game thực tế có thể không cần
            System.err.println("OBJ_Chest: SaveLoadManager instance is null in GamePanel!");
            gp.getUi().showMessage("Error: Save system not available.");
        }
    }
}