package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class NPC_OldMan extends Character {
    public NPC_OldMan(GamePanel gp) {
        super(gp);
        direction = "down";
        speed = 1;
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.tileSize * 21; // Lấy từ AssetSetter
        worldY = gp.tileSize * 21; // Lấy từ AssetSetter
        speed = 1;
        direction = "down";

        solidArea.x = 8;      // Điều chỉnh cho phù hợp với sprite của NPC
        solidArea.y = 16;     // Điều chỉnh cho phù hợp với sprite của NPC
        solidArea.width = 32; // Ví dụ: 32x32 pixels
        solidArea.height = 32;
        getImages(); // Tải ảnh ở đây
    }
    protected void getImages() {
        // Sử dụng phương thức helper setup() từ lớp cha (Character) để tải từng tệp ảnh.
        // Đường dẫn ảnh được giả định là đúng như cấu trúc thư mục resource của bạn (/player/...).
        // Lưu ý: Dựa theo đường dẫn ảnh trong code gốc, ảnh đi lên/phải dùng chung và xuống/trái dùng chung.
        // Nếu muốn hoạt ảnh khác nhau cho 4 hướng, bạn cần các tệp ảnh riêng biệt và cập nhật đường dẫn ở đây.

        up1 = setup("/npc/oldman_up_1.png");
        up2 = setup("/npc/oldman_up_2.png");
        up3 = up1; // Lặp lại frame 1
        up4 = up2; // Lặp lại frame 2
        up5 = up1; // Lặp lại frame 1

        down1 = setup("/npc/oldman_down_1.png");
        down2 = setup("/npc/oldman_down_2.png");
        down3 = down1;
        down4 = down2;
        down5 = down1;

        left1 = setup("/npc/oldman_left_1.png");
        left2 = setup("/npc/oldman_left_2.png");
        left3 = left1;
        left4 = left2;
        left5 = left1;

        right1 = setup("/npc/oldman_right_1.png");
        right2 = setup("/npc/oldman_right_2.png");
        right3 = right1;
        right4 = right2;
        right5 = right1;
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
        BufferedImage image = getCurrentFrame(); // Lấy frame hiện tại từ lớp Character
        if (image != null) {
            // Tính toán vị trí vẽ trên màn hình tương tự như cách vẽ Tile hoặc SuperItem
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            // Chỉ vẽ NPC nếu nó nằm trong tầm nhìn của camera
            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                    worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                    worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                    worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}

