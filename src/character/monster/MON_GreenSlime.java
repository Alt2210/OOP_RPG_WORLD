package character.monster;

import character.Character;
import main.GamePanel;
import ai.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MON_GreenSlime extends Monster {

    public MON_GreenSlime(GamePanel gp) {
        super(gp);
        cip.setNumSprite(2);
        direction = "down";
        speed = 1;
        this.pathFinder = new PathFinder(gp);
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

        cip.getImage("/monster", "greenslime");
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
            g2.setColor(Color.RED); // Hoặc một màu khác để phân biệt
// screenX và screenY là tọa độ vẽ của sprite trên màn hình
            g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
        }
    }

    @Override
    public void damageReaction() {
    }
    @Override
    public void update(){
        this.playerChasing();
        super.update();


    }
    @Override
    protected void onDeath(Character attacker) {
        checkDrop();
        gp.getUi().showMessage(attacker.getName() + " đã đánh bại " + getName() + "!");
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
