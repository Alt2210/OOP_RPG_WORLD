package projectile;

import character.Character;
import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class GolemArmProjectile extends Projectile {
    private BufferedImage upImage, downImage, leftImage, rightImage;
    private int tileSize;

    public GolemArmProjectile(GamePanel gp) {
        super(gp);
        tileSize = gp.getTileSize();
        speed = 8; // Tốc độ di chuyển
        maxRange = tileSize * 10; // Tầm xa tối đa: 10 ô
        solidArea = new Rectangle(0, 0, 35, 14); // Kích thước vùng va chạm: 35x14 pixel
        loadImage();
    }

    private void loadImage() {
        try {
            // Tải hình ảnh cho hướng "up"
            String upPath = "/projectile/golemboss_projectileup.png";
            InputStream upIs = getClass().getResourceAsStream(upPath);
            if (upIs == null) {
                System.err.println("Cảnh báo: Không tìm thấy " + upPath + ". Sử dụng placeholder.");
                upImage = createPlaceholderImage();
            } else {
                upImage = ImageIO.read(upIs);
            }

            // Tải hình ảnh cho hướng "down"
            String downPath = "/projectile/golemboss_projectiledown.png";
            InputStream downIs = getClass().getResourceAsStream(downPath);
            if (downIs == null) {
                System.err.println("Cảnh báo: Không tìm thấy " + downPath + ". Sử dụng placeholder.");
                downImage = createPlaceholderImage();
            } else {
                downImage = ImageIO.read(downIs);
            }

            // Tải hình ảnh cho hướng "left"
            String leftPath = "/projectile/golemboss_projectileleft.png";
            InputStream leftIs = getClass().getResourceAsStream(leftPath);
            if (leftIs == null) {
                System.err.println("Cảnh báo: Không tìm thấy " + leftPath + ". Sử dụng placeholder.");
                leftImage = createPlaceholderImage();
            } else {
                leftImage = ImageIO.read(leftIs);
            }

            // Tải hình ảnh cho hướng "right"
            String rightPath = "/projectile/golemboss_projectileright.png";
            InputStream rightIs = getClass().getResourceAsStream(rightPath);
            if (rightIs == null) {
                System.err.println("Cảnh báo: Không tìm thấy " + rightPath + ". Sử dụng placeholder.");
                rightImage = createPlaceholderImage();
            } else {
                rightImage = ImageIO.read(rightIs);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh GolemArmProjectile: " + e.getMessage());
            upImage = downImage = leftImage = rightImage = createPlaceholderImage();
        }
    }

    private BufferedImage createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(35, 14, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();
        g.setColor(new Color(150, 75, 0, 200)); // Màu nâu mờ cho cánh tay
        g.fillRect(0, 0, placeholder.getWidth(), placeholder.getHeight());
        g.dispose();
        return placeholder;
    }

    @Override
    public void set(int worldX, int worldY, String direction, Character caster, int damage) {
        this.worldX = worldX - solidArea.width / 2; // Căn giữa tại điểm xuất phát
        this.worldY = worldY - solidArea.height / 2;
        this.direction = direction;
        this.caster = caster;
        this.damage = damage;
        this.alive = true;
        this.distanceTraveled = 0;
    }

    @Override
    public void update() {
        if (!alive) return;

        // Di chuyển projectile
        int prevWorldX = worldX;
        int prevWorldY = worldY;
        switch (direction) {
            case "up": worldY -= speed; break;
            case "down": worldY += speed; break;
            case "left": worldX -= speed; break;
            case "right": worldX += speed; break;
        }
        distanceTraveled += Math.abs(worldX - prevWorldX) + Math.abs(worldY - prevWorldY);

        // Kiểm tra va chạm với tile
        if (checkTileCollision(worldX + solidArea.width / 2, worldY + solidArea.height / 2)) {
            return;
        }

        // Kiểm tra tầm bắn tối đa
        if (distanceTraveled > maxRange) {
            alive = false;
            return;
        }

        // Va chạm với Player sẽ được CombatSystem xử lý
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!alive) return;

        // Chọn hình ảnh dựa trên hướng
        BufferedImage image = null;
        switch (direction) {
            case "up": image = upImage; break;
            case "down": image = downImage; break;
            case "left": image = leftImage; break;
            case "right": image = rightImage; break;
        }

        if (image == null) {
            System.err.println("Hình ảnh cho hướng " + direction + " không tồn tại!");
            return;
        }

        int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
        int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;

        if (worldX + solidArea.width > gp.getPlayer().worldX - gp.getPlayer().screenX &&
                worldX - solidArea.width < gp.getPlayer().worldX + gp.getPlayer().screenX &&
                worldY + solidArea.height > gp.getPlayer().worldY - gp.getPlayer().screenY &&
                worldY - solidArea.height < gp.getPlayer().worldY + gp.getPlayer().screenY) {
            // Vẽ hình ảnh với kích thước gốc 35x14 pixel
            int drawWidth = 35 * 3;
            int drawHeight = 14 * 3;
            int offsetX = (solidArea.width - drawWidth) / 2; // Căn giữa trong solidArea (35-35=0)
            int offsetY = (solidArea.height - drawHeight) / 2; // Căn giữa trong solidArea (14-14=0)
            g2.drawImage(image, screenX + offsetX, screenY + offsetY, drawWidth, drawHeight, null);

            // Vẽ hitbox để debug
            g2.setColor(Color.RED);
            g2.drawRect(screenX, screenY, solidArea.width, solidArea.height);
        }
    }
}