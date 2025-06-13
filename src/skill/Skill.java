// Gợi ý package: skill/Skill.java
package skill;

import character.Character;
import character.role.Player;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;

public abstract class Skill {
    protected String name;
    protected String description;
    protected int manaCost;
    protected int cooldownDuration; // Thời gian hồi chiêu tính bằng frame (60 frame = 1 giây)
    protected SkillImageProcessor sip;

    public Skill(String name, String description, int manaCost, int cooldownDuration) {
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
        this.cooldownDuration = cooldownDuration;
    }

    /**
     * Kích hoạt logic của kỹ năng.
     * @param caster Người chơi sử dụng kỹ năng.
     * @param gp Tham chiếu đến GamePanel.
     */
    public abstract void activate(Player caster, GamePanel gp);

    // Getters để UI có thể lấy thông tin
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getCooldownDuration() {
        return cooldownDuration;
    }
}