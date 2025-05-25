package character;

import imageProcessor.CharacterImageProcessor;
import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Character {

    protected GamePanel gp;

    public int worldX, worldY;
    public int speed;
    public String direction;

    protected CharacterImageProcessor cip;

    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public int actionLockCounter = 0;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // STATE
    public boolean onPath = false;


    // CHARACTER ATTRIBUTES
    // Tính phần máu còn lại (giả sử maxHealth và currentHealth có sẵn)
    protected int maxHealth;  // ví dụ
    protected int currentHealth;  // ví dụ, bạn có thể làm thành thuộc tính nhân vật
    protected int defaultSpeed;
    protected int attack;
    protected int defense;

    String name;


    public Character(GamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle();
        cip = new CharacterImageProcessor(gp, this);
    }

    public abstract void setAction();
    public abstract void draw(Graphics2D g2);

    public void setDefaultValues() {
    }


    public int getCenterX() {
        return worldX + cip.getCurFrame().getWidth()/2;
    }

    public int getCenterY() {
        return worldY + cip.getCurFrame().getHeight()/2;
    }

    public int getXDistance(Character target) {
        return Math.abs(getCenterX() - target.getCenterX());
    }

    public int getYDistance(Character target) {
        return Math.abs(getCenterY() - target.getCenterY());
    }

    public int getTileDistance(Character target) {
        return (getXDistance(target) + getYDistance(target))/ gp.getTileSize();
    }

    public int getGoalCol(Character target) {
        return (target.worldX + target.solidArea.x) / gp.getTileSize();
    }

    public int getGoalRow(Character target) {
        return (target.worldY + target.solidArea.y) / gp.getTileSize();
    }

    public void update() {
        collisionOn = false;
        gp.getcChecker().checkTile(this);
        if (!(this instanceof Player)) {
            boolean contactWithPlayer = gp.getcChecker().checkPlayer(this);
        }

        if(!collisionOn) {
            switch(direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }

        cip.update();
    }
    public void drawHealthBar(Graphics2D g2, int screenX, int screenY) {
        int barWidth = gp.getTileSize();
        int barHeight = 6;
        int x = screenX; // Dùng screenX thay vì worldX
        int y = screenY - barHeight - 5; // Dùng screenY thay vì worldY

        double healthPercent = (double) currentHealth / maxHealth;
        int healthBarWidth = (int) (barWidth * healthPercent);

        if (currentHealth <= maxHealth && currentHealth > 0) { // Ví dụ: chỉ hiện khi bị mất máu hoặc còn máu
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

}