package character.sideCharacter;

import main.GamePanel;
import dialogue.*;

public class NPC_OldMan extends SideCharacter {
    private Dialogue defaultDialogue;

    public NPC_OldMan(GamePanel gp) {
        super(gp);

        // Đặt các thuộc tính ban đầu
        this.direction = "down";
        this.speed = 1;

        // Tải các giá trị và hội thoại mặc định
        setDefaultValues();
        initializeDialogues();
    }

    @Override
    public void initializeDialogues() {
        defaultDialogue = new Dialogue();
        defaultDialogue.addLine("NPC OldMan", "Chào cậu, nhà thám hiểm trẻ tuổi!");
        defaultDialogue.addLine("NPC OldMan", "Thế giới này đầy rẫy nguy hiểm.\nHãy cẩn thận ngoài kia.");
        defaultDialogue.addLine("NPC OldMan", "Hãy tìm công chúa và mang lại hòa bình.");
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 21;
        worldY = gp.getTileSize() * 21;

        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        if (this.cip != null) {
            this.cip.setNumSprite(2);
            this.cip.getImage("/npc","oldman");
        }
        this.name = "Old Man";
    }

    @Override
    public void initiateDialogue(GamePanel gpReference) {
        if (defaultDialogue != null) {
            gpReference.getDialogueManager().startDialogue(this, defaultDialogue);
        } else {
            System.err.println("NPC_OldMan: defaultDialogue is null. Cannot initiate dialogue.");
            gpReference.gameState = gpReference.playState;
            if (gpReference.getInteractingNPC() == this) {
                gpReference.setInteractingNPC(null);
            }
        }
    }
}