package main;
import character.*;
import character.Character;
import character.monster.*;
import item.itemConsumable.Item_HealthPotion;
import item.itemConsumable.Item_ManaPotion;
import item.itemEquippable.Item_Book;
import item.itemEquippable.Item_Sword;
import worldObject.pickableObject.*;
import worldObject.unpickableObject.*;

import java.util.ArrayList;
import java.util.List;


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

    public void removeDeadMonster(ArrayList<Monster> monsters, int index, int mapIndex) { //
        if (monsters != null && index >= 0 && index < monsters.size() && monsters.get(index) != null) { //
            System.out.println("Removing " + monsters.get(index).getName() + " at index " + index + " from map " + mapIndex); //
            monsters.remove(index); //
        }
    }

    // đặt objects, NPCs, Monsters cho map 0
    private void setMap0_Objects() {


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
        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(0).setWorldX(gp.getTileSize() * 43);
        gp.getMonster().get(0).setWorldY(gp.getTileSize() * 86);

        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(1).setWorldX(gp.getTileSize() * 33);
        gp.getMonster().get(1).setWorldY(gp.getTileSize() * 91);

        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(2).setWorldX(gp.getTileSize() * 31);
        gp.getMonster().get(2).setWorldY(gp.getTileSize() * 67);

        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(3).setWorldX(gp.getTileSize() * 44);
        gp.getMonster().get(3).setWorldY(gp.getTileSize() * 63);

        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(4).setWorldX(gp.getTileSize() * 12);
        gp.getMonster().get(4).setWorldY(gp.getTileSize() * 42);

        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(5).setWorldX(gp.getTileSize() * 22);
        gp.getMonster().get(5).setWorldY(gp.getTileSize() * 46);

        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(6).setWorldX(gp.getTileSize() * 41);
        gp.getMonster().get(6).setWorldY(gp.getTileSize() * 37);

        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(7).setWorldX(gp.getTileSize() * 56);
        gp.getMonster().get(7).setWorldY(gp.getTileSize() * 31);

        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(8).setWorldX(gp.getTileSize() * 73);
        gp.getMonster().get(8).setWorldY(gp.getTileSize() * 25);

        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(9).setWorldX(gp.getTileSize() * 83);
        gp.getMonster().get(9).setWorldY(gp.getTileSize() * 35);


// --- Thêm Bats ---
        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(10).setWorldX(gp.getTileSize() * 35);
        gp.getMonster().get(10).setWorldY(gp.getTileSize() * 35);

        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(11).setWorldX(gp.getTileSize() * 9);
        gp.getMonster().get(11).setWorldY(gp.getTileSize() * 51);

        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(12).setWorldX(gp.getTileSize() * 4);
        gp.getMonster().get(12).setWorldY(gp.getTileSize() * 29);

        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(13).setWorldX(gp.getTileSize() * 53);
        gp.getMonster().get(13).setWorldY(gp.getTileSize() * 33);

        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(14).setWorldX(gp.getTileSize() * 67);
        gp.getMonster().get(14).setWorldY(gp.getTileSize() * 39);


// --- Thêm Skeleton Lord (Boss) ---
        gp.getMonster().add(new MON_SkeletonLord(gp));
        gp.getMonster().get(15).setWorldX(gp.getTileSize() * 49);
        gp.getMonster().get(15).setWorldY(gp.getTileSize() * 33);

        System.out.println("Map 0 Monsters Set.");
    }

    private void setMap1_Objects() {
        gp.getwObjects()[3] = new OBJ_Key(gp);
        gp.getwObjects()[3].setWorldX(35 * gp.getTileSize());
        gp.getwObjects()[3].setWorldY(92 * gp.getTileSize());

        gp.getwObjects()[0] = new OBJ_Door();
        gp.getwObjects()[0].setWorldX(74 * gp.getTileSize());
        gp.getwObjects()[0].setWorldY(80 * gp.getTileSize());

        OBJ_Chest chest = new OBJ_Chest(gp);
        gp.getwObjects()[1] = chest;
        gp.getwObjects()[1].setWorldX(12 * gp.getTileSize());
        gp.getwObjects()[1].setWorldY(12 * gp.getTileSize());
        chest.getInventory().addItem(new Item_HealthPotion(gp), 3);
        chest.getInventory().addItem(new Item_ManaPotion(gp), 3);

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
        gp.getMonster().add(new MON_GolemBoss(gp));
        gp.getMonster().get(0).setWorldX(gp.getTileSize() * 36);
        gp.getMonster().get(0).setWorldY(gp.getTileSize() * 90);

        gp.getMonster().add(new MON_GolemBoss(gp));
        gp.getMonster().get(1).setWorldX(gp.getTileSize() * 10);
        gp.getMonster().get(1).setWorldY(gp.getTileSize() * 25);

// --- Thêm Bats ---
        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(2).setWorldX(gp.getTileSize() * 26);
        gp.getMonster().get(2).setWorldY(gp.getTileSize() * 45);

        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(3).setWorldX(gp.getTileSize() * 26);
        gp.getMonster().get(3).setWorldY(gp.getTileSize() * 43);

        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(4).setWorldX(gp.getTileSize() * 36);
        gp.getMonster().get(4).setWorldY(gp.getTileSize() * 56);

        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(5).setWorldX(gp.getTileSize() * 19);
        gp.getMonster().get(5).setWorldY(gp.getTileSize() * 72);

        gp.getMonster().add(new MON_Bat(gp));
        gp.getMonster().get(6).setWorldX(gp.getTileSize() * 30);
        gp.getMonster().get(6).setWorldY(gp.getTileSize() * 72);

// --- Thêm Orcs ---
        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(7).setWorldX(gp.getTileSize() * 19);
        gp.getMonster().get(7).setWorldY(gp.getTileSize() * 22);

        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(8).setWorldX(gp.getTileSize() * 38);
        gp.getMonster().get(8).setWorldY(gp.getTileSize() * 14);

        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(9).setWorldX(gp.getTileSize() * 5);
        gp.getMonster().get(9).setWorldY(gp.getTileSize() * 25);

        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(10).setWorldX(gp.getTileSize() * 19);
        gp.getMonster().get(10).setWorldY(gp.getTileSize() * 22);

        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(11).setWorldX(gp.getTileSize() * 57);
        gp.getMonster().get(11).setWorldY(gp.getTileSize() * 51);

        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(12).setWorldX(gp.getTileSize() * 77);
        gp.getMonster().get(12).setWorldY(gp.getTileSize() * 45);

        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(13).setWorldX(gp.getTileSize() * 89);
        gp.getMonster().get(13).setWorldY(gp.getTileSize() * 52);

        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(14).setWorldX(gp.getTileSize() * 69);
        gp.getMonster().get(14).setWorldY(gp.getTileSize() * 64);

        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(15).setWorldX(gp.getTileSize() * 75);
        gp.getMonster().get(15).setWorldY(gp.getTileSize() * 67);

        gp.getMonster().add(new MON_Orc(gp));
        gp.getMonster().get(16).setWorldX(gp.getTileSize() * 25);
        gp.getMonster().get(16).setWorldY(gp.getTileSize() * 12);
        System.out.println("Map 1 Monsters Set.");
    }
}