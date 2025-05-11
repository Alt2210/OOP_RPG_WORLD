package entity;

import main.GamePanel;
// import object.OBJ_Door_Iron; // OBJ_Door_Iron must extend OBject
// import tile_interactive.IT_MetalPlate; // IT_MetalPlate might be a OBject
// import tile_interactive.InteractiveTile; // Base class for IT_MetalPlate

import java.awt.*;
import java.util.ArrayList;

public class NPC_BigRock extends OBject { // Kế thừa từ OBject

    public static final String npcName = "Big Rock"; // Static for easy reference
    public int speed = 4; // BigRock specific property
    public String direction = "down"; // BigRock specific property
    // public String[][] dialogues = new String[1][1]; // If BigRock can "speak"
    public GameObject linkedEntity; // The plate it's on (IT_MetalPlate which is a GameObject)

    public NPC_BigRock(GamePanel gp) {
        super(gp);
        this.name = npcName;
        this.type = type_obstacle; // Or a custom type for movable puzzle elements

        // Original had direction and speed, implies it moves like a creature.
        // If it's purely an object, these might not be needed or handled differently.
        // For this example, we keep them as it seems to have move() logic.


        solidArea = new Rectangle(2, 6, 44, 40);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // dialogueSet = -1; // If using dialogue
        getImage();
        setDialogue();
    }

    public void getImage() { // If BigRock has sprites (it does)
        // Assuming one image for BigRock
        image = setup("/npc/bigrock", gp.tileSize, gp.tileSize);
    }

    public void setDialogue() { // If it can be "inspected"
        // dialogues[0][0] = "It's a giant rock.";
        // gp.ui.currentDialogue = "It's a giant rock."; // Simpler for single message
    }

    @Override
    public void interact() { // Called when player interacts
        // gp.ui.currentDialogue = "It's a giant rock.";
        // Or if it has more complex dialogue:
        // facePlayer(); // Method might need to be in GameObject or a utility
        // startDialogue(this, dialogueSet);
    }


    // BigRock specific methods
    public void move(String d) { // Needs its own direction and speed
        this.direction = d;
        this.collisionOn = false; // Reset before check
        gp.cChecker.checkTile(this);
        // gp.cChecker.checkObject(this, false); // Check collision with other objects
        // gp.cChecker.checkCreature(this, gp.player); // Check collision with player if needed


        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }
        detectPlate();
    }

    public void detectPlate() {
        // ArrayList<InteractiveTile> plateList = new ArrayList<>(); // InteractiveTile should be OBject
        ArrayList<OBject> plateList = new ArrayList<>();
        ArrayList<NPC_BigRock> rockList = new ArrayList<>(); // Specifically list of BigRocks

        // Create a plate list
        for (int i = 0; i < gp.iTile[gp.currentMap].length; i++) { // Assuming iTile is OBject[]
            OBject tile = gp.iTile[gp.currentMap][i];
            // if (tile != null && tile.name != null && tile.name.equals(IT_MetalPlate.itName)) {
            //    plateList.add(tile);
            // }
        }

        // Create a rock list (scan through gp.obj or a dedicated list for movable objects)
        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] instanceof NPC_BigRock) {
                rockList.add((NPC_BigRock) gp.obj[gp.currentMap][i]);
            }
        }


        int count = 0;
        for (OBject plate : plateList) {
            int xDistance = Math.abs(worldX - plate.worldX);
            int yDistance = Math.abs(worldY - plate.worldY);
            int distance = Math.max(xDistance, yDistance);

            if (distance < 8) { // If this rock is on a plate
                if (this.linkedEntity == null) {
                    this.linkedEntity = plate; // Link this rock to the plate
                    gp.playSE(3);
                }
            } else {
                if (this.linkedEntity == plate) { // If this rock was on this plate but moved off
                    this.linkedEntity = null;
                }
            }
        }

        // Scan all rocks to see how many are on plates
        for (NPC_BigRock rock : rockList) {
            if (rock.linkedEntity != null) { // If a rock is linked to a plate
                count++;
            }
        }

        // If all rocks are on plates
        if (!rockList.isEmpty() && count == rockList.size()) {
            for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
                OBject door = gp.obj[gp.currentMap][i];
                // if (door != null && door.name.equals(OBJ_Door_Iron.objName)) {
                //    gp.obj[gp.currentMap][i] = null; // Open the door
                //    gp.playSE(21);
                // }
            }
        }
    }

    @Override
    public void update() {
        // BigRock might not update every frame unless it has continuous logic
        // Movement is triggered by player interaction (Player calls BigRock.move())
    }

    // Draw method is inherited from OBject, uses 'this.image'
}