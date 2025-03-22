package main;

import Entity.Entity;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp){
        this.gp = gp;
    }

    public void checkTile(Entity entity){

        int entityLeftWorldX = entity.WorldX + entity.solidArea.x;
        int entityRightWorldX = entity.WorldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.WorldY + entity.solidArea.y;
        int entityBottomWorldY = entity.WorldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX/gp.tileSize;
        int entityRightCol = entityRightWorldX/gp.tileSize;
        int entityTopRow = entityTopWorldY/gp.tileSize;
        int entityBottomRow = entityBottomWorldY/gp.tileSize;

        int tile1, tile2;

        switch(entity.direction){
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
                tile1 = gp.tileM.mapTileNum[entityTopRow][entityLeftCol];
                tile2 = gp.tileM.mapTileNum[entityTopRow][entityRightCol];
                if(gp.tileM.tile[tile1].collision || gp.tileM.tile[tile2].collision){
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed)/gp.tileSize;
                tile1 = gp.tileM.mapTileNum[entityBottomRow][entityLeftCol];
                tile2 = gp.tileM.mapTileNum[entityBottomRow][entityRightCol];
                if(gp.tileM.tile[tile1].collision || gp.tileM.tile[tile2].collision){
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed)/gp.tileSize;
                tile1 = gp.tileM.mapTileNum[entityTopRow][entityLeftCol];
                tile2 = gp.tileM.mapTileNum[entityBottomRow][entityLeftCol];
                if(gp.tileM.tile[tile1].collision || gp.tileM.tile[tile2].collision){
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed)/gp.tileSize;
                tile1 = gp.tileM.mapTileNum[entityTopRow][entityRightCol];
                tile2 = gp.tileM.mapTileNum[entityBottomRow][entityRightCol];
                if(gp.tileM.tile[tile1].collision || gp.tileM.tile[tile2].collision){
                    entity.collisionOn = true;
                }
                break;

        }

    }
}
