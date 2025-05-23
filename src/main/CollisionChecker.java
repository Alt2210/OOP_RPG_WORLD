package main;

import entity.Character;
import entity.Player; // Cần import lớp Player để sử dụng instanceof

import java.awt.Rectangle; // Import lớp Rectangle

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Character entity) {
        // Tọa độ các cạnh của vùng va chạm của entity trên bản đồ thế giới
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Tính toán cột và hàng của tile mà entity sẽ va chạm tới
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        // Kiểm tra hướng di chuyển và các tile tương ứng
        // Đồng thời kiểm tra xem có vượt ra ngoài biên của bản đồ không
        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                // Kiểm tra biên trên của bản đồ
                if (entityTopRow < 0 || entityLeftCol < 0 || entityRightCol >= gp.maxWorldCol) {
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                // Kiểm tra biên dưới của bản đồ
                if (entityBottomRow >= gp.maxWorldRow || entityLeftCol < 0 || entityRightCol >= gp.maxWorldCol) {
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                // Kiểm tra biên trái của bản đồ
                if (entityLeftCol < 0 || entityTopRow < 0 || entityBottomRow >= gp.maxWorldRow) {
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                // Kiểm tra biên phải của bản đồ
                if (entityRightCol >= gp.maxWorldCol || entityTopRow < 0 || entityBottomRow >= gp.maxWorldRow) {
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    // Kiểm tra va chạm của 'entity' (thường là Player) với một mảng các 'target' (NPC, monster)
    public int checkEntity(Character entity, Character[] target) {
        int index = 999; // Giá trị mặc định nếu không có va chạm hoặc không phải player va chạm

        // Vùng va chạm của entity (vị trí hiện tại)
        // Không cần tạo lại trong vòng lặp nếu không thay đổi
        Rectangle entityBounds = new Rectangle(
                entity.worldX + entity.solidArea.x,
                entity.worldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height
        );

        for (int i = 0; i < target.length; i++) {
            if (target[i] != null && target[i] != entity) { // Đảm bảo target tồn tại và không phải là chính entity

                // Vùng va chạm của target[i]
                Rectangle targetBounds = new Rectangle(
                        target[i].worldX + target[i].solidArea.x,
                        target[i].worldY + target[i].solidArea.y,
                        target[i].solidArea.width,
                        target[i].solidArea.height
                );

                // Tạo một Rectangle mới cho vị trí *dự kiến* của entity sau khi di chuyển
                // Điều này tránh thay đổi trực tiếp solidArea của entity
                Rectangle entityNextPositionBounds = new Rectangle(entityBounds); // Sao chép từ entityBounds hiện tại

                // Xác định vị trí tiếp theo dựa trên hướng di chuyển của entity
                switch (entity.direction) {
                    case "up":
                        entityNextPositionBounds.y -= entity.speed;
                        break;
                    case "down":
                        entityNextPositionBounds.y += entity.speed;
                        break;
                    case "left":
                        entityNextPositionBounds.x -= entity.speed;
                        break;
                    case "right":
                        entityNextPositionBounds.x += entity.speed;
                        break;
                }

                // Kiểm tra nếu vùng va chạm dự kiến của entity giao với vùng va chạm của target
                if (entityNextPositionBounds.intersects(targetBounds)) {
                    // Nếu có va chạm, đặt cờ collisionOn của entity là true
                    // Giả sử mọi Character trong mảng target đều là vật cản nếu va chạm
                    entity.collisionOn = true;

                    // Nếu entity là Player, lưu lại index của target mà nó va chạm
                    // (Bạn cần có lớp Player và import entity.Player)
                    if (entity instanceof Player) {
                        index = i;
                    }

                }
            }
        }
        return index; // Trả về chỉ số của target (nếu Player va chạm) hoặc 999
    }
    public boolean checkPlayer(Character entity) {
        boolean contactPlayer = false;

        // Vùng va chạm của entity (NPC/Monster) ở vị trí hiện tại
        Rectangle entityBounds = new Rectangle(
                entity.worldX + entity.solidArea.x,
                entity.worldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height);

        // Vùng va chạm của Player
        Rectangle playerBounds = new Rectangle(
                gp.player.worldX + gp.player.solidArea.x,
                gp.player.worldY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height);

        // Dự đoán vị trí tiếp theo của entity (NPC/Monster)
        Rectangle entityNextPositionBounds = new Rectangle(entityBounds); // Sao chép từ entityBounds
        switch (entity.direction) {
            case "up":
                entityNextPositionBounds.y -= entity.speed;
                break;
            case "down":
                entityNextPositionBounds.y += entity.speed;
                break;
            case "left":
                entityNextPositionBounds.x -= entity.speed;
                break;
            case "right":
                entityNextPositionBounds.x += entity.speed;
                break;
        }

        // Kiểm tra nếu vùng va chạm dự kiến của entity giao với vùng va chạm của Player
        if (entityNextPositionBounds.intersects(playerBounds)) {
            entity.collisionOn = true; // Entity này bị chặn bởi Player (không di chuyển qua được)
            contactPlayer = true;      // Ghi nhận có sự tiếp xúc
        }

        return contactPlayer;
    }

    // Kiem tra va cham co phai la nguoi choi khong
    public int checkItem(Character character, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.item.length; i++) {
            if (gp.item[i] != null) {
                // vi tri solid Area cua nhan vat
                character.solidArea.x = character.worldX + character.solidArea.x;
                character.solidArea.y = character.worldY + character.solidArea.y;
                // vi tri solid area cua item
                gp.item[i].solidArea.x = gp.item[i].worldX + gp.item[i].solidArea.x;
                gp.item[i].solidArea.y = gp.item[i].worldY + gp.item[i].solidArea.y;

                switch (character.direction) {
                    case "up":
                        character.solidArea.y -= character.speed;
                        if (character.solidArea.intersects(gp.item[i].solidArea)) {
                            if (gp.item[i].collision == true) {
                                character.collisionOn = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;
                    case "down":
                        character.solidArea.y += character.speed;
                        if (character.solidArea.intersects(gp.item[i].solidArea)) {
                            if (gp.item[i].collision == true) {
                                character.collisionOn = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;
                    case "left":
                        character.solidArea.x -= character.speed;
                        if (character.solidArea.intersects(gp.item[i].solidArea)) {
                            if (gp.item[i].collision == true) {
                                character.collisionOn = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;
                    case "right":
                        character.solidArea.x += character.speed;
                        if (character.solidArea.intersects(gp.item[i].solidArea)) {
                            if (gp.item[i].collision == true) {
                                character.collisionOn = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;
                }
                character.solidArea.x = character.solidAreaDefaultX;
                character.solidArea.y = character.solidAreaDefaultY;
                gp.item[i].solidArea.x = gp.item[i].solidAreaDefaultX;
                gp.item[i].solidArea.y = gp.item[i].solidAreaDefaultY;
            }
        }
        return index;
    }
}