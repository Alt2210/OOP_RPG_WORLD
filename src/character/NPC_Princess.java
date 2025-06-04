package character;

// Đảm bảo import đúng các lớp từ package dialogue
import dialogue.Dialogue;
import dialogue.DialogueLine;
import dialogue.DialogueSpeaker;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class NPC_Princess extends Character implements DialogueSpeaker {
    private Dialogue victoryDialogue;
    // GamePanel gp đã có sẵn từ lớp cha Character (là protected)
    // nên không cần khai báo lại 'private GamePanel gp;' ở đây.

    public NPC_Princess(GamePanel gp) {
        super(gp); // Gọi constructor của lớp cha Character, this.gp sẽ được gán ở lớp cha.

        this.direction = "down";  // Hướng mặc định ban đầu (ví dụ: hướng xuống để chào đón Player)
        this.speed = 1;

        // Đặt numSprite sau khi cip (CharacterImageProcessor) đã chắc chắn được khởi tạo trong super(gp)
        if (this.cip != null) {
            this.cip.setNumSprite(7); // Princess có 7 frame cho hoạt ảnh left/right
        } else {
            System.err.println("NPC_Princess Constructor: CharacterImageProcessor (cip) is null!");
        }
        // Gọi setDefaultValues và initializeDialogues ở cuối constructor là một lựa chọn tốt
        setDefaultValues();
        initializeDialogues();
    }

    private void initializeDialogues() {
        victoryDialogue = new Dialogue();
        // Thêm các câu thoại của Princess ở đây
        victoryDialogue.addLine("Princess", "Ôi, dũng sĩ! Cuối cùng ngài cũng đã đến!"); // Sửa lại text cho phù hợp
        victoryDialogue.addLine("Princess", "Ngài đã giải cứu vương quốc chúng ta..."); //
        victoryDialogue.addLine("Player", "..."); //
        victoryDialogue.addLine("Princess", "Xin cảm ơn ngài. Giờ đây, hòa bình sẽ trở lại."); //
    }

    @Override
    public void setDefaultValues() {
        // Sử dụng this.gp (kế thừa từ Character)
        worldX = this.gp.getTileSize() * 23; // Vị trí ví dụ
        worldY = this.gp.getTileSize() * 20; // Điều chỉnh vị trí cho phù hợp
        // speed và direction đã được đặt trong constructor hoặc có thể đặt lại ở đây nếu muốn.
        this.speed = 1; // Đảm bảo Princess đứng yên nếu đó là ý muốn
        // this.direction = "down"; // Đã đặt trong constructor

        solidArea.x = 8; //
        solidArea.y = 16; //
        solidArea.width = 32; //
        solidArea.height = 32; //
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        if (this.cip != null) {

            this.cip.getImage("/npc", "Princess"); //
            }
    }

    public void setAction() {

        actionLockCounter++; //
        if (actionLockCounter >= 180) { // Ví dụ: thay đổi sau mỗi 3 giây
            Random random = new Random(); //
            int i = random.nextInt(100) + 1; // 1-100

            if (i <= 25) { //
                direction = "up";
            } else if (i <= 50) { //
                direction = "down";
            } else if (i <= 75) { //
                direction = "left";
            } else { //
                direction = "right";
            }
            actionLockCounter = 0; //

        }
    }
    @Override
    public void update() {
        // Gọi setAction() để NPC quyết định hướng di chuyển tiếp theo
        setAction();
        super.update();
    }
    // --- Triển khai phương thức của DialogueSpeaker ---
    @Override
    public void initiateDialogue(GamePanel gpReference) { // gpReference ở đây chính là this.gp
        if (victoryDialogue != null) {
            // Truyền 'this' (NPC_Princess hiện tại) và 'victoryDialogue' cho DialogueManager
            gpReference.getDialogueManager().startDialogue(this, victoryDialogue);
        } else {
            System.err.println("NPC_Princess: victoryDialogue is null. Cannot initiate dialogue.");
            gpReference.gameState = gpReference.playState; // Quay lại trạng thái chơi
            if (gpReference.getInteractingNPC() == this) {
                gpReference.setInteractingNPC(null);
            }
        }
    }

    // Các phương thức advanceDialogue, hasMoreDialogue, getCurrentDialogueLine
    // không còn trong interface DialogueSpeaker mới của bạn nữa.
    // Chúng ta sẽ xóa chúng khỏi NPC_Princess để tuân thủ interface.

    @Override
    public void draw(Graphics2D g2) {
        // Giữ nguyên logic vẽ của bạn, nó đã tốt.
        BufferedImage image = null;
        if (cip != null) {
            image = cip.getCurFrame(); //
        }

        if (image != null) {
            int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX; //
            int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY; //

            if (worldX + gp.getTileSize() > gp.getPlayer().worldX - gp.getPlayer().screenX && //
                    worldX - gp.getTileSize() < gp.getPlayer().worldX + gp.getPlayer().screenX && //
                    worldY + gp.getTileSize() > gp.getPlayer().worldY - gp.getPlayer().screenY && //
                    worldY - gp.getTileSize() < gp.getPlayer().worldY + gp.getPlayer().screenY) { //
                g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null); //
            }
        } else {
            // System.err.println("NPC_Princess (" + this.name + "): Image is null in draw method. Direction: " + this.direction);
        }
    }
}