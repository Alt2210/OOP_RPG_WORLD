package main;
import character.*;
import character.Character;
import character.monster.*;
import worldObject.pickableObject.OBJ_HealthPotion;
import worldObject.pickableObject.OBJ_Key;
import worldObject.pickableObject.OBJ_ManaPotion;
import worldObject.unpickableObject.OBJ_Chest;
import worldObject.unpickableObject.OBJ_Door;
import worldObject.unpickableObject.OBJ_Portal; // THÊM IMPORT CHO PORTAL

import java.util.ArrayList;
import java.util.List;


public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }
    public void setupMapAssets(int mapIndex) {
        System.out.println("AssetSetter: Setting up assets for map " + mapIndex);

        if (mapIndex == 0) { // Map "Plain"
            setMap0_Objects();
            setMap0_NPCs();
            setMap0_Monsters();
        } else if (mapIndex == 1) { // Map "Dungeon"
            setMap1_Objects();
            setMap1_NPCs();
            setMap1_Monsters();
        }
        // Thêm else if cho các map khác nếu có
        // else if (mapIndex == 2) { ... }
    }
    public void removeDeadMonster(MON_GreenSlime[] monsterArray, int index, int mapIndex) { //
        if (monsterArray != null && index >= 0 && index < monsterArray.length && monsterArray[index] != null) { //
            System.out.println("Removing " + monsterArray[index].getName() + " at index " + index + " from map " + mapIndex); //
            monsterArray[index] = null; //
        }
    }

    public void removeDeadMonster(MON_Bat[] monsterArray, int index, int mapIndex) { //
        if (monsterArray != null && index >= 0 && index < monsterArray.length && monsterArray[index] != null) { //
            System.out.println("Removing " + monsterArray[index].getName() + " at index " + index + " from map " + mapIndex); //
            monsterArray[index] = null; //
        }
    }
    public void removeDeadMonster(MON_GolemBoss[] monsterArray, int index, int mapIndex) { //
        if (monsterArray != null && index >= 0 && index < monsterArray.length && monsterArray[index] != null) { //
            System.out.println("Removing " + monsterArray[index].getName() + " at index " + index + " from map " + mapIndex); //
            monsterArray[index] = null; //
        }
    }
    public void removeDeadMonster(MON_Orc[] monsterArray, int index, int mapIndex) {
        if (monsterArray != null && index >= 0 && index < monsterArray.length && monsterArray[index] != null) {
            System.out.println("Removing " + monsterArray[index].getName() + " at index " + index + " from map " + mapIndex);
            monsterArray[index] = null;
        }
    }
    public void removeDeadMonster(MON_SkeletonLord[] monsterArray, int index, int mapIndex) {
        if (monsterArray != null && index >= 0 && index < monsterArray.length && monsterArray[index] != null) {
            System.out.println("Removing " + monsterArray[index].getName() + " at index " + index + " from map " + mapIndex);
            monsterArray[index] = null;
        }
    }


    // đặt objects, NPCs, Monsters cho map 0
    private void setMap0_Objects() {
        gp.getwObjects()[0] = new OBJ_Key(gp);
        gp.getwObjects()[0].worldX = 20 * gp.getTileSize();
        gp.getwObjects()[0].worldY = 20 * gp.getTileSize();

        gp.getwObjects()[3] = new OBJ_ManaPotion(gp);
        gp.getwObjects()[3].worldX = 19 * gp.getTileSize();
        gp.getwObjects()[3].worldY = 20 * gp.getTileSize();

        gp.getwObjects()[4] = new OBJ_HealthPotion(gp);
        gp.getwObjects()[4].worldX = 17 * gp.getTileSize();
        gp.getwObjects()[4].worldY = 20 * gp.getTileSize();

        gp.getwObjects()[1] = new OBJ_Door();
        gp.getwObjects()[1].worldX = 22 * gp.getTileSize();
        gp.getwObjects()[1].worldY = 20 * gp.getTileSize();

        // Portal từ Map 0 (Plain) đến Map 1 (Dungeon)
        gp.getwObjects()[2] = new OBJ_Portal(gp, 1, 10, 12); // targetMap=1, playerTileX_onNewMap=10, playerTileY_onNewMap=12
        gp.getwObjects()[2].worldX = 67 * gp.getTileSize(); // Vị trí portal trên map 0
        gp.getwObjects()[2].worldY = 40 * gp.getTileSize(); // Vị trí portal trên map 0

        gp.getwObjects()[5] = new worldObject.pickableObject.OBJ_Sword(gp);
        gp.getwObjects()[5].worldX = 21 * gp.getTileSize();
        gp.getwObjects()[5].worldY = 21 * gp.getTileSize();

        gp.getwObjects()[6] = new worldObject.pickableObject.OBJ_Book(gp);
        gp.getwObjects()[6].worldX = 22 * gp.getTileSize();
        gp.getwObjects()[6].worldY = 22 * gp.getTileSize();

        System.out.println("Map 0 Objects Set. Portal to map 1 at (67,40) leading to (10,12) on map 1.");
    }

    private void setMap0_NPCs() {
        gp.getNpc()[0] = new NPC_OldMan(gp);
        gp.getNpc()[0].worldX = gp.getTileSize() * 50;
        gp.getNpc()[0].worldY = gp.getTileSize() * 92;
        System.out.println("Map 0 NPCs Set.");
    }

    private void setMap0_Monsters() {
        gp.getMON_GreenSlime()[0] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[0].worldX = gp.getTileSize() * 43;
        gp.getMON_GreenSlime()[0].worldY = gp.getTileSize() * 86;

        gp.getMON_GreenSlime()[1] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[1].worldX = gp.getTileSize() * 33;
        gp.getMON_GreenSlime()[1].worldY = gp.getTileSize() * 91;

        gp.getMON_GreenSlime()[2] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[2].worldX = gp.getTileSize() * 31;
        gp.getMON_GreenSlime()[2].worldY = gp.getTileSize() * 67;

        gp.getMON_GreenSlime()[3] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[3].worldX = gp.getTileSize() * 44;
        gp.getMON_GreenSlime()[3].worldY = gp.getTileSize() * 63;

        gp.getMON_GreenSlime()[4] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[4].worldX = gp.getTileSize() * 12;
        gp.getMON_GreenSlime()[4].worldY = gp.getTileSize() * 42;

        gp.getMON_GreenSlime()[5] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[5].worldX = gp.getTileSize() * 22;
        gp.getMON_GreenSlime()[5].worldY = gp.getTileSize() * 46;

        gp.getMON_GreenSlime()[6] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[6].worldX = gp.getTileSize() * 41;
        gp.getMON_GreenSlime()[6].worldY = gp.getTileSize() * 37;

        gp.getMON_GreenSlime()[7] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[7].worldX = gp.getTileSize() * 56;
        gp.getMON_GreenSlime()[7].worldY = gp.getTileSize() * 31;

        gp.getMON_GreenSlime()[8] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[8].worldX = gp.getTileSize() * 73;
        gp.getMON_GreenSlime()[8].worldY = gp.getTileSize() * 25;

        gp.getMON_GreenSlime()[9] = new MON_GreenSlime(gp);
        gp.getMON_GreenSlime()[9].worldX = gp.getTileSize() * 83;
        gp.getMON_GreenSlime()[9].worldY = gp.getTileSize() * 35;

        gp.getMON_Bat()[0] = new MON_Bat(gp);
        gp.getMON_Bat()[0].worldX = gp.getTileSize()*35;
        gp.getMON_Bat()[0].worldY = gp.getTileSize()*35;

        gp.getMON_Bat()[1] = new MON_Bat(gp);
        gp.getMON_Bat()[1].worldX = gp.getTileSize()*9;
        gp.getMON_Bat()[1].worldY = gp.getTileSize()*51;

        gp.getMON_Bat()[2] = new MON_Bat(gp);
        gp.getMON_Bat()[2].worldX = gp.getTileSize()*4;
        gp.getMON_Bat()[2].worldY = gp.getTileSize()*29;

        gp.getMON_Bat()[3] = new MON_Bat(gp);
        gp.getMON_Bat()[3].worldX = gp.getTileSize()*53;
        gp.getMON_Bat()[3].worldY = gp.getTileSize()*33;

        gp.getMON_Bat()[4] = new MON_Bat(gp);
        gp.getMON_Bat()[4].worldX = gp.getTileSize()*67;
        gp.getMON_Bat()[4].worldY = gp.getTileSize()*39;

        gp.getSkeletonLord()[0] = new MON_SkeletonLord(gp);
        gp.getMON_GreenSlime()[0].worldX = gp.getTileSize() * 49;
        gp.getMON_GreenSlime()[0].worldY = gp.getTileSize() * 91;
        System.out.println("Map 0 Monsters Set.");

    }

    // đặt objects, NPCs, Monsters cho map 1
    private void setMap1_Objects() {
        gp.getwObjects()[0] = new OBJ_Key(gp);
        gp.getwObjects()[0].worldX = 35 * gp.getTileSize();
        gp.getwObjects()[0].worldY = 35 * gp.getTileSize();

        gp.getwObjects()[1] = new OBJ_Chest();
        gp.getwObjects()[1].worldX = 12 * gp.getTileSize();
        gp.getwObjects()[1].worldY = 12 * gp.getTileSize();

        // portal từ map 1 về map 0
        int portalIndex = 2;
        if(gp.getwObjects().length > portalIndex) {
            gp.getwObjects()[portalIndex] = new OBJ_Portal(gp, 0, 26, 25);
            gp.getwObjects()[portalIndex].worldX = 12 * gp.getTileSize();
            gp.getwObjects()[portalIndex].worldY = 10 * gp.getTileSize();
        }
    }

    private void setMap1_NPCs() {
        gp.getNpc()[0] = new NPC_Princess(gp);
        gp.getNpc()[0].worldX = gp.getTileSize() * 74;
        gp.getNpc()[0].worldY = gp.getTileSize() * 84;
        System.out.println("Map 1 NPCs Set.");
    }

    private void setMap1_Monsters() {
        // Ví dụ, đặt Golem Boss ở map Dungeon
        gp.getMON_GolemBoss()[0] = new MON_GolemBoss(gp);
        gp.getMON_GolemBoss()[0].worldX = gp.getTileSize() * 36;
        gp.getMON_GolemBoss()[0].worldY = gp.getTileSize() * 90;

        gp.getMON_GolemBoss()[1] = new MON_GolemBoss(gp);
        gp.getMON_GolemBoss()[1].worldX = gp.getTileSize() * 10;
        gp.getMON_GolemBoss()[1].worldY = gp.getTileSize() * 25;

        gp.getMON_Bat()[0] = new MON_Bat(gp);
        gp.getMON_Bat()[0].worldX = gp.getTileSize()*26;
        gp.getMON_Bat()[0].worldY = gp.getTileSize()*45;

        gp.getMON_Bat()[1] = new MON_Bat(gp);
        gp.getMON_Bat()[1].worldX = gp.getTileSize()*26;
        gp.getMON_Bat()[1].worldY = gp.getTileSize()*43;

        gp.getMON_Bat()[2] = new MON_Bat(gp);
        gp.getMON_Bat()[2].worldX = gp.getTileSize()*36;
        gp.getMON_Bat()[2].worldY = gp.getTileSize()*56;

        gp.getMON_Bat()[3] = new MON_Bat(gp);
        gp.getMON_Bat()[3].worldX = gp.getTileSize()*19;
        gp.getMON_Bat()[3].worldY = gp.getTileSize()*72;

        gp.getMON_Bat()[4] = new MON_Bat(gp);
        gp.getMON_Bat()[4].worldX = gp.getTileSize()*30;
        gp.getMON_Bat()[4].worldY = gp.getTileSize()*72;

        gp.getMON_Orc()[0] = new MON_Orc(gp);
        gp.getMON_Orc()[0].worldX = gp.getTileSize()*19;
        gp.getMON_Orc()[0].worldY = gp.getTileSize()*22;

        gp.getMON_Orc()[1] = new MON_Orc(gp);
        gp.getMON_Orc()[1].worldX = gp.getTileSize()*38;
        gp.getMON_Orc()[1].worldY = gp.getTileSize()*14;

        gp.getMON_Orc()[2] = new MON_Orc(gp);
        gp.getMON_Orc()[2].worldX = gp.getTileSize()*5;
        gp.getMON_Orc()[2].worldY = gp.getTileSize()*25;

        gp.getMON_Orc()[3] = new MON_Orc(gp);
        gp.getMON_Orc()[3].worldX = gp.getTileSize()*19;
        gp.getMON_Orc()[3].worldY = gp.getTileSize()*22;

        gp.getMON_Orc()[4] = new MON_Orc(gp);
        gp.getMON_Orc()[4].worldX = gp.getTileSize()*57;
        gp.getMON_Orc()[4].worldY = gp.getTileSize()*51;

        gp.getMON_Orc()[5] = new MON_Orc(gp);
        gp.getMON_Orc()[5].worldX = gp.getTileSize()*77;
        gp.getMON_Orc()[5].worldY = gp.getTileSize()*45;

        gp.getMON_Orc()[6] = new MON_Orc(gp);
        gp.getMON_Orc()[6].worldX = gp.getTileSize()*89;
        gp.getMON_Orc()[6].worldY = gp.getTileSize()*52;

        gp.getMON_Orc()[7] = new MON_Orc(gp);
        gp.getMON_Orc()[7].worldX = gp.getTileSize()*69;
        gp.getMON_Orc()[7].worldY = gp.getTileSize()*64;

        gp.getMON_Orc()[8] = new MON_Orc(gp);
        gp.getMON_Orc()[8].worldX = gp.getTileSize()*75;
        gp.getMON_Orc()[8].worldY = gp.getTileSize()*67;

        gp.getMON_Orc()[9] = new MON_Orc(gp);
        gp.getMON_Orc()[9].worldX = gp.getTileSize()*25;
        gp.getMON_Orc()[9].worldY = gp.getTileSize()*12;
        System.out.println("Map 1 Monsters Set.");
    }
}