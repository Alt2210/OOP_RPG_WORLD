package character.monster;

import ai.Node;
import ai.PathFinder;
import character.Character;
import character.Player;
import main.GamePanel;
import worldObject.WorldObject;

import java.awt.*;
import java.util.Random;

public abstract class Monster extends Character {

    protected int exp;
    PathFinder pathFinder;

    public Monster(GamePanel gp) {
        super(gp);
    }

    @Override
    public void draw(Graphics2D g2) {

    }
    @Override
    public void update() {
        super.update(); // Gọi logic update của lớp Character (di chuyển, animation, cooldown)

    }
    public void checkStartChasingOrNot(Character target, int distance, int rate) {
        if (getTileDistance(target) < distance) {
            int i = new Random().nextInt(rate);

            if (i == 0) {
                onPath = true;
            }
        }
    }

    public void checkStopChasingOrNot(Character target, int distance, int rate) {
        if (getTileDistance(target) > distance) {
            int i = new Random().nextInt(rate);

            if (i == 0) {
                onPath = false;
            }
        }
    }

    public void getRandomDirection(int interval) {
        actionLockCounter++;

        if (actionLockCounter > interval) {
            Random random = new Random();
            int i = random.nextInt(100) + 1; // pick up a number from 1 to 100

            if (i <= 25) { direction = "up"; }
            if (i > 25 && i <= 50) { direction = "down"; }
            if (i > 50 && i <= 75) { direction = "left"; }
            if (i > 75) { direction = "right"; }

            actionLockCounter = 0;
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
            int playerCenterX = player.worldX + player.solidArea.x + player.solidArea.width / 2;
            int playerCenterY = player.worldY + player.solidArea.y + player.solidArea.height / 2;
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

    public void damageReaction() {
        actionLockCounter = 0;
//        direction = gp.player.direction;
        onPath = true;
    }

    // Phương thức thả vật phẩm tại vị trí của quái vật
    protected void dropItem(WorldObject item) {
        for (int i = 0; i < gp.getwObjects().length; i++) {
            if (gp.getwObjects()[i] == null) {
                gp.getwObjects()[i] = item;
                item.worldX = this.worldX;
                item.worldY = this.worldY;
                break;
            }
        }
    }


}
