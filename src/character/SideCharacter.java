package character;

import dialogue.DialogueSpeaker;
import main.GamePanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Lớp trừu tượng đại diện cho các nhân vật phụ (NPC) trong game.
 * Kế thừa từ Character và chứa các logic chung như di chuyển và vẽ.
 */
public abstract class SideCharacter extends Character implements DialogueSpeaker {

    public SideCharacter(GamePanel gp) {
        super(gp);
    }

    /**
     * Phương thức trừu tượng để khởi tạo các đoạn hội thoại riêng cho mỗi NPC.
     * Các lớp con bắt buộc phải triển khai phương thức này.
     */
    public abstract void initializeDialogues();

    /**
     * Thiết lập hành động ngẫu nhiên cho NPC, thay đổi hướng sau một khoảng thời gian.
     */
    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter >= 120) { // Thay đổi hướng sau mỗi 2 giây (120 frames)
            Random random = new Random();
            int i = random.nextInt(100) + 1; // Chọn một số từ 1 đến 100

            if (i <= 25) {
                direction = "up";
            } else if (i <= 50) {
                direction = "down";
            } else if (i <= 75) {
                direction = "left";
            } else {
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }

    @Override
    public void update() {
        // Gọi setAction() để NPC quyết định hướng di chuyển tiếp theo
        setAction();

        // Kiểm tra va chạm trước khi di chuyển (logic này đã có trong super.update())
        super.update();
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        if (cip != null) {
            image = cip.getCurFrame();
        }

        if (image != null) {
            int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
            int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

            // Chỉ vẽ nếu NPC nằm trong khung hình của người chơi
            if (worldX + gp.getTileSize() > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() &&
                    worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
                    worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
                    worldY - gp.getTileSize() < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) {
                g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
            }
        }
    }
}