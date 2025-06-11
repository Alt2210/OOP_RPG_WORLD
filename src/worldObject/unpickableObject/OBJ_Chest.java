package worldObject.unpickableObject;

import character.role.Player;
import item.Inventory;
import main.GamePanel; // Quan trọng: import GamePanel
import worldObject.WorldObject;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Chest extends WorldObject {

    // Rương giờ đây có một kho đồ riêng
    private Inventory inventory;
    private final int CHEST_CAPACITY = 10; // Sức chứa 10 ô

    public OBJ_Chest(GamePanel gp) { // Thêm GamePanel vào constructor
        name = "Chest";
        inventory = new Inventory(CHEST_CAPACITY); // Khởi tạo kho đồ cho rương
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/chest.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;
    }

    // Getter để lấy kho đồ của rương
    public Inventory getInventory() {
        return inventory;
    }

    // Chúng ta sẽ không dùng phương thức này nữa,
    // vì việc tương tác sẽ do Player chủ động bằng phím 'F'.
    @Override
    public void interactPlayer(Player player, int i, GamePanel gp) {
        // Có thể thêm một hành động mặc định ở đây nếu muốn,
        // ví dụ: phát ra âm thanh "cạch" khi va chạm.
        // Hiện tại để trống.
    }
}