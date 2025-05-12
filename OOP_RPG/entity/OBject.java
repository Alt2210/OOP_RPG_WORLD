package entity;

import main.GamePanel;
import java.awt.*;

public abstract class OBject extends GameObject { // Renamed to avoid conflict

    // Common Object properties (from original Entity or new)
    public String description = "";
    public int useCost; // For consumables or usable objects
    public int value; // Coin value if sellable
    public int price; // Coin price if buyable
    public int knockBackPower; // If it's a weapon or has knockback effect
    public boolean stackable = false;
    public int amount = 1;
    public int lightRadius; // If it's a light source
    public boolean invincible = false;
    public int invincibleCounter = 0;
    public Rectangle attackArea = new Rectangle(0,0,0,0); // For weapons
    public int attackValue; // For weapons
    public int defenseValue; // For shields
    public int motion1_duration; // For weapon animation
    public int motion2_duration; // For weapon animation
    public boolean temp = false;
    public OBject loot;
    public boolean opened = false; // For doors, chests    public GameObject loot; // Object this item might drop or contain
    public int coin;
    public OBject(GamePanel gp) {
        super(gp);
    }

    // Methods specific to objects
    public void setLoot(OBject lootItem) {
        this.loot = lootItem;
    }
    public void checkDrop() {
        // Logic to drop loot if applicable
        if (loot != null) {
            // Example: gp.obj[gp.currentMap][findEmptySlot()] = loot;
            // loot.worldX = this.worldX; loot.worldY = this.worldY;
            // this.loot = null; // Clear loot after dropping
        }
    }

    public void dropItem(OBject droppedItem) {
        if (droppedItem == null) return;
        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) { // Assuming gp.obj is GameObject[][]
            if (gp.obj[gp.currentMap][i] == null) {
                gp.obj[gp.currentMap][i] = droppedItem;
                gp.obj[gp.currentMap][i].worldX = worldX;
                gp.obj[gp.currentMap][i].worldY = worldY;
                break;
            }
        }
    }

    // Default update for static objects. Dynamic objects (Projectile, Particle) will override.
    @Override
    public void update() {
        // Most static objects won't have complex update logic unless interactive
    }

    // Default draw. Specific objects will have their own images.
    @Override
    public void draw(Graphics2D g2) {
        if (!drawing || !inCamera() || image == null) return;
        int screenX = getScreenX();
        int screenY = getScreenY();
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        // Debug
        // g2.setColor(Color.blue);
        // g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
    }
}