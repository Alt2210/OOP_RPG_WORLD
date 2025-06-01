package main;

import character.Character;
import character.Player;
import character.monster.Monster; // Quan trọng: Thêm import cho Monster

import java.awt.Rectangle;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Character entity) {
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.getTileSize();
        int entityRightCol = entityRightWorldX / gp.getTileSize();
        int entityTopRow = entityTopWorldY / gp.getTileSize();
        int entityBottomRow = entityBottomWorldY / gp.getTileSize();

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gp.getTileSize();
                // Kiểm tra xem có ra ngoài biên trên hoặc các biên khác không
                if (entityTopRow < 0 || entityLeftCol < 0 || entityRightCol >= gp.getMaxWorldCol() || entityLeftCol >= gp.getMaxWorldCol() || entityRightCol < 0 || entityTopRow >= gp.getMaxWorldRow()) {
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.getTileM().mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.getTileM().mapTileNum[entityRightCol][entityTopRow];
                if (gp.getTileM().tile[tileNum1].collision || gp.getTileM().tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.getTileSize();
                if (entityBottomRow >= gp.getMaxWorldRow() || entityLeftCol < 0 || entityRightCol >= gp.getMaxWorldCol() || entityLeftCol >= gp.getMaxWorldCol() || entityRightCol < 0 || entityBottomRow < 0) {
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.getTileM().mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.getTileM().mapTileNum[entityRightCol][entityBottomRow];
                if (gp.getTileM().tile[tileNum1].collision || gp.getTileM().tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.getTileSize();
                if (entityLeftCol < 0 || entityTopRow < 0 || entityBottomRow >= gp.getMaxWorldRow() || entityTopRow >= gp.getMaxWorldRow() || entityBottomRow < 0 || entityLeftCol >= gp.getMaxWorldCol()) {
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.getTileM().mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.getTileM().mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.getTileM().tile[tileNum1].collision || gp.getTileM().tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gp.getTileSize();
                if (entityRightCol >= gp.getMaxWorldCol() || entityTopRow < 0 || entityBottomRow >= gp.getMaxWorldRow() || entityTopRow >= gp.getMaxWorldRow() || entityBottomRow < 0 || entityRightCol < 0 ) {
                    entity.collisionOn = true;
                    return;
                }
                tileNum1 = gp.getTileM().mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.getTileM().mapTileNum[entityRightCol][entityBottomRow];
                if (gp.getTileM().tile[tileNum1].collision || gp.getTileM().tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    public int checkEntity(Character entity, Character[] targetArray) {
        int index = 999; // Giá trị mặc định nếu không có va chạm

        // Vùng va chạm hiện tại của entity đang kiểm tra
        Rectangle entityCurrentBounds = new Rectangle(
                entity.worldX + entity.solidArea.x,
                entity.worldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height
        );

        for (int i = 0; i < targetArray.length; i++) {
            if (targetArray[i] != null && targetArray[i] != entity) { // Mục tiêu tồn tại và không phải là chính entity đó

                // Vùng va chạm hiện tại của mục tiêu
                Rectangle targetCurrentBounds = new Rectangle(
                        targetArray[i].worldX + targetArray[i].solidArea.x,
                        targetArray[i].worldY + targetArray[i].solidArea.y,
                        targetArray[i].solidArea.width,
                        targetArray[i].solidArea.height
                );

                // Tính toán vùng va chạm dự kiến của entity ở bước di chuyển tiếp theo
                Rectangle entityNextStepBounds = new Rectangle(entityCurrentBounds);
                switch (entity.direction) {
                    case "up": entityNextStepBounds.y -= entity.speed; break;
                    case "down": entityNextStepBounds.y += entity.speed; break;
                    case "left": entityNextStepBounds.x -= entity.speed; break;
                    case "right": entityNextStepBounds.x += entity.speed; break;
                }

                // Kiểm tra nếu vùng va chạm dự kiến của entity giao với vùng va chạm hiện tại của mục tiêu
                if (entityNextStepBounds.intersects(targetCurrentBounds)) {
                    entity.collisionOn = true; // Đặt cờ va chạm cho entity đang kiểm tra
                    index = i; // Trả về chỉ số của mục tiêu đã va chạm
                    // (Hữu ích cho Player biết đã va chạm NPC/Monster nào, hoặc Monster biết đã va chạm Player)
                }
            }
        }
        return index;
    }


    public boolean checkPlayer(Character entity) { // entity ở đây là Monster (hoặc NPC nếu muốn)
        boolean contactOccurred = false;
        Player player = gp.getPlayer();

        // Không xử lý nếu Player đã chết
        if (player.getCurrentHealth() <= 0) {
            return false;
        }

        // Vùng va chạm dự đoán của entity (Monster) ở bước di chuyển tiếp theo
        Rectangle entityNextStepBounds = new Rectangle(
                entity.worldX + entity.solidArea.x,
                entity.worldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height
        );
        // Chỉ điều chỉnh bounds nếu entity thực sự đang di chuyển (speed > 0)
        if (entity.speed > 0) {
            switch (entity.direction) {
                case "up": entityNextStepBounds.y -= entity.speed; break;
                case "down": entityNextStepBounds.y += entity.speed; break;
                case "left": entityNextStepBounds.x -= entity.speed; break;
                case "right": entityNextStepBounds.x += entity.speed; break;
            }
        }

        // Vùng va chạm hiện tại của Player
        Rectangle playerCurrentBounds = new Rectangle(
                player.worldX + player.solidArea.x,
                player.worldY + player.solidArea.y,
                player.solidArea.width,
                player.solidArea.height
        );

        // Kiểm tra xem vùng va chạm dự kiến (hoặc hiện tại nếu entity đứng yên) của Monster
        // có giao với vùng va chạm hiện tại của Player không.
        if (entityNextStepBounds.intersects(playerCurrentBounds)) {
            entity.collisionOn = true; // Ngăn Monster di chuyển vào/xuyên qua Player
            contactOccurred = true;

            // Nếu entity là một Monster và có thể gây sát thương chạm (chưa cooldown)
            if (entity instanceof Monster) {
                Monster monster = (Monster) entity;
                if (monster.canDealContactDamage()) {
                    // System.out.println("[CONTACT_DAMAGE] " + monster.getName() + " chạm Player. HP Player trước: " + player.getCurrentHealth() + ", Cooldown Monster: " + monster.contactDamageCooldown);
                    player.receiveDamage(monster.getContactDamageAmount(), monster); // Player nhận sát thương
                    // System.out.println("[CONTACT_DAMAGE] HP Player sau: " + player.getCurrentHealth());
                    monster.resetContactDamageCooldown(); // Đặt lại cooldown sát thương chạm cho Monster này
                    // gp.playSoundEffect(Sound.SFX_PLAYER_HURT); // Ví dụ âm thanh Player bị thương
                } else {
                    // System.out.println("[CONTACT_INFO] " + monster.getName() + " chạm Player nhưng đang trong cooldown sát thương chạm (" + monster.contactDamageCooldown + ")");
                }
            }
        }
        return contactOccurred;
    }

    public int checkItem(Character character, boolean isPlayer) {
        int index = 999;
        for (int i = 0; i < gp.getwObjects().length; i++) {
            if (gp.getwObjects()[i] != null) {
                Rectangle characterCurrentBounds = new Rectangle(
                        character.worldX + character.solidArea.x,
                        character.worldY + character.solidArea.y,
                        character.solidArea.width,
                        character.solidArea.height);

                Rectangle objectBounds = new Rectangle(
                        gp.getwObjects()[i].worldX + gp.getwObjects()[i].solidArea.x,
                        gp.getwObjects()[i].worldY + gp.getwObjects()[i].solidArea.y,
                        gp.getwObjects()[i].solidArea.width,
                        gp.getwObjects()[i].solidArea.height);

                Rectangle characterNextStepBounds = new Rectangle(characterCurrentBounds);
                if (character.speed > 0) { // Chỉ điều chỉnh nếu đang di chuyển
                    switch (character.direction) {
                        case "up": characterNextStepBounds.y -= character.speed; break;
                        case "down": characterNextStepBounds.y += character.speed; break;
                        case "left": characterNextStepBounds.x -= character.speed; break;
                        case "right": characterNextStepBounds.x += character.speed; break;
                    }
                }

                if (characterNextStepBounds.intersects(objectBounds)) {
                    if (gp.getwObjects()[i].collision) { // Nếu item đó là vật cản
                        character.collisionOn = true;
                    }
                    if (isPlayer) { // Nếu người kiểm tra là player (để nhặt item)
                        index = i;
                    }
                }
            }
        }
        return index;
    }

    }