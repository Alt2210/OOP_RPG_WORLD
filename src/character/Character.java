package character;

import imageProcessor.CharacterImageProcessor;
import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

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

    // CHARACTER ATTRIBUTES
    // Tính phần máu còn lại (giả sử maxHealth và currentHealth có sẵn)
    int maxHealth;  // ví dụ
    int currentHealth;  // ví dụ, bạn có thể làm thành thuộc tính nhân vật

    public Character(GamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle();
        cip = new CharacterImageProcessor(gp, this);
    }

    public abstract void setAction();
    public abstract void draw(Graphics2D g2);

    public void setDefaultValues() {
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