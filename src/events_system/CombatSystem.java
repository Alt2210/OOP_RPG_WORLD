package events_system;

import character.Character;
import character.Player;
import character.monster.Monster;
import main.GamePanel;

public class CombatSystem {
    private GamePanel gp;

    // Khởi tạo CombatSystem với tham chiếu đến GamePanel
    public CombatSystem(GamePanel gp) {
        this.gp = gp;
    }

    // Phương thức thực hiện tấn công từ attacker sang target
    public void performAttack(Character attacker, Character target) {
        if (attacker.canAttack() && target.getCurrentHealth() > 0) {
            int damage = attacker.getAttack();
            target.receiveDamage(damage, attacker);
            attacker.resetAttackCooldown();
            gp.getUi().showMessage(attacker.getName() + " gây " + Math.max(0, damage - target.getDefense()) + " sát thương cho " + target.getName());
        }
    }

    // Kiểm tra và xử lý chiến đấu giữa người chơi và quái vật
    public void checkPlayerMonsterCombat(Player player, Monster[] monsters) {
        int monsterIndex = gp.getcChecker().checkEntity(player, monsters);
        if (monsterIndex != 999) {
            Monster monster = monsters[monsterIndex];
            if (gp.getKeyH().attackPressed && player.canAttack()) {
                performAttack(player, monster);
                if (monster.getCurrentHealth() <= 0) {
                    monsters[monsterIndex] = null;
                }
            } else if (monster.canAttack()) {
                performAttack(monster, player);
            }
        }
    }
}