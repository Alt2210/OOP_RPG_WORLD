package character;

import imageProcessor.CharacterImageProcessor;
import main.GamePanel;

import java.awt.*;

public abstract class Character {

    protected GamePanel gp;
    protected CharacterImageProcessor cip;

    protected int worldX, worldY;
    protected int speed;
    protected String direction;
    protected Rectangle solidArea;
    protected int solidAreaDefaultX, solidAreaDefaultY;
    protected boolean collisionOn = false;
    protected int actionLockCounter = 0;

    // STATE
    protected boolean onPath = false;
    protected int defaultSpeed;
    protected String name;

    public Character(GamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle();
        cip = new CharacterImageProcessor(gp, this);
    }

    public abstract void draw(Graphics2D g2);

    /**
     * Cập nhật trạng thái của nhân vật, chủ yếu là di chuyển.
     * Các lớp con sẽ gọi phương thức này sau khi đã xử lý logic riêng (input, AI) và kiểm tra va chạm.
     */
    public void update() {
        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }
        // Luôn cập nhật animation
        if (cip != null) {
            cip.update();
        }
    }

    /**
     * Trạng thái tấn công mặc định cho các nhân vật không chiến đấu.
     * @return luôn trả về false.
     */
    public boolean isAttacking() {
        return false;
    }

    // --- Các getters và setters cho thuộc tính phi chiến đấu ---
    // (Giữ nguyên các getters/setters cho worldX, worldY, speed, name, direction, solidArea, v.v.)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public boolean isCollisionOn() { return collisionOn; }
    public void setCollisionOn(boolean collisionOn) { this.collisionOn = collisionOn; }
    public boolean isOnPath() { return onPath; }
    public void setOnPath(boolean onPath) { this.onPath = onPath; }
    public int getActionLockCounter() { return actionLockCounter; }
    public void setActionLockCounter(int actionLockCounter) { this.actionLockCounter = actionLockCounter; }
    public Rectangle getSolidArea() { return solidArea; }
    public int getSolidAreaDefaultX() { return solidAreaDefaultX; }
    public int getSolidAreaDefaultY() { return solidAreaDefaultY; }
    public int getWorldX() { return worldX; }
    public void setWorldX(int worldX) { this.worldX = worldX; }
    public int getWorldY() { return worldY; }
    public void setWorldY(int worldY) { this.worldY = worldY; }
    public int getSpeed() { return speed; }
    public GamePanel getGp() { return gp; }

    public Rectangle getHitbox() {
        return new Rectangle(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
    }

    public void setDefaultValues() {}
    public int getCenterX() { return worldX + solidArea.width / 2; }
    public int getCenterY() { return worldY + solidArea.height / 2; }
    public int getXDistance(Character target) { return Math.abs(getCenterX() - target.getCenterX()); }
    public int getYDistance(Character target) { return Math.abs(getCenterY() - target.getCenterY()); }
    public int getTileDistance(Character target) { return (getXDistance(target) + getYDistance(target)) / gp.getTileSize(); }
    public int getGoalCol(Character target) { return (target.worldX + target.solidArea.x) / gp.getTileSize(); }
    public int getGoalRow(Character target) { return (target.worldY + target.solidArea.y) / gp.getTileSize(); }
}