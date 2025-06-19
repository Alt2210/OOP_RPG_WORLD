package character.monster;

import character.Character;
import character.CombatableCharacter;
import character.role.Player;
import main.GamePanel;
import pathfinder.Node;
import pathfinder.PathFinder; // PathFinder được kế thừa từ lớp Monster
import worldObject.pickableObject.OBJ_HealthPotion;
import worldObject.pickableObject.OBJ_ManaPotion;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MON_Orc extends Monster {

    private boolean attacking = false;
    private int attackAnimationCounter = 0;
    // Thời gian cho mỗi frame của animation tấn công, giả sử attack có 2 frame thì 30/2 = 15 frame game/1 frame animation
    private final int ATTACK_ANIMATION_DURATION = 30;
    private final int ORC_ATTACK_RANGE_TILES = 1;     // Orc tấn công khi Player cách 1 ô
    private final int ORC_ATTACK_CHECK_INTERVAL = 45; // Tần suất Orc thử tấn công (ví dụ: mỗi 45 frame)
    private int attackCheckCounter = 0;

    public MON_Orc(GamePanel gp) {
        super(gp);

        // Khởi tạo PathFinder nếu lớp Monster cha chưa làm
        if (this.pathFinder == null) {
            this.pathFinder = new PathFinder(gp);
        }
        cip.setNumSprite(2); // Số frame cho hoạt ảnh đi bộ (và tấn công nếu cùng số frame)

        direction = "down";
        setDefaultValues();
    }

    @Override
    protected void onDeath(CombatableCharacter attacker) {
        super.onDeath(attacker);
        int i = new Random().nextInt(100) + 1;

        // SET THE MONSTER DROP
        if (i < 30) {
            dropItem(new OBJ_HealthPotion(gp));
        }
        // từ 30 ~ 69 không drop ra gì
        if (i >= 70 && i < 100) {
            dropItem(new OBJ_ManaPotion(gp));
        }
        gp.getUi().showMessage(attacker.getName() + " đã đánh bại " + getName() + "!" + "\n"
                + "Bạn nhận được " + this.coinValue + "vàng!");
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 30;
        worldY = gp.getTileSize() * 40;

        setName("Orc");
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxHealth = 100;
        currentHealth = maxHealth;
        attack = 12;
        defense = 3;
        exp = 15;
        coinValue = 300;
        contactDamageAmount = 5;

        // Thiết lập Cooldown cho đòn tấn công của Orc
        this.ATTACK_COOLDOWN_DURATION = 120; // Ví dụ: Orc chỉ có thể tấn công mỗi 2 giây (120 frames @ 60FPS)

        // Tầm đánh hitbox, có thể sử dụng cho CombatSystem hoặc logic riêng
        this.attackRange = gp.getTileSize(); // Tầm đánh hiệu quả của đòn tấn công là 1 tile

        solidArea.x = 4;
        solidArea.y = 4;
        solidArea.width = 40;
        solidArea.height = 44;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // CharacterImageProcessor sẽ tải ảnh đi bộ và tấn công cho "orc"
        // Đảm bảo CharacterImageProcessor được cấu hình để xử lý "orc" với 2 frame/hướng cho đi bộ và tấn công
        cip.getImage("/monster", "orc");
    }

    public boolean isAttacking() {
        return this.attacking;
    }

    private void attemptToAttackPlayer(Player player) {
        attackCheckCounter++;
        if (attackCheckCounter < ORC_ATTACK_CHECK_INTERVAL) {
            return; // Chưa đến lúc kiểm tra tấn công
        }
        attackCheckCounter = 0; // Reset bộ đếm

        if (getTileDistance(player) <= ORC_ATTACK_RANGE_TILES && canAttack()) { // canAttack() kiểm tra attackCooldown
            attacking = true;
            attackAnimationCounter = ATTACK_ANIMATION_DURATION;
            speed = 0; // Orc đứng yên khi thực hiện hành động tấn công

            // Quay mặt về phía Player khi tấn công
            int playerCenterX = player.getWorldX() + player.getSolidArea().x + player.getSolidArea().width / 2;
            int playerCenterY = player.getWorldY() + player.getSolidArea().y + player.getSolidArea().height / 2;
            int orcCenterX = this.getWorldX() + this.getSolidArea().x + this.getSolidArea().width / 2;
            int orcCenterY = this.getWorldY() + this.getSolidArea().y + this.getSolidArea().height / 2;

            int dx = playerCenterX - orcCenterX;
            int dy = playerCenterY - orcCenterY;

            if (Math.abs(dx) > Math.abs(dy)) {
                direction = (dx > 0) ? "right" : "left";
            } else {
                direction = (dy > 0) ? "down" : "up";
            }

            // Tạo hitbox cho đòn tấn công của Orc
            Rectangle orcAttackHitbox = new Rectangle();
            // Kích thước hitbox có thể khác với kích thước sprite, tùy vào animation
            int hitboxWidth = gp.getTileSize();
            int hitboxHeight = gp.getTileSize();

            int solidRectCenterX = worldX + solidArea.x + solidArea.width / 2;
            int solidRectCenterY = worldY + solidArea.y + solidArea.height / 2;

            switch (direction) {
                case "up":
                    orcAttackHitbox.setBounds(solidRectCenterX - hitboxWidth / 2,
                            worldY + solidArea.y - hitboxHeight,
                            hitboxWidth, hitboxHeight);
                    break;
                case "down":
                    orcAttackHitbox.setBounds(solidRectCenterX - hitboxWidth / 2,
                            worldY + solidArea.y + solidArea.height,
                            hitboxWidth, hitboxHeight);
                    break;
                case "left":
                    orcAttackHitbox.setBounds(worldX + solidArea.x - hitboxWidth,
                            solidRectCenterY - hitboxHeight / 2,
                            hitboxWidth, hitboxHeight);
                    break;
                case "right":
                    orcAttackHitbox.setBounds(worldX + solidArea.x + solidArea.width,
                            solidRectCenterY - hitboxHeight / 2,
                            hitboxWidth, hitboxHeight);
                    break;
            }

            Rectangle playerBounds = new Rectangle(player.getWorldX() + player.getSolidArea().x,
                    player.getWorldY() + player.getSolidArea().y,
                    player.getSolidArea().width,
                    player.getSolidArea().height);

            if (orcAttackHitbox.intersects(playerBounds)) {
                // gp.playSoundEffect(SOUND_ORC_ATTACK_HIT); // Thêm âm thanh nếu có
                gp.getCombatSystem().performAttack(this, player);
            }
            resetAttackCooldown(); // Kích hoạt cooldown sau khi thực hiện tấn công (dù trúng hay không)
        }
    }

    @Override
    public void update() {
        if (attacking) {
            attackAnimationCounter--;
            if (attackAnimationCounter <= 0) {
                attacking = false;
                speed = defaultSpeed;
            }
        } else {
            speed = defaultSpeed;
            Player player = gp.getPlayer();
            if (player != null && player.getCurrentHealth() > 0) {
                attemptToAttackPlayer(player);
            }
            if(!attacking) {
                playerChasing();
            }
        }
        super.update(); // Gọi Character.update() để di chuyển và gọi cip.update()
    }

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
                    int currentOrcCenterX = worldX + solidArea.x + solidArea.width / 2;
                    int currentOrcCenterY = worldY + solidArea.y + solidArea.height / 2;
                    int deltaX = nextNodeCenterX - currentOrcCenterX;
                    int deltaY = nextNodeCenterY - currentOrcCenterY;

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

    @Override
    public void damageReaction(CombatableCharacter attacker) {
        super.damageReaction(attacker);
        attacking = false;
        attackAnimationCounter = 0;
        speed = defaultSpeed; // Đảm bảo Orc có thể di chuyển sau khi nhận sát thương
    }

    public void checkDrop() {
        int i = new Random().nextInt(100) + 1;
        if (i < 50) {
            gp.getUi().showMessage(getName() + " dropped a Bronze Coin!");
        } else if (i < 75) {
            gp.getUi().showMessage(getName() + " dropped a Heart!");
        } else {
            gp.getUi().showMessage(getName() + " dropped a Mana Crystal!");
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = cip.getCurFrame(); // CharacterImageProcessor sẽ tự chọn frame (đi bộ/tấn công)

        if (image != null) {
            // Vị trí trên màn hình của góc trên-trái của ô tile chuẩn mà Orc đang đứng
            int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
            int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

            // Kích thước vẽ mặc định (cho đi bộ hoặc nếu không có logic tấn công đặc biệt)
            int drawWidth = gp.getTileSize();
            int drawHeight = gp.getTileSize();
            int drawOffsetX = 0; // Độ lệch X để vẽ so với screenX
            int drawOffsetY = 0; // Độ lệch Y để vẽ so với screenY

            if (attacking) { // Nếu Orc đang trong trạng thái animation tấn công
                // Giả sử worldX, worldY là điểm tham chiếu trên cùng bên trái của ô tile chuẩn
                // mà Orc chiếm giữ khi không tấn công.
                // Ảnh tấn công sẽ được căn chỉnh dựa trên điểm này.

                switch (direction) {
                    case "up":
                        drawWidth = gp.getTileSize();       // Rộng 1 tile (16px gốc -> 48px scaled)
                        drawHeight = gp.getTileSize() * 2;    // Cao 2 tile (32px gốc -> 96px scaled)
                        // Vẽ dịch lên trên 1 tile để phần chân/thân dưới của sprite tấn công (cao 2 tile)
                        // khớp với vị trí của sprite đi bộ (cao 1 tile).
                        // Tức là, điểm (worldX, worldY - tileSize) sẽ là góc trên-trái của ảnh tấn công.
                        drawOffsetX = 0; // Không lệch ngang so với vị trí tile chuẩn
                        drawOffsetY = -gp.getTileSize();
                        break;
                    case "down":
                        drawWidth = gp.getTileSize();
                        drawHeight = gp.getTileSize() * 2;
                        // Nếu worldY là đỉnh của ô tile chuẩn, và ảnh tấn công xuống cao 2 tile
                        // thì không cần drawOffsetY nếu bạn muốn nó phủ xuống dưới từ worldY.
                        // Hoặc nếu bạn muốn tâm của phần thân Orc vẫn giữ nguyên, bạn có thể cần điều chỉnh.
                        // Giả sử worldY là đỉnh, sprite sẽ bắt đầu từ (worldX, worldY).
                        drawOffsetX = 0;
                        drawOffsetY = 0; // Hoặc ví dụ: -gp.getTileSize()/2 nếu muốn căn giữa khác
                        break;
                    case "left":
                        drawWidth = gp.getTileSize() * 2;   // Rộng 2 tile
                        drawHeight = gp.getTileSize();     // Cao 1 tile
                        // Vẽ dịch sang trái 1 tile để phần bên phải của sprite tấn công (rộng 2 tile)
                        // khớp với vị trí của sprite đi bộ (rộng 1 tile).
                        // Tức là, điểm (worldX - tileSize, worldY) sẽ là góc trên-trái của ảnh tấn công.
                        drawOffsetX = -gp.getTileSize();
                        drawOffsetY = 0; // Không lệch dọc so với vị trí tile chuẩn
                        break;
                    case "right":
                        drawWidth = gp.getTileSize() * 2;
                        drawHeight = gp.getTileSize();
                        // Nếu worldX là cạnh trái của ô tile chuẩn, và sprite tấn công sang phải rộng 2 tile,
                        // thì không cần drawOffsetX nếu bạn muốn nó phủ sang phải từ worldX.
                        drawOffsetX = 0;
                        drawOffsetY = 0;
                        break;
                }
            }

            // Tính toán vị trí vẽ thực tế trên màn hình
            int actualDrawX = screenX + drawOffsetX;
            int actualDrawY = screenY + drawOffsetY;

            // Culling: Chỉ vẽ nếu một phần của sprite (đã tính offset và kích thước mới) nằm trong màn hình
            if (actualDrawX + drawWidth > 0 && actualDrawX < gp.getScreenWidth() &&
                    actualDrawY + drawHeight > 0 && actualDrawY < gp.getScreenHeight()) {

                g2.drawImage(image, actualDrawX, actualDrawY, drawWidth, drawHeight, null);
            }

            // Vẽ thanh máu ở vị trí cố định so với ô tile gốc của Orc (screenX, screenY),
            // không bị ảnh hưởng bởi offset/resize của sprite tấn công.
            // Điều kiện culling cho thanh máu:
            if (screenX + gp.getTileSize() > 0 && screenX < gp.getScreenWidth() &&
                    screenY + gp.getTileSize() > 0 && screenY < gp.getScreenHeight()) {
                drawHealthBar(g2, screenX, screenY);
            }
        }
    }
}