package character.Role;

import character.monster.Monster;
import main.GamePanel;
import main.KeyHandler;
import projectile.Fireball;
import sound.Sound;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Soldier extends Player {

    // Thuộc tính riêng của Soldier: kỹ năng Fireball
    private int fireballManaCost;
    private int fireballCooldown;
    private final int FIREBALL_COOLDOWN_DURATION = 60;

    public Soldier(GamePanel gp, KeyHandler keyH) {
        super(gp, keyH);
        this.characterClassIdentifier = "sodier";
        setDefaultValues();
    }


    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 10;
        worldY = gp.getTileSize() * 20;
        speed = 4;
        direction = "down";
        hasKey=0;
        maxHealth = 120;
        currentHealth = maxHealth;
        attack = 10;
        defense = 3;
        attackRange = gp.getTileSize();
        maxMana = 50;
        currentMana = maxMana;
        this.ATTACK_COOLDOWN_DURATION = 45;
        this.currentAttackStateDuration = 30;

        // Đặt giá trị cho kỹ năng Fireball
        this.fireballManaCost = 10;
        this.fireballCooldown = 0;

        setName("sodier");
        loadCharacterSprites();
    }

    @Override
    protected void loadCharacterSprites() {
        if (cip != null) {
            cip.setNumSprite(5); // Đi bộ 5 frame
            cip.getImage("/player", this.characterClassIdentifier);
        }
    }

    @Override
    protected void performNormalAttackAction() {
        this.attackStateCounter = this.currentAttackStateDuration;
        resetAttackCooldown();
        this.attackDamageAppliedThisSwing = false;
        if (cip != null) {
            cip.setNumSprite(6); // Tấn công 6 frame
        }

        System.out.println("Soldier attacks! Direction: " + direction);
    }
    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = cip.getCurFrame();
        if (image == null) {
            // Vẽ một hình placeholder nếu không có ảnh, để dễ dàng debug
            g2.setColor(Color.RED);
            g2.fillRect(screenX, screenY, gp.getTileSize(), gp.getTileSize());
            return;
        }

        // Lấy kích thước gốc của frame ảnh
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // Scale ảnh theo đúng tỷ lệ gốc để không bị méo
        int drawWidth = originalWidth * gp.getScale();
        int drawHeight = originalHeight * gp.getScale();

        // Neo vị trí vẽ để chân nhân vật ổn định và căn giữa theo chiều ngang
        // Căn giữa theo chiều ngang so với ô tile logic
        int drawX = screenX + (gp.getTileSize() - drawWidth) / 2;
        // Neo đáy của ảnh vào đáy của ô tile. Giúp nhân vật không "bay" lên.
        int drawY = screenY + (gp.getTileSize() - drawHeight);

        // Vẽ ảnh với kích thước và vị trí đã tính toán
        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);

        // Vẽ thanh máu dựa trên vị trí gốc
        drawHealthBar(g2, screenX, screenY);
    }
    @Override
    public void update() {
        super.update(); // Gọi Player.update() để xử lý input và logic chung

        // Giảm cooldown cho Fireball
        if (fireballCooldown > 0) {
            fireballCooldown--;
        }


        if (!isAttacking() && cip != null && "sodier".equals(this.characterClassIdentifier)) {
            if(cip.getNumSprite() != 5) {
                cip.setNumSprite(5);
            }
        }
    }

    @Override
    protected void handleSkillInputs() {
        // Xử lý input cho kỹ năng Fireball của riêng Soldier
        if (keyH.skill1Pressed && fireballCooldown == 0 && currentMana >= fireballManaCost && !isAttacking()) {
            castFireball();
        }
    }

    private void castFireball() {
        this.attackStateCounter = 15;

        spendMana(fireballManaCost);
        fireballCooldown = FIREBALL_COOLDOWN_DURATION;
        gp.getUi().showMessage("Fireball! -" + fireballManaCost + " MP");
        gp.playSoundEffect(Sound.SFX_FIREBALL_SHOOT);

        Fireball fireball = new Fireball(gp);
        int playerSolidCenterX = worldX + solidArea.x + solidArea.width / 2;
        int playerSolidCenterY = worldY + solidArea.y + solidArea.height / 2;
        // ... (Logic tính toán vị trí spawn của fireball như cũ) ...
        int fireballHitboxWidth = fireball.solidArea.width;
        int fireballHitboxHeight = fireball.solidArea.height;
        int fireballSpawnCenterX = playerSolidCenterX;
        int fireballSpawnCenterY = playerSolidCenterY;
        int gap = 2;
        int offsetX = solidArea.width / 2 + fireballHitboxWidth / 2 + gap;
        int offsetY = solidArea.height / 2 + fireballHitboxHeight / 2 + gap;
        switch (direction) {
            case "up": fireballSpawnCenterY -= offsetY; break;
            case "down": fireballSpawnCenterY += offsetY; break;
            case "left": fireballSpawnCenterX -= offsetX; break;
            case "right": fireballSpawnCenterX += offsetX; break;
        }
        fireball.set(fireballSpawnCenterX, fireballSpawnCenterY, this.direction, this, this.attack * 2);
        gp.projectiles.add(fireball);
    }
    @Override
    public void checkAttack() {
        // Tạo một vùng hitbox cho đòn tấn công của Soldier
        Rectangle attackArea = new Rectangle();
        int attackAreaWidth = 36;
        int attackAreaHeight = 36;

        // Căn chỉnh hitbox dựa trên hướng của người chơi
        int solidAreaCenterX = worldX + solidArea.x + solidArea.width / 2;
        int solidAreaCenterY = worldY + solidArea.y + solidArea.height / 2;

        switch (direction) {
            case "up":
                attackArea.setBounds(solidAreaCenterX - attackAreaWidth / 2, worldY + solidArea.y - attackAreaHeight, attackAreaWidth, attackAreaHeight);
                break;
            case "down":
                attackArea.setBounds(solidAreaCenterX - attackAreaWidth / 2, worldY + solidArea.y + solidArea.height, attackAreaWidth, attackAreaHeight);
                break;
            case "left":
                attackArea.setBounds(worldX + solidArea.x - attackAreaWidth, solidAreaCenterY - attackAreaHeight / 2, attackAreaWidth, attackAreaHeight);
                break;
            case "right":
                attackArea.setBounds(worldX + solidArea.x + solidArea.width, solidAreaCenterY - attackAreaHeight / 2, attackAreaWidth, attackAreaHeight);
                break;
        }

        // Tạo một danh sách các mảng quái vật để kiểm tra
        Monster[][] allMonsters = {
                gp.getMON_GreenSlime(),
                gp.getMON_Bat(),
                gp.getMON_Orc(),
                gp.getMON_GolemBoss()
        };

        // Lặp qua từng loại quái vật
        for (Monster[] monsterArray : allMonsters) {
            if (monsterArray == null) continue;

            for (int i = 0; i < monsterArray.length; i++) {
                Monster monster = monsterArray[i];
                if (monster != null && monster.getCurrentHealth() > 0) {
                    // Lấy hitbox của quái vật
                    Rectangle monsterHitbox = new Rectangle(
                            monster.worldX + monster.solidArea.x,
                            monster.worldY + monster.solidArea.y,
                            monster.solidArea.width,
                            monster.solidArea.height
                    );

                    // Kiểm tra va chạm giữa hitbox tấn công và hitbox của quái vật
                    if (attackArea.intersects(monsterHitbox)) {
                        System.out.println("Soldier hit " + monster.getName()); // Dòng gỡ lỗi
                        // Gọi CombatSystem để xử lý sát thương
                        gp.getCombatSystem().performAttack(this, monster);
                    }
                }
            }
        }
    }
    @Override
    protected void drawCharacterSpecifics(Graphics2D g2) {
     }
}