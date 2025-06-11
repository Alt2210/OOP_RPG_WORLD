package skillEffect.projectile;

import character.Character;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;
import skillEffect.areaOfEffect.Explosion_area;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tornado extends Projectile{
    SkillImageProcessor sip;

    public Tornado(GamePanel gp, SkillImageProcessor sip) {
        super(gp);
        if (gp != null) { // Thêm kiểm tra null cho gp
            solidArea.width = (gp.getTileSize() * 2);
            solidArea.height = (gp.getTileSize() * 2);
        } else {
            solidArea.width = 92;
            solidArea.height = 92;
        }
        this.sip = sip;
    }

    @Override
    public void set(int startWorldX, int startWorldY, String direction, Character caster, int damage) {
        this.worldX = startWorldX - solidArea.width / 2;   // Căn giữa tại điểm xuất phát
        this.worldY = startWorldY - solidArea.height / 2;
        this.direction = direction;
        this.caster = caster;
        this.damage = damage * 2; // Sát thương được truyền vào

        // Các thuộc tính riêng của Tornado
        this.speed = 3; // Tốc độ của Tornado
        this.maxRange = 6 * gp.getTileSize(); // Tầm bay tối đa (ví dụ: 10 ô)
        this.alive = true;
        this.distanceTraveled = 0;
        this.damageTickCounter = 20;
    }

    @Override
    public boolean isSingleHit(){
        return false;
    }

    public void applyDamage(){
        gp.getCombatSystem().checkSingleAttack(this);
    }

    @Override
    public void update() {
        if (!alive) {
            return;
        }

        // Di chuyển skillEffect.projectile
        int prevWorldX = worldX;
        int prevWorldY = worldY;
        double moveAmountX = 0;
        double moveAmountY = 0;

        switch (direction) {
            case "up": moveAmountY = -speed; break;
            case "down": moveAmountY = speed; break;
            case "left": moveAmountX = -speed; break;
            case "right": moveAmountX = speed; break;
        }
        worldX += (int)moveAmountX;
        worldY += (int)moveAmountY;

        distanceTraveled += Math.sqrt(Math.pow(worldX - prevWorldX, 2) + Math.pow(worldY - prevWorldY, 2));
        // Cập nhật hoạt ảnh
        sip.update();
        // Kiểm tra va chạm với tile (tại tâm của skillEffect.projectile)
        if (checkTileCollision(worldX + solidArea.width / 2, worldY + solidArea.height / 2)) {
            return;
        }

        damageTickCounter--;
        if (damageTickCounter <= 0) {
            applyDamage();
            damageTickCounter = 20; // Đặt lại bộ đếm cho lần gây sát thương tiếp theo
        }


        // Kiểm tra tầm bay tối đa
        if (distanceTraveled > maxRange) {
            this.alive = false;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage currentFrame = sip.getCurFrame(this.direction);
        if (!alive || currentFrame == null) {
            return;
        }

        int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().getScreenX();
        int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().getScreenY();

        // Chỉ vẽ nếu skillEffect.projectile nằm trong màn hình
        if (worldX + solidArea.width > gp.getPlayer().worldX - gp.getPlayer().getScreenX() &&
                worldX - solidArea.width < gp.getPlayer().worldX + gp.getPlayer().getScreenX() && // Sửa: trừ solidArea.width
                worldY + solidArea.height > gp.getPlayer().worldY - gp.getPlayer().getScreenY() &&
                worldY - solidArea.height < gp.getPlayer().worldY + gp.getPlayer().getScreenY()) { // Sửa: trừ solidArea.height

            // Vẽ fireball với kích thước của solidArea (đã được đặt dựa trên ảnh)
            g2.drawImage(currentFrame, screenX, screenY, solidArea.width, solidArea.height, null);
            // Nếu muốn vẽ kích thước gốc của ảnh:
            //g2.drawImage(image, screenX, screenY, image.getWidth(), image.getHeight(), null);
            //g2.drawImage(image, screenX, screenY, image.getWidth() * gp.getScale(), image.getHeight() * gp.getScale(), null);
            // Optional: Vẽ vùng solidArea để debug
            g2.setColor(Color.RED);
            g2.drawRect(screenX, screenY, solidArea.width , solidArea.height);
        }
    }
}
