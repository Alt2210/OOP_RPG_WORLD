// Gợi ý package: skill/FireballSkill.java
package skill;

import character.Character;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;
import skillEffect.projectile.Fireball;

public class S_Fireball extends Skill {

    public S_Fireball(Character caster, GamePanel gp) {
        // Gọi constructor của lớp cha Skill
        super("Fireball", "Bắn ra một quả cầu lửa gây sát thương.", 10, 60); // Tên, Mô tả, Mana, Cooldown (1 giây)
        this.sip = new SkillImageProcessor(gp, caster, true);
        sip.getImage("projectile", "fireball_right_", 2);
    }



    @Override
    public void activate(Character caster, GamePanel gp) {
        // Đây là logic bạn đã có trong Soldier.castFireball()
        gp.getUi().showMessage("Fireball! -" + this.getManaCost() + " MP");
//        gp.playSoundEffect(Sound.SFX_FIREBALL_SHOOT);

        Fireball fireball = new Fireball(gp, sip);

        // Tính toán vị trí xuất hiện của quả cầu lửa
        int playerSolidCenterX = caster.worldX + caster.solidArea.x + caster.solidArea.width / 2;
        int playerSolidCenterY = caster.worldY + caster.solidArea.y + caster.solidArea.height / 2;

        int fireballHitboxWidth = fireball.solidArea.width;
        int fireballHitboxHeight = fireball.solidArea.height;
        int fireballSpawnCenterX = playerSolidCenterX;
        int fireballSpawnCenterY = playerSolidCenterY;
        int gap = 2;
        int offsetX = caster.solidArea.width / 2 + fireballHitboxWidth / 2 + gap;
        int offsetY = caster.solidArea.height / 2 + fireballHitboxHeight / 2 + gap;

        switch (caster.direction) {
            case "up": fireballSpawnCenterY -= offsetY; break;
            case "down": fireballSpawnCenterY += offsetY; break;
            case "left": fireballSpawnCenterX -= offsetX; break;
            case "right": fireballSpawnCenterX += offsetX; break;
        }

        // Sát thương của Fireball có thể dựa trên chỉ số của người chơi
        int fireballDamage = caster.getAttack() * 2;

        fireball.set(fireballSpawnCenterX, fireballSpawnCenterY, caster.direction, caster, fireballDamage);
        gp.skillEffects.add(fireball);
    }
}