package main;

import character.Character;
import character.role.Player;
import character.monster.Monster; // Quan trọng: Thêm import cho Monster
import worldObject.WorldObject;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class CollisionChecker {

    private GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Character entity) {
        int entityLeftWorldX = entity.getWorldX() + entity.getSolidArea().x;
        int entityRightWorldX = entity.getWorldX() + entity.getSolidArea().x + entity.getSolidArea().width;
        int entityTopWorldY = entity.getWorldY() + entity.getSolidArea().y;
        int entityBottomWorldY = entity.getWorldY() + entity.getSolidArea().y + entity.getSolidArea().height;

        int entityLeftCol = entityLeftWorldX / gp.getTileSize();
        int entityRightCol = entityRightWorldX / gp.getTileSize();
        int entityTopRow = entityTopWorldY / gp.getTileSize();
        int entityBottomRow = entityBottomWorldY / gp.getTileSize();

        int tileNum1, tileNum2;

        switch (entity.getDirection()) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.getSpeed()) / gp.getTileSize();
                // Kiểm tra xem có ra ngoài biên trên hoặc các biên khác không
                if (entityTopRow < 0 || entityLeftCol < 0 || entityRightCol >= gp.getMaxWorldCol() || entityLeftCol >= gp.getMaxWorldCol() || entityRightCol < 0 || entityTopRow >= gp.getMaxWorldRow()) {
                    entity.setCollisionOn(true);
                    return;
                }
                tileNum1 = gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][entityLeftCol][entityTopRow];
                tileNum2 = gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][entityRightCol][entityTopRow];
                if (gp.getTileM().getTile()[tileNum1].isCollision() || gp.getTileM().getTile()[tileNum2].isCollision()) {
                    entity.setCollisionOn(true);
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / gp.getTileSize();
                if (entityBottomRow >= gp.getMaxWorldRow() || entityLeftCol < 0 || entityRightCol >= gp.getMaxWorldCol() || entityLeftCol >= gp.getMaxWorldCol() || entityRightCol < 0 || entityBottomRow < 0) {
                    entity.setCollisionOn(true);
                    return;
                }
                tileNum1 = gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][entityLeftCol][entityBottomRow];
                tileNum2 = gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][entityRightCol][entityBottomRow];
                if (gp.getTileM().getTile()[tileNum1].isCollision() || gp.getTileM().getTile()[tileNum2].isCollision()) {
                    entity.setCollisionOn(true);
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / gp.getTileSize();
                if (entityLeftCol < 0 || entityTopRow < 0 || entityBottomRow >= gp.getMaxWorldRow() || entityTopRow >= gp.getMaxWorldRow() || entityBottomRow < 0 || entityLeftCol >= gp.getMaxWorldCol()) {
                    entity.setCollisionOn(true);
                    return;
                }
                tileNum1 = gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][entityLeftCol][entityTopRow];
                tileNum2 = gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][entityLeftCol][entityBottomRow];
                if (gp.getTileM().getTile()[tileNum1].isCollision() || gp.getTileM().getTile()[tileNum2].isCollision()) {
                    entity.setCollisionOn(true);
                }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.getSpeed()) / gp.getTileSize();
                if (entityRightCol >= gp.getMaxWorldCol() || entityTopRow < 0 || entityBottomRow >= gp.getMaxWorldRow() || entityTopRow >= gp.getMaxWorldRow() || entityBottomRow < 0 || entityRightCol < 0 ) {
                    entity.setCollisionOn(true);
                    return;
                }
                tileNum1 = gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][entityRightCol][entityTopRow];
                tileNum2 = gp.getTileM().getMapTileNum()[gp.getCurrentMapIndex()][entityRightCol][entityBottomRow];
                if (gp.getTileM().getTile()[tileNum1].isCollision() || gp.getTileM().getTile()[tileNum2].isCollision()) {
                    entity.setCollisionOn(true);
                }
                break;
        }
    }

    public int checkEntity(Character entity, java.util.List<? extends Character> targetList) { // MODIFIED
        int index = 999; // Giá trị mặc định nếu không có va chạm

        // Vùng va chạm hiện tại của entity đang kiểm tra
        Rectangle entityCurrentBounds = new Rectangle(
                entity.getWorldX() + entity.getSolidArea().x,
                entity.getWorldY() + entity.getSolidArea().y,
                entity.getSolidArea().width,
                entity.getSolidArea().height
        );

        // MODIFIED: Loop through List
        for (int i = 0; i < targetList.size(); i++) { // MODIFIED
            // MODIFIED: Access element using .get()
            if (targetList.get(i) != null && targetList.get(i) != entity) { // MODIFIED

                // Vùng va chạm hiện tại của mục tiêu
                Rectangle targetCurrentBounds = new Rectangle(
                        // MODIFIED: Access element using .get()
                        targetList.get(i).getWorldX() + targetList.get(i).getSolidArea().x, // MODIFIED
                        targetList.get(i).getWorldY() + targetList.get(i).getSolidArea().y, // MODIFIED
                        targetList.get(i).getSolidArea().width, // MODIFIED
                        targetList.get(i).getSolidArea().height // MODIFIED
                );

                // Tính toán vùng va chạm dự kiến của entity ở bước di chuyển tiếp theo
                Rectangle entityNextStepBounds = new Rectangle(entityCurrentBounds);
                switch (entity.getDirection()) {
                    case "up": entityNextStepBounds.y -= entity.getSpeed(); break;
                    case "down": entityNextStepBounds.y += entity.getSpeed(); break;
                    case "left": entityNextStepBounds.x -= entity.getSpeed(); break;
                    case "right": entityNextStepBounds.x += entity.getSpeed(); break;
                }

                // Kiểm tra nếu vùng va chạm dự kiến của entity giao với vùng va chạm hiện tại của mục tiêu
                if (entityNextStepBounds.intersects(targetCurrentBounds)) {
                    entity.setCollisionOn(true); // Đặt cờ va chạm cho entity đang kiểm tra
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
                entity.getWorldX() + entity.getSolidArea().x,
                entity.getWorldY() + entity.getSolidArea().y,
                entity.getSolidArea().width,
                entity.getSolidArea().height
        );
        // Chỉ điều chỉnh bounds nếu entity thực sự đang di chuyển (speed > 0)
        if (entity.getSpeed() > 0) {
            switch (entity.getDirection()) {
                case "up": entityNextStepBounds.y -= entity.getSpeed(); break;
                case "down": entityNextStepBounds.y += entity.getSpeed(); break;
                case "left": entityNextStepBounds.x -= entity.getSpeed(); break;
                case "right": entityNextStepBounds.x += entity.getSpeed(); break;
            }
        }

        // Vùng va chạm hiện tại của Player
        Rectangle playerCurrentBounds = new Rectangle(
                player.getWorldX() + player.getSolidArea().x,
                player.getWorldY() + player.getSolidArea().y,
                player.getSolidArea().width,
                player.getSolidArea().height
        );

        // Kiểm tra xem vùng va chạm dự kiến (hoặc hiện tại nếu entity đứng yên) của Monster
        // có giao với vùng va chạm hiện tại của Player không.
        if (entityNextStepBounds.intersects(playerCurrentBounds)) {
            entity.setCollisionOn(true); // Ngăn Monster di chuyển vào/xuyên qua Player
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
        for (int i = 0; i < gp.getCurrentMap().getwObjects().size(); i++) {
            if (gp.getCurrentMap().getwObjects().get(i) != null) {
                Rectangle characterCurrentBounds = new Rectangle(
                        character.getWorldX() + character.getSolidArea().x,
                        character.getWorldY() + character.getSolidArea().y,
                        character.getSolidArea().width,
                        character.getSolidArea().height);

                Rectangle objectBounds = new Rectangle(
                        gp.getCurrentMap().getwObjects().get(i).getWorldX() + gp.getCurrentMap().getwObjects().get(i).getSolidArea().x,
                        gp.getCurrentMap().getwObjects().get(i).getWorldY() + gp.getCurrentMap().getwObjects().get(i).getSolidArea().y,
                        gp.getCurrentMap().getwObjects().get(i).getSolidArea().width,
                        gp.getCurrentMap().getwObjects().get(i).getSolidArea().height);

                Rectangle characterNextStepBounds = new Rectangle(characterCurrentBounds);
                if (character.getSpeed() > 0) { // Chỉ điều chỉnh nếu đang di chuyển
                    switch (character.getDirection()) {
                        case "up": characterNextStepBounds.y -= character.getSpeed(); break;
                        case "down": characterNextStepBounds.y += character.getSpeed(); break;
                        case "left": characterNextStepBounds.x -= character.getSpeed(); break;
                        case "right": characterNextStepBounds.x += character.getSpeed(); break;
                    }
                }

                if (characterNextStepBounds.intersects(objectBounds)) {
                    if (gp.getCurrentMap().getwObjects().get(i).isCollision()) { // Nếu item đó là vật cản
                        character.setCollisionOn(true);
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