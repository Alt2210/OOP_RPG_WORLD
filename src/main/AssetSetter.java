package main;
import character.*;
import character.NPC_OldMan;
import character.monster.MON_GreenSlime;
import character.monster.Monster;
import item.Item_Key;
import worldObject.pickableObject.OBJ_Key;
import worldObject.unpickableObject.OBJ_Chest;
import worldObject.unpickableObject.OBJ_Door;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setWObjects() {
        gp.getwObjects()[0] = new OBJ_Key(gp);
        gp.getwObjects()[0].worldX = 35 * gp.getTileSize();
        gp.getwObjects()[0].worldY = 35 * gp.getTileSize();

        gp.getwObjects()[1] = new OBJ_Key(gp);
        gp.getwObjects()[1].worldX = 35 * gp.getTileSize();
        gp.getwObjects()[1].worldY = 38 * gp.getTileSize();

        gp.getwObjects()[2] = new OBJ_Door();
        gp.getwObjects()[2].worldX = 35 * gp.getTileSize();
        gp.getwObjects()[2].worldY = 40 * gp.getTileSize();

        gp.getwObjects()[3] = new OBJ_Door();
        gp.getwObjects()[3].worldX = 35 * gp.getTileSize();
        gp.getwObjects()[3].worldY = 42 * gp.getTileSize();

        gp.getwObjects()[4] = new OBJ_Door();
        gp.getwObjects()[4].worldX = 35 * gp.getTileSize();
        gp.getwObjects()[4].worldY = 44 * gp.getTileSize();

        gp.getwObjects()[5] = new OBJ_Chest();
        gp.getwObjects()[5].worldX = 35 * gp.getTileSize();
        gp.getwObjects()[5].worldY = 43 * gp.getTileSize();


    }
    public void setNPC(){
        gp.getNpc()[0] = new NPC_OldMan(gp);
        gp.getNpc()[0].worldX = gp.getTileSize()*21;
        gp.getNpc()[0].worldY = gp.getTileSize()*21;
        gp.getNpc()[1] = new NPC_Princess(gp);
        gp.getNpc()[1].worldX = gp.getTileSize()*25;
        gp.getNpc()[1].worldY = gp.getTileSize()*25;
    }
    public void setGreenSlime(){
        gp.getMON_GreenSlime()[0] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[0].worldX = gp.getTileSize()*33;
        gp.getMON_GreenSlime()[0].worldY = gp.getTileSize()*33;
    }

}
