// NPC_OldMan.java
package entity;
import main.GamePanel;
import java.util.Random;
import java.awt.Rectangle; // Import if needed

public class NPC_OldMan extends Character { // Kế thừa từ Character

    public NPC_OldMan(GamePanel gp) {
        super(gp);
        this.name = "Old Man"; // Example
        this.type = type_npc;
        direction = "down";
        speed = 1;
        // solidArea, etc. from Character/GameObject
        solidArea = new Rectangle(8, 16, 32, 32); // Example
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // dialogueSet = -1; // Already in Character
        getImage();
        setDialogue();
    }

    @Override
    public void getImage() {
        up1 = setup("/npc/oldman_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/npc/oldman_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/npc/oldman_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/npc/oldman_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/npc/oldman_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/npc/oldman_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/npc/oldman_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/npc/oldman_right_2", gp.tileSize, gp.tileSize);
    }
    @Override
    public void getAttackImage(){ /* NPCs might not have attack images */ }


    @Override
    public void setDialogue() {
        dialogues[0][0] = "Hello, lad.";
        // ... other dialogues
    }

    @Override
    public void setAction() { // Override Character's setAction
        if (onPath) {
            int goalCol = (gp.player.worldX + gp.player.solidArea.x) / gp.tileSize;
            int goalRow = (gp.player.worldY + gp.player.solidArea.y) / gp.tileSize;
            searchPath(goalCol, goalRow);
        } else {
            actionLockCounter++;
            if (actionLockCounter >= 120) { // Use >=
                Random random = new Random();
                int i = random.nextInt(100) + 1;
                if (i <= 25) direction = "up";
                else if (i <= 50) direction = "down";
                else if (i <= 75) direction = "left";
                else direction = "right";
                actionLockCounter = 0;
            }
        }
    }

    @Override
    public void speak() { // Override Character's speak
        facePlayer();
        startDialogue(this, dialogueSet); // Use Character's startDialogue

        // Logic for advancing dialogue sets specific to OldMan
        // This was original logic, ensure it makes sense with Character's dialogueSet handling
        // dialogueSet++;
        // if(dialogues[dialogueSet][0] == null) {
        //    dialogueSet--;
        // }
        // The speak in Character handles advancing index, this can be for advancing sets
    }
}