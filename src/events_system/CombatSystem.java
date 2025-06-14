package events_system;

import character.CombatableCharacter;
import character.role.Player;
import character.monster.Monster;
import main.GamePanel;
import skillEffect.SkillEffect;
import skillEffect.projectile.Projectile;
import sound.Sound;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CombatSystem {
    private GamePanel gp;

    public CombatSystem(GamePanel gp) {
        this.gp = gp;
    }

    public void performAttack(CombatableCharacter attacker, CombatableCharacter target) {
        System.out.println("[" + System.currentTimeMillis() + "] performAttack: " + attacker.getName() + " (hướng: " + attacker.getDirection() + ") định tấn công " + target.getName() + " (hướng: " + target.getDirection() + ")");
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
                skillEffect.getWorldX() + skillEffect.getSolidArea().x, // Giả sử solidArea.x/y là offset so với worldX/Y
                skillEffect.getWorldY() + skillEffect.getSolidArea().y,
                skillEffect.getSolidArea().width,
                skillEffect.getSolidArea().height
        );
        // Chỉ xử lý nếu người bắn là Player (để gây sát thương cho Monster)
        // Nếu bạn muốn Monster cũng bắn skillEffect.skillEffect trúng Player, bạn cần mở rộng logic này
        if (skillEffect.getCaster() instanceof Player) {
            // Tạo một danh sách tạm thời chứa tất cả quái vật còn sống để duyệt
            // Hoặc bạn có thể duyệt qua từng mảng quái vật riêng biệt như trong checkPlayerMonsterCombat
            List<Monster> monsters = gp.getCurrentMap().getMonster();

            for (Monster monster : monsters) {
                if (monster != null && monster.getCurrentHealth() > 0) {
                    Rectangle monsterBounds = new Rectangle(
                            monster.getWorldX() + monster.getSolidArea().x,
                            monster.getWorldY() + monster.getSolidArea().y,
                            monster.getSolidArea().width,
                            monster.getSolidArea().height
                    );

                    if (skillEffectBounds.intersects(monsterBounds)) {
                        if (skillEffect.isSingleHit()) {
                            System.out.println("[" + System.currentTimeMillis() + "] CombatSystem: " +
                                    skillEffect.getCaster().getName() + "'s skill trúng " + monster.getName());

                            int actualDamageDealt = monster.receiveDamage(skillEffect.getDamageValue(), skillEffect.getCaster());

                            gp.getUi().showMessage(skillEffect.getCaster().getName() + " bắn trúng " +
                                    monster.getName() + " gây " + actualDamageDealt + " sát thương!");

                            gp.playSoundEffect(Sound.SFX_FIREBALL_HIT);
                            skillEffect.setAlive(false); // Vô hiệu hóa skill effect

                            // Không cần xóa monster ở đây, GamePanel.update() sẽ làm việc đó
                            return; // Thoát vì skill đã trúng mục tiêu
                        }
                    }
                }
            }
        } else if (skillEffect.getCaster() instanceof Monster) {
            // SkillEffect do Monster bắn, tìm mục tiêu là Player
            Player player = gp.getPlayer();
            if (player != null && player.getCurrentHealth() > 0) {
                Rectangle playerBounds = new Rectangle(
                        player.getWorldX() + player.getSolidArea().x,
                        player.getWorldY() + player.getSolidArea().y,
                        player.getSolidArea().width,
                        player.getSolidArea().height
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

    private boolean isWithinAttackRange(CombatableCharacter attacker, CombatableCharacter target) {
        // Tính trung tâm của attacker và target
        int attackerCenterX = attacker.getWorldX() + attacker.getSolidArea().x + attacker.getSolidArea().width / 2;
        int attackerCenterY = attacker.getWorldY() + attacker.getSolidArea().y + attacker.getSolidArea().height / 2;
        int targetCenterX = target.getWorldX() + target.getSolidArea().x + target.getSolidArea().width / 2;
        int targetCenterY = target.getWorldY() + target.getSolidArea().y + target.getSolidArea().height / 2;

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

        switch (attacker.getDirection()) {
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
            int targetLeft = target.getWorldX() + target.getSolidArea().x;
            int targetRight = targetLeft + target.getSolidArea().width;
            int targetTop = target.getWorldY() + target.getSolidArea().y;
            int targetBottom = targetTop + target.getSolidArea().height;

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
                if (attacker.getDirection().equals("right")) {
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


    public void checkPlayerMonsterCombat(Player player, java.util.List<? extends Monster> monsters){ // MODIFIED
        if (player == null || player.getCurrentHealth() <= 0) {
            return;
        }


        int monsterIndex = gp.getcChecker().checkEntity(player, monsters);
        if (monsterIndex != 999) {
            Monster monster = monsters.get(monsterIndex);
            System.out.println("[" + System.currentTimeMillis() + "] checkPlayerMonsterCombat: Player (hướng: " + player.getDirection() + ") định di chuyển vào " + monster.getName() + " (hướng: " + monster.getDirection() + ", HP: " + monster.getCurrentHealth() + ")");

            if (monster.canAttack()) {
                System.out.println("    Player di chuyển vào " + monster.getName() + ". Monster (cooldown: " + monster.getAttackCooldown() + ") thực hiện phản công.");
                performAttack(monster, player);
            } else {
                System.out.println("    Player di chuyển vào " + monster.getName() + ". Monster (cooldown: " + monster.getAttackCooldown() + ") KHÔNG THỂ phản công (đang cooldown).");
            }
        }
    }
    public void handleMonsterCollisionAttack(Player player, java.util.List<? extends Monster> monsters) { // MODIFIED
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
    
    public void checkAoEAttack(CombatableCharacter caster, Rectangle aoeBounds, int aoeDamage) {
        // Logic cho caster là Player tấn công Monster
        if (caster instanceof character.role.Player) {
            List<Monster> monsters = gp.getCurrentMap().getMonster();
            for (Monster monster : monsters) {
                if (monster != null && monster.getCurrentHealth() > 0) {
                    if (aoeBounds.intersects(monster.getHitbox())) {
                        attackAOE(caster, monster, aoeDamage);
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


    public void checkAoEAttack(CombatableCharacter caster, int centerX, int centerY, int radius, int aoeDamage) {
        List<Monster> monsters = gp.getCurrentMap().getMonster();
        for (Monster target : monsters) {
            if (target != null && target.getCurrentHealth() > 0) {
                int monsterLeft = target.getWorldX() + target.getSolidArea().x;
                int monsterRight = monsterLeft + target.getSolidArea().width;
                int monsterTop = target.getWorldY() + target.getSolidArea().y;
                int monsterBottom = monsterTop + target.getSolidArea().height;

                int closestX = Math.max(monsterLeft, Math.min(centerX, monsterRight));
                int closestY = Math.max(monsterTop, Math.min(centerY, monsterBottom));

                double distance = Math.sqrt(Math.pow(centerX - closestX, 2) + Math.pow(centerY - closestY, 2));

                if (distance <= radius) {
                    attackAOE(caster, target, aoeDamage);
                }
            }
        }
    }

    public void attackAOE(CombatableCharacter caster, CombatableCharacter target, int aoeDamage) {
        if (target != null && target.getCurrentHealth() > 0 && caster != null) {
            // Gọi receiveDamage và LƯU LẠI sát thương thực tế đã gây ra
            int actualDamageDealt = target.receiveDamage(aoeDamage, caster);

            // Hiển thị thông báo với lượng sát thương thực tế
            if (actualDamageDealt > 0) { // Chỉ hiện thông báo nếu có sát thương
                gp.getUi().showMessage(caster.getName() + " hits " + target.getName() + " for " + actualDamageDealt + " damage!");
            }
        }
    }

    public void checkSingleAttack(Projectile projectile) {
        // Chỉ xử lý nếu projectile còn tồn tại và được bắn bởi Player
        if (projectile == null || !projectile.isAlive() || !(projectile.getCaster() instanceof Player)) {
            return;
        }

        Rectangle projectileBounds = new Rectangle(
                projectile.getWorldX() + projectile.getSolidArea().x,
                projectile.getWorldY() + projectile.getSolidArea().y,
                projectile.getSolidArea().width,
                projectile.getSolidArea().height
        );

        List<Monster> monsters = gp.getCurrentMap().getMonster();
        for (Monster monster : monsters) {
            if (monster != null && monster.getCurrentHealth() > 0) {
                Rectangle monsterBounds = new Rectangle(
                        monster.getWorldX() + monster.getSolidArea().x,
                        monster.getWorldY() + monster.getSolidArea().y,
                        monster.getSolidArea().width,
                        monster.getSolidArea().height
                );

                if (projectileBounds.intersects(monsterBounds)) {
                    int actualDamageDealt = monster.receiveDamage(projectile.getDamageValue(), projectile.getCaster());
                    gp.getUi().showMessage(projectile.getCaster().getName() + " hits " + monster.getName() + " for " + actualDamageDealt + " damage!");

                    gp.playSoundEffect(Sound.SFX_FIREBALL_HIT);

                    if (projectile.isSingleHit()) {
                        projectile.setAlive(false);
                    }
                    return; // Thoát ngay khi trúng
                }
            }
        }
    }
}