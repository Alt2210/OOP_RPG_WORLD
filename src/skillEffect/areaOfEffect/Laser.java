package skillEffect.areaOfEffect;

import character.Character;
import character.monster.Monster;
import character.role.Player;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Laser extends AreaOfEffect {

    private String direction;
    private SkillImageProcessor sip;

    public Laser(GamePanel gp, SkillImageProcessor sip) {
        super(gp);
        this.alive = false;
        this.sip = sip;
    }

    @Override
    public void set(int worldX, int worldY, Character caster, int damage) {
        this.caster = caster;
        this.damage = damage;
        this.alive = true;
        this.durationCounter = 300; // 5 giây * 60 FPS
        this.direction = caster.getDirection();

        // Tính toán vị trí và kích thước vùng laser
        int laserLength = gp.getTileSize() * 6;
        int laserWidth = gp.getTileSize() * 1;
        int casterBodyWidth = caster.getSolidArea().width;
        int casterBodyHeight = caster.getSolidArea().height;
        int casterCenterX = caster.getWorldX() + caster.getSolidArea().x + casterBodyWidth / 2;
        int casterCenterY = caster.getWorldY() + caster.getSolidArea().y + casterBodyHeight / 2;
        int range = gp.getTileSize() / 2; // Khoảng cách từ người cast đến laser

        switch (direction) {
            case "up":
                solidArea.setBounds(casterCenterX - laserWidth / 2, casterCenterY - casterBodyHeight / 2 - laserLength - range, laserWidth, laserLength);
                break;
            case "down":
                solidArea.setBounds(casterCenterX - laserWidth / 2, casterCenterY + casterBodyHeight / 2 + range, laserWidth, laserLength);
                break;
            case "left":
                solidArea.setBounds(casterCenterX - casterBodyWidth / 2 - laserLength - range, casterCenterY - laserWidth / 2, laserLength, laserWidth);
                break;
            case "right":
                solidArea.setBounds(casterCenterX + casterBodyWidth / 2 + range, casterCenterY - laserWidth / 2, laserLength, laserWidth);
                break;
        }
        this.worldX = solidArea.x;
        this.worldY = solidArea.y;
    }

    private void applyAoeDamage() {
        if (gp.getCombatSystem() != null) {
            // Gọi phương thức mới trong CombatSystem để xử lý sát thương vùng hình chữ nhật
            gp.getCombatSystem().checkAoEAttack(this.caster, this.getSolidArea(), this.damage);
        }
    }

    @Override
    public void update() {
        if (!alive) return;

        // Cập nhật animation
        if (sip != null) {
            sip.update();
        }

        // Xử lý thời gian tồn tại
        durationCounter--;
        if (durationCounter <= 0) {
            this.alive = false;
            return;
        }

        // Đến lúc gây sát thương, reset bộ đếm
        damageTickCounter--;
        if (damageTickCounter < 0) {
            damageTickCounter = 60;
            applyAoeDamage();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!alive) return;

        BufferedImage curFrame = sip.getCurFrame(this.direction);
        int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
        int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

        if (worldX + solidArea.width > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() &&
                worldX < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
                worldY + solidArea.height > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
                worldY < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) {

            if (curFrame != null) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.drawImage(curFrame, screenX, screenY, solidArea.width, solidArea.height, null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            } else {
                g2.setColor(new Color(255, 50, 50, 150));
                g2.fillRect(screenX, screenY, solidArea.width, solidArea.height);
            }
        }
    }
}