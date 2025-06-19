package character.sideCharacter;

import dialogue.Dialogue;
import dialogue.DialogueSpeaker;
import main.GamePanel;

public class NPC_Princess extends SideCharacter implements DialogueSpeaker {
    private Dialogue victoryDialogue;

    public NPC_Princess(GamePanel gp) {
        super(gp);

        this.direction = "down";
        this.speed = 1;

        setDefaultValues();
        initializeDialogues();
    }

    @Override
    public void initializeDialogues() {
        victoryDialogue = new Dialogue();
        victoryDialogue.addLine("Princess", "Ôi, dũng sĩ! Cuối cùng ngài cũng đã đến!");
        victoryDialogue.addLine("Princess", "Ngài đã giải cứu vương quốc chúng ta...");
        victoryDialogue.addLine("Princess", "Xin cảm ơn ngài. Giờ đây, hòa bình sẽ trở lại.");
    }

    @Override
    public void setDefaultValues() {
        worldX = this.gp.getTileSize() * 23;
        worldY = this.gp.getTileSize() * 20;

        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        if (this.cip != null) {
            this.cip.setNumSprite(7);
            this.cip.getImage("/npc", "princess");
        }
        this.setName("Peach");
    }

    @Override
    public void initiateDialogue(GamePanel gpReference) {
        if (victoryDialogue != null) {
            gpReference.getDialogueManager().startDialogue(this, victoryDialogue);
        } else {
            System.err.println("NPC_Princess: victoryDialogue is null. Cannot initiate dialogue.");
            gpReference.gameState = gpReference.playState;
            if (gpReference.getInteractingNPC() == this) {
                gpReference.setInteractingNPC(null);
            }
        }
    }
}