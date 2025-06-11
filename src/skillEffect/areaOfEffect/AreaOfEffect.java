package skillEffect.areaOfEffect;

import character.Character;
import main.GamePanel;
import skillEffect.SkillEffect;

public abstract class AreaOfEffect extends SkillEffect {
    protected int durationCounter;
    protected int radius;
    protected String direction;

    public AreaOfEffect(GamePanel gp) {
        super(gp);
    }

    public abstract void set(int worldX, int worldY, Character caster, int damage);

    // Vùng hiệu ứng không bao giờ biến mất khi va chạm
    public boolean isSingleHit() {
        return false;
    }


}