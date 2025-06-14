package character;

import main.GamePanel;

import java.awt.*;

/**
 * Lớp trừu tượng cho các nhân vật có khả năng chiến đấu.
 * Kế thừa từ Character và bổ sung các thuộc tính, phương thức liên quan đến chiến đấu.
 */
public abstract class CombatableCharacter extends Character {

    // Thuộc tính chiến đấu
    protected int maxHealth;
    protected int currentHealth;
    protected int attack;
    protected int defense;
    protected int attackRange;
    protected int attackCooldown;
    protected int ATTACK_COOLDOWN_DURATION;

    public CombatableCharacter(GamePanel gp) {
        super(gp);
        // Khởi tạo các giá trị liên quan đến chiến đấu
        this.attackCooldown = 0;
    }

    // Getters và Setters cho các thuộc tính chiến đấu
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public int getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
        if (this.currentHealth < 0) this.currentHealth = 0;
        if (this.currentHealth > this.maxHealth) this.currentHealth = this.maxHealth;
    }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getAttackRange() { return attackRange; }
    public int getAttackCooldown() { return attackCooldown; }

    /**
     * Ghi đè phương thức isAttacking từ lớp Character.
     * @return true nếu nhân vật đang trong trạng thái tấn công.
     */
    @Override
    public boolean isAttacking() {
        return false; // Các lớp con cụ thể (Player, Monster) sẽ ghi đè logic này
    }

    /**
     * Kiểm tra xem nhân vật có thể tấn công hay không (dựa trên cooldown).
     * @return true nếu có thể tấn công.
     */
    public boolean canAttack() {
        return attackCooldown == 0;
    }

    /**
     * Đặt lại thời gian hồi chiêu sau khi thực hiện một đòn tấn công.
     */
    public void resetAttackCooldown() {
        attackCooldown = ATTACK_COOLDOWN_DURATION;
    }

    /**
     * Xử lý logic khi nhân vật nhận sát thương.
     * @param damage Sát thương gốc nhận vào.
     * @param attacker Nhân vật thực hiện đòn tấn công.
     * @return Sát thương thực tế đã nhận sau khi trừ giáp.
     */
    public int receiveDamage(int damage, CombatableCharacter attacker) {
        int actualDamage = Math.max(0, damage - defense);
        setCurrentHealth(currentHealth - actualDamage);
        if (currentHealth <= 0) {
            onDeath(attacker);
        }
        return actualDamage;
    }

    /**
     * Phương thức được gọi khi nhân vật hết máu.
     * Sẽ được ghi đè bởi Player và Monster.
     * @param attacker Nhân vật đã gây ra đòn đánh cuối cùng.
     */
    protected void onDeath(CombatableCharacter attacker) {
        // Logic mặc định (có thể để trống)
    }

    /**
     * Vẽ thanh máu phía trên nhân vật.
     * @param g2 Đối tượng Graphics2D để vẽ.
     * @param screenX Tọa độ X trên màn hình.
     * @param screenY Tọa độ Y trên màn hình.
     */
    public void drawHealthBar(Graphics2D g2, int screenX, int screenY) {
        if (currentHealth > 0 && currentHealth < maxHealth) {
            int barWidth = gp.getTileSize();
            int barHeight = 6;
            int x = screenX;
            int y = screenY - barHeight - 5;

            double healthPercent = (double) currentHealth / maxHealth;
            int healthBarWidth = (int) (barWidth * healthPercent);

            // Vẽ nền thanh máu
            g2.setColor(new Color(60, 0, 0, 200));
            g2.fillRect(x, y, barWidth, barHeight);

            // Vẽ lượng máu còn lại
            g2.setColor(new Color(60, 215, 60, 220));
            g2.fillRect(x, y, healthBarWidth, barHeight);

            // Vẽ viền
            g2.setColor(new Color(20, 20, 20));
            g2.drawRect(x, y, barWidth, barHeight);
        }
    }

    /**
     * Cập nhật trạng thái chiến đấu của nhân vật (ví dụ: cooldown).
     */
    @Override
    public void update() {
        super.update(); // Gọi update của lớp CombatableCharacter để xử lý di chuyển
        if (attackCooldown > 0) {
            attackCooldown--;
        }
    }
}