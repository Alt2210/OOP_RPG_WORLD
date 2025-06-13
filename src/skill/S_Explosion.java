// Gợi ý package: skill/FireballSkill.java
package skill;

import character.Character;
import character.role.Player;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;
import skillEffect.areaOfEffect.StellaField;
import skillEffect.projectile.Explosion_projectile;
import skillEffect.projectile.Fireball;

public class S_Explosion extends Skill {

    public S_Explosion(Player caster, GamePanel gp) {
        // Gọi constructor của lớp cha Skill
        super("Explosion", "Bắn ra một quả cầu lửa gây sát thương, khi chạm trúng mục tiêu cầu lửa sẽ gây nổ tạo sát thương diện rộng", 50, 60); // Tên, Mô tả, Mana, Cooldown (1 giây)
        this.sip = new SkillImageProcessor(gp, caster, true);
        sip.getImage("projectile", "fireball_right_", 2);
    }

    @Override
    public void activate(Player caster, GamePanel gp) {
        // Đây là logic bạn đã có trong Soldier.castFireball()
        gp.getUi().showMessage("Fireball! -" + this.getManaCost() + " MP");
//        gp.playSoundEffect(Sound.SFX_FIREBALL_SHOOT);

        Explosion_projectile explosion = new Explosion_projectile(gp, this.sip);

        // Tính toán vị trí xuất hiện của quả cầu lửa
        int playerSolidCenterX = caster.getWorldX() + caster.getSolidArea().x + caster.getSolidArea().width / 2;
        int playerSolidCenterY = caster.getWorldY() + caster.getSolidArea().y + caster.getSolidArea().height / 2;

        int fireballHitboxWidth = explosion.getSolidArea().width;
        int fireballHitboxHeight = explosion.getSolidArea().height;
        int fireballSpawnCenterX = playerSolidCenterX;
        int fireballSpawnCenterY = playerSolidCenterY;
        int gap = 2;
        int offsetX = caster.getSolidArea().width / 2 + fireballHitboxWidth / 2 + gap;
        int offsetY = caster.getSolidArea().height / 2 + fireballHitboxHeight / 2 + gap;

        switch (caster.getDirection()) {
            case "up": fireballSpawnCenterY -= offsetY; break;
            case "down": fireballSpawnCenterY += offsetY; break;
            case "left": fireballSpawnCenterX -= offsetX; break;
            case "right": fireballSpawnCenterX += offsetX; break;
        }

        // Sát thương của Fireball có thể dựa trên chỉ số của người chơi
        int fireballDamage = caster.getAttack() * 2;



        explosion.set(fireballSpawnCenterX, fireballSpawnCenterY, caster.getDirection(), caster, fireballDamage);
        gp.skillEffects.add(explosion);
    }
}