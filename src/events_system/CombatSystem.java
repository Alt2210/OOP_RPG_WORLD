package events_system;

import character.Character;
import character.Player;
import character.monster.Monster;
import main.GamePanel;

import java.awt.*;

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
    public void handleMonsterCollisionAttack(Player player, Monster[] monsters) {
        if (player == null || player.getCurrentHealth() <= 0) {
            return; // Không làm gì nếu Player không hợp lệ hoặc đã chết
        }

        Rectangle playerBounds = new Rectangle(
                player.worldX + player.solidArea.x,
                player.worldY + player.solidArea.y,
                player.solidArea.width,
                player.solidArea.height
        );

        for (Monster monster : monsters) {
            if (monster != null && monster.getCurrentHealth() > 0) {
                Rectangle monsterBounds = new Rectangle(
                        monster.worldX + monster.solidArea.x,
                        monster.worldY + monster.solidArea.y,
                        monster.solidArea.width,
                        monster.solidArea.height
                );

                // Kiểm tra va chạm trực tiếp giữa vị trí HIỆN TẠI của Player và Monster
                if (playerBounds.intersects(monsterBounds)) {
                    // Nếu chạm nhau, Monster sẽ tấn công Player (nếu cooldown cho phép)
                    if (monster.canAttack()) {
                        System.out.println("CombatSystem: " + monster.getName() + " is attacking " + player.getName() + " on direct collision.");
                        performAttack(monster, player);
                        // Nếu Player chết sau đòn tấn công này, có thể dừng vòng lặp sớm
                        if (player.getCurrentHealth() <= 0) {
                            break;
                        }
                    }
                }
            }
        }
    }
}