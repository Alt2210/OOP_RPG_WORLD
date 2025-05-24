package character.monster;

import character.Character;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MON_GreenSlime extends Monster {

    public MON_GreenSlime(GamePanel gp) {
        super(gp);
        cip.setNumSprite(2);
        direction = "down";
        speed = 1;

        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 33; // Lấy từ AssetSetter
        worldY = gp.getTileSize() * 33; // Lấy từ AssetSetter
        direction = "down";

        setName("Green Slime");
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxHealth = 4;
        currentHealth = maxHealth;
        attack = 5;
        defense = 0;
        exp = 2;

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        cip.getImage("/monster", "greenslime_");
    }


    public void setAction() {
        if (onPath) {
            // Check if it stops chasing
            checkStopChasingOrNot(gp.getPlayer(), 15, 100);

            // Search the direction to go
            /*
            searchPath(getGoalCol(gp.getPlayer()), getGoalRow(gp.getPlayer()));
            */
        } else {
            // Check if it starts chasing
            checkStartChasingOrNot(gp.getPlayer(), 5, 100);

            // Get a random direction
            getRandomDirection(120);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = cip.getCurFrame(); // Lấy frame hiện tại từ lớp Character
        if (image != null) {
            // Tính toán vị trí vẽ trên màn hình tương tự như cách vẽ Tile hoặc SuperItem
            int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
            int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;

            // Chỉ vẽ NPC nếu nó nằm trong tầm nhìn của camera
            if (worldX + gp.getTileSize() > gp.getPlayer().worldX - gp.getPlayer().screenX &&
                    worldX - gp.getTileSize() < gp.getPlayer().worldX + gp.getPlayer().screenX &&
                    worldY + gp.getTileSize() > gp.getPlayer().worldY - gp.getPlayer().screenY &&
                    worldY - gp.getTileSize() < gp.getPlayer().worldY + gp.getPlayer().screenY) {
                g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
            }

                drawHealthBar(g2, screenX, screenY);
        }
    }

    public void damageReaction() {
        actionLockCounter = 0;
//        direction = gp.player.direction;
        onPath = true;
    }

    public void checkDrop() {
        // CAST A DIE
        int i = new Random().nextInt(100) + 1;

        // SET THE MONSTER DROP
        /*if (i < 50) {
            dropItem(new OBJ_Coin_Bronze(gp));
        }
        if (i >= 50 && i < 75) {
            dropItem(new OBJ_Heart(gp));
        }
        if (i >= 75 && i < 100) {
            dropItem(new OBJ_ManaCrystal(gp));
        }*/
    }
}
