package character.monster;

import character.Character;
import main.GamePanel;
import ai.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MON_GreenSlime extends Monster {
    private PathFinder pathFinder;
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
        maxHealth = 4;
        currentHealth = maxHealth;
        attack = 5;
        defense = 0;
        exp = 2;

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        cip.getImage("/monster", "greenslime_");
    }


    public void setAction() {
        if (onPath) {
            checkStopChasingOrNot(gp.getPlayer(), 15, 100);

            // Vị trí hiện tại của monster (tính theo ô tile)
            int currentMonsterCol = worldX / gp.getTileSize();
            int currentMonsterRow = worldY / gp.getTileSize();

            // Vị trí của Player (tính theo ô tile)
            int goalCol = gp.getPlayer().worldX / gp.getTileSize();
            int goalRow = gp.getPlayer().worldY / gp.getTileSize();

            // Thiết lập các node cho PathFinder
            pathFinder.setNodes(currentMonsterCol, currentMonsterRow, goalCol, goalRow, this);

            // Tìm kiếm đường đi
            if (pathFinder.search()) {
                // Nếu tìm thấy đường đi, pathFinder.pathList sẽ chứa các Node
                // Lấy Node tiếp theo trên đường đi
                if (!pathFinder.pathList.isEmpty()) {
                    Node nextNode = pathFinder.pathList.get(0); // Node đầu tiên trong path là điểm đến tiếp theo
                    int nextX = nextNode.col * gp.getTileSize();
                    int nextY = nextNode.row * gp.getTileSize();

                    // Xác định hướng di chuyển đến nextNode
                    if (worldY > nextY && worldX == nextX) direction = "up";
                    else if (worldY < nextY && worldX == nextX) direction = "down";
                    else if (worldX > nextX && worldY == nextY) direction = "left";
                    else if (worldX < nextX && worldY == nextY) direction = "right";
                        // (Xử lý di chuyển chéo nếu có)
                        // Nếu không di chuyển chính xác đến tile tiếp theo trong 1 frame:
                        // else if (worldY > nextY && worldX > nextX) direction = "up-left"; // Cần xử lý sprite và di chuyển
                        // ...
                    else {
                        // Nếu không thể xác định hướng rõ ràng (ví dụ đã ở rất gần nextNode)
                        // hoặc nếu nextNode chính là currentNode (hiếm khi xảy ra nếu pathList > 0)
                        // Có thể giữ nguyên hướng cũ hoặc dừng lại một chút
                    }
                } else {
                    // pathList rỗng mặc dù search() trả về true (có thể goalNode = startNode)
                    // Hoặc không còn node nào trong pathList (đã đến đích)
                    onPath = false; // Đã đến đích hoặc không còn đường
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
    public void draw(Graphics2D g2) {
        BufferedImage image = cip.getCurFrame(); // Lấy frame hiện tại từ lớp Character
        if (image != null) {
            // Tính toán vị trí vẽ trên màn hình tương tự như cách vẽ Tile hoặc SuperItem
            int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
            int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;

            // Chỉ vẽ NPC nếu nó nằm trong tầm nhìn của camera
            if (worldX + gp.getTileSize() > gp.getPlayer().worldX - gp.getPlayer().screenX &&
                    worldX - gp.getTileSize() < gp.getPlayer().worldX + gp.getPlayer().screenX &&
                    worldY + gp.getTileSize() > gp.getPlayer().worldY - gp.getPlayer().screenY &&
                    worldY - gp.getTileSize() < gp.getPlayer().worldY + gp.getPlayer().screenY) {
                g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
            }

                drawHealthBar(g2, screenX, screenY);
        }
    }

    public void damageReaction() {
        actionLockCounter = 0;
//        direction = gp.player.direction;
        onPath = true;
    }

    public void checkDrop() {
        // CAST A DIE
        int i = new Random().nextInt(100) + 1;

        // SET THE MONSTER DROP
        /*if (i < 50) {
            dropItem(new OBJ_Coin_Bronze(gp));
        }
        if (i >= 50 && i < 75) {
            dropItem(new OBJ_Heart(gp));
        }
        if (i >= 75 && i < 100) {
            dropItem(new OBJ_ManaCrystal(gp));
        }*/
    }
}
