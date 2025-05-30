package events_system;

import character.Character;
import character.Player;
import character.monster.Monster;
import main.GamePanel;

import java.awt.*;

public class CombatSystem {
    private GamePanel gp;

    public CombatSystem(GamePanel gp) {
        this.gp = gp;
    }

    public void performAttack(Character attacker, Character target) {
        System.out.println("[" + System.currentTimeMillis() + "] performAttack: " + attacker.getName() + " (hướng: " + attacker.direction + ") định tấn công " + target.getName() + " (hướng: " + target.direction + ")");
        System.out.println("    Attacker ("+ attacker.getName() +") canAttack: " + attacker.canAttack() + " (Cooldown: " + attacker.getAttackCooldown() + ")");
        System.out.println("    Target ("+ target.getName() +") currentHealth: " + target.getCurrentHealth());

        if (attacker.canAttack() && target.getCurrentHealth() > 0) {
            int damage = attacker.getAttack();
            int defense = target.getDefense();

            double damageMultiplier = 1.0;
            if (isAttackedFromBehind(attacker, target)) {
                damageMultiplier = 1.5;
                System.out.println("    " + target.getName() + " bị tấn công từ phía sau! Sát thương tăng 50%.");
            }

            int actualDamage = (int) (Math.max(0, damage - defense) * damageMultiplier);

            System.out.println("    => CUỘC TẤN CÔNG XÁC NHẬN: " + attacker.getName() + " đánh " + target.getName());
            System.out.println("        RawDamage=" + damage + ", TargetDefense=" + defense + ", DamageMultiplier=" + damageMultiplier + ", ActualDamage=" + actualDamage);

            target.receiveDamage(damage, attacker);
            attacker.resetAttackCooldown();

            System.out.println("    " + attacker.getName() + " cooldown được đặt lại thành: " + attacker.getAttackCooldown());
            gp.getUi().showMessage(attacker.getName() + " gây " + actualDamage + " sát thương cho " + target.getName());
        } else {
            System.out.println("    => CUỘC TẤN CÔNG BỊ HỦY: Attacker không thể tấn công (cooldown: " + attacker.getAttackCooldown() + ") hoặc Target đã hết máu (HP: " + target.getCurrentHealth() + ").");
        }
    }

    private boolean isAttackedFromBehind(Character attacker, Character target) {
        String attackerDir = attacker.direction;
        String targetDir = target.direction;

        switch (attackerDir) {
            case "right":
                return targetDir.equals("left");
            case "left":
                return targetDir.equals("right");
            case "up":
                return targetDir.equals("down");
            case "down":
                return targetDir.equals("up");
            default:
                return false;
        }
    }

