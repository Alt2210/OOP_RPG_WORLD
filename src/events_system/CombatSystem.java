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
        // Log khi phương thức được gọi
        System.out.println("[" + System.currentTimeMillis() + "] performAttack: " + attacker.getName() + " (hướng: " + attacker.direction + ") định tấn công " + target.getName() + " (hướng: " + target.direction + ")");
        System.out.println("    Attacker ("+ attacker.getName() +") canAttack: " + attacker.canAttack() + " (Cooldown: " + attacker.getAttackCooldown() + ")");
        System.out.println("    Target ("+ target.getName() +") currentHealth: " + target.getCurrentHealth());

        if (attacker.canAttack() && target.getCurrentHealth() > 0) {
            int damage = attacker.getAttack();
            int defense = target.getDefense();
            int actualDamage = Math.max(0, damage - defense); // Sát thương thực tế không thể âm

            System.out.println("    => CUỘC TẤN CÔNG XÁC NHẬN: " + attacker.getName() + " đánh " + target.getName());
            System.out.println("        RawDamage=" + damage + ", TargetDefense=" + defense + ", ActualDamage=" + actualDamage);

            target.receiveDamage(damage, attacker); // Target nhận sát thương
            attacker.resetAttackCooldown();         // Đặt lại cooldown cho attacker

            System.out.println("    " + attacker.getName() + " cooldown được đặt lại thành: " + attacker.getAttackCooldown());
            gp.getUi().showMessage(attacker.getName() + " gây " + actualDamage + " sát thương cho " + target.getName());
        } else {
            System.out.println("    => CUỘC TẤN CÔNG BỊ HỦY: Attacker không thể tấn công (cooldown: " + attacker.getAttackCooldown() + ") hoặc Target đã hết máu (HP: " + target.getCurrentHealth() + ").");
        }
    }

    // Kiểm tra và xử lý chiến đấu giữa người chơi và quái vật
    public void checkPlayerMonsterCombat(Player player, Monster[] monsters) {
        // Log khi bắt đầu kiểm tra (có thể bỏ nếu quá nhiều log)
        // System.out.println("[" + System.currentTimeMillis() + "] checkPlayerMonsterCombat: Bắt đầu cho Player " + player.getName());

        // player là 'entity' đang được kiểm tra, monsters là mảng 'target'
        // checkEntity kiểm tra xem bước đi *tiếp theo* của player có va chạm với monster nào không
        int monsterIndex = gp.getcChecker().checkEntity(player, monsters);

        // Log kết quả từ checkEntity (có thể bỏ nếu quá nhiều log)
        // if (monsterIndex != 999) {
        //     System.out.println("    checkEntity (player vs monster): Player (hướng: " + player.direction + ") có khả năng va chạm với " + monsters[monsterIndex].getName() + " (index: " + monsterIndex + ")");
        // } else {
        //     System.out.println("    checkEntity (player vs monster): Player (hướng: " + player.direction + ") không có khả năng va chạm với monster nào ở bước tiếp theo.");
        // }


        if (monsterIndex != 999) { // Nếu bước đi tiếp theo của player sẽ va chạm với một monster
            Monster monster = monsters[monsterIndex];
            System.out.println("[" + System.currentTimeMillis() + "] checkPlayerMonsterCombat: Player (hướng: " + player.direction + ") định di chuyển vào " + monster.getName() + " (hướng: " + monster.direction + ", HP: " + monster.getCurrentHealth() + ")");

            if (gp.getKeyH().attackPressed && player.canAttack()) { // Nếu player nhấn nút tấn công VÀ có thể tấn công (không trong cooldown)
                System.out.println("    Player chủ động tấn công " + monster.getName());
                performAttack(player, monster);
                if (monster.getCurrentHealth() <= 0) {
                    System.out.println("    " + monster.getName() + " đã bị Player đánh bại.");
                    monsters[monsterIndex] = null; // Loại bỏ monster khỏi mảng (hoặc đánh dấu là đã chết)
                }
            } else if (monster.canAttack()) { // Nếu player KHÔNG chủ động tấn công, NHƯNG monster có thể tấn công (không trong cooldown)
                // Đây là trường hợp "monster phản công" khi player đi vào.
                System.out.println("    Player di chuyển vào " + monster.getName() + ". Monster (cooldown: " + monster.getAttackCooldown() + ") thực hiện phản công.");
                performAttack(monster, player); // Monster tấn công Player
            } else if (!monster.canAttack()){ // Player di chuyển vào nhưng monster đang cooldown
                System.out.println("    Player di chuyển vào " + monster.getName() + ". Monster (cooldown: " + monster.getAttackCooldown() + ") KHÔNG THỂ phản công (đang cooldown).");
            }
        }
    }

    public void handleMonsterCollisionAttack(Player player, Monster[] monsters) {
        if (player == null || player.getCurrentHealth() <= 0) {
            // System.out.println("[" + System.currentTimeMillis() + "] handleMonsterCollisionAttack: Bỏ qua vì Player null hoặc đã hết máu.");
            return;
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
                int collisionTolerance = 1; // Hoặc 0 nếu bạn muốn chạm chính xác
                boolean collisionOccurs = gp.getcChecker().areRectsNearlyColliding(playerBounds, monsterBounds, collisionTolerance);

                // Log thông tin bounds và hướng của cả hai
//                System.out.println("[" + System.currentTimeMillis() + "] handleMonsterCollisionAttack - KIỂM TRA TRỰC TIẾP: " + monster.getName() + " vs " + player.getName());
//                System.out.println("    Player dir: " + player.direction + ", Monster dir: " + monster.direction);
//                System.out.println("    Player Bounds: X=" + playerBounds.x + ", Y=" + playerBounds.y + ", W=" + playerBounds.width + ", H=" + playerBounds.height + " (Player worldX: " + player.worldX + ", worldY: " + player.worldY + ")");
//                System.out.println("    Monster Bounds: X=" + monsterBounds.x + ", Y=" + monsterBounds.y + ", W=" + monsterBounds.width + ", H=" + monsterBounds.height + " (Monster worldX: " + monster.worldX + ", worldY: " + monster.worldY + ")");

                //boolean collisionOccurs = playerBounds.intersects(monsterBounds);
              //  System.out.println("    Va chạm thực tế (intersects): " + collisionOccurs);

                if (collisionOccurs) {
//                    System.out.println("    -> VA CHẠM ĐƯỢC PHÁT HIỆN giữa " + player.getName() + " và " + monster.getName());
//                    System.out.println("    -> Kiểm tra " + monster.getName() + " có thể tấn công không. Cooldown: " + monster.getAttackCooldown());

                    if (monster.canAttack()) { // Kiểm tra monster có thể tấn công không
                        //System.out.println("    -> " + monster.getName() + " CÓ THỂ tấn công. Gọi performAttack.");
                        performAttack(monster, player); // Monster tấn công Player
                        if (player.getCurrentHealth() <= 0) {
                           // System.out.println("    -> Player " + player.getName() + " đã bị " + monster.getName() + " đánh bại sau va chạm trực tiếp.");
                            break; // Thoát vòng lặp nếu player đã chết
                        }
                    } else {
                      //  System.out.println("    -> " + monster.getName() + " KHÔNG THỂ tấn công (cooldown: " + monster.getAttackCooldown() + ").");
                    }
                }
                // System.out.println("    ---- Kết thúc kiểm tra với " + monster.getName() + " ----"); // Phân tách log cho từng monster nếu cần
            }
        }
    }
}
