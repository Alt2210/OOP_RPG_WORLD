package skillEffect.projectile;

import character.Character;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Slimeball extends Projectile {

    // Sử dụng một ảnh tĩnh cho mỗi hướng hoặc bạn có thể làm phức tạp hơn với animation
    private BufferedImage imageDefault; // Một ảnh chung nếu không có ảnh theo hướng

    public Slimeball(GamePanel gp) {
        super(gp);
        loadImages();
        if (gp != null) {
            // Kích thước vùng va chạm của Slimeball
            solidArea.width = (int) (gp.getTileSize() * 0.8);
            solidArea.height = (int) (gp.getTileSize() * 0.8);
        } else {
            solidArea.width = 30; // Giá trị mặc định
            solidArea.height = 30;
        }
    }

    private void loadImages() {
        try {
            // Thay thế "/skillEffect.projectile/slimeball_default.png" bằng đường dẫn thực tế đến ảnh của bạn
            // Nếu bạn có ảnh theo hướng, hãy tải chúng tương tự như Fireball
            InputStream is = getClass().getResourceAsStream("/projectile/slimeball.png");
            if (is == null) {
                System.err.println("Cảnh báo: Không tìm thấy ảnh cho Slimeball! Sử dụng placeholder.");
                imageDefault = createPlaceholderImage();
            } else {
                imageDefault = ImageIO.read(is);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi tải ảnh Slimeball: " + e.getMessage());
            imageDefault = createPlaceholderImage();
        }
    }

    private BufferedImage createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(solidArea.width > 0 ? solidArea.width : 20,
                solidArea.height > 0 ? solidArea.height : 20,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();
        g.setColor(new Color(0, 150, 0, 200)); // Màu xanh lá mờ
        g.fillOval(0, 0, placeholder.getWidth(), placeholder.getHeight());
        g.dispose();
        return placeholder;
    }

    @Override
    public void set(int startWorldX, int startWorldY, String direction, Character caster, int damage) {
        // Căn giữa skillEffect.projectile tại điểm bắn
        this.worldX = startWorldX - solidArea.width / 2;
        this.worldY = startWorldY - solidArea.height / 2;
        this.direction = direction;
        this.caster = caster; // Caster là MON_GreenSlime
        this.damage = damage; // Sát thương của Slimeball

        this.speed = 4; // Tốc độ của Slimeball (có thể chậm hơn Fireball)
        this.maxRange = 6 * gp.getTileSize(); // Tầm bắn (ví dụ: 6 ô)
        this.alive = true;
        this.distanceTraveled = 0;
    }

    @Override
    public void update() {
        if (!alive) {
            return;
        }

        // Di chuyển
        switch (direction) {
            case "up": worldY -= speed; break;
            case "down": worldY += speed; break;
            case "left": worldX -= speed; break;
            case "right": worldX += speed; break;
        }
        distanceTraveled += speed;

        // Cập nhật vị trí solidArea của skillEffect.projectile (nếu solidArea.x/y là offset)
        // this.solidArea.x = worldX;
        // this.solidArea.y = worldY;

        // Kiểm tra va chạm với tile (tâm skillEffect.projectile)
        if (checkTileCollision(worldX + solidArea.width / 2, worldY + solidArea.height / 2)) {
            // alive đã được đặt false trong checkTileCollision
            // gp.playSoundEffect(Sound.SFX_SLIMEBALL_HIT_WALL); // Âm thanh riêng nếu có
            return;
        }

        // Kiểm tra tầm bắn tối đa
        if (distanceTraveled > maxRange) {
            this.alive = false;
            return;
        }

        gp.getCombatSystem().checkSingleAttack(this);
        // Việc kiểm tra va chạm với Player sẽ được CombatSystem xử lý
        // sau khi GamePanel gọi combatSystem.processProjectileImpacts(this);
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!alive || imageDefault == null) {
            return;
        }

        int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().getScreenX();
        int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().getScreenY();

        if (worldX + solidArea.width > gp.getPlayer().worldX - gp.getPlayer().getScreenX() &&
                worldX - solidArea.width < gp.getPlayer().worldX + gp.getPlayer().getScreenX() &&
                worldY + solidArea.height > gp.getPlayer().worldY - gp.getPlayer().getScreenY() &&
                worldY - solidArea.height < gp.getPlayer().worldY + gp.getPlayer().getScreenY()) {

            g2.drawImage(imageDefault, screenX, screenY, solidArea.width, solidArea.height, null);
            // Optional: Vẽ solidArea để debug
            // g2.setColor(Color.GREEN);
            // g2.drawRect(screenX, screenY, solidArea.width, solidArea.height);
            g2.drawRect(screenX, screenY, solidArea.width , solidArea.height);
        }
    }
}