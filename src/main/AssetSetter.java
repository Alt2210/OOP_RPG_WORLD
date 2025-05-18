package main;

import entity.NPC_OldMan;
import item.Item_Chest;
import item.Item_Door;
import item.Item_Key;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setItem() {
        gp.item[0] = new Item_Key();
        gp.item[0].worldX = 35 * gp.tileSize;
        gp.item[0].worldY = 35 * gp.tileSize;

        gp.item[1] = new Item_Key();
        gp.item[1].worldX = 35 * gp.tileSize;
        gp.item[1].worldY = 38 * gp.tileSize;

        gp.item[2] = new Item_Door();
        gp.item[2].worldX = 35 * gp.tileSize;
        gp.item[2].worldY = 40 * gp.tileSize;

        gp.item[3] = new Item_Door();
        gp.item[3].worldX = 35 * gp.tileSize;
        gp.item[3].worldY = 42 * gp.tileSize;

        gp.item[4] = new Item_Door();
        gp.item[4].worldX = 35 * gp.tileSize;
        gp.item[4].worldY = 44 * gp.tileSize;

        gp.item[5] = new Item_Chest();
        gp.item[5].worldX = 35 * gp.tileSize;
        gp.item[5].worldY = 43 * gp.tileSize;


    }
    public void setNPC(){
        gp.npc[0] = new NPC_OldMan(gp);
        gp.npc[0].worldX = gp.tileSize*21;
        gp.npc[0].worldY = gp.tileSize*21;
    }
}
