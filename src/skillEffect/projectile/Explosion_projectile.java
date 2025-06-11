package skillEffect.projectile;

import character.Character;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;
import skillEffect.areaOfEffect.Explosion_area;
import skillEffect.areaOfEffect.StellaField;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Explosion_projectile extends Projectile{
    SkillImageProcessor sip;

    public Explosion_projectile(GamePanel gp, SkillImageProcessor sip) {
        super(gp);
        if (gp != null) { // Thêm kiểm tra null cho gp
            solidArea.width = (int) (gp.getTileSize() * 0.75); // Kích thước mới cho Fireball
            solidArea.height = (int) (gp.getTileSize() * 0.75); // Kích thước mới cho Fireball
        } else { // Fallback nếu gp null (không nên xảy ra)
            solidArea.width = 36; // Giá trị mặc định nếu gp null
            solidArea.height = 36; // Giá trị mặc định nếu gp null
        }
        this.sip = sip;
    }

    @Override
    public void set(int startWorldX, int startWorldY, String direction, Character caster, int damage) {
        this.worldX = startWorldX - solidArea.width / 2;   // Căn giữa fireball tại điểm xuất phát
        this.worldY = startWorldY - solidArea.height / 2;
        this.direction = direction;
        this.caster = caster;
        this.damage = damage; // Sát thương được truyền vào

        // Các thuộc tính riêng của Fireball
        this.speed = 5; // Tốc độ của Fireball
        this.maxRange = 4 * gp.getTileSize(); // Tầm bay tối đa (ví dụ: 10 ô)
        this.alive = true;
        this.distanceTraveled = 0;
    }

    public void explode(){
        SkillImageProcessor explosion =  new SkillImageProcessor(gp, caster, false);
        explosion.getImage("Skill", "boom_L_", 14);
        Explosion_area field = new Explosion_area(gp, explosion);
        int explosionDamage = this.damage * 10;
        field.set(this.worldX, this.worldY, caster, explosionDamage);
        gp.skillEffects.add(field);
        gp.getUi().showMessage("Explosion!!!");
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
            explode();
            return;
        }

        gp.getCombatSystem().checkSingleAttack(this);

        if (!this.alive) {
            explode();          // Gây nổ
            return;             // Kết thúc update
        }
        // Kiểm tra tầm bay tối đa
        if (distanceTraveled > maxRange) {
            this.alive = false;
            explode();
            // System.out.println("Fireball reached max range"); // Debug
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage currentFrame = sip.getCurFrame(this.direction);
        if (!alive || currentFrame == null) {
            return;
        }

        int screenX = worldX - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenX();
        int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

        // Chỉ vẽ nếu skillEffect.projectile nằm trong màn hình
        if (worldX + solidArea.width > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenX() &&
                worldX - solidArea.width < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenX() && // Sửa: trừ solidArea.width
                worldY + solidArea.height > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
                worldY - solidArea.height < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) { // Sửa: trừ solidArea.height

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