    private boolean isWithinAttackRange(Character attacker, Character target) {
        // Tính trung tâm của attacker và target
        int attackerCenterX = attacker.worldX + attacker.solidArea.x + attacker.solidArea.width / 2;
        int attackerCenterY = attacker.worldY + attacker.solidArea.y + attacker.solidArea.height / 2;
        int targetCenterX = target.worldX + target.solidArea.x + target.solidArea.width / 2;
        int targetCenterY = target.worldY + target.solidArea.y + target.solidArea.height / 2;

        // Tính khoảng cách giữa hai trung tâm
        double distance = Math.sqrt(Math.pow(attackerCenterX - targetCenterX, 2) + Math.pow(attackerCenterY - targetCenterY, 2));

        // Nếu khoảng cách vượt quá attackRange, không cần kiểm tra thêm
        if (distance > attacker.getAttackRange()) {
            return false;
        }

        // Tính góc giữa attacker và target
        double angle = Math.toDegrees(Math.atan2(targetCenterY - attackerCenterY, targetCenterX - attackerCenterX));
        if (angle < 0) angle += 360;

        // THAY ĐỔI: Kiểm tra khu vực hình quạt chặt chẽ hơn
        boolean isInDirection = false;
        double fanAngle = 90; // Góc mở của hình quạt (90 độ)
        double startAngle, endAngle;

        switch (attacker.direction) {
            case "right":
                startAngle = 315; // 0 - 45 độ
                endAngle = 45;
                isInDirection = angle >= startAngle || angle <= endAngle;
                break;
            case "left":
                startAngle = 135; // 135 - 225 độ
                endAngle = 225;
                isInDirection = angle >= startAngle && angle <= endAngle;
                break;
            case "up":
                startAngle = 225; // 225 - 315 độ
                endAngle = 315;
                isInDirection = angle >= startAngle && angle <= endAngle;
                break;
            case "down":
                startAngle = 45; // 45 - 135 độ
                endAngle = 135;
                isInDirection = angle >= startAngle && angle <= endAngle;
                break;
            default:
                startAngle = 0;
                endAngle = 0;
                isInDirection = false;
        }

        // THÊM MỚI: Kiểm tra xem solidArea của target có giao với khu vực hình quạt không
        boolean isInFanShape = false;
        if (isInDirection) {
            // Tính các điểm của solidArea của target
            int targetLeft = target.worldX + target.solidArea.x;
            int targetRight = targetLeft + target.solidArea.width;
            int targetTop = target.worldY + target.solidArea.y;
            int targetBottom = targetTop + target.solidArea.height;

            // Kiểm tra từng góc của solidArea của target
            int[][] targetCorners = new int[][] {
                    {targetLeft, targetTop},    // Góc trên-trái
                    {targetRight, targetTop},   // Góc trên-phải
                    {targetLeft, targetBottom}, // Góc dưới-trái
                    {targetRight, targetBottom} // Góc dưới-phải
            };

            for (int[] corner : targetCorners) {
                double cornerAngle = Math.toDegrees(Math.atan2(corner[1] - attackerCenterY, corner[0] - attackerCenterX));
                if (cornerAngle < 0) cornerAngle += 360;
                double cornerDistance = Math.sqrt(Math.pow(corner[0] - attackerCenterX, 2) + Math.pow(corner[1] - attackerCenterY, 2));

                boolean cornerInDirection = false;
                if (attacker.direction.equals("right")) {
                    cornerInDirection = cornerAngle >= startAngle || cornerAngle <= endAngle;
                } else {
                    cornerInDirection = cornerAngle >= startAngle && cornerAngle <= endAngle;
                }

                if (cornerDistance <= attacker.getAttackRange() && cornerInDirection) {
                    isInFanShape = true;
                    break;
                }
            }
        }


        return distance <= attacker.getAttackRange() && isInFanShape;
    }

    public void checkPlayerMonsterCombat(Player player, Monster[] monsters) {
        if (player == null || player.getCurrentHealth() <= 0) {
            return;
        }

        if (gp.getKeyH().attackPressed && player.canAttack()) {
            for (int i = 0; i < monsters.length; i++) {
                Monster monster = monsters[i];
                if (monster != null && monster.getCurrentHealth() > 0) {
                    if (isWithinAttackRange(player, monster)) {
                        System.out.println("[" + System.currentTimeMillis() + "] checkPlayerMonsterCombat: Player tấn công " + monster.getName() + " trong tầm đánh hình quạt.");
                        performAttack(player, monster);
                        if (monster.getCurrentHealth() <= 0) {
                            System.out.println("    " + monster.getName() + " đã bị Player đánh bại.");
                            monsters[i] = null;
                        }
                        break;
                    }
                }
            }
        } else {
            int monsterIndex = gp.getcChecker().checkEntity(player, monsters);
            if (monsterIndex != 999) {
                Monster monster = monsters[monsterIndex];
                System.out.println("[" + System.currentTimeMillis() + "] checkPlayerMonsterCombat: Player (hướng: " + player.direction + ") định di chuyển vào " + monster.getName() + " (hướng: " + monster.direction + ", HP: " + monster.getCurrentHealth() + ")");

                if (monster.canAttack()) {
                    System.out.println("    Player di chuyển vào " + monster.getName() + ". Monster (cooldown: " + monster.getAttackCooldown() + ") thực hiện phản công.");
                    performAttack(monster, player);
                } else {
                    System.out.println("    Player di chuyển vào " + monster.getName() + ". Monster (cooldown: " + monster.getAttackCooldown() + ") KHÔNG THỂ phản công (đang cooldown).");
                }
            }
        }
    }

    public void handleMonsterCollisionAttack(Player player, Monster[] monsters) {
        if (player == null || player.getCurrentHealth() <= 0) {
            return;
        }

        for (Monster monster : monsters) {
            if (monster != null && monster.getCurrentHealth() > 0) {
                if (isWithinAttackRange(monster, player)) {
                    if (monster.canAttack()) {
                        System.out.println("[" + System.currentTimeMillis() + "] handleMonsterCollisionAttack: " + monster.getName() + " tấn công Player trong tầm đánh hình quạt.");
                        performAttack(monster, player);
                        if (player.getCurrentHealth() <= 0) {
                            break;
                        }
                    } else {
                        System.out.println("    " + monster.getName() + " KHÔNG THỂ tấn công (cooldown: " + monster.getAttackCooldown() + ").");
                    }
                }
            }
        }
    }
}