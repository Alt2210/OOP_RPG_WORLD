package events_system;

import character.Character;
import character.role.Player;
import character.monster.Monster;
import main.GamePanel;
import skillEffect.SkillEffect;
import sound.Sound;

import java.awt.*;

public class CombatSystem {
    private GamePanel gp;

    public CombatSystem(GamePanel gp) {
        this.gp = gp;
    }

    public void performAttack(Character attacker, Character target) {
        System.out.println("[" + System.currentTimeMillis() + "] performAttack: " + attacker.getName() + " (hướng: " + attacker.direction + ") định tấn công " + target.getName() + " (hướng: " + target.direction + ")");
        System.out.println("    Attacker (" + attacker.getName() + ") canAttack: " + attacker.canAttack() + " (Cooldown: " + attacker.getAttackCooldown() + ")");
        System.out.println("    Target (" + target.getName() + ") currentHealth: " + target.getCurrentHealth());

        if ( target.getCurrentHealth() > 0) {
            int damage = attacker.getAttack();
            int defense = target.getDefense();

            double damageMultiplier = 1.0;

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

    public void processSkillEffectImpacts(SkillEffect skillEffect) {
        if (skillEffect == null || !skillEffect.isAlive() || skillEffect.getCaster() == null) {
            return;
        }
        // Vùng va chạm của skillEffect.skillEffect (đã được cập nhật trong skillEffect.skillEffect.update() dựa trên worldX, worldY)
        Rectangle skillEffectBounds = new Rectangle(
                skillEffect.worldX + skillEffect.solidArea.x, // Giả sử solidArea.x/y là offset so với worldX/Y
                skillEffect.worldY + skillEffect.solidArea.y,
                skillEffect.solidArea.width,
                skillEffect.solidArea.height
        );
        // Chỉ xử lý nếu người bắn là Player (để gây sát thương cho Monster)
        // Nếu bạn muốn Monster cũng bắn skillEffect.skillEffect trúng Player, bạn cần mở rộng logic này
        if (skillEffect.getCaster() instanceof Player) {
            // Tạo một danh sách tạm thời chứa tất cả quái vật còn sống để duyệt
            // Hoặc bạn có thể duyệt qua từng mảng quái vật riêng biệt như trong checkPlayerMonsterCombat
            Monster[] allMonstersToCheck[] = {
                    gp.getMON_GreenSlime(),
                    gp.getMON_Bat(),
                    gp.getMON_GolemBoss(),
                    // Thêm các mảng quái vật khác ở đây nếu có
            };

            for (Monster[] monsterArray : allMonstersToCheck) {
                if (monsterArray == null) continue;

                for (int i = 0; i < monsterArray.length; i++) {
                    Monster monster = monsterArray[i];
                    if (monster != null && monster.getCurrentHealth() > 0) {
                        Rectangle monsterBounds = new Rectangle(
                                monster.worldX + monster.solidAreaDefaultX,
                                monster.worldY + monster.solidAreaDefaultY,
                                monster.solidArea.width,
                                monster.solidArea.height
                        );

                        if (skillEffectBounds.intersects(monsterBounds)) {
                            // Va chạm đã xảy ra.
                            // StellarField không gây sát thương ở đây, nó có bộ đếm riêng.
                            // Chúng ta chỉ cần xử lý cho các skillEffect.skillEffect thông thường.
                            if (skillEffect.isSingleHit()) {
                                System.out.println("[" + System.currentTimeMillis() + "] CombatSystem.processSkillEffectImpacts: " +
                                        skillEffect.getCaster().getName() + "'s skillEffect.skillEffect trúng " + monster.getName());

                                int skillEffectDamageValue = skillEffect.getDamageValue();
                                int actualDamageDealt = monster.receiveDamage(skillEffectDamageValue, skillEffect.getCaster());

                                gp.getUi().showMessage(skillEffect.getCaster().getName() + " bắn trúng " +
                                        monster.getName() + " gây " + actualDamageDealt + " sát thương!");

                                gp.playSoundEffect(Sound.SFX_FIREBALL_HIT);
                                skillEffect.setAlive(false); // Chỉ biến mất nếu là single-hit

                                if (monster.getCurrentHealth() <= 0) {
                                    System.out.println("    " + monster.getName() + " đã bị skillEffect.skillEffect đánh bại.");
                                    monsterArray[i] = null;
                                }
                                return; // Dừng lại vì skillEffect.skillEffect single-hit đã hoàn thành nhiệm vụ.
                            }
                        }
                    }
                }
            }
        } else if (skillEffect.getCaster() instanceof Monster) {
            // SkillEffect do Monster bắn, tìm mục tiêu là Player
            Player player = gp.getPlayer();
            if (player != null && player.getCurrentHealth() > 0) {
                Rectangle playerBounds = new Rectangle(
                        player.worldX + player.solidArea.x,
                        player.worldY + player.solidArea.y,
                        player.solidArea.width,
                        player.solidArea.height
                );

                if (skillEffectBounds.intersects(playerBounds)) {
                    System.out.println("[" + System.currentTimeMillis() + "] CombatSystem.processSkillEffectImpacts: " +
                            skillEffect.getCaster().getName() + "'s skillEffect.skillEffect trúng Player!");

                    int skillEffectDamageValue = skillEffect.getDamageValue();
                    int actualDamageDealt = player.receiveDamage(skillEffectDamageValue, skillEffect.getCaster());

                    gp.getUi().showMessage(skillEffect.getCaster().getName() + " bắn trúng bạn gây " +
                            actualDamageDealt + " sát thương!");

                    gp.playSoundEffect(Sound.SFX_FIREBALL_HIT); // Hoặc một âm thanh trúng player riêng
                    skillEffect.setAlive(false);

                    if (player.getCurrentHealth() <= 0) {
                        System.out.println("    Player đã bị " + skillEffect.getCaster().getName() + " đánh bại bằng skillEffect.skillEffect.");
                        // Logic Game Over sẽ được xử lý bởi Player.onDeath() -> gp.gameState
                    }
                    return; // SkillEffect đã trúng Player
                }
            }
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

    public void checkAoEAttack(Character caster, Rectangle aoeBounds, int aoeDamage) {
        // Logic cho caster là Player tấn công Monster
        if (caster instanceof character.role.Player) {
            Monster[][] allMonsterArrays = {
                    gp.getMON_GreenSlime(), gp.getMON_Bat(), gp.getMON_Orc(),
                    gp.getMON_GolemBoss(), gp.getSkeletonLord()
            };
            for (Monster[] monsterArray : allMonsterArrays) {
                if (monsterArray == null) continue;
                for (Monster monster : monsterArray) {
                    if (monster != null && monster.getCurrentHealth() > 0) {
                        if (aoeBounds.intersects(monster.getHitbox())) {
                            // Gọi phương thức AoE chuyên dụng
                            attackAOE(caster, monster, aoeDamage);
                        }
                    }
                }
            }
        }
        // Logic cho caster là Monster tấn công Player
        else if (caster instanceof character.monster.Monster) {
            Player player = gp.getPlayer();
            if (player != null && player.getCurrentHealth() > 0) {
                if (aoeBounds.intersects(player.getHitbox())) {
                    // Gọi phương thức AoE chuyên dụng
                    attackAOE(caster, player, aoeDamage);
                }
            }
        }
    }


    public void checkAoEAttack(Character caster, int centerX, int centerY, int radius, int aoeDamage) {
        Monster[][] allMonsters = {
                gp.getMON_GreenSlime(), gp.getMON_Bat(), gp.getMON_Orc(), gp.getMON_GolemBoss()
        };

        for (Monster[] monsterArray : allMonsters) {
            if (monsterArray == null) continue;
            for (int i = 0; i < monsterArray.length; i++) {
                Monster target = monsterArray[i];
                if (target != null && target.getCurrentHealth() > 0) {
                    int monsterLeft = target.worldX + target.solidArea.x;
                    int monsterRight = monsterLeft + target.solidArea.width;
                    int monsterTop = target.worldY + target.solidArea.y;
                    int monsterBottom = monsterTop + target.solidArea.height;

                    int closestX = Math.max(monsterLeft, Math.min(centerX, monsterRight));
                    int closestY = Math.max(monsterTop, Math.min(centerY, monsterBottom));

                    double distance = Math.sqrt(Math.pow(centerX - closestX, 2) + Math.pow(centerY - closestY, 2));

                    if (distance <= radius) {
                        // THAY ĐỔI: Gọi attackAOE thay vì performAttack
                        attackAOE(caster, target, aoeDamage);
                    }
                }
            }
        }
    }

    public void attackAOE(Character caster, Character target, int aoeDamage) {
        if (target != null && target.getCurrentHealth() > 0 && caster != null) {
            // Gọi receiveDamage và LƯU LẠI sát thương thực tế đã gây ra
            int actualDamageDealt = target.receiveDamage(aoeDamage, caster);

            // Hiển thị thông báo với lượng sát thương thực tế
            if (actualDamageDealt > 0) { // Chỉ hiện thông báo nếu có sát thương
                gp.getUi().showMessage(caster.getName() + " hits " + target.getName() + " for " + actualDamageDealt + " damage!");
            }
        }
    }
}