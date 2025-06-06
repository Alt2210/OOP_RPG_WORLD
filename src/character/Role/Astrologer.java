package character.Role;

import main.GamePanel;
import main.KeyHandler;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Astrologer extends Player {

    private ArrayList<BufferedImage> normalAttackEffectFrames;
    private boolean showNormalAttackEffect = false;
    private int normalAttackEffectAnimCounter = 0;
    private int normalAttackEffectSpriteNum = 0;
    private final int NORMAL_ATTACK_EFFECT_TOTAL_DURATION = 20;
    private final int NORMAL_ATTACK_EFFECT_FRAMES_COUNT = 4;
    private int normalAttackEffectFrameHoldTime;

    public Astrologer(GamePanel gp, KeyHandler keyH) {
        super(gp, keyH);
        this.characterClassIdentifier = "astrologist";
        this.normalAttackEffectFrames = new ArrayList<>();
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        // Đặt các chỉ số riêng của Astrologer
        worldX = gp.getTileSize() * 10;
        worldY = gp.getTileSize() * 20;
        speed = 20;
        direction = "down";
        hasKey = 0;
        maxHealth = 80;
        currentHealth = maxHealth;
        attack = 15;
        defense = 1;
        attackRange = gp.getTileSize() * 3;
        maxMana = 150;
        currentMana = maxMana;
        this.ATTACK_COOLDOWN_DURATION = 60;
        this.currentAttackStateDuration = NORMAL_ATTACK_EFFECT_TOTAL_DURATION;
        cip.setNumSprite(16);
        setName("Astrologer");
        loadCharacterSprites(); // Tải sprite và hiệu ứng

        // Tính toán thời gian giữ mỗi frame của animation hiệu ứng
        if (NORMAL_ATTACK_EFFECT_FRAMES_COUNT > 0 && !this.normalAttackEffectFrames.isEmpty()) {
            this.normalAttackEffectFrameHoldTime = NORMAL_ATTACK_EFFECT_TOTAL_DURATION / NORMAL_ATTACK_EFFECT_FRAMES_COUNT;
            if (this.normalAttackEffectFrameHoldTime <= 0) this.normalAttackEffectFrameHoldTime = 1;
        } else {
            this.normalAttackEffectFrameHoldTime = NORMAL_ATTACK_EFFECT_TOTAL_DURATION;
        }
    }

    @Override
    protected void loadCharacterSprites() {
        if (cip != null) {
            // Không cần setNumSprite ở đây nữa, vì logic lặp frame đã linh hoạt
            cip.getImage("/player", this.characterClassIdentifier);

            if (this.cip instanceof imageProcessor.CharacterImageProcessor) {
                imageProcessor.CharacterImageProcessor playerCip = (imageProcessor.CharacterImageProcessor) this.cip;
                // Sửa tên phương thức getter cho đúng với phiên bản CIP đã cập nhật
                this.normalAttackEffectFrames = playerCip.getLoadedDetachedAttackFrames();
                if (this.normalAttackEffectFrames == null || this.normalAttackEffectFrames.isEmpty()) {
                    System.err.println("Astrologer loadSprites: Normal attack effect frames NOT loaded!");
                }
            }
        }
    }
    @Override
    public void draw(Graphics2D g2) {
        // Phần vẽ nhân vật Astrologer
        BufferedImage bodyImage = cip.getCurFrame();
        if (bodyImage != null) {
            int targetWidth = gp.getTileSize();
            int targetHeight = gp.getTileSize();

            int originalWidth = bodyImage.getWidth();
            int originalHeight = bodyImage.getHeight();
            double aspectRatio = (double) originalWidth / originalHeight;

            int newWidth, newHeight;
            if (aspectRatio > 1) { // Ảnh rộng
                newWidth = targetWidth;
                newHeight = (int) (newWidth / aspectRatio);
            } else { // Ảnh cao hoặc vuông
                newHeight = targetHeight;
                newWidth = (int) (newHeight * aspectRatio);
            }

            int drawX = screenX + (targetWidth - newWidth) / 2;
            int drawY = screenY + (targetHeight - newHeight) / 2;

            g2.drawImage(bodyImage, drawX, drawY, newWidth, newHeight, null);
        }

        // Phần vẽ hiệu ứng tấn công tách rời (giữ nguyên logic cũ của Astrologer)
        drawCharacterSpecifics(g2);

        // Vẽ thanh máu
        drawHealthBar(g2, screenX, screenY);
    }
    @Override
    protected void performNormalAttackAction() {
        // Khi người chơi nhấn tấn công, kích hoạt các trạng thái
        this.attackStateCounter = this.currentAttackStateDuration; // Đặt trạng thái "bận"
        resetAttackCooldown(); // Kích hoạt cooldown
        this.attackDamageAppliedThisSwing = false;
        showNormalAttackEffect = true;
        normalAttackEffectAnimCounter = NORMAL_ATTACK_EFFECT_TOTAL_DURATION;
        normalAttackEffectSpriteNum = 0;

        System.out.println("Astrologer casts normal attack effect!");
        // Logic gây sát thương thực tế có thể được xử lý bởi CombatSystem trong GamePanel
        // khi nó kiểm tra va chạm của hitbox hiệu ứng với monster.
    }

    @Override
    protected void handleSkillInputs() {
        // Nơi để thêm logic cho các kỹ năng khác của Astrologer sau này
        // Ví dụ: if (keyH.skill2Pressed && ...) { castBlink(); }
    }

    @Override
    public void update() {
        // 1. Gọi logic update chung của Player cha.
        // Điều này sẽ xử lý input tấn công (gọi performNormalAttackAction), di chuyển, và giảm cooldown.
        super.update();

        // 2. Cập nhật logic riêng của Astrologer (animation hiệu ứng)
        if (showNormalAttackEffect) {
            normalAttackEffectAnimCounter--;
            if (normalAttackEffectAnimCounter <= 0) {
                showNormalAttackEffect = false;
            } else {
                if (normalAttackEffectFrameHoldTime > 0) {
                    int elapsedTime = NORMAL_ATTACK_EFFECT_TOTAL_DURATION - normalAttackEffectAnimCounter;
                    normalAttackEffectSpriteNum = elapsedTime / normalAttackEffectFrameHoldTime;
                    if (normalAttackEffectSpriteNum >= NORMAL_ATTACK_EFFECT_FRAMES_COUNT) {
                        normalAttackEffectSpriteNum = NORMAL_ATTACK_EFFECT_FRAMES_COUNT - 1;
                    }
                }
            }
        }
    }

    @Override
    protected void drawCharacterSpecifics(Graphics2D g2) {
        // Chỉ vẽ hiệu ứng nếu trạng thái tấn công đang được kích hoạt
        if (showNormalAttackEffect && normalAttackEffectFrames != null &&
                normalAttackEffectSpriteNum >= 0 &&
                normalAttackEffectSpriteNum < normalAttackEffectFrames.size()) {

            BufferedImage effectImage = normalAttackEffectFrames.get(normalAttackEffectSpriteNum);

            if (effectImage != null) {
                // 1. Xác định vị trí MỤC TIÊU của hiệu ứng (ô tile ngay trước mặt Player)
                int targetX = screenX;
                int targetY = screenY;
                int rangeInTiles = 2;
                switch (direction) {
                    case "up":    targetY -= gp.getTileSize() * rangeInTiles; break;
                    case "down":  targetY += gp.getTileSize() * rangeInTiles; break;
                    case "left":  targetX -= gp.getTileSize() * rangeInTiles; break;
                    case "right": targetX += gp.getTileSize() * rangeInTiles; break;
                }

                // 2. Xác định kích thước mới cho hiệu ứng (gấp đôi kích thước ô tile)
                int effectWidth = gp.getTileSize() * 2;
                int effectHeight = gp.getTileSize() * 2;

                // 3. Tính toán vị trí vẽ (drawX, drawY) để hiệu ứng LỚN được CĂN GIỮA trên ô tile MỤC TIÊU
                int drawX = targetX - (effectWidth - gp.getTileSize()) / 2;
                int drawY = targetY - (effectHeight - gp.getTileSize()) / 2;

                // 4. Vẽ hiệu ứng tấn công lên màn hình
                g2.drawImage(effectImage, drawX, drawY, effectWidth, effectHeight, null);
            }
        }
    }
    @Override
    public void checkAttack() {
        // Xác định vị trí MỤC TIÊU của hiệu ứng (ô tile ngay trước mặt Player)
        // Logic này tương tự như trong phương thức drawCharacterSpecifics
        int targetWorldX = this.worldX;
        int targetWorldY = this.worldY;
        int rangeInTiles = 2; // Tầm xa của kỹ năng là 2 ô

        switch (direction) {
            case "up":    targetWorldY -= gp.getTileSize() * rangeInTiles; break;
            case "down":  targetWorldY += gp.getTileSize() * rangeInTiles; break;
            case "left":  targetWorldX -= gp.getTileSize() * rangeInTiles; break;
            case "right": targetWorldX += gp.getTileSize() * rangeInTiles; break;
        }

        // Hiệu ứng có kích thước 2x2 tiles, nên tâm của nó sẽ ở vị trí của ô tile đó + nửa tile
        int aoeCenterX = targetWorldX + gp.getTileSize() / 2;
        int aoeCenterY = targetWorldY + gp.getTileSize() / 2;

        // Bán kính của vùng ảnh hưởng (ví dụ: 1 ô tile)
        int radius = gp.getTileSize();

        System.out.println("Astrologer AoE check at: (" + aoeCenterX + ", " + aoeCenterY + ") with radius " + radius); // Dòng gỡ lỗi

        // Gọi phương thức kiểm tra sát thương vùng từ CombatSystem
        gp.getCombatSystem().checkAoEAttack(this, aoeCenterX, aoeCenterY, radius);
    }
}