package skillEffect.areaOfEffect;

import character.Character;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Explosion_area extends AreaOfEffect{

    private int durationCounter; // Đếm ngược thời gian tồn tại của vùng
    private int radius; // Bán kính của vùng
    private SkillImageProcessor sip; // Các khung hình cho animation

    public Explosion_area(GamePanel gp, SkillImageProcessor sip) {
        super(gp);
        this.sip = sip;
    }

    // Phương thức set sẽ được gọi từ Skill khi kích hoạt kỹ năng
    public void set(int worldX, int worldY, Character caster, int damage) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.caster = caster;
        this.damage = damage;
        this.alive = true;

        // Cấu hình cho kỹ năng
        this.durationCounter = 60; // 10 giây * 60 FPS = 600 frames
        this.damageTickCounter = 30; // Gây sát thương mỗi giây (60 frames)
        this.radius = gp.getTileSize() * 3; // Bán kính là 2 ô tile

        // Vùng solidArea không dùng để va chạm vật lý, mà để xác định tâm của hiệu ứng
        this.solidArea.width = radius * 2;
        this.solidArea.height = radius * 2;
        // Đặt worldX, worldY là tâm của vùng
        this.worldX = worldX - radius;
        this.worldY = worldY - radius;
    }


    @Override
    public void update() {
        if (!alive) {
            return;
        }

        // --- Logic cốt lõi của kỹ năng ---
        durationCounter--;
        damageTickCounter--;

        // 1. Kiểm tra thời gian tồn tại
        if (durationCounter <= 0) {
            this.alive = false; // Hết thời gian, đánh dấu để xóa khỏi game
            return;
        }

        // 2. Kiểm tra đến lúc gây sát thương chưa
        if (damageTickCounter <= 0) {
            applyAoeDamage();
            damageTickCounter = 60; // Đặt lại bộ đếm cho lần gây sát thương tiếp theo
        }

        // 3. Cập nhật animation
        sip.update();
    }

    private void applyAoeDamage() {
        // Sử dụng lại logic kiểm tra AoE từ CombatSystem để đảm bảo tính nhất quán
        // Tâm của AoE là (this.worldX + radius, this.worldY + radius)
        gp.getCombatSystem().checkAoEAttack(caster, this.worldX + radius, this.worldY + radius, this.radius, this.damage);
    }

    @Override
    public void draw(Graphics2D g2) {

        int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
        int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

        // Chỉ vẽ nếu vùng kỹ năng nằm trong màn hình
        if (worldX + (radius*2) > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() &&
                worldX - (radius*2) < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
                worldY + (radius*2) > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
                worldY - (radius*2) < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) {

            Rectangle boundingRect = new Rectangle(screenX, screenY, radius * 2, radius * 2);
            g2.draw(boundingRect);
            BufferedImage currentFrame = sip.getCurFrame();
            g2.drawImage(currentFrame, screenX, screenY, radius * 2, radius * 2, null);
        }
    }


    @Override
    public boolean isSingleHit() {
        return false;
    }
}
