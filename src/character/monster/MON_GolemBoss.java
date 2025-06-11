package character.monster;

import character.Character;
import character.role.Player;
import main.GamePanel;
import pathfinder.Node;
import pathfinder.PathFinder;
import skillEffect.projectile.GolemArmProjectile;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MON_GolemBoss extends Monster {
    private PathFinder pathFinder;
    private boolean isChargingLaser;
    private boolean isFiringLaser;
    private int laserChargeCounter;
    private final int LASER_CHARGE_DURATION = 120;
    private final int CHARGING_PHASE_DURATION = 30;
    private int laserAttackCounter;
    private final int LASER_ATTACK_INTERVAL = 600;
    private Rectangle laserHitbox;
    private final int BOSS_SIZE_MULTIPLIER = 3;

    // Biến cho chiêu thức bắn cánh tay
    private boolean isChargingArmShot;
    private boolean isFiringArmShot;
    private int armShotCounter;
    private final int ARM_SHOT_CHARGE_DURATION = 40; // 60 frame để "tháo cánh tay"
    private final int ARM_SHOT_FIRE_DURATION = 30;  // 30 frame để bắn
    private final int ARM_SHOT_INTERVAL = 180;      // Thời gian giữa các lần bắn
    private int armShotIntervalCounter = 0;
    private GolemArmProjectile armProjectile;

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
        worldX = gp.getTileSize() * 21;
        worldY = gp.getTileSize() * 21;
        direction = "right";
        setName("GolemBoss");
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxHealth = 500;
        currentHealth = maxHealth;
        attack = 8;
        defense = 2;
        exp = 50;
        attackRange = gp.getTileSize() * 3;
        ATTACK_COOLDOWN_DURATION = 600;
        contactDamageAmount = attack;
        solidArea.width = gp.getTileSize() * 2;
        solidArea.height = gp.getTileSize() * 2;
        solidArea.x = (gp.getTileSize() * 3 - solidArea.width) / 2;
        solidArea.y = (gp.getTileSize() * 3 - solidArea.height) / 2;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        cip.getImage("/monster", "golemboss");
    }

    public boolean isChargingLaser() {
        return isChargingLaser;
    }

    public boolean isChargingArmShot() {
        return isChargingArmShot;
    }

    private String getDirectionToTarget(Character target) {
        if (target == null || target.getCurrentHealth() <= 0) return this.direction;
        int dx = target.getCenterX() - this.getCenterX();
        int dy = target.getCenterY() - this.getCenterY();
        double angle = Math.atan2(dy, dx) * 180 / Math.PI;

        if (angle >= -45 && angle < 45) return "right";
        else if (angle >= 45 && angle < 135) return "down";
        else if (angle >= -135 && angle < -45) return "up";
        else return "left";
    }

    @Override
    protected void onDeath(Character attacker) {
        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            player.gainExp(this.exp); // this.exp đã có sẵn trong lớp Monster
        }

        //checkDrop();
        gp.getUi().showMessage(attacker.getName() + " đã đánh bại " + getName() + "!");
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
            int playerCenterX = player.getWorldX() + player.getSolidArea().x + player.getSolidArea().width / 2;
            int playerCenterY = player.getWorldY() + player.getSolidArea().y + player.getSolidArea().height / 2;
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
        int laserLength = 261 * 4;
        int laserWidth = 26 * 4;

        int leftEdge = worldX + solidArea.x;
        int rightEdge = leftEdge + solidArea.width;
        int topEdge = worldY + solidArea.y;
        int bottomEdge = topEdge + solidArea.height;

        switch (direction) {
            case "up":
                laserHitbox.setBounds(
                        leftEdge + (solidArea.width - laserWidth) / 2,
                        topEdge - laserLength,
                        laserWidth,
                        laserLength
                );
                break;
            case "down":
                laserHitbox.setBounds(
                        leftEdge + (solidArea.width - laserWidth) / 2,
                        bottomEdge,
                        laserWidth,
                        laserLength
                );
                break;
            case "left":
                laserHitbox.setBounds(
                        leftEdge - laserLength,
                        topEdge + (solidArea.height - laserWidth) / 2,
                        laserLength,
                        laserWidth
                );
                break;
            case "right":
                laserHitbox.setBounds(
                        rightEdge,
                        topEdge + (solidArea.height - laserWidth) / 2,
                        laserLength,
                        laserWidth
                );
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
                            player.getWorldX() + player.getSolidArea().x,
                            player.getWorldY() + player.getSolidArea().y,
                            player.getSolidArea().width,
                            player.getSolidArea().height
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

    private void attemptArmShotAttack() {
        armShotIntervalCounter++;
        if (armShotIntervalCounter >= ARM_SHOT_INTERVAL && !isChargingLaser && !isFiringLaser) {
            isChargingArmShot = true;
            armShotCounter = 0;
            armShotIntervalCounter = 0;
            direction = getDirectionToTarget(gp.getPlayer());
            System.out.println("GolemBoss started charging arm shot, direction: " + direction);
        }
        if (isChargingArmShot) {
            armShotCounter++;
            if (armShotCounter >= ARM_SHOT_CHARGE_DURATION) {
                isChargingArmShot = false;
                isFiringArmShot = true;
                armShotCounter = 0;
                armProjectile = new GolemArmProjectile(gp);
                int startX = worldX + solidArea.x + solidArea.width / 2;
                int startY = worldY + solidArea.y + solidArea.height / 2;
                // Thêm offset
                int offsetDistance = gp.getTileSize() / 2; // ~24 pixel
                switch (direction) {
                    case "up": startY -= offsetDistance; break;
                    case "down": startY += offsetDistance; break;
                    case "left": startX -= offsetDistance; break;
                    case "right": startX += offsetDistance; break;
                }
                armProjectile.set(startX, startY, direction, this, attack);
                gp.skillEffects.add(armProjectile); // Thêm vào skillEffects
                System.out.println("GolemBoss fired arm projectile at (" + startX + ", " + startY + "), direction: " + direction);
            }
        }
        if (isFiringArmShot) {
            armShotCounter++;
            if (armShotCounter >= ARM_SHOT_FIRE_DURATION) {
                isFiringArmShot = false;
                System.out.println("GolemBoss finished arm shot attack");
            }
        }
        // Không cần tự cập nhật armProjectile vì GamePanel sẽ xử lý
        // if (armProjectile != null && armProjectile.isAlive()) {
        //     armProjectile.update();
        // }
    }

    @Override
    public void update() {
        if (!isChargingLaser && !isChargingArmShot) {
            direction = getDirectionToTarget(gp.getPlayer());
        }

        try {
            if (isChargingLaser) {
                cip.setNumSprite(7);
            } else if (isChargingArmShot) {
                cip.setNumSprite(9); // 9 khung hình cho hoạt ảnh "tháo cánh tay"
            } else {
                cip.setNumSprite(4);
                playerChasing();
            }

            attemptLaserAttack();
            attemptArmShotAttack();

            collisionOn = false;
            gp.getcChecker().checkTile(this);
            gp.getcChecker().checkPlayer(this);

            if (!collisionOn && !isChargingLaser && !isChargingArmShot) {
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
            int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
            int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

            // Lấy kích thước thực tế của hoạt ảnh
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            int scaledWidth = originalWidth * BOSS_SIZE_MULTIPLIER;
            int scaledHeight = originalHeight * BOSS_SIZE_MULTIPLIER;

            // Căn giữa dựa trên kích thước thực tế trong khung 144x144 pixel
            int offsetX = (gp.getTileSize() * BOSS_SIZE_MULTIPLIER - scaledWidth) / 2;
            int offsetY = (gp.getTileSize() * BOSS_SIZE_MULTIPLIER - scaledHeight) / 2;

            if (worldX + (gp.getTileSize() * BOSS_SIZE_MULTIPLIER) > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() &&
                    worldX - (gp.getTileSize() * BOSS_SIZE_MULTIPLIER) < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
                    worldY + (gp.getTileSize() * BOSS_SIZE_MULTIPLIER) > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
                    worldY - (gp.getTileSize() * BOSS_SIZE_MULTIPLIER) < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) {
                g2.drawImage(image, screenX + offsetX, screenY + offsetY, scaledWidth, scaledHeight, null);
                drawHealthBar(g2, screenX, screenY);

                if (isChargingLaser && isFiringLaser) {
                    BufferedImage laserFrame = cip.getLaserFrame(direction);
                    if (laserFrame != null) {
                        int laserImageWidth = 26 * 4;
                        int laserImageHeight = 261 * 4;
                        int drawX = (int) (laserHitbox.x - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX());
                        int drawY = (int) (laserHitbox.y - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY());
                        switch (direction) {
                            case "up":
                                drawX += (laserHitbox.width - laserImageWidth) / 2;
                                drawY += (laserHitbox.height - laserImageHeight);
                                g2.drawImage(laserFrame, drawX, drawY, laserImageWidth, laserImageHeight, null);
                                break;
                            case "down":
                                drawX += (laserHitbox.width - laserImageWidth) / 2;
                                g2.drawImage(laserFrame, drawX, drawY, laserImageWidth, laserImageHeight, null);
                                break;
                            case "left":
                                drawY += (laserHitbox.height - laserImageWidth) / 2;
                                drawX += (laserHitbox.width - laserImageHeight);
                                g2.drawImage(laserFrame, drawX, drawY, laserImageHeight, laserImageWidth, null);
                                break;
                            case "right":
                                drawY += (laserHitbox.height - laserImageWidth) / 2;
                                g2.drawImage(laserFrame, drawX, drawY, laserImageHeight, laserImageWidth, null);
                                break;
                        }
                    }

                    g2.setColor(Color.RED);
                    g2.drawRect(
                            (int) (laserHitbox.x - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX()),
                            (int) (laserHitbox.y - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()),
                            laserHitbox.width,
                            laserHitbox.height
                    );
                }

                // Vẽ cánh tay skillEffect.projectile
                if (armProjectile != null && armProjectile.isAlive()) {
                    armProjectile.draw(g2);
                }
            }
        } catch (Exception e) {
            System.err.println("Error drawing MON_GolemBoss: " + e.getMessage());
            e.printStackTrace();
        }
    }
}