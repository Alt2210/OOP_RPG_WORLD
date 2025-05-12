package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class GameObject {

    public GamePanel gp;
    public BufferedImage image, image2, image3; // General images, specific sprites in subclasses
    public String name;
    public boolean collision = false; // General collision property

    // STATE
    public int worldX, worldY;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public boolean drawing = true; // Whether the object should be drawn

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; // Sprite cho di chuyển
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2; // Sprite cho tấn công (Character sẽ dùng nhiều)
    public BufferedImage guardUp, guardDown, guardLeft, guardRight; // Sprite cho phòng thủ (Character sẽ dùng nhiều)

    // TYPE (Still can be useful for broad categorization if needed, or remove if too granular)
    public int type;
    public final int type_player = 0;
    public final int type_npc = 1;
    public final int type_monster = 2;
    public final int type_sword = 3;
    public final int type_axe = 4;
    public final int type_shield = 5;
    public final int type_consumable = 6;
    public final int type_pickupOnly = 7;
    public final int type_obstacle = 8;
    public final int type_light = 9;
    public final int type_pickaxe = 10;
    public final int type_projectile = 11; // Example new type
    public final int type_particle = 12;   // Example new type


    public GameObject(GamePanel gp) {
        this.gp = gp;
    }

    // Common methods from old Entity class
    public int getScreenX() {
        return worldX - gp.player.worldX + gp.player.screenX;
    }

    public int getScreenY() {
        return worldY - gp.player.worldY + gp.player.screenY;
    }

    public int getLeftX() {
        return worldX + solidArea.x;
    }

    public int getRightX() {
        // Original had solidArea.width + solidArea.width, assuming typo and meant solidArea.x + solidArea.width
        return worldX + solidArea.x + solidArea.width;
    }

    public int getTopY() {
        return worldY + solidArea.y;
    }

    public int getBottomY() {
        return worldY + solidArea.y + solidArea.height;
    }

    public int getCol() {
        return (worldX + solidArea.x) / gp.tileSize;
    }

    public int getRow() {
        return (worldY + solidArea.y) / gp.tileSize;
    }

    public boolean inCamera() {
        return worldX + gp.tileSize * 5 > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize * 5 > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY;
    }
    public void interact(Character character) {
        // Hành vi tương tác mặc định (có thể không làm gì)
        System.out.println("Interacted with " + this.name);
    }
    public BufferedImage setup(String imagePath, int width, int height) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage scaledImage = null;
        try {
            scaledImage = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            if (scaledImage != null) {
                scaledImage = uTool.scaleImage(scaledImage, width, height);
            } else {
                System.err.println("Could not load image: " + imagePath + ".png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledImage;
    }

    public void changeAlpha(Graphics2D g2, float alphaValue) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
    }

    // Abstract methods to be implemented by subclasses
    public abstract void update();
    public abstract void draw(Graphics2D g2);

    // Common interaction methods (can be overridden)
    public void interact() {
        // Default interaction behavior
    }

    public boolean use(Character user) {
        // Default use behavior for objects
        return false;
    }
    public void speak(){
        // Default speak behavior
    }

    // Methods that were in Entity, decide if they belong here or in specific subclasses
    // For example, particle generation might be tied to Creatures or specific Objects.
    // Let's keep a generic version for now or move it if it's too specific.
    public Color getParticleColor() { return null; }
    public int getParticleSize() { return 0; }
    public int getParticleSpeed() { return 0; }
    public int getParticleMaxLife() { return 0; }

    public void generateParticle(GameObject generator, GameObject target) {
        Color color = generator.getParticleColor();
        int size = generator.getParticleSize();
        int speed = generator.getParticleSpeed();
        int maxLife = generator.getParticleMaxLife();

        if (color == null || size == 0 || speed == 0 || maxLife == 0) return; // Basic check

        Particle p1 = new Particle(gp, target, color, size, speed, maxLife, -2, -1);
        Particle p2 = new Particle(gp, target, color, size, speed, maxLife, 2, -1);
        Particle p3 = new Particle(gp, target, color, size, speed, maxLife, -2, 1);
        Particle p4 = new Particle(gp, target, color, size, speed, maxLife, 2, 1);

        gp.particleList.add(p1);
        gp.particleList.add(p2);
        gp.particleList.add(p3);
        gp.particleList.add(p4);
    }
}