// Gợi ý package: skill/FireballSkill.java
package skill;

import character.Character;
import character.role.Player;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;
import skillEffect.projectile.Fireball;
import sound.Sound;

public class S_Fireball extends Skill {

    public S_Fireball(Player caster, GamePanel gp) {
        // Gọi constructor của lớp cha Skill
        super("Fireball", "Bắn ra một quả cầu lửa gây sát thương.", 10, 60); // Tên, Mô tả, Mana, Cooldown (1 giây)
        this.sip = new SkillImageProcessor(gp, caster, true);
        sip.getImage("projectile", "fireball_right_", 2);
    }



    @Override
    public void activate(Player caster, GamePanel gp) {
        // Đây là logic bạn đã có trong Soldier.castFireball()
        gp.getUi().showMessage("Fireball! -" + this.getManaCost() + " MP");
        gp.playSoundEffect(Sound.SFX_FIREBALL_WHOOSH);

        Fireball fireball = new Fireball(gp, sip);

        // Tính toán vị trí xuất hiện của quả cầu lửa
        int playerSolidCenterX = caster.getWorldX() + caster.getSolidArea().x + caster.getSolidArea().width / 2;
        int playerSolidCenterY = caster.getWorldY() + caster.getSolidArea().y + caster.getSolidArea().height / 2;

        int fireballHitboxWidth = fireball.getSolidArea().width;
        int fireballHitboxHeight = fireball.getSolidArea().height;
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

        fireball.set(fireballSpawnCenterX, fireballSpawnCenterY, caster.getDirection(), caster, fireballDamage);
        gp.skillEffects.add(fireball);
    }
}