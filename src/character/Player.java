package character;

import dialogue.DialogueSpeaker;
import main.GamePanel;
import main.KeyHandler;
import sound.Sound;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Character {

    public KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    private int hasKey;

    // THÊM MỚI: Bộ đếm thời gian tấn công
    private int attackAnimationCounter = 0;

    public Player(GamePanel gp, KeyHandler keyH) {

        super(gp);
        cip.setNumSprite(5);

        // Lưu tham chiếu đến KeyHandler.
        this.keyH = keyH;


        screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);
        screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);

        solidArea.x = 8; // Offset X từ góc trên bên trái sprite của Player
        solidArea.y = 16; // Offset Y từ góc trên bên trái sprite của Player
        solidArea.width = 32; // Chiều rộng vùng va chạm của Player
        solidArea.height = 32; // Chiều cao vùng va chạm của Player

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues(); // Thiết lập worldX, worldY, speed, direction ban đầu.
        cip.getImage("/player", "sodier");      // Tải hình ảnh hoạt ảnh của Player.
    }




    public void setAction(){}

    @Override // Sử dụng annotation @Override là một cách tốt để kiểm tra lỗi nếu phương thức ở lớp cha bị đổi tên hoặc xóa.
    public void setDefaultValues() {

        worldX = gp.getTileSize() * 10; // Ví dụ: Bắt đầu ở cột 30
        worldY = gp.getTileSize() * 20; // Ví dụ: Bắt đầu ở hàng 30
        speed = 4; // Tốc độ di chuyển của Player
        direction = "down"; // Hướng ban đầu của Player khi game bắt đầu
        hasKey=0; // ms vao k co key

        maxHealth = 100;
        currentHealth = maxHealth;
        attack = 10; // Giá trị tấn công của người chơi
        defense = 2; // Giá trị phòng thủ của người chơi
        name = "Đạt đẹp trai";
    }

    @Override
    public void update() {
        // THÊM MỚI: Kiểm soát thời gian tấn công
        if (keyH.attackPressed) {
            attackAnimationCounter = 30; // 0.5 giây tại 60 FPS
        }
        if (attackAnimationCounter > 0) {
            attackAnimationCounter--;
        }

        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            // 1. Xác định hướng di chuyển dựa trên input
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            collisionOn = false;

            gp.getcChecker().checkTile(this); // Phương thức này sẽ đặt this.collisionOn = true nếu có va chạm tile

            int itemIndex = gp.getcChecker().checkItem(this, true);
            pickUpItem(itemIndex);

            // 3c. Kiểm tra va chạm với NPCs
            int npcIndex = gp.getcChecker().checkEntity(this, gp.getNpc());
            if (npcIndex != 999) {
                // Nếu va chạm NPC, đặt collisionOn của Player là true để ngăn di chuyển
                // và sau đó xử lý tương tác.
                this.collisionOn = true;
                interactWithNPC(npcIndex);
            }

            if (!collisionOn) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }



            cip.update();

        } else {
            // THAY ĐỔI: Gọi cip.update() nếu đang trong trạng thái tấn công
            if (attackAnimationCounter > 0) {
                cip.update();
            } else {
                cip.setSpriteNum(0);
            }
        }
    }

    // Triển khai phương thức trừu tượng draw(Graphics2D g2) từ lớp Character.
    // Định nghĩa cách vẽ Player lên màn hình.
    @Override
    public void draw(Graphics2D g2) {

        // --- Logic Vẽ Player ---
        // Lấy hình ảnh (BufferedImage) của frame hoạt ảnh hiện tại.
        // Phương thức getCurrentFrame() từ lớp cha Character sẽ trả về ảnh phù hợp
        // dựa trên direction và spriteNum hiện tại (đã được cập nhật trong update()).
        BufferedImage image = cip.getCurFrame();

        // Vẽ hình ảnh lên màn hình.
        // Player được vẽ tại vị trí CỐ ĐỊNH trên màn hình (screenX, screenY).
        // Kích thước vẽ được scale bằng gp.getTileSize().
        // null là tham số ImageObserver, thường dùng null khi vẽ trực tiếp lên Graphics.
        int width = image.getWidth()*gp.getScale();
        int height = gp.getTileSize();
        g2.drawImage(image, screenX, screenY, width, height, null);

        drawHealthBar(g2, screenX, screenY); // Truyền screenX và screenY vào
        drawHealthBar(g2, screenX, screenY); // Truyền screenX và screenY vào
        // --- Tùy chọn: Vẽ vùng va chạm (solidArea) để debug ---
        // Điều này giúp bạn thấy rõ vùng va chạm của nhân vật trên màn hình.
        // Vị trí vẽ vùng va chạm: Tọa độ màn hình của Player + offset của solidArea.
         g2.setColor(Color.red); // Đặt màu vẽ là đỏ
         g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height); // Vẽ hình chữ nhật


    }
    // Sau khi chet
    @Override
    protected void onDeath(Character attacker) {
        gp.getUi().showMessage("Bạn đã bị đánh bại bởi " + attacker.getName() + "!");
        gp.gameState = gp.gameOverState;
    }
    public Rectangle getAttackArea() {
        int attackAreaWidth = gp.getTileSize(); // Chiều rộng vùng tấn công
        int attackAreaHeight = gp.getTileSize(); // Chiều cao vùng tấn công
        int attackX = worldX + solidArea.x;
        int attackY = worldY + solidArea.y;

        // Điều chỉnh vị trí vùng tấn công dựa trên hướng của Player
        // Đây là ví dụ đơn giản, bạn có thể làm chi tiết hơn
        switch (direction) {
            case "up":
                attackY -= attackAreaHeight; // Vùng tấn công ở trên Player
                attackX += (solidArea.width / 2) - (attackAreaWidth / 2); // Căn giữa theo chiều ngang
                break;
            case "down":
                attackY += solidArea.height; // Vùng tấn công ở dưới Player
                attackX += (solidArea.width / 2) - (attackAreaWidth / 2);
                break;
            case "left":
                attackX -= attackAreaWidth; // Vùng tấn công ở bên trái Player
                attackY += (solidArea.height / 2) - (attackAreaHeight / 2); // Căn giữa theo chiều dọc
                break;
            case "right":
                attackX += solidArea.width; // Vùng tấn công ở bên phải Player
                attackY += (solidArea.height / 2) - (attackAreaHeight / 2);
                break;
        }
        return new Rectangle(attackX, attackY, attackAreaWidth, attackAreaHeight);
    }
    public void interactWithNPC(int npcIndex) {
        if (npcIndex != 999) { // Kiểm tra xem có va chạm với NPC hợp lệ không
            Character npcCharacter = gp.getNpc()[npcIndex]; // Lấy đối tượng NPC từ mảng

            // Kiểm tra xem NPC này có khả năng nói chuyện không (có implement DialogueSpeaker không)
            if (npcCharacter instanceof NPC_Princess && !(npcCharacter instanceof DialogueSpeaker)) {
                // Xử lý Princess kết thúc game nếu Princess KHÔNG phải là DialogueSpeaker
                // (Nếu Princess cũng nói chuyện trước khi kết thúc, thì nó nên là DialogueSpeaker)
                gp.gameState = gp.victoryEndState;
            } else if (npcCharacter instanceof DialogueSpeaker) {
                // Không cần chuyển gameState ở đây nữa, DialogueManager.startDialogue sẽ làm
                ((DialogueSpeaker) npcCharacter).initiateDialogue(gp);
            }
        }
    }


    public int getHasKey() { return hasKey; }
    public void incrementKeyCount() { this.hasKey++; }
     public void decrementKeyCount() {
     if (this.hasKey > 0) {
         this.hasKey--;
     }
 }
    public void pickUpItem(int i) {
        // the object array's index
        if (i != 999) {
            gp.getwObjects()[i].interactPlayer(this, i,this.gp);
        }
    }

    // THÊM MỚI: Phương thức kiểm tra trạng thái tấn công
    public boolean isAttacking() {
        return attackAnimationCounter > 0;
    }

}