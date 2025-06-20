package character.monster;

import character.Character;
import character.CombatableCharacter;
import character.role.Player;
import main.GamePanel;
import skillEffect.projectile.Slimeball;
import pathfinder.*;
import worldObject.pickableObject.OBJ_HealthPotion;
import worldObject.pickableObject.OBJ_ManaPotion;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MON_GreenSlime extends Monster {
    private int projectileCooldown;
    private final int PROJECTILE_COOLDOWN_DURATION = 60 * 5; // 5 giây (60 FPS * 5 giây)
    private final int SHOOTING_RANGE_TILES = 7; // Slime sẽ bắn nếu Player trong phạm vi 7 ô

    public MON_GreenSlime(GamePanel gp) {
        super(gp);
        cip.setNumSprite(2);
        direction = "down";
        speed = 1;
        this.pathFinder = new PathFinder(gp);
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 33; // Lấy từ AssetSetter
        worldY = gp.getTileSize() * 33; // Lấy từ AssetSetter
        direction = "down";

        setName("Green Slime");
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxHealth = 80;
        currentHealth = maxHealth;
        attack = 5;
        defense = 0;
        exp = 2;
        coinValue = 100;
        ATTACK_COOLDOWN_DURATION = 30;
        contactDamageAmount = 3;


        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        cip.getImage("/monster", "greenslime");
    }
    private String getDirectionToTarget(Character target) {
        if (target == null) return this.direction; // Giữ hướng cũ nếu không có mục tiêu

        int targetCenterX = target.getWorldX() + target.getSolidArea().x + target.getSolidArea().width / 2;
        int targetCenterY = target.getWorldY() + target.getSolidArea().y + target.getSolidArea().height / 2;
        int selfCenterX = this.getWorldX() + this.getSolidArea().x + this.getSolidArea().width / 2;
        int selfCenterY = this.getWorldY() + this.getSolidArea().y + this.getSolidArea().height / 2;

        int dx = targetCenterX - selfCenterX;
        int dy = targetCenterY - selfCenterY;

        if (Math.abs(dx) > Math.abs(dy)) {
            return (dx > 0) ? "right" : "left";
        } else {
            return (dy > 0) ? "down" : "up";
        }
    }

    public void attemptToShoot() {
        if (projectileCooldown > 0) {
            projectileCooldown--;
            return;
        }

        Player player = gp.getPlayer();
        // Chỉ bắn nếu player tồn tại, còn sống và trong tầm bắn
        if (player != null && player.getCurrentHealth() > 0 && getTileDistance(player) <= SHOOTING_RANGE_TILES) {

            String shootingDirection = getDirectionToTarget(player);
            this.direction = shootingDirection; // Slime quay mặt về phía Player khi bắn

            Slimeball slimeball = new Slimeball(gp);
            int projectileDamage = this.attack; // Sát thương của slimeball, có thể điều chỉnh (vd: this.attack / 2)

            // Điểm bắt đầu của skillEffect.projectile (ví dụ: từ giữa Slime)
            int spawnX = this.getWorldX() + this.getSolidArea().x + this.getSolidArea().width / 2;
            int spawnY = this.getWorldY() + this.getSolidArea().y + this.getSolidArea().height / 2;

            // Dịch chuyển điểm spawn ra ngoài solidArea của Slime một chút theo hướng bắn
            int offsetDistance = gp.getTileSize() / 2;
            switch(shootingDirection) {
                case "up": spawnY -= offsetDistance; break;
                case "down": spawnY += offsetDistance; break;
                case "left": spawnX -= offsetDistance; break;
                case "right": spawnX += offsetDistance; break;
            }

            slimeball.set(spawnX, spawnY, shootingDirection, this, projectileDamage);
            gp.skillEffects.add(slimeball);

            projectileCooldown = PROJECTILE_COOLDOWN_DURATION; // Reset cooldown
            // gp.playSoundEffect(Sound.SFX_SLIME_SHOOT); // Thêm âm thanh nếu có
           //  System.out.println(getName() + " bắn Slimeball về phía Player!");
        }
    }



    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = cip.getCurFrame(); // Lấy frame hiện tại từ lớp Character
        if (image != null) {
            // Tính toán vị trí vẽ trên màn hình tương tự như cách vẽ Tile hoặc SuperItem
            int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
            int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

            // Chỉ vẽ NPC nếu nó nằm trong tầm nhìn của camera
            if (worldX + gp.getTileSize() > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() &&
                    worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
                    worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
                    worldY - gp.getTileSize() < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) {
                g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
            }

                drawHealthBar(g2, screenX, screenY);
            //g2.setColor(Color.RED); // Hoặc một màu khác để phân biệt
            // screenX và screenY là tọa độ vẽ của sprite trên màn hình
            //g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
        }
    }

    public void playerChasing() {
        if (onPath) {
            checkStopChasingOrNot(gp.getPlayer(), 15, 10);

            // Vị trí hiện tại của monster (tính theo ô tile)
            int monsterCenterX = worldX + solidArea.x + solidArea.width / 2;
            int monsterCenterY = worldY + solidArea.y + solidArea.height / 2;
            int currentMonsterCol = monsterCenterX / gp.getTileSize();
            int currentMonsterRow = monsterCenterY / gp.getTileSize();

            // Vị trí của Player (tính theo ô tile, dựa vào tâm solidArea)
            Player player = gp.getPlayer(); // Lấy đối tượng Player một lần
            int playerCenterX = player.getWorldX() + player.getSolidArea().x + player.getSolidArea().width / 2;
            int playerCenterY = player.getWorldY() + player.getSolidArea().y + player.getSolidArea().height / 2;
            int goalCol = playerCenterX / gp.getTileSize();
            int goalRow = playerCenterY / gp.getTileSize();

            currentMonsterCol = Math.max(0, Math.min(currentMonsterCol, gp.getMaxWorldCol() - 1));
            currentMonsterRow = Math.max(0, Math.min(currentMonsterRow, gp.getMaxWorldRow() - 1));
            goalCol = Math.max(0, Math.min(goalCol, gp.getMaxWorldCol() - 1));
            goalRow = Math.max(0, Math.min(goalRow, gp.getMaxWorldRow() - 1));
            // Thiết lập các node cho PathFinder
             pathFinder.setNodes(currentMonsterCol, currentMonsterRow, goalCol, goalRow, this);

            // Tìm kiếm đường đi
            if (pathFinder.search()) {
                // Nếu tìm thấy đường đi, pathFinder.pathList sẽ chứa các Node
                // Lấy Node tiếp theo trên đường đi
                if (!pathFinder.getPathList().isEmpty()) {
                    Node nextNode = pathFinder.getPathList().getFirst(); // Node đầu tiên trong path là điểm đến tiếp theo
                    int nextX = nextNode.getCol() * gp.getTileSize();
                    int nextY = nextNode.getRow() * gp.getTileSize();

                    // Xác định hướng di chuyển đến nextNode
                    if (worldY > nextY && worldX == nextX) direction = "up";
                    else if (worldY < nextY && worldX == nextX) direction = "down";
                    else if (worldX > nextX && worldY == nextY) direction = "left";
                    else if (worldX < nextX && worldY == nextY) direction = "right";
                        // (Xử lý di chuyển chéo nếu có)
                        // Nếu không di chuyển chính xác đến tile tiếp theo trong 1 frame:
                        // else if (worldY > nextY && worldX > nextX) direction = "up-left"; // Cần xử lý sprite và di chuyển
                        // ...
                    //else {
                        // Nếu không thể xác định hướng rõ ràng (ví dụ đã ở rất gần nextNode)
                        // hoặc nếu nextNode chính là currentNode (hiếm khi xảy ra nếu pathList > 0)
                        // Có thể giữ nguyên hướng cũ hoặc dừng lại một chút
                    //}
                } else {
                    // pathList rỗng mặc dù search() trả về true (có thể goalNode = startNode)
                    // Hoặc không còn node nào trong pathList (đã đến đích)
                    int monsterCurrentTileCol = (worldX + solidArea.x + solidArea.width / 2) / gp.getTileSize();
                    int monsterCurrentTileRow = (worldY + solidArea.y + solidArea.height / 2) / gp.getTileSize();

                    // goalCol và goalRow là vị trí tile của player
                    if (monsterCurrentTileCol == goalCol && monsterCurrentTileRow == goalRow) {

                        onPath = false;} // Đã đến đích hoặc không còn đường
                }
            } else {
                // Không tìm thấy đường đi, chuyển sang di chuyển ngẫu nhiên
                onPath = false;
                getRandomDirection(120);
            }

        } else { // Not onPath
            checkStartChasingOrNot(gp.getPlayer(), 5, 100);
            getRandomDirection(120);
        }
    }

    @Override
    public void damageReaction(CombatableCharacter attacker) {
        actionLockCounter = 120; // Reset bộ đếm khóa hành động để quái vật có thể hành động ngay
        onPath = true;
    }
    @Override
    public int receiveDamage(int damage, CombatableCharacter attacker) {
        super.receiveDamage(damage, attacker); // Gọi Character.receiveDamage() để giảm máu và kiểm tra onDeath

        if (this.currentHealth > 0) { // Nếu quái vật vẫn còn sống sau khi nhận sát thương
            this.damageReaction(attacker);    // Gọi phản ứng sát thương của nó
        }
        return damage; // Trả về sát thương đã nhận
    }
    @Override
    public void update(){
        attemptToShoot(); // Thử bắn skillEffect.projectile
        this.playerChasing();
        super.update();
    }

    @Override
    protected void onDeath(CombatableCharacter attacker) {
        super.onDeath(attacker);

        // CAST A DIE
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


}
