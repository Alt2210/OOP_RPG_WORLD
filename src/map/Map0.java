package map;

import main.GamePanel;
import character.monster.*;
import character.sideCharacter.*;
import item.itemConsumable.Item_HealthPotion;
import item.itemConsumable.Item_ManaPotion;
import item.itemEquippable.Item_Book;
import item.itemEquippable.Item_Sword;
import worldObject.WorldObject; // Thêm import này
import worldObject.unpickableObject.OBJ_Chest;
import worldObject.unpickableObject.OBJ_Portal;
import worldObject.unpickableObject.OBJ_ReviveStatue;
import character.Character; // Thêm import này

public class Map0 extends GameMap{
    public Map0(GamePanel gp) {
        super(gp);
    }

    @Override
    public void initialize() {
        // Clear old entities to prevent duplicates if map is re-initialized
        worldObjects.clear();
        npcs.clear();
        monsters.clear();

        // OBJECTS
        WorldObject obj1 = new OBJ_ReviveStatue(gp);
        obj1.setWorldX(49 * gp.getTileSize());
        obj1.setWorldY(54 * gp.getTileSize());
        worldObjects.add(obj1); // Thêm vào list của GameMap

        WorldObject obj2 = new OBJ_Portal(gp, 1, 10, 12);
        obj2.setWorldX(67 * gp.getTileSize());
        obj2.setWorldY(40 * gp.getTileSize());
        worldObjects.add(obj2);

        OBJ_Chest chest = new OBJ_Chest(gp);
        chest.getInventory().addItem(new Item_Sword(gp), 1);
        chest.getInventory().addItem(new Item_Book(gp), 1);
        chest.getInventory().addItem(new Item_HealthPotion(gp), 3);
        chest.getInventory().addItem(new Item_ManaPotion(gp), 3);
        chest.setWorldX(12 * gp.getTileSize());
        chest.setWorldY(91 * gp.getTileSize());
        worldObjects.add(chest);

        // NPCs
        Character npc0 = new NPC_OldMan(gp);
        npc0.setWorldX(gp.getTileSize() * 50);
        npc0.setWorldY(gp.getTileSize() * 92);
        npcs.add(npc0);

        Character npc1 = new NPC_Merchant(gp);
        npc1.setWorldX(gp.getTileSize() * 11);
        npc1.setWorldY(gp.getTileSize() * 23);
        npcs.add(npc1);

        // MONSTERS
        // Sử dụng phương thức spawnMonster mới
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

        System.out.println("Map 0 Initialized.");
    }

    // Phương thức helper để thêm quái vật vào danh sách của bản đồ
    private void spawnMonster(Monster monster, int tileX, int tileY) {
        monster.setWorldX(gp.getTileSize() * tileX);
        monster.setWorldY(gp.getTileSize() * tileY);
        monsters.add(monster); // Thêm vào list của GameMap
    }
}