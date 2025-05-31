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

    private boolean canInteractWithCurrentNPC = true; // Cho phép tương tác với NPC hiện tại đang va chạm
    private Character currentlyCollidingNPC = null;   // NPC hiện tại đang va chạm
    private Character lastInteractedNPC = null;       // NPC cuối cùng đã hoàn thành hội thoại

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

        worldX = gp.getTileSize() * 30; // Ví dụ: Bắt đầu ở cột 30
        worldY = gp.getTileSize() * 30; // Ví dụ: Bắt đầu ở hàng 30
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
       if (keyH.attackPressed && canAttack()) { // Kiểm tra thêm canAttack() nếu Player có cooldown tấn công
            attackAnimationCounter = 30; // 0.5 giây tại 60 FPS cho hoạt ảnh
       }
        if (attackAnimationCounter > 0) {
            attackAnimationCounter--;
        }

        boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;

        if (isMoving) {
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }
        }

        collisionOn = false; // Reset cờ va chạm mỗi frame

        gp.getcChecker().checkTile(this);

        int itemIndex = gp.getcChecker().checkItem(this, true);
        pickUpItem(itemIndex);

        int npcIndex = gp.getcChecker().checkEntity(this, gp.getNpc());

        if (npcIndex != 999) { // Player đang va chạm với một NPC
            Character newlyCollidedNPC = gp.getNpc()[npcIndex];
            this.collisionOn = true; // Player bị chặn bởi NPC này

            if (this.currentlyCollidingNPC != newlyCollidedNPC) {
               this.currentlyCollidingNPC = newlyCollidedNPC;
                this.canInteractWithCurrentNPC = true;
            }

          if (gp.gameState == gp.playState && this.canInteractWithCurrentNPC && this.currentlyCollidingNPC != null) {
                interactWithNPC(npcIndex); // Phương thức này sẽ đặt canInteractWithCurrentNPC = false
            }
        } else { // Player KHÔNG va chạm với bất kỳ NPC nào
            if (this.currentlyCollidingNPC != null) {
               this.canInteractWithCurrentNPC = true;
                this.currentlyCollidingNPC = null;
            }
        }

        if (isMoving && !collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }

        if (isMoving || isAttacking()) { // isAttacking() là phương thức bạn đã có (return attackAnimationCounter > 0)
            cip.update();
        } else {
            cip.setSpriteNum(0); // Đứng yên, không di chuyển, không tấn công -> frame đầu tiên của idle (nếu có)
        }
    }


    @Override
    public void draw(Graphics2D g2) {

       BufferedImage image = cip.getCurFrame();

        int width = image.getWidth()*gp.getScale();
        int height = gp.getTileSize();
        g2.drawImage(image, screenX, screenY, width, height, null);

        drawHealthBar(g2, screenX, screenY); // Truyền screenX và screenY vào
        drawHealthBar(g2, screenX, screenY); // Truyền screenX và screenY vào
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
        if (npcIndex != 999) {
            if (gp.gameState == gp.playState) {
                Character npcCharacter = gp.getNpc()[npcIndex];

                if (npcCharacter instanceof DialogueSpeaker) {
                    ((DialogueSpeaker) npcCharacter).initiateDialogue(gp); // Điều này sẽ chuyển gameState sang dialogueState

                    if (!(npcCharacter instanceof NPC_Princess && gp.gameState == gp.victoryEndState) ) {
                        this.canInteractWithCurrentNPC = false;
                    }
                } else if (npcCharacter instanceof NPC_Princess) {
                    gp.gameState = gp.victoryEndState;
                }
            }
        }
    }

    public int getMaxHealth() {
        return this.maxHealth; // maxHealth là protected trong Character, Player có thể truy cập
    }
    public void setMaxHealth(int maxHealth){ this.maxHealth = maxHealth;}
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }
        if (this.currentHealth > this.maxHealth) {
            this.currentHealth = this.maxHealth;
        }
    }


    public int getHasKey() { return hasKey; }
    public void setHasKey(int count) {
        this.hasKey = count;
        if (this.hasKey < 0) {
            this.hasKey = 0; // Đảm bảo số chìa khóa không âm
        }
    }

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