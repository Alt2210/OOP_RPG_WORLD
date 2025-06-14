package worldObject.unpickableObject;

import character.role.Player;
import item.Inventory;
import item.*;
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

    public void transferItem(Player player, int slotIndex, int commandNum, GamePanel gp) {
        Inventory playerInv = player.getInventory();
        Inventory chestInv = this.inventory;

        if (commandNum == 0) { // Player Panel (chuyển từ Player sang Chest)
            ItemStack stackToMove = playerInv.getItemStack(slotIndex);
            if (stackToMove != null) {
                // Không cho phép chuyển vũ khí đang trang bị
                if (stackToMove.getItem() == player.getCurrentWeapon()) {
                    gp.getUi().showMessage("Không thể di chuyển vũ khí đang trang bị!");
                    return;
                }
                if (chestInv.addItem(stackToMove.getItem(), stackToMove.getQuantity())) {
                    playerInv.removeStack(slotIndex);
                    gp.getUi().showMessage("Đã di chuyển " + stackToMove.getItem().getName() + " vào rương.");
                    // Đặt isOpened = false nếu rương có thể đóng lại khi có vật phẩm
                    // (hoặc nếu bạn muốn nó tự động đóng nếu nó hoàn toàn trống rỗng sau khi chuyển)
                    // Hiện tại, logic isOpened chỉ được đặt khi rương trống.
                } else {
                    gp.getUi().showMessage("Rương đã đầy!");
                }
            }
        } else { // Chest Panel (chuyển từ Chest sang Player)
            ItemStack stackToMove = chestInv.getItemStack(slotIndex);
            if (stackToMove != null) {
                if (playerInv.addItem(stackToMove.getItem(), stackToMove.getQuantity())) {
                    chestInv.removeStack(slotIndex);
                    gp.getUi().showMessage("Đã lấy " + stackToMove.getItem().getName() + " từ rương.");
                    // Nếu rương trống rỗng sau khi lấy đồ, đánh dấu là đã mở
                    if (chestInv.getItemStack() == 0) {
                        this.setOpened(true); // Đánh dấu là đã mở (trống rỗng)
                    }
                } else {
                    gp.getUi().showMessage("Túi đồ của bạn đã đầy!");
                }
            }
        }
    }
}