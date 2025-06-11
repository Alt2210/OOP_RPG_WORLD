package character;

import main.GamePanel;
import dialogue.*; // Import cả package dialogue
// import dialogue.Dialogue; // Không cần thiết nếu đã import dialogue.*
// import dialogue.DialogueSpeaker; // Không cần thiết nếu đã import dialogue.*
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class NPC_OldMan extends Character implements DialogueSpeaker {
    private Dialogue defaultDialogue;
    // Bỏ: private Dialogue currentActiveDialogue; // DialogueManager sẽ quản lý cái này

    public NPC_OldMan(GamePanel gp) {
        super(gp);
        // Đảm bảo cip được khởi tạo trong super(gp) trước khi gọi setNumSprite
        if (this.cip != null) { // Sử dụng this.cip
            this.cip.setNumSprite(2); //
        } else {
            System.err.println("NPC_OldMan Constructor: CharacterImageProcessor (cip) is null!");
        }
        this.direction = "down"; // Sử dụng this. cho các trường của instance
        this.speed = 1; //
        setDefaultValues(); //
        initializeDialogues();
    }

    private void initializeDialogues() {
        defaultDialogue = new Dialogue();
        defaultDialogue.addLine("NPC OldMan", "Chào cậu, nhà thám hiểm trẻ tuổi!"); //
        defaultDialogue.addLine("NPC OldMan", "Thế giới này đầy rẫy nguy hiểm.\nHãy cẩn thận ngoài kia."); //
        defaultDialogue.addLine("NPC OldMan", "Hãy tìm công chúa và mang lại hòa bình."); //
        // Bỏ: currentActiveDialogue = defaultDialogue;
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 21; //
        worldY = gp.getTileSize() * 21; //
        speed = 1; //
        direction = "down"; //


        solidArea.x = 8;      // Điều chỉnh cho phù hợp với sprite của NPC
        solidArea.y = 16;     // Điều chỉnh cho phù hợp với sprite của NPC
        solidArea.width = 32; // Ví dụ: 32x32 pixels
        solidArea.height = 32;
        cip.getImage("/npc","oldman"); // Tải ảnh ở đây
        name = "Old Man";
    }

    // Nếu Character không có setAction abstract, thì bỏ @Override
    public void setAction(){ //
        actionLockCounter++; //
        if(actionLockCounter >= 120){ //
            Random random = new Random(); //
            int i = random.nextInt(100) + 1; //
            if(i <= 25){ //
                direction = "up"; //
            }
            if(i>25 && i<= 50){ //
                direction = "down"; //
            }
            if(i> 50 && i<= 75){ //
                direction = "left"; //
            }
            if(i> 75 && i<= 100) direction = "right"; //
            actionLockCounter =0; //
        }
    }

    // --- Triển khai phương thức của DialogueSpeaker (interface mới chỉ có initiateDialogue) ---
    @Override
    public void initiateDialogue(GamePanel gpReference) { // gpReference ở đây chính là this.gp
        if (defaultDialogue != null) {
            gpReference.getDialogueManager().startDialogue(this, defaultDialogue);
        } else {
            System.err.println("NPC_OldMan: defaultDialogue is null. Cannot initiate dialogue.");
            // Nếu không có dialogue, có thể kết thúc tương tác ngay
            gpReference.gameState = gpReference.playState;
            if (gpReference.getInteractingNPC() == this) { // Chỉ reset nếu NPC này đang tương tác
                gpReference.setInteractingNPC(null);
            }
        }
    }

    @Override
    public void update() {
        // Gọi setAction() để NPC quyết định hướng di chuyển tiếp theo
        setAction();
        super.update();
    }
    @Override
    public void draw(Graphics2D g2) { //
        BufferedImage image = null;
        if (cip != null) {
            image = cip.getCurFrame(); //
        }

        if (image != null) {
            int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().getScreenX(); //
            int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().getScreenY(); //

            if (worldX + gp.getTileSize() > gp.getPlayer().worldX - gp.getPlayer().getScreenX() && //
                    worldX - gp.getTileSize() < gp.getPlayer().worldX + gp.getPlayer().getScreenX() && //
                    worldY + gp.getTileSize() > gp.getPlayer().worldY - gp.getPlayer().getScreenY() && //
                    worldY - gp.getTileSize() < gp.getPlayer().worldY + gp.getPlayer().getScreenY()) { //
                g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null); //
            }
        }
    }
}