package character.monster;

import character.Character;
import character.Player;
import main.GamePanel;
import ai.Node;
import ai.PathFinder;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MON_GolemBoss extends Monster {
    private PathFinder pathFinder;
    private boolean isChargingLaser;
    private boolean isFiringLaser;
    private int laserChargeCounter;
    private final int LASER_CHARGE_DURATION = 60;
    private final int CHARGING_PHASE_DURATION = 30;
    private int laserAttackCounter;
    private final int LASER_ATTACK_INTERVAL = 300;
    private Rectangle laserHitbox;
    private final int BOSS_SIZE_MULTIPLIER = 3; // Tăng kích thước boss lên 3x3 ô

    public MON_GolemBoss(GamePanel gp) {
        super(gp);
        this.pathFinder = new PathFinder(gp);
        cip.setNumSprite(7);
        direction = "down";
        speed = 1;
        isChargingLaser = false;
        isFiringLaser = false;
        laserChargeCounter = 0;
        laserAttackCounter = 0;
        laserHitbox = new Rectangle();
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 30;
        worldY = gp.getTileSize() * 30;
        direction = "right";
        setName("GolemBoss");
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxHealth = 200;
        currentHealth = maxHealth;
        attack = 8;
        defense = 2;
        exp = 50;
        attackRange = gp.getTileSize() * 3;
        ATTACK_COOLDOWN_DURATION = 60;
        contactDamageAmount = attack;
        // Tăng kích thước solidArea để bao phủ 3x3 ô
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = gp.getTileSize() * BOSS_SIZE_MULTIPLIER;
        solidArea.height = gp.getTileSize() * BOSS_SIZE_MULTIPLIER;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        cip.getImage("/monster", "golemboss");
    }

    public boolean isChargingLaser() {
        return isChargingLaser;
    }

    private String getDirectionToTarget(Character target) {
        if (target == null || target.getCurrentHealth() <= 0) return this.direction;
        int dx = target.getCenterX() - this.getCenterX();
        int dy = target.getCenterY() - this.getCenterY();
        double angle = Math.atan2(dy, dx) * 180 / Math.PI;
        if (angle >= -45 && angle < 45) return "right";
        if (angle >= 135 || angle < -135) return "left";
        return this.direction;
    }

    public void playerChasing() {
        if (onPath && !isChargingLaser) {
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
            int playerCenterX = player.worldX + player.solidArea.x + player.solidArea.width / 2;
            int playerCenterY = player.worldY + player.solidArea.y + player.solidArea.height / 2;
            int goalCol = playerCenterX / gp.getTileSize();
            int goalRow = playerCenterY / gp.getTileSize();

            currentMonsterCol = Math.max(0, Math.min(currentMonsterCol, gp.getMaxWorldCol() - 1));
            currentMonsterRow = Math.max(0, Math.min(currentMonsterRow, gp.getMaxWorldRow() - 1));
            goalCol = Math.max(0, Math.min(goalCol, gp.getMaxWorldCol() - 1));
            goalRow = Math.max(0, Math.min(goalRow, gp.getMaxWorldRow() - 1));

            pathFinder.setNodes(currentMonsterCol, currentMonsterRow, goalCol, goalRow, this);
            if (pathFinder.search() && !pathFinder.pathList.isEmpty()) {
                Node nextNode = pathFinder.pathList.getFirst();
                int nextX = nextNode.col * gp.getTileSize();
                int nextY = nextNode.row * gp.getTileSize();

                if (worldY > nextY && worldX == nextX) direction = "up";
                else if (worldY < nextY && worldX == nextX) direction = "down";
                else if (worldX > nextX && worldY == nextY) direction = "left";
                else if (worldX < nextX && worldY == nextY) direction = "right";
            } else {
                onPath = false;
                getRandomDirection(120);
            }
        } else if (!isChargingLaser) {
            checkStartChasingOrNot(gp.getPlayer(), 5, 100);
            getRandomDirection(120);
        }
    }

    private void updateLaserHitbox() {
        int tileSize = gp.getTileSize();
        int laserLength = tileSize * 7; // Tăng chiều dài lên 7 ô
        int laserWidth = tileSize * 2; // Tăng chiều rộng lên 2 ô
        int centerX = worldX + solidArea.x + solidArea.width / 2;
        int centerY = worldY + solidArea.y + solidArea.height / 2;

        switch (direction) {
            case "up":
                laserHitbox.setBounds(centerX - laserWidth / 2, centerY - laserLength, laserWidth, laserLength);
                break;
            case "down":
                laserHitbox.setBounds(centerX - laserWidth / 2, centerY, laserWidth, laserLength);
                break;
            case "left":
                laserHitbox.setBounds(centerX - laserLength, centerY - laserWidth / 2, laserLength, laserWidth);
                break;
            case "right":
                laserHitbox.setBounds(centerX, centerY - laserWidth / 2, laserLength, laserWidth);
                break;
        }
    }

    private void attemptLaserAttack() {
        laserAttackCounter++;
        if (laserAttackCounter >= LASER_ATTACK_INTERVAL) {
            if (!isChargingLaser) {
                isChargingLaser = true;
                laserChargeCounter = 0;
                direction = getDirectionToTarget(gp.getPlayer());
                System.out.println("GolemBoss started charging laser, direction: " + direction);
            }
        }

        if (isChargingLaser) {
            laserChargeCounter++;
            isFiringLaser = laserChargeCounter >= CHARGING_PHASE_DURATION;

            if (isFiringLaser) {
                updateLaserHitbox();
                Player player = gp.getPlayer();
                if (player != null && player.getCurrentHealth() > 0) {
                    Rectangle playerBounds = new Rectangle(
                            player.worldX + player.solidArea.x,
                            player.worldY + player.solidArea.y,
                            player.solidArea.width,
                            player.solidArea.height
                    );
                    if (laserHitbox.intersects(playerBounds)) {
                        player.receiveDamage(attack, this);
                        System.out.println("Player took " + attack + " damage from GolemBoss laser");
                    }
                }
            }

            if (laserChargeCounter >= LASER_CHARGE_DURATION) {
                isChargingLaser = false;
                isFiringLaser = false;
                laserAttackCounter = 0;
                System.out.println("GolemBoss finished laser attack");
            }
        }
    }

    @Override
    public void update() {
        try {
            if (isChargingLaser) {
                cip.setNumSprite(7);
            } else {
                cip.setNumSprite(4);
                playerChasing();
            }

            attemptLaserAttack();

            collisionOn = false;
            gp.getcChecker().checkTile(this);
            gp.getcChecker().checkPlayer(this);

            if (!collisionOn && !isChargingLaser) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }

            cip.update();
        } catch (Exception e) {
            System.err.println("Error updating MON_GolemBoss: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        try {
            BufferedImage image = cip.getCurFrame();
            if (image == null) {
                System.err.println("No sprite for MON_GolemBoss at (" + worldX + ", " + worldY + ")");
                return;
            }
            int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
            int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;
            if (worldX + (gp.getTileSize() * BOSS_SIZE_MULTIPLIER) > gp.getPlayer().worldX - gp.getPlayer().screenX &&
                    worldX - (gp.getTileSize() * BOSS_SIZE_MULTIPLIER) < gp.getPlayer().worldX + gp.getPlayer().screenX &&
                    worldY + (gp.getTileSize() * BOSS_SIZE_MULTIPLIER) > gp.getPlayer().worldY - gp.getPlayer().screenY &&
                    worldY - (gp.getTileSize() * BOSS_SIZE_MULTIPLIER) < gp.getPlayer().worldY + gp.getPlayer().screenY) {
                // Vẽ boss với kích thước 3x3 ô
                int bossSize = gp.getTileSize() * BOSS_SIZE_MULTIPLIER;
                g2.drawImage(image, screenX, screenY, bossSize, bossSize, null);
                drawHealthBar(g2, screenX, screenY);

                // Vẽ cầu năng lượng hoặc tia laser
                if (isChargingLaser) {
                    int tileSize = gp.getTileSize();
                    int laserLength = tileSize * 7; // Tăng chiều dài lên 7 ô
                    int laserWidth = tileSize * 2; // Tăng chiều rộng lên 2 ô
                    int centerX = screenX + solidArea.x + solidArea.width / 2;
                    int centerY = screenY + solidArea.y + solidArea.height / 2;

                    if (isFiringLaser) {
                        // Giai đoạn bắn tia laser: vẽ tia laser dài
                        BufferedImage laserFrame = cip.getLaserFrame(direction);
                        if (laserFrame != null) {
                            if (direction.equals("right")) {
                                for (int i = 0; i < laserLength / tileSize; i++) {
                                    int laserX = centerX + (i * tileSize);
                                    int laserY = centerY - laserWidth / 2;
                                    g2.drawImage(laserFrame, laserX, laserY, tileSize, laserWidth, null);
                                }
                            } else if (direction.equals("left")) {
                                for (int i = 0; i < laserLength / tileSize; i++) {
                                    int laserX = centerX - ((i + 1) * tileSize);
                                    int laserY = centerY - laserWidth / 2;
                                    g2.drawImage(laserFrame, laserX, laserY, tileSize, laserWidth, null);
                                }
                            } else if (direction.equals("up")) {
                                for (int i = 0; i < laserLength / tileSize; i++) {
                                    int laserX = centerX - laserWidth / 2;
                                    int laserY = centerY - ((i + 1) * tileSize);
                                    g2.drawImage(laserFrame, laserX, laserY, laserWidth, tileSize, null);
                                }
                            } else if (direction.equals("down")) {
                                for (int i = 0; i < laserLength / tileSize; i++) {
                                    int laserX = centerX - laserWidth / 2;
                                    int laserY = centerY + (i * tileSize);
                                    g2.drawImage(laserFrame, laserX, laserY, laserWidth, tileSize, null);
                                }
                            }
                        }

                        // Vẽ hitbox để debug (có thể xóa sau khi hoàn thiện)
                        g2.setColor(Color.RED);
                        g2.drawRect(
                                (int) (laserHitbox.x - gp.getPlayer().worldX + gp.getPlayer().screenX),
                                (int) (laserHitbox.y - gp.getPlayer().worldY + gp.getPlayer().screenY),
                                laserHitbox.width,
                                laserHitbox.height
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error drawing MON_GolemBoss: " + e.getMessage());
            e.printStackTrace();
        }
    }
}