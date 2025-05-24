package item;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public abstract class SuperItem {
    protected BufferedImage image;
    protected String name;
    protected int id;
    protected boolean collision;
    protected int worldX, worldY;

    protected Rectangle solidArea;
    protected int solidAreaDefaultX;
    protected int solidAreaDefaultY;

    public SuperItem(int id, String name) {
        this.id = id;
        this.name = name;
        this.collision = false;
        this.worldX = 0;
        this.worldY = 0;
        this.solidArea = new Rectangle(0, 0, 48, 48);
        this.solidAreaDefaultX = 0;
        this.solidAreaDefaultY = 0;
    }

    // Getters and Setters
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean hasCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
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

    public Rectangle getSolidArea() {
        return solidArea;
    }

    public void setSolidArea(Rectangle solidArea) {
        this.solidArea = solidArea;
    }

    // Draw method: Truyền thông tin thay vì cả GamePanel
    public void draw(Graphics2D g2, int tileSize, int playerWorldX, int playerWorldY, int playerScreenX, int playerScreenY) {
        int screenX = worldX - playerWorldX + playerScreenX;
        int screenY = worldY - playerWorldY + playerScreenY;

        if (worldX + tileSize > playerWorldX - playerScreenX &&
                worldX - tileSize < playerWorldX + playerScreenX &&
                worldY + tileSize > playerWorldY - playerScreenY &&
                worldY - tileSize < playerWorldY + playerScreenY) {

            g2.drawImage(image, screenX, screenY, tileSize, tileSize, null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SuperItem)) return false;
        SuperItem item = (SuperItem) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
