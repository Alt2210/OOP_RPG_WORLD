package main;

// Đảm bảo import đúng các lớp đã tái cấu trúc
import entity.Character;
import entity.GameObject;
import entity.OBject; // Hoặc Game_Object tùy theo tên bạn đã chọn
import entity.Projectile; // Nếu cần kiểm tra va chạm với Projectile một cách đặc biệt
import tile_interactive.InteractiveTile; // Nếu cần kiểm tra va chạm với InteractiveTile

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(GameObject gameObject) { // Thay Entity bằng GameObject
        int entityLeftWorldX = gameObject.worldX + gameObject.solidArea.x;
        int entityRightWorldX = gameObject.worldX + gameObject.solidArea.x + gameObject.solidArea.width;
        int entityTopWorldY = gameObject.worldY + gameObject.solidArea.y;
        int entityBottomWorldY = gameObject.worldY + gameObject.solidArea.y + gameObject.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        // Xác định hướng và tốc độ của GameObject
        String direction = "down"; // Hướng mặc định
        int speed = 0;          // Tốc độ mặc định

        if (gameObject instanceof Character) {
            Character character = (Character) gameObject;
            direction = character.direction;
            speed = character.speed;
            if (character.knockBack) {
                direction = character.knockBackDirection;
            }
        } else if (gameObject instanceof Projectile) {
            Projectile projectile = (Projectile) gameObject;
            direction = projectile.direction; // Projectile phải có trường direction
            speed = projectile.speed;       // Projectile phải có trường speed
        }
        // Nếu các OBject khác cũng có thể di chuyển, thêm logic tương tự

        // Kiểm tra biên của bản đồ trước khi truy cập mapTileNum
        if (entityLeftCol < 0 || entityRightCol >= gp.maxWorldCol || entityTopRow < 0 || entityBottomRow >= gp.maxWorldRow) {
            gameObject.collisionOn = true; // Va chạm với biên nếu di chuyển ra ngoài
            return;
        }


        switch (direction) {
            case "up":
                entityTopRow = (entityTopWorldY - speed) / gp.tileSize;
                if (entityTopRow < 0) { gameObject.collisionOn = true; return; } // Kiểm tra biên trên
                if (entityLeftCol >= 0 && entityLeftCol < gp.maxWorldCol && entityRightCol >= 0 && entityRightCol < gp.maxWorldCol) { //Đảm bảo col hợp lệ
                    tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                    tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
                    if ((gp.tileM.tile[tileNum1] != null && gp.tileM.tile[tileNum1].collision) ||
                            (gp.tileM.tile[tileNum2] != null && gp.tileM.tile[tileNum2].collision)) {
                        gameObject.collisionOn = true;
                    }
                } else { gameObject.collisionOn = true; } // Nếu col không hợp lệ
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + speed) / gp.tileSize;
                if (entityBottomRow >= gp.maxWorldRow) { gameObject.collisionOn = true; return; } // Kiểm tra biên dưới
                if (entityLeftCol >= 0 && entityLeftCol < gp.maxWorldCol && entityRightCol >= 0 && entityRightCol < gp.maxWorldCol) {
                    tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
                    tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
                    if ((gp.tileM.tile[tileNum1] != null && gp.tileM.tile[tileNum1].collision) ||
                            (gp.tileM.tile[tileNum2] != null && gp.tileM.tile[tileNum2].collision)) {
                        gameObject.collisionOn = true;
                    }
                } else { gameObject.collisionOn = true; }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - speed) / gp.tileSize;
                if (entityLeftCol < 0) { gameObject.collisionOn = true; return; } // Kiểm tra biên trái
                if (entityTopRow >=0 && entityTopRow < gp.maxWorldRow && entityBottomRow >=0 && entityBottomRow < gp.maxWorldRow) { // Đảm bảo row hợp lệ
                    tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                    tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
                    if ((gp.tileM.tile[tileNum1] != null && gp.tileM.tile[tileNum1].collision) ||
                            (gp.tileM.tile[tileNum2] != null && gp.tileM.tile[tileNum2].collision)) {
                        gameObject.collisionOn = true;
                    }
                } else { gameObject.collisionOn = true; }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + speed) / gp.tileSize;
                if (entityRightCol >= gp.maxWorldCol) { gameObject.collisionOn = true; return; } // Kiểm tra biên phải
                if (entityTopRow >=0 && entityTopRow < gp.maxWorldRow && entityBottomRow >=0 && entityBottomRow < gp.maxWorldRow) {
                    tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
                    tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
                    if ((gp.tileM.tile[tileNum1] != null && gp.tileM.tile[tileNum1].collision) ||
                            (gp.tileM.tile[tileNum2] != null && gp.tileM.tile[tileNum2].collision)) {
                        gameObject.collisionOn = true;
                    }
                } else { gameObject.collisionOn = true; }
                break;
        }
    }

    // Kiểm tra va chạm của một GameObject với mảng các OBject (ví dụ: items, obstacles)
    public int checkObject(GameObject gameObject, boolean isPlayerChecking) {
        int index = 999;

        String direction = "down"; // Hướng mặc định
        int speed = 0;

        if (gameObject instanceof Character) {
            Character character = (Character) gameObject;
            direction = character.direction;
            speed = character.speed;
            if (character.knockBack) {
                direction = character.knockBackDirection;
            }
        } else if (gameObject instanceof Projectile) {
            Projectile projectile = (Projectile) gameObject;
            direction = projectile.direction;
            speed = projectile.speed;
        }

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) { // Giả sử gp.obj là OBject[][]
            OBject currentObject = gp.obj[gp.currentMap][i];
            if (currentObject != null) {
                // Lưu vị trí solidArea hiện tại của gameObject
                int originalSolidAreaX = gameObject.solidArea.x;
                int originalSolidAreaY = gameObject.solidArea.y;

                // Tính toán vị trí solidArea tuyệt đối của gameObject
                gameObject.solidArea.x = gameObject.worldX + originalSolidAreaX;
                gameObject.solidArea.y = gameObject.worldY + originalSolidAreaY;

                // Tính toán vị trí solidArea tuyệt đối của currentObject
                currentObject.solidArea.x = currentObject.worldX + currentObject.solidArea.x; // Giả sử currentObject.solidArea.x là offset
                currentObject.solidArea.y = currentObject.worldY + currentObject.solidArea.y; // Giả sử currentObject.solidArea.y là offset


                switch (direction) {
                    case "up": gameObject.solidArea.y -= speed; break;
                    case "down": gameObject.solidArea.y += speed; break;
                    case "left": gameObject.solidArea.x -= speed; break;
                    case "right": gameObject.solidArea.x += speed; break;
                }

                if (gameObject.solidArea.intersects(currentObject.solidArea)) {
                    if (currentObject.collision) { // 'collision' là thuộc tính của GameObject
                        gameObject.collisionOn = true;
                    }
                    if (isPlayerChecking) { // Chỉ player mới có thể nhặt object
                        index = i;
                    }
                }
                // Reset vị trí solidArea về offset tương đối
                gameObject.solidArea.x = originalSolidAreaX; // Hoặc gameObject.solidAreaDefaultX
                gameObject.solidArea.y = originalSolidAreaY; // Hoặc gameObject.solidAreaDefaultY

                currentObject.solidArea.x = currentObject.solidAreaDefaultX;
                currentObject.solidArea.y = currentObject.solidAreaDefaultY;
            }
        }
        return index;
    }

    // Kiểm tra va chạm của một GameObject với một mảng các Character (NPC hoặc Monster)
    public int checkCreatures(GameObject gameObject, Character[][] targets) {
        int index = 999;

        String direction = "down";
        int speed = 0;

        if (gameObject instanceof Character) {
            Character c = (Character) gameObject;
            direction = c.direction;
            speed = c.speed;
            if (c.knockBack) {
                direction = c.knockBackDirection;
            }
        } else if (gameObject instanceof Projectile) {
            Projectile p = (Projectile) gameObject;
            direction = p.direction;
            speed = p.speed;
        }


        for (int i = 0; i < targets[gp.currentMap].length; i++) {
            Character targetCharacter = targets[gp.currentMap][i];
            if (targetCharacter != null && targetCharacter != gameObject) { // Không tự va chạm với chính mình

                int originalSolidAreaX_gameObject = gameObject.solidArea.x;
                int originalSolidAreaY_gameObject = gameObject.solidArea.y;
                gameObject.solidArea.x = gameObject.worldX + originalSolidAreaX_gameObject;
                gameObject.solidArea.y = gameObject.worldY + originalSolidAreaY_gameObject;

                int originalSolidAreaX_target = targetCharacter.solidArea.x;
                int originalSolidAreaY_target = targetCharacter.solidArea.y;
                targetCharacter.solidArea.x = targetCharacter.worldX + originalSolidAreaX_target;
                targetCharacter.solidArea.y = targetCharacter.worldY + originalSolidAreaY_target;


                switch (direction) { // Hướng di chuyển của gameObject
                    case "up": gameObject.solidArea.y -= speed; break;
                    case "down": gameObject.solidArea.y += speed; break;
                    case "left": gameObject.solidArea.x -= speed; break;
                    case "right": gameObject.solidArea.x += speed; break;
                }

                if (gameObject.solidArea.intersects(targetCharacter.solidArea)) {
                    // Tất cả Character đều có 'collision' (kế thừa từ GameObject),
                    // và mặc định là true cho NPC/Monster để không đi xuyên qua nhau.
                    if (targetCharacter.collision) {
                        gameObject.collisionOn = true;
                    }
                    index = i; // Trả về index của creature bị va chạm
                }

                gameObject.solidArea.x = originalSolidAreaX_gameObject; // Reset
                gameObject.solidArea.y = originalSolidAreaY_gameObject;
                targetCharacter.solidArea.x = originalSolidAreaX_target; // Reset
                targetCharacter.solidArea.y = originalSolidAreaY_target;
            }
        }
        return index;
    }

    // Kiểm tra va chạm của một GameObject với Player
    public boolean checkPlayer(GameObject gameObject) { // Thay Entity bằng GameObject
        boolean contactPlayer = false;

        // Player không thể tự va chạm với chính nó theo logic này
        if (gameObject == gp.player) {
            return false;
        }

        // Lưu vị trí solidArea gốc để reset
        int originalGameObjectSolidAreaX = gameObject.solidArea.x;
        int originalGameObjectSolidAreaY = gameObject.solidArea.y;
        int originalPlayerSolidAreaX = gp.player.solidArea.x;
        int originalPlayerSolidAreaY = gp.player.solidArea.y;

        // Tính toán vị trí solidArea tuyệt đối của gameObject
        gameObject.solidArea.x = gameObject.worldX + originalGameObjectSolidAreaX;
        gameObject.solidArea.y = gameObject.worldY + originalGameObjectSolidAreaY;

        // Tính toán vị trí solidArea tuyệt đối của Player
        gp.player.solidArea.x = gp.player.worldX + originalPlayerSolidAreaX;
        gp.player.solidArea.y = gp.player.worldY + originalPlayerSolidAreaY;

        // Không cần dự đoán vị trí của Player ở đây, chỉ kiểm tra giao nhau hiện tại.
        // Nếu gameObject đang di chuyển, thì solidArea của nó đã được điều chỉnh (nếu logic gọi checkPlayer sau khi dự đoán vị trí).
        // Hoặc, nếu checkPlayer được gọi để xem gameObject CÓ THỂ va chạm player không nếu nó di chuyển,
        // thì logic di chuyển solidArea của gameObject nên được thực hiện TRƯỚC khi gọi intersects.
        // Code gốc của bạn có vẻ như điều chỉnh solidArea của gameObject (entity) dựa trên hướng của nó.
        // Chúng ta sẽ giữ lại logic đó, nhưng cần đảm bảo gameObject có direction và speed.

        String direction = "down";
        int speed = 0;
        if (gameObject instanceof Character) {
            Character c = (Character) gameObject;
            direction = c.direction; // Giả sử Character có direction
            speed = c.speed;     // Giả sử Character có speed
        } else if (gameObject instanceof Projectile) {
            Projectile p = (Projectile) gameObject;
            direction = p.direction; // Projectile có direction
            speed = p.speed;     // Projectile có speed
        }
        // Nếu các GameObject khác cũng di chuyển và cần kiểm tra, thêm logic tương tự


        switch (direction) { // Hướng của gameObject
            case "up": gameObject.solidArea.y -= speed; break;
            case "down": gameObject.solidArea.y += speed; break;
            case "left": gameObject.solidArea.x -= speed; break;
            case "right": gameObject.solidArea.x += speed; break;
        }

        if (gameObject.solidArea.intersects(gp.player.solidArea)) {
            if (gp.player.collision) { // Nếu player cũng có cờ collision (thường là true)
                gameObject.collisionOn = true; // Đặt cờ va chạm cho gameObject
            }
            contactPlayer = true;
        }

        // Reset vị trí solidArea về offset tương đối (RẤT QUAN TRỌNG)
        gameObject.solidArea.x = originalGameObjectSolidAreaX; // Hoặc gameObject.solidAreaDefaultX
        gameObject.solidArea.y = originalGameObjectSolidAreaY; // Hoặc gameObject.solidAreaDefaultY
        gp.player.solidArea.x = originalPlayerSolidAreaX;       // Hoặc gp.player.solidAreaDefaultX
        gp.player.solidArea.y = originalPlayerSolidAreaY;       // Hoặc gp.player.solidAreaDefaultY

        return contactPlayer;
    }

    // Bạn có thể cần thêm phương thức checkInteractiveTile nếu logic phức tạp
    public int checkInteractiveTile(GameObject gameObject, InteractiveTile[][] iTiles) {
        int index = 999;
        // Logic tương tự như checkObject hoặc checkCreatures
        // Duyệt qua mảng iTiles[gp.currentMap]
        // Kiểm tra gameObject.solidArea.intersects(iTile.solidArea)
        // ...
        return index;
    }
}