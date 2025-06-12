package worldObject.unpickableObject;

import character.role.Player;
import item.Inventory;
import main.GamePanel; // Quan trọng: import GamePanel
import worldObject.WorldObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OBJ_Chest extends WorldObject {

    // Rương giờ đây có một kho đồ riêng
    private Inventory inventory;
    private final int CHEST_CAPACITY = 10; // Sức chứa 10 ô

    private BufferedImage imageOpened;
    private boolean isOpened = false;

    public OBJ_Chest(GamePanel gp) { // Thêm GamePanel vào constructor
        name = "Chest";
        inventory = new Inventory(CHEST_CAPACITY); // Khởi tạo kho đồ cho rương
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/chest.png"));
            imageOpened = ImageIO.read(getClass().getResourceAsStream("/objects/chest_opened.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;
    }

    // Getter để lấy kho đồ của rương
    public Inventory getInventory() {
        return inventory;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    @Override
    public void interactPlayer(Player player, int i, GamePanel gp) {
        // Logic tương tác để mở giao diện rương nằm trong KeyHandler,
        // phương thức này có thể để trống hoặc dùng cho mục đích khác.
    }
    // Ghi đè phương thức draw để chọn ảnh phù hợp
    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        BufferedImage imageToDraw = isOpened ? imageOpened : image;

        if (imageToDraw == null) return;

        int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
        int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

        if(worldX + gp.getTileSize() > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() &&
                worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
                worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
                worldY - gp.getTileSize() < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) {

            g2.drawImage(imageToDraw, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
        }
    }
}