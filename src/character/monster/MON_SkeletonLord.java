package character.monster;

import character.Character;
import character.CombatableCharacter;
import character.role.Player;
import main.GamePanel;
import pathfinder.Node;
import pathfinder.PathFinder; // PathFinder được kế thừa từ lớp Monster
import worldObject.pickableObject.OBJ_HealthPotion;
import worldObject.pickableObject.OBJ_Key;
import worldObject.pickableObject.OBJ_ManaPotion;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MON_SkeletonLord extends Monster {

    private boolean attacking = false;
    private int attackAnimationCounter = 0;
    // Thời gian cho mỗi frame của animation tấn công, giả sử attack có 2 frame thì 30/2 = 15 frame game/1 frame animation
    private final int ATTACK_ANIMATION_DURATION = 30;
    private final int MELEE_ATTACK_RANGE_TILES = 1;     // Skeleton Lord tấn công khi Player cách 1 ô (giống Orc)
    private final int ATTACK_CHECK_INTERVAL = 45; // Tần suất Skeleton Lord thử tấn công (giống Orc)
    private int attackCheckCounter = 0;

    // Biến cho phase (chỉ dùng để đổi ảnh)
    private int currentPhase;
    private static final int PHASE_ONE = 1;
    private static final int PHASE_TWO = 2;
    private final int PHASE_TWO_HEALTH_THRESHOLD; // Ngưỡng máu để chuyển phase (ví dụ: 50% max health)
    private boolean phaseChanged = false; // Cờ để đảm bảo logic chuyển phase chỉ chạy một lần


    public MON_SkeletonLord(GamePanel gp) {
        super(gp);

        // Khởi tạo PathFinder nếu lớp Monster cha chưa làm
        if (this.pathFinder == null) {
            this.pathFinder = new PathFinder(gp);
        }
        cip.setNumSprite(2); // Số frame cho hoạt ảnh đi bộ (và tấn công nếu cùng số frame)

        direction = "down";
        setDefaultValues();
        this.PHASE_TWO_HEALTH_THRESHOLD = (int) (maxHealth * 0.5); // 50% máu
        this.currentPhase = PHASE_ONE; // Bắt đầu ở phase 1
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 30;
        worldY = gp.getTileSize() * 40;

        setName("Skeleton Lord"); // Đổi tên
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxHealth = 250; // Tăng máu
        currentHealth = maxHealth;
        attack = 15; // Tăng sát thương (phù hợp boss)
        defense = 5; // Tăng phòng thủ (phù hợp boss)
        exp = 100; // Tăng EXP
        coinValue = 1000;
        contactDamageAmount = 8; // Sát thương chạm

        // Thiết lập Cooldown cho đòn tấn công của Skeleton Lord (giống Orc nhưng có thể điều chỉnh)
        this.ATTACK_COOLDOWN_DURATION = 90; // Ví dụ: 1.5 giây (90 frames @ 60FPS)

        // Tầm đánh hitbox (giữ nguyên Orc logic là 1 tile tiếp xúc)
        this.attackRange = gp.getTileSize();

        // Kích thước solidArea lớn hơn cho boss (giữ nguyên logic bạn đã có)
        int bossBaseSize = gp.getTileSize() * 3; // Ví dụ: 3x3 tiles cho tổng kích thước
        solidArea.x = (bossBaseSize - gp.getTileSize()) / 2; // Căn giữa hitbox
        solidArea.y = (bossBaseSize - gp.getTileSize()) / 2; // Căn giữa hitbox
        solidArea.width = gp.getTileSize(); // Giữ hitbox là 1 tile
        solidArea.height = gp.getTileSize();
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // Tải ảnh cho Skeleton Lord Phase 1 ban đầu
        cip.getImage("/monster", "skeletonlord");
    }

    public boolean isAttacking() {
        return this.attacking;
    }

    // Sao chép logic tấn công từ MON_Orc.attemptToAttackPlayer
    private void slashAttack(Player player) {
        attackCheckCounter++;
        if (attackCheckCounter < ATTACK_CHECK_INTERVAL) {
            return; // Chưa đến lúc kiểm tra tấn công
        }
        attackCheckCounter = 0; // Reset bộ đếm

        if (getTileDistance(player) <= MELEE_ATTACK_RANGE_TILES && canAttack()) { // canAttack() kiểm tra attackCooldown
            attacking = true;
            attackAnimationCounter = ATTACK_ANIMATION_DURATION;
            speed = 0; // Orc đứng yên khi thực hiện hành động tấn công

            // Quay mặt về phía Player khi tấn công
            int playerCenterX = player.getWorldX() + player.getSolidArea().x + player.getSolidArea().width / 2;
            int playerCenterY = player.getWorldY() + player.getSolidArea().y + player.getSolidArea().height / 2;
            int monsterCenterX = this.getWorldX() + this.getSolidArea().x + this.getSolidArea().width / 2;
            int monsterCenterY = this.getWorldY() + this.getSolidArea().y + this.getSolidArea().height / 2;

            int dx = playerCenterX - monsterCenterX;
            int dy = playerCenterY - monsterCenterY;

            if (Math.abs(dx) > Math.abs(dy)) {
                direction = (dx > 0) ? "right" : "left";
            } else {
                direction = (dy > 0) ? "down" : "up";
            }

            // Tạo hitbox cho đòn tấn công của Orc
            Rectangle monsterAttackHitbox = new Rectangle();
            // Kích thước hitbox có thể khác với kích thước sprite, tùy vào animation
            int hitboxWidth = gp.getTileSize();
            int hitboxHeight = gp.getTileSize();

            int solidRectCenterX = worldX + solidArea.x + solidArea.width / 2;
            int solidRectCenterY = worldY + solidArea.y + solidArea.height / 2;

            switch (direction) {
                case "up":
                    monsterAttackHitbox.setBounds(solidRectCenterX - hitboxWidth / 2,
                            worldY + solidArea.y - hitboxHeight,
                            hitboxWidth, hitboxHeight);
                    break;
                case "down":
                    monsterAttackHitbox.setBounds(solidRectCenterX - hitboxWidth / 2,
                            worldY + solidArea.y + solidArea.height,
                            hitboxWidth, hitboxHeight);
                    break;
                case "left":
                    monsterAttackHitbox.setBounds(worldX + solidArea.x - hitboxWidth,
                            solidRectCenterY - hitboxHeight / 2,
                            hitboxWidth, hitboxHeight);
                    break;
                case "right":
                    monsterAttackHitbox.setBounds(worldX + solidArea.x + solidArea.width,
                            solidRectCenterY - hitboxHeight / 2,
                            hitboxWidth, hitboxHeight);
                    break;
            }

            Rectangle playerBounds = new Rectangle(player.getWorldX() + player.getSolidArea().x,
                    player.getWorldY() + player.getSolidArea().y,
                    player.getSolidArea().width,
                    player.getSolidArea().height);

            if (monsterAttackHitbox.intersects(playerBounds)) {
                // gp.playSoundEffect(SOUND_ORC_ATTACK_HIT); // Thêm âm thanh nếu có
                gp.getCombatSystem().performAttack(this, player);
            }
            resetAttackCooldown(); // Kích hoạt cooldown sau khi thực hiện tấn công (dù trúng hay không)
        }
    }

    // Sao chép logic update từ MON_Orc
    @Override
    public void update() {
        // Kiểm tra chuyển phase (chỉ để thay đổi ảnh, không ảnh hưởng cơ chế tấn công/di chuyển)
        if (currentPhase == PHASE_ONE && currentHealth <= PHASE_TWO_HEALTH_THRESHOLD && !phaseChanged) {
            currentPhase = PHASE_TWO;
            phaseChanged = true; // Đặt cờ để chỉ chuyển phase một lần
            gp.getUi().showMessage("Skeleton Lord đã tiến hóa! Nó trở nên mạnh mẽ và đáng sợ hơn!");
            System.out.println("Skeleton Lord transitioned to Phase 2!");

            // Tải lại sprite cho Phase 2 (thông qua CharacterImageProcessor)
            cip.getImage("/monster", "skeletonlord_phase2");
            // Optionally, increase speed/attack in Phase 2, but for now, we only change images.
            // speed = defaultSpeed + 1;
            // attack = attack + 5;
        }


        if (attacking) {
            attackAnimationCounter--;
            if (attackAnimationCounter <= 0) {
                attacking = false;
                speed = defaultSpeed; // Trở lại tốc độ mặc định sau khi tấn công
            }
        } else {
            speed = defaultSpeed; // Luôn đảm bảo tốc độ mặc định khi không tấn công
            Player player = gp.getPlayer();
            if (player != null && player.getCurrentHealth() > 0) {
                slashAttack(player); // Sử dụng phương thức giống Orc
            }
            if(!attacking) {
                playerChasing(); // Sử dụng phương thức giống Orc
            }
        }
        super.update(); // Gọi Character.update() để di chuyển và gọi cip.update()
    }

    // Sao chép logic tìm đường từ MON_Orc.playerChasing()
    @Override
    public void playerChasing() {
        Player player = gp.getPlayer();
        if (player == null || player.getCurrentHealth() <= 0) {
            onPath = false;
            // attacking đã được xử lý trong update()
            getRandomDirection(120);
            return;
        }

        if (onPath) {
            checkStopChasingOrNot(player, 15, 100);

            int goalCol = getGoalCol(player);
            int goalRow = getGoalRow(player);

            int monsterCurrentTileCol = (worldX + solidArea.x + solidArea.width / 2) / gp.getTileSize();
            int monsterCurrentTileRow = (worldY + solidArea.y + solidArea.height / 2) / gp.getTileSize();

            pathFinder.setNodes(monsterCurrentTileCol, monsterCurrentTileRow, goalCol, goalRow, this);

            if (pathFinder.search()) {
                if (!pathFinder.getPathList().isEmpty()) {
                    Node nextNode = pathFinder.getPathList().get(0);
                    int nextNodeCenterX = nextNode.getCol() * gp.getTileSize() + gp.getTileSize() / 2;
                    int nextNodeCenterY = nextNode.getRow() * gp.getTileSize() + gp.getTileSize() / 2;
                    int currentMonsterCenterX = worldX + solidArea.x + solidArea.width / 2;
                    int currentMonsterCenterY = worldY + solidArea.y + solidArea.height / 2;
                    int deltaX = nextNodeCenterX - currentMonsterCenterX;
                    int deltaY = nextNodeCenterY - currentMonsterCenterY;

                    float threshold = speed * 0.8f;
                    if (Math.abs(deltaX) > Math.abs(deltaY)) { // Ưu tiên di chuyển theo trục có chênh lệch lớn hơn
                        if (Math.abs(deltaX) > threshold) direction = (deltaX > 0) ? "right" : "left";
                    } else if (Math.abs(deltaY) > Math.abs(deltaX)) {
                        if (Math.abs(deltaY) > threshold) direction = (deltaY > 0) ? "down" : "up";
                    } else { // Nếu chênh lệch bằng nhau (di chuyển chéo) hoặc rất nhỏ
                        if (Math.abs(deltaX) > threshold) direction = (deltaX > 0) ? "right" : "left";
                        else if (Math.abs(deltaY) > threshold) direction = (deltaY > 0) ? "down" : "up";
                        // Nếu cả hai delta đều nhỏ hơn threshold, giữ nguyên hướng cũ để tránh rung lắc
                    }
                } else {
                    onPath = false;
                }
            } else {
                onPath = false;
            }
        } else {
            checkStartChasingOrNot(player, 5, 100);
            getRandomDirection(120);
        }
    }

    // Sao chép logic damageReaction từ MON_Orc
    @Override
    public void damageReaction(CombatableCharacter attacker) {
        super.damageReaction(attacker);
        attacking = false; // Đảm bảo không còn trong trạng thái tấn công
        attackAnimationCounter = 0; // Reset animation counter
        speed = defaultSpeed; // Đảm bảo Skeleton Lord có thể di chuyển sau khi nhận sát thương
        onPath = true; // Bắt đầu đuổi theo người chơi sau khi nhận sát thương
    }

    @Override
    protected void onDeath(CombatableCharacter attacker) {
        super.onDeath(attacker);
        int i = new Random().nextInt(100) + 1; // Tạo số ngẫu nhiên từ 1 đến 100
        // Cấu trúc if - else if để tỉ lệ không bị chồng chéo
        if (i <= 40) { // Tỉ lệ 40% (số từ 1 đến 40)
            dropItem(new OBJ_HealthPotion(gp));
        } else if (i <= 50) { // Tỉ lệ 10% (số từ 41 đến 50)
            dropItem(new OBJ_ManaPotion(gp));
        }
        dropItem(new OBJ_HealthPotion(gp));
        gp.getUi().showMessage(attacker.getName() + " đã đánh bại " + getName() + "!" + "\n"
                + "Bạn nhận được " + this.coinValue + "vàng!");
    }

    // Phần draw() của Skeleton Lord (đã được tối ưu tốt, giữ nguyên)
    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = cip.getCurFrame();

        if (image != null) {
            int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
            int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

            int originalImageWidth = image.getWidth();
            int originalImageHeight = image.getHeight();

            int drawWidth = originalImageWidth * gp.getScale();
            int drawHeight = originalImageHeight * gp.getScale();

            int solidScreenX = screenX + solidArea.x;
            int solidScreenY = screenY + solidArea.y;

            int actualDrawX = solidScreenX;
            int actualDrawY = solidScreenY;

            if (attacking) {
                if (direction.equals("up") || direction.equals("down")) {
                    int drawOffsetX = (drawWidth - gp.getTileSize()) / 2;
                    int drawOffsetY = drawHeight - gp.getTileSize();

                    if(direction.equals("up")) {
                        actualDrawY = solidScreenY - (drawHeight - gp.getTileSize());
                        actualDrawX = solidScreenX - drawOffsetX;
                    } else if (direction.equals("down")) {
                        actualDrawX = solidScreenX - drawOffsetX;
                        actualDrawY = solidScreenY - (drawHeight - gp.getTileSize());
                    }
                }
                else if (direction.equals("left") || direction.equals("right")) {
                    int drawOffsetX = drawWidth - gp.getTileSize();
                    int drawOffsetY = (drawHeight - gp.getTileSize()) / 2;

                    if(direction.equals("left")) {
                        actualDrawX = solidScreenX - (drawWidth - gp.getTileSize());
                        actualDrawY = solidScreenY - drawOffsetY;
                    } else if (direction.equals("right")) {
                        actualDrawX = solidScreenX;
                        actualDrawY = solidScreenY - drawOffsetY;
                    }
                }
            } else {
                int drawOffsetX = (drawWidth - gp.getTileSize()) / 2;
                int drawOffsetY = drawHeight - gp.getTileSize();

                actualDrawX = solidScreenX - drawOffsetX;
                actualDrawY = solidScreenY - drawOffsetY;
            }

            if (screenX + (gp.getTileSize()*3) > 0 && screenX - (gp.getTileSize()*3) < gp.getScreenWidth() &&
                    screenY + (gp.getTileSize()*3) > 0 && screenY - (gp.getTileSize()*3) < gp.getScreenHeight()) {

                g2.drawImage(image, actualDrawX, actualDrawY, drawWidth, drawHeight, null);
            }

            drawHealthBar(g2, screenX, screenY);
        }
    }
}