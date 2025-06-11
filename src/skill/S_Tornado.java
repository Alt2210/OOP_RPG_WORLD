package skill;

import character.Character;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;
import skillEffect.projectile.Tornado;

public class S_Tornado extends  Skill{
    public S_Tornado(character.Character caster, GamePanel gp) {
        // Gọi constructor của lớp cha Skill
        super("Tornado", "Tạo ra một lốc xoáy hủy diệt mọi thứ trên đường đi của nó.", 50, 60); // Tên, Mô tả, Mana, Cooldown (1 giây)
        this.sip = new SkillImageProcessor(gp, caster, false);
        sip.getImage("projectile", "EF_cyclone2_", 7);
    }

    @Override
    public void activate(Character caster, GamePanel gp) {
        // Đây là logic bạn đã có trong Soldier.castTornado()
        gp.getUi().showMessage("Tornado! -" + this.getManaCost() + " MP");
//        gp.playSoundEffect(Sound.SFX_SHOOT);

        Tornado tornado = new Tornado(gp, sip);

        int playerSolidCenterX = caster.worldX + caster.solidArea.x + caster.solidArea.width / 2;
        int playerSolidCenterY = caster.worldY + caster.solidArea.y + caster.solidArea.height / 2;

        int tornadoHitboxWidth = tornado.solidArea.width;
        int tornadoHitboxHeight = tornado.solidArea.height;
        int tornadoSpawnCenterX = playerSolidCenterX;
        int tornadoSpawnCenterY = playerSolidCenterY;
        int gap = 2;
        int offsetX = caster.solidArea.width / 2 + tornadoHitboxWidth / 2 + gap;
        int offsetY = caster.solidArea.height / 2 + tornadoHitboxHeight / 2 + gap;

        switch (caster.direction) {
            case "up": tornadoSpawnCenterY -= offsetY; break;
            case "down": tornadoSpawnCenterY += offsetY; break;
            case "left": tornadoSpawnCenterX -= offsetX; break;
            case "right": tornadoSpawnCenterX += offsetX; break;
        }

        // Sát thương của Tornado có thể dựa trên chỉ số của người chơi
        int tornadoDamage = caster.getAttack() * 2;

        tornado.set(tornadoSpawnCenterX, tornadoSpawnCenterY, caster.direction, caster, tornadoDamage);
        gp.skillEffects.add(tornado);
    }
}
