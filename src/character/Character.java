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
}