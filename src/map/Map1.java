package map;

import main.GamePanel;
import character.Character; // Thêm import này
import character.monster.*;
import character.sideCharacter.NPC_Princess;
import item.Item_Key;
import item.itemConsumable.Item_HealthPotion;
import item.itemConsumable.Item_ManaPotion;
import worldObject.WorldObject; // Thêm import này
import worldObject.unpickableObject.OBJ_Chest;
import worldObject.unpickableObject.OBJ_Door;
import worldObject.unpickableObject.OBJ_Portal;
import worldObject.unpickableObject.OBJ_ReviveStatue;
import worldObject.unpickableObject.OBJ_Spike;

public class Map1 extends GameMap {
    public Map1(GamePanel gp) {
        super(gp);
    }

    @Override
    public void initialize() {
        // Clear old entities to prevent duplicates if map is re-initialized
        worldObjects.clear();
        npcs.clear();
        monsters.clear();

        // OBJECTS
        WorldObject obj0 = new OBJ_Door();
        obj0.setWorldX(74 * gp.getTileSize());
        obj0.setWorldY(80 * gp.getTileSize());
        worldObjects.add(obj0);

        OBJ_Chest chest = new OBJ_Chest(gp);
        chest.setWorldX(12 * gp.getTileSize());
        chest.setWorldY(12 * gp.getTileSize());
        chest.getInventory().addItem(new Item_HealthPotion(gp), 3);
        chest.getInventory().addItem(new Item_ManaPotion(gp), 3);
        worldObjects.add(chest);

        WorldObject obj2 = new OBJ_ReviveStatue(gp);
        obj2.setWorldX(20 * gp.getTileSize());
        obj2.setWorldY(14 * gp.getTileSize());
        worldObjects.add(obj2);

        WorldObject obj4 = new OBJ_ReviveStatue(gp);
        obj4.setWorldX(20 * gp.getTileSize());
        obj4.setWorldY(14 * gp.getTileSize());
        worldObjects.add(obj4);

        int[][] trapCoordinates = {
                {22, 20}, {12, 29}, {28, 31}, {34, 45}, {29, 55}, {25, 63}, {36, 65},
                {29, 89}, {37, 91}, {28, 85}, {60, 49}, {71, 62}, {83, 48}, {70, 53}, {73, 74}
        };

        for (int[] coord : trapCoordinates) {
            WorldObject spike = new OBJ_Spike(gp);
            spike.setWorldX(coord[0] * gp.getTileSize());
            spike.setWorldY(coord[1] * gp.getTileSize());
            worldObjects.add(spike);
        }

        WorldObject portal = new OBJ_Portal(gp, 0, 64, 37);
        portal.setWorldX(12 * gp.getTileSize());
        portal.setWorldY(10 * gp.getTileSize());
        worldObjects.add(portal);


        // NPCs
        Character npc0 = new NPC_Princess(gp);
        npc0.setWorldX(gp.getTileSize() * 74);
        npc0.setWorldY(gp.getTileSize() * 84);
        npcs.add(npc0);

        // MONSTERS
        spawnMonster(new MON_GolemBoss(gp), 36, 90);
        spawnMonster(new MON_GolemBoss(gp), 10, 25);
        spawnMonster(new MON_Bat(gp), 26, 45);
        spawnMonster(new MON_Bat(gp), 26, 43);
        spawnMonster(new MON_Bat(gp), 36, 56);
        spawnMonster(new MON_Bat(gp), 19, 72);
        spawnMonster(new MON_Bat(gp), 30, 72);
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

        System.out.println("Map 1 Initialized.");
    }

    // Phương thức helper để thêm quái vật vào danh sách của bản đồ
    private void spawnMonster(Monster monster, int tileX, int tileY) {
        monster.setWorldX(gp.getTileSize() * tileX);
        monster.setWorldY(gp.getTileSize() * tileY);
        monsters.add(monster); // Thêm vào list của GameMap
    }
}