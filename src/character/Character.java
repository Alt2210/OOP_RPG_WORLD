package character;

import character.role.Player;
import imageProcessor.CharacterImageProcessor;
import main.GamePanel;
import skill.Skill;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    // CHARACTER ATTRIBUTES
    // Tính phần máu còn lại (giả sử maxHealth và currentHealth có sẵn)
    protected int maxHealth;  // ví dụ
    protected int currentHealth;  // ví dụ, bạn có thể làm thành thuộc tính nhân vật
    protected int defaultSpeed;
    protected int attack;
    protected int defense;
    protected int attackRange;
    protected int attackCooldown; // Số frame cho đến khi được tấn công tiếp
    protected int ATTACK_COOLDOWN_DURATION; // 0.5 giây tại 60 FPS

    protected String name;

    public Character(GamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle();
        cip = new CharacterImageProcessor(gp, this);
        // THÊM MỚI: Khởi tạo attackCooldown
        attackCooldown = 0;

       }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isCollisionOn() {
        return collisionOn;
    }

    public void setCollisionOn(boolean collisionOn) {
        this.collisionOn = collisionOn;
    }

    public boolean isOnPath() {
        return onPath;
    }

    public void setOnPath(boolean onPath) {
        this.onPath = onPath;
    }

    public int getActionLockCounter() {
        return actionLockCounter;
    }

    public void setActionLockCounter(int actionLockCounter) {
        this.actionLockCounter = actionLockCounter;
    }

    public Rectangle getSolidArea() {
        return solidArea;
    }

    public int getSolidAreaDefaultX() {
        return solidAreaDefaultX;
    }

    public int getSolidAreaDefaultY() {
        return solidAreaDefaultY;
    }

    public int getWorldX() {
        return worldX;
    }

    public void setWorldX(int worldX) {
        this.worldX = worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public void setWorldY(int worldY) {
        this.worldY = worldY;
    }

    public int getSpeed() {
        return speed;
    }



    public abstract void draw(Graphics2D g2);

    public Rectangle getHitbox() {
        return new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height
        );
    }

    public void setDefaultValues() {
    }

    public GamePanel getGp() {
        return gp;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getCenterX() {
        return worldX + cip.getCurFrame().getWidth() / 2;
    }

    public int getCenterY() {
        return worldY + cip.getCurFrame().getHeight() / 2;
    }

    public int getXDistance(Character target) {
        return Math.abs(getCenterX() - target.getCenterX());
    }

    public int getYDistance(Character target) {
        return Math.abs(getCenterY() - target.getCenterY());
    }

    public int getTileDistance(Character target) {
        return (getXDistance(target) + getYDistance(target)) / gp.getTileSize();
    }

    public int getGoalCol(Character target) {
        return (target.worldX + target.solidArea.x) / gp.getTileSize();
    }

    public int getGoalRow(Character target) {
        return (target.worldY + target.solidArea.y) / gp.getTileSize();
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getAttackCooldown() {
        return attackCooldown;
    }


    public int getCurrentHealth() { // Bạn đã có phương thức này
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
        // Đảm bảo máu hiện tại không bao giờ âm hoặc vượt quá máu tối đa
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }
        if (this.currentHealth > this.maxHealth) {
            this.currentHealth = this.maxHealth;
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }




    public boolean isAttacking() {
        return false;
    }

    public void update() {
        collisionOn = false;
        gp.getcChecker().checkTile(this);
        if (!(this instanceof Player)) {
            boolean contactWithPlayer = gp.getcChecker().checkPlayer(this);
        }

        if (!collisionOn) {
            switch (direction) {
                case "up":
                    worldY -= speed;
                    break;
                case "down":
                    worldY += speed;
                    break;
                case "left":
                    worldX -= speed;
                    break;
                case "right":
                    worldX += speed;
                    break;
            }
        }

        // Giảm thời gian hồi chiêu tấn công trong mỗi frame
        if (attackCooldown > 0) {
            attackCooldown--;
        }


        cip.update();
    }

    // Phương thức kiểm tra xem có thể tấn công hay không
    public boolean canAttack() {
        return attackCooldown == 0;
    }

    // Đặt lại thời gian hồi chiêu sau khi tấn công
    public void resetAttackCooldown() {
        attackCooldown = ATTACK_COOLDOWN_DURATION;
    }

    public void drawHealthBar(Graphics2D g2, int screenX, int screenY) {
        int barWidth = gp.getTileSize();
        int barHeight = 6;
        int x = screenX; // Dùng screenX thay vì worldX
        int y = screenY - barHeight - 5; // Dùng screenY thay vì worldY

        double healthPercent = (double) currentHealth / maxHealth;
        int healthBarWidth = (int) (barWidth * healthPercent);

        if (currentHealth < maxHealth && currentHealth > 0) { // Ví dụ: chỉ hiện khi bị mất máu hoặc còn máu
            // Vẽ viền thanh máu
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth, barHeight);

            // Vẽ thanh máu màu đỏ (phần máu mất)
            g2.setColor(Color.RED);
            g2.fillRect(x + 1, y + 1, barWidth - 1, barHeight - 1);

            // Vẽ thanh máu màu xanh (phần máu còn)
            g2.setColor(Color.GREEN);
            g2.fillRect(x + 1, y + 1, healthBarWidth - 1, barHeight - 1);
        }
    }

    public int receiveDamage(int damage, Character attacker) {
        int actualDamage = Math.max(0, damage - defense);
        currentHealth = Math.max(0, currentHealth - actualDamage);
        if (currentHealth <= 0) {
            onDeath(attacker);
        }
        return actualDamage;
    }

    protected void onDeath(Character attacker) {
        // Được ghi đè bởi các lớp con
    }


}