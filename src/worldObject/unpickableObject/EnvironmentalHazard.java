package worldObject.unpickableObject;

import character.role.Player;
import main.GamePanel;
import worldObject.WorldObject;

/**
 * Lớp trừu tượng cho các vật thể môi trường có khả năng gây sát thương cho người chơi.
 * Chứa logic chung về sát thương và cooldown.
 */
public abstract class EnvironmentalHazard extends WorldObject {

    protected int damageAmount;
    protected long damageCooldown;
    protected long lastDamageTime;

    public EnvironmentalHazard(GamePanel gp) {
        collision = false;
        // các giá trị mặc định
        lastDamageTime = 0;
        damageAmount = 0;
        damageCooldown = 1000;
    }

    @Override
    public void interactPlayer(Player player, int i, GamePanel gp) {
        if (System.currentTimeMillis() - lastDamageTime > damageCooldown) {
            applyEffect(player, gp);
            this.lastDamageTime = System.currentTimeMillis();
        }
    }
    protected abstract void applyEffect(Player player, GamePanel gp);
}