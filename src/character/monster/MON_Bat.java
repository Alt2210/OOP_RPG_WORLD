package character.monster;

import character.CombatableCharacter;
import character.role.Player;
import main.GamePanel;
import pathfinder.Node;
import pathfinder.PathFinder;
import worldObject.pickableObject.OBJ_HealthPotion;
import worldObject.pickableObject.OBJ_ManaPotion;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MON_Bat extends Monster {
    private int dashCounter;
    private final int DASH_INTERVAL = 240;
    private final int DASH_SPEED = 14;
    private final int DASH_DURATION_MAX = 20;
    private boolean isDashing;
    private int dashDurationCounter;
    private String lockedDashDirection;
    private PathFinder pathFinder;

    public MON_Bat(GamePanel gp) {
        super(gp);
        this.pathFinder = new PathFinder(gp);
        cip.setNumSprite(10);
        direction = "down";
        isDashing = false;
        lockedDashDirection = direction;
        contactDamageAmount = 4;
        setDefaultValues();
    }

    public boolean isDashing() {
        return isDashing;
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 25;
        worldY = gp.getTileSize() * 25;
        direction = "down";
        setName("Bat");
        defaultSpeed = 3;
        speed = defaultSpeed;
        maxHealth = 50;
        currentHealth = maxHealth;
        attack = 4;
        defense = 0;
        exp = 2;
        coinValue = 50;
        attackRange = 10;
        ATTACK_COOLDOWN_DURATION = 60;
        contactDamageAmount = attack;
        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        cip.getImage("/monster", "bat");
    }

    private String getDirectionToTarget(CombatableCharacter target) {
        if (target == null || target.getCurrentHealth() <= 0) return this.direction;
        int dx = target.getCenterX() - this.getCenterX();
        int dy = target.getCenterY() - this.getCenterY();
        double angle = Math.atan2(dy, dx) * 180 / Math.PI;
        if (angle >= -45 && angle < 45) return "right";
        if (angle >= 45 && angle < 135) return "down";
        if (angle >= 135 || angle < -135) return "left";
        return "up";
    }

    protected void onDeath(CombatableCharacter attacker) {
        super.onDeath(attacker);

        int i = new Random().nextInt(100) + 1;

        // SET THE MONSTER DROP
        if (i <= 30) {
            dropItem(new OBJ_HealthPotion(gp));
        }
        // từ 21~79 không drop ra gì
        if (i >= 70 && i < 100) {
            dropItem(new OBJ_ManaPotion(gp));
        }

        gp.getUi().showMessage(attacker.getName() + " đã đánh bại " + getName() + "!");
    }

    public void flyAttack() {
        updateContactDamageCooldown();
        Player player = gp.getPlayer();
        if (player == null || player.getCurrentHealth() <= 0) {
            isDashing = false;
            speed = defaultSpeed;
            return;
        }

        if (isDashing) {
            dashDurationCounter++;
            if (dashDurationCounter >= DASH_DURATION_MAX) {
                isDashing = false;
                speed = defaultSpeed;
            }
        } else {
            dashCounter++;
            if (dashCounter >= DASH_INTERVAL) {
                isDashing = true;
                dashDurationCounter = 0;
                lockedDashDirection = getDirectionToTarget(player);
                this.direction = lockedDashDirection;
                speed = DASH_SPEED;
                dashCounter = 0;
                System.out.println("MON_Bat started dashing, direction: " + lockedDashDirection);
            }
        }
    }

    public void playerChasing() {
        if (onPath) {
            checkStopChasingOrNot(gp.getPlayer(), 15, 10);
            int monsterCenterX = worldX + solidArea.x + solidArea.width / 2;
            int monsterCenterY = worldY + solidArea.y + solidArea.height / 2;
            int currentMonsterCol = monsterCenterX / gp.getTileSize();
            int currentMonsterRow = monsterCenterY / gp.getTileSize();

            Player player = gp.getPlayer();
            if (player == null || player.getCurrentHealth() <= 0) {
                onPath = false;
                return;
            }
            int playerCenterX = player.getWorldX() + player.getSolidArea().x + player.getSolidArea().width / 2;
            int playerCenterY = player.getWorldY() + player.getSolidArea().y + player.getSolidArea().height / 2;
            int goalCol = playerCenterX / gp.getTileSize();
            int goalRow = playerCenterY / gp.getTileSize();

            currentMonsterCol = Math.max(0, Math.min(currentMonsterCol, gp.getMaxWorldCol() - 1));
            currentMonsterRow = Math.max(0, Math.min(currentMonsterRow, gp.getMaxWorldRow() - 1));
            goalCol = Math.max(0, Math.min(goalCol, gp.getMaxWorldCol() - 1));
            goalRow = Math.max(0, Math.min(goalRow, gp.getMaxWorldRow() - 1));

            pathFinder.setNodes(currentMonsterCol, currentMonsterRow, goalCol, goalRow, this);
            if (pathFinder.search() && !pathFinder.getPathList().isEmpty()) {
                Node nextNode = pathFinder.getPathList().getFirst();
                int nextX = nextNode.getCol() * gp.getTileSize();
                int nextY = nextNode.getRow() * gp.getTileSize();

                if (worldY > nextY && worldX == nextX) direction = "up";
                else if (worldY < nextY && worldX == nextX) direction = "down";
                else if (worldX > nextX && worldY == nextY) direction = "left";
                else if (worldX < nextX && worldY == nextY) direction = "right";
            } else {
                onPath = false;
                getRandomDirection(120);
            }
        } else {
            checkStartChasingOrNot(gp.getPlayer(), 5, 100);
            getRandomDirection(120);
        }
    }

    @Override
    public void update() {
        try {
            // 1. Logic quyết định hành động
            flyAttack(); // Cập nhật trạng thái lướt, tốc độ và hướng
            if (!isDashing) {
                playerChasing(); // Cập nhật hướng khi đang đuổi theo
            }

            // 2. Gọi logic update của lớp Monster cha
            // Monster.update() giờ đây sẽ xử lý tất cả các va chạm (tile, item, player...)
            // và thực hiện di chuyển nếu không có va chạm.
            super.update();

            // 3. Xử lý logic sau khi va chạm đã được phát hiện
            // Nếu đang lướt và cờ collisionOn được bật lên (do va chạm tường), dừng lướt.
            if (collisionOn && isDashing) {
                isDashing = false;
                speed = defaultSpeed; // Trả lại tốc độ bình thường
                dashCounter = DASH_INTERVAL - 30; // Chờ một chút trước khi thử lướt lại
            }
        } catch (Exception e) {
            System.err.println("Error updating MON_Bat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        try {
            BufferedImage image = cip.getCurFrame();
            if (image == null) {
                System.err.println("No sprite for MON_Bat at (" + worldX + ", " + worldY + ")");
                return;
            }
            int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
            int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();
            if (worldX + gp.getTileSize() > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() &&
                    worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
                    worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
                    worldY - gp.getTileSize() < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) {
                if (isDashing) {
                    float[] opacities = {0.5f, 0.3f};
                    int[] offsets = {10, 20};
                    for (int i = 0; i < opacities.length; i++) {
                        int offsetX = 0, offsetY = 0;
                        switch (lockedDashDirection) {
                            case "up": offsetY = offsets[i]; break;
                            case "down": offsetY = -offsets[i]; break;
                            case "left": offsetX = offsets[i]; break;
                            case "right": offsetX = -offsets[i]; break;
                        }
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacities[i]));
                        g2.drawImage(image, screenX + offsetX, screenY + offsetY, gp.getTileSize(), gp.getTileSize(), null);
                    }
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
                drawHealthBar(g2, screenX, screenY);
            }
        } catch (Exception e) {
            System.err.println("Error drawing MON_Bat: " + e.getMessage());
            e.printStackTrace();
        }
    }
}