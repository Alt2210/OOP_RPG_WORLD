package entity;
import main.GamePanel;
// import object.*; // Import specific OBject items
import java.awt.Rectangle;

public class NPC_Merchant extends Character { // Kế thừa từ Character

    public NPC_Merchant(GamePanel gp) {
        super(gp);
        this.name = "Merchant";
        this.type = type_npc;
        direction = "down";
        speed = 1;

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
        setDialogue();
        setItems();
    }

    @Override
    public void getImage() {
        // Assumes merchant uses same sprite for all directions
        down1 = setup("/npc/merchant_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/npc/merchant_down_2", gp.tileSize, gp.tileSize);
        up1 = down1; up2 = down2; left1 = down1; left2 = down2; right1 = down1; right2 = down2;
    }
    @Override
    public void getAttackImage(){ /* No attack images */ }


    @Override
    public void setDialogue() {
        dialogues[0][0] = "He he ha, so you found me.\nI have some good stuff. \nDo you want to trade?";
        // ...
    }

    public void setItems() { // Adds Game_Objects to inventory
        // inventory.add(new OBJ_Potion_Red(gp)); // OBJ_Potion_Red must extend OBject
        // inventory.add(new OBJ_Axe(gp));
        // ...
    }

    @Override
    public void speak() {
        facePlayer();
        gp.gameState = gp.tradeState;
        gp.ui.npc = this; // UI still refers to this NPC
    }
}