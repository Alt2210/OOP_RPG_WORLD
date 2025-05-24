package character;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class NPC_OldMan extends Character {
    public NPC_OldMan(GamePanel gp) {
        super(gp);
        cip.setNumSprite(2);
        direction = "down";
        speed = 1;
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 21; // Lấy từ AssetSetter
        worldY = gp.getTileSize() * 21; // Lấy từ AssetSetter
        speed = 1;
        direction = "down";

        solidArea.x = 8;      // Điều chỉnh cho phù hợp với sprite của NPC
        solidArea.y = 16;     // Điều chỉnh cho phù hợp với sprite của NPC
        solidArea.width = 32; // Ví dụ: 32x32 pixels
        solidArea.height = 32;
        cip.getImage("/npc","oldman_"); // Tải ảnh ở đây
    }

    public void setAction(){
        actionLockCounter++;
        if(actionLockCounter == 120){
            Random random = new Random();
            int i = random.nextInt(100) + 1;
            if(i <= 25){
                direction = "up";
            }
            if(i>25 && i<= 50){
                direction = "down";
            }
            if(i> 50 && i<= 75){
                direction = "left";
            }
            if(i> 75 && i<= 100) direction = "right";
            actionLockCounter =0;
        }


    }
    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = cip.getCurFrame(); // Lấy frame hiện tại từ lớp Character
        if (image != null) {
            // Tính toán vị trí vẽ trên màn hình tương tự như cách vẽ Tile hoặc SuperItem
            int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
            int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;

            // Chỉ vẽ NPC nếu nó nằm trong tầm nhìn của camera
            if (worldX + gp.getTileSize() > gp.getPlayer().worldX - gp.getPlayer().screenX &&
                    worldX - gp.getTileSize() < gp.getPlayer().worldX + gp.getPlayer().screenX &&
                    worldY + gp.getTileSize() > gp.getPlayer().worldY - gp.getPlayer().screenY &&
                    worldY - gp.getTileSize() < gp.getPlayer().worldY + gp.getPlayer().screenY) {
                g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
            }
        }
    }
}

