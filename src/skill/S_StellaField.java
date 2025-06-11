package skill;

import character.Character;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;
import skillEffect.areaOfEffect.StellaField; // Import lớp mới của chúng ta

public class S_StellaField extends Skill {

    public S_StellaField(Character caster, GamePanel gp) {
        // Gọi constructor của lớp cha để thiết lập các thuộc tính cơ bản cho kỹ năng
        super("Stellar Field",                          // Tên kỹ năng
                "Tạo ra một vùng gây sát thương liên tục.", // Mô tả
                30,                                      // Lượng mana tiêu tốn
                60 * 15);                                // Cooldown 15 giây (60 FPS * 15 giây)
        this.sip = new SkillImageProcessor(gp, caster, false);
        sip.getImage("Skill", "astrologist_star_", 24);
    }

    @Override
    public void activate(Character caster, GamePanel gp) {

        // 1. Hiển thị thông báo và âm thanh (nếu có)
        gp.getUi().showMessage("Stellar Field!");
        // gp.playSoundEffect(Sound.SFX_STELLAR_FIELD); // Bạn có thể thêm âm thanh ở đây

        // 2. Tạo một thực thể StellaField mới
        StellaField field = new StellaField(gp, this.sip);

        // 3. Xác định vị trí và sát thương
        // Kỹ năng xuất hiện ngay tại tâm của người chơi
        int casterCenterX = caster.getWorldX() + caster.getSolidArea().x + caster.getSolidArea().width / 2;
        int casterCenterY = caster.getWorldY() + caster.getSolidArea().y + caster.getSolidArea().height / 2;

        // Sát thương có thể dựa trên chỉ số của người chơi, ví dụ: một nửa chỉ số tấn công
        int damagePerTick = caster.getAttack() / 2;

        // 4. Thiết lập các thông số cho vùng kỹ năng
        field.set(casterCenterX, casterCenterY, caster, damagePerTick);

        // 5. Thêm vùng kỹ năng vào danh sách projectiles của GamePanel để nó được update và draw mỗi frame
        gp.skillEffects.add(field); //
    }
}