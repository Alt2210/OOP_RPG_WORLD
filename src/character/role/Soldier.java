package character.role;

import character.monster.Monster;
import main.GamePanel;
import main.KeyHandler;
import skill.*;
import sound.Sound;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Soldier extends Player {

    public Soldier(GamePanel gp, KeyHandler keyH) {
        super(gp, keyH);
        this.characterClassIdentifier = "sodier";
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        setInitLocation();
        setInitStamina();
        defaultSpeed = 13;
        speed = defaultSpeed;
        direction = "down";
        hasKey = 0;
        maxHealth = 120;
        currentHealth = maxHealth;
        attack = 40;
        defense = 3;
        attackRange = gp.getTileSize();
        maxMana = 100;
        currentMana = maxMana;
        manaRegenSpeed = 1;
        this.ATTACK_COOLDOWN_DURATION = 45;
        this.currentAttackStateDuration = 30;


        setName("sodier");
        loadCharacterSprites();
        addSkill(new S_Fireball(this, gp));
        addSkill(new S_Explosion(this, gp));


        currentWeapon = null;

        setInitLevel();

        baseAttack = attack;
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
        gp.playSoundEffect(Sound.SFX_SWORD_SWING);
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


        if (!isAttacking() && cip != null && "sodier".equals(this.characterClassIdentifier)) {
            if(cip.getNumSprite() != 5) {
                cip.setNumSprite(5);
            }
        }
    }

    @Override
    public void checkAttack() {
        Rectangle attackArea = new Rectangle();
        int attackAreaWidth = 36;
        int attackAreaHeight = 36;

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

        // Lấy danh sách quái vật duy nhất từ GamePanel
        List<Monster> monsters =  gp.getCurrentMap().getMonster();

        // Lặp qua danh sách quái vật để kiểm tra va chạm
        for (Monster monster : monsters) {
            if (monster != null && monster.getCurrentHealth() > 0) {
                // Lấy hitbox của quái vật
                Rectangle monsterHitbox = new Rectangle(
                        monster.getWorldX() + monster.getSolidArea().x,
                        monster.getWorldY() + monster.getSolidArea().y,
                        monster.getSolidArea().width,
                        monster.getSolidArea().height
                );

                // Kiểm tra va chạm và gây sát thương
                if (attackArea.intersects(monsterHitbox)) {
                    gp.getCombatSystem().performAttack(this, monster);
                    // Lưu ý: Đòn đánh của Soldier có thể trúng nhiều mục tiêu
                    // nếu chúng đứng gần nhau. Nếu bạn muốn nó chỉ trúng 1 mục tiêu,
                    // hãy thêm 'break;' sau dòng performAttack.
                }
            }
        }
    }
    @Override
    protected void drawCharacterSpecifics(Graphics2D g2) {
     }
}