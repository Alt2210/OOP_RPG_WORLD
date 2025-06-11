package worldObject;

import character.role.Player;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WorldObject {
    public BufferedImage image;
    public String name;
    public int id;
    public boolean collision = false;
    public int worldX, worldY;

    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().getScreenX();
        int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().getScreenY();

        if(worldX + gp.getTileSize() > gp.getPlayer().worldX - gp.getPlayer().getScreenX() &&
                worldX - gp.getTileSize() < gp.getPlayer().worldX + gp.getPlayer().getScreenX() &&
                worldY + gp.getTileSize() > gp.getPlayer().worldY - gp.getPlayer().getScreenY() &&
                worldY - gp.getTileSize() < gp.getPlayer().worldY + gp.getPlayer().getScreenY()) {

            g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
        }

    }

    public void interactPlayer(Player player, int i, GamePanel gp){

    }


}
