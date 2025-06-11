package skill;

import character.Character;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;
import skillEffect.areaOfEffect.Laser; // Import lớp Laser mới

public class S_Laser extends Skill {

    private SkillImageProcessor sip;

    public S_Laser(Character caster, GamePanel gp) {
        super("Static Laser",
                "Tạo ra một bức tường laser tồn tại trong 5 giây, gây sát thương khi chạm phải.",
                0,   // Ví dụ: 40 mana
                60 * 2); // Ví dụ: cooldown 15 giây (15 * 60)
        this.sip = new SkillImageProcessor(gp, caster, true);
        sip.getImage("skill", "laserbeam_right0", 6);
    }

    @Override
    public void activate(Character caster, GamePanel gp) {
        // Khóa hành động của caster trong 30 frame (0.5 giây) để thực hiện animation "thi triển"
        caster.setActionLockCounter(300);

        gp.getUi().showMessage(caster.getName() + " triển khai Static Laser!");
        // gp.playSoundEffect(...); // Thêm âm thanh nếu có

        // Tạo một đối tượng Laser mới
        Laser laserEffect = new Laser(gp, sip);

        // Sát thương của laser có thể dựa trên chỉ số của người bắn
        int damage = caster.getAttack()*2;

        // Thiết lập các thông số cho laser
        laserEffect.set(caster.getWorldX(), caster.getWorldY(), caster, damage);

        // Thêm hiệu ứng vào danh sách quản lý của game để nó được update và vẽ
        gp.skillEffects.add(laserEffect);
    }
}