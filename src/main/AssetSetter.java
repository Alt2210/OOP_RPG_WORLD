package main;
import character.monster.*;
import character.sideCharacter.NPC_OldMan;
import character.sideCharacter.NPC_Princess;
import item.itemConsumable.Item_HealthPotion;
import item.itemConsumable.Item_ManaPotion;
import item.itemEquippable.Item_Book;
import item.itemEquippable.Item_Sword;
import worldObject.WorldObject;
import worldObject.pickableObject.*;
import worldObject.unpickableObject.*;


public class AssetSetter {
    private GamePanel gp;

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

    /*public void removeDeadMonster(ArrayList<Monster> monsters, int index, int mapIndex) { //
        if (monsters != null && index >= 0 && index < monsters.size() && monsters.get(index) != null) { //
            System.out.println("Removing " + monsters.get(index).getName() + " at index " + index + " from map " + mapIndex); //
            monsters.remove(index); //
        }
    }*/
    public void spawnMonster(Monster monster, int tileX, int tileY) {
        monster.setWorldX(gp.getTileSize() * tileX);
        monster.setWorldY(gp.getTileSize() * tileY);
        gp.getMonster().add(monster);
    }

    public void spawnObject(WorldObject obj, int tileX, int tileY) {
        obj.setWorldX(gp.getTileSize() * tileX);
        obj.setWorldY(gp.getTileSize() * tileY);

    }

    // đặt objects, NPCs, Monsters cho map 0
    private void setMap0_Objects() {


        gp.getwObjects()[1] = new OBJ_ReviveStatue(gp);
        gp.getwObjects()[1].setWorldX(49 * gp.getTileSize());
        gp.getwObjects()[1].setWorldY(54 * gp.getTileSize());

        // Portal từ Map 0 (Plain) đến Map 1 (Dungeon)
        gp.getwObjects()[2] = new OBJ_Portal(gp, 1, 10, 12); // targetMap=1, playerTileX_onNewMap=10, playerTileY_onNewMap=12
        gp.getwObjects()[2].setWorldX(67 * gp.getTileSize()); // Vị trí portal trên map 0
        gp.getwObjects()[2].setWorldY(40 * gp.getTileSize()); // Vị trí portal trên map 0

        OBJ_Chest chest = new OBJ_Chest(gp);
        chest.getInventory().addItem(new Item_Sword(gp), 1);
        chest.getInventory().addItem(new Item_Book(gp), 1);
        chest.getInventory().addItem(new Item_HealthPotion(gp), 3);
        chest.getInventory().addItem(new Item_ManaPotion(gp), 3);

        gp.getwObjects()[3] = chest;
        gp.getwObjects()[3].setWorldX(12 * gp.getTileSize());
        gp.getwObjects()[3].setWorldY(91 * gp.getTileSize());

        System.out.println("Map 0 Objects Set. Portal to map 1 at (67,40) leading to (10,12) on map 1.");
    }

    private void setMap0_NPCs() {
        gp.getNpc()[0] = new NPC_OldMan(gp);
        gp.getNpc()[0].setWorldX(gp.getTileSize() * 50);
        gp.getNpc()[0].setWorldY(gp.getTileSize() * 92);
        System.out.println("Map 0 NPCs Set.");
    }

    private void setMap0_Monsters(){
        // --- Thêm Green Slimes ---
        spawnMonster(new MON_GreenSlime(gp), 43, 86);
        spawnMonster(new MON_GreenSlime(gp), 33, 91);
        spawnMonster(new MON_GreenSlime(gp), 31, 67);
        spawnMonster(new MON_GreenSlime(gp), 44, 63);
        spawnMonster(new MON_GreenSlime(gp), 12, 42);
        spawnMonster(new MON_GreenSlime(gp), 22, 46);
        spawnMonster(new MON_GreenSlime(gp), 41, 37);
        spawnMonster(new MON_GreenSlime(gp), 56, 31);
        spawnMonster(new MON_GreenSlime(gp), 73, 25);
        spawnMonster(new MON_GreenSlime(gp), 83, 35);

        // --- Thêm Bats ---
        spawnMonster(new MON_Bat(gp), 35, 35);
        spawnMonster(new MON_Bat(gp), 9, 51);
        spawnMonster(new MON_Bat(gp), 4, 29);
        spawnMonster(new MON_Bat(gp), 53, 33);
        spawnMonster(new MON_Bat(gp), 67, 39);

// --- Thêm Skeleton Lord (Boss) ---
        spawnMonster(new MON_SkeletonLord(gp), 49, 33);

        System.out.println("Map 0 Monsters Set.");
    }

    private void setMap1_Objects() {

        gp.getwObjects()[0] = new OBJ_Door();
        gp.getwObjects()[0].setWorldX(74 * gp.getTileSize());
        gp.getwObjects()[0].setWorldY(80 * gp.getTileSize());

        OBJ_Chest chest = new OBJ_Chest(gp);
        gp.getwObjects()[1] = chest;
        gp.getwObjects()[1].setWorldX(12 * gp.getTileSize());
        gp.getwObjects()[1].setWorldY(12 * gp.getTileSize());
        chest.getInventory().addItem(new Item_HealthPotion(gp), 3);
        chest.getInventory().addItem(new Item_ManaPotion(gp), 3);

        gp.getwObjects()[2] = new OBJ_ReviveStatue(gp);
        gp.getwObjects()[2].setWorldX(20 * gp.getTileSize());
        gp.getwObjects()[2].setWorldY(14 * gp.getTileSize());

        gp.getwObjects()[4] = new OBJ_ReviveStatue(gp);
        gp.getwObjects()[4].setWorldX(20 * gp.getTileSize());
        gp.getwObjects()[4].setWorldY(14 * gp.getTileSize());

        int[][] trapCoordinates = {
                // khu spawn
                {22, 20}, {12, 29}, {28, 31},
                // mê cung
                {34, 45}, {29, 55}, {25, 63}, {36, 65},
                // phòng boss ẩn
                {29, 89}, {37, 91}, {28, 85},
                // khu quái cạnh mê cung
                {60, 49}, {71, 62}, {83, 48}, {70, 53}, {73, 74}
        };
        int objectIndex = 5;
        for (int[] coord : trapCoordinates) {
            if (objectIndex < gp.getwObjects().length) {
                gp.getwObjects()[objectIndex] = new OBJ_Spike(gp);
                gp.getwObjects()[objectIndex].setWorldX(coord[0] * gp.getTileSize());
                gp.getwObjects()[objectIndex].setWorldY(coord[1] * gp.getTileSize());
                objectIndex++;
            } else {
                System.err.println("AssetSetter: Not enough space in wObjects array to place all traps.");
                break;
            }
        }

        System.out.println("Map 1 Objects and Traps Set.");

        int portalIndex = 2;
        if(gp.getwObjects().length > portalIndex) {
            gp.getwObjects()[portalIndex] = new OBJ_Portal(gp, 0, 64, 37);
            gp.getwObjects()[portalIndex].setWorldX(12 * gp.getTileSize());
            gp.getwObjects()[portalIndex].setWorldY(10 * gp.getTileSize());
        }
    }

    private void setMap1_NPCs() {
        gp.getNpc()[0] = new NPC_Princess(gp);
        gp.getNpc()[0].setWorldX(gp.getTileSize() * 74);
        gp.getNpc()[0].setWorldY(gp.getTileSize() * 84);
        System.out.println("Map 1 NPCs Set.");
    }

    private void setMap1_Monsters(){
        // --- Thêm Golem Boss ---
        spawnMonster(new MON_GolemBoss(gp), 36, 90);
        spawnMonster(new MON_GolemBoss(gp), 10, 25);

// --- Thêm Bats ---
        spawnMonster(new MON_Bat(gp), 26, 45);
        spawnMonster(new MON_Bat(gp), 26, 43);
        spawnMonster(new MON_Bat(gp), 36, 56);
        spawnMonster(new MON_Bat(gp), 19, 72);
        spawnMonster(new MON_Bat(gp), 30, 72);

// --- Thêm Orcs ---
        spawnMonster(new MON_Orc(gp), 19, 22);
        spawnMonster(new MON_Orc(gp), 38, 14);
        spawnMonster(new MON_Orc(gp), 5, 25);
        spawnMonster(new MON_Orc(gp), 19, 22);
        spawnMonster(new MON_Orc(gp), 57, 51);
        spawnMonster(new MON_Orc(gp), 77, 45);
        spawnMonster(new MON_Orc(gp), 89, 52);
        spawnMonster(new MON_Orc(gp), 69, 64);
        spawnMonster(new MON_Orc(gp), 75, 67);
        spawnMonster(new MON_Orc(gp), 25, 12);
        System.out.println("Map 1 Monsters Set.");
    }
}