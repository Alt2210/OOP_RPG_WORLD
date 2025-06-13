package map;

import main.GamePanel;
import character.monster.*;
import character.sideCharacter.NPC_Princess;
import item.Item_Key;
import item.itemConsumable.Item_HealthPotion;
import item.itemConsumable.Item_ManaPotion;
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
        // OBJECTS
        gp.getwObjects()[0] = new OBJ_Door();
        gp.getwObjects()[0].setWorldX(74 * gp.getTileSize());
        gp.getwObjects()[0].setWorldY(80 * gp.getTileSize());

        OBJ_Chest chest = new OBJ_Chest(gp);
        gp.getwObjects()[1] = chest;
        gp.getwObjects()[1].setWorldX(12 * gp.getTileSize());
        gp.getwObjects()[1].setWorldY(12 * gp.getTileSize());
        chest.getInventory().addItem(new Item_HealthPotion(gp), 3);
        chest.getInventory().addItem(new Item_ManaPotion(gp), 3);
        chest.getInventory().addItem(new Item_Key(gp), 3);

        gp.getwObjects()[2] = new OBJ_ReviveStatue(gp);
        gp.getwObjects()[2].setWorldX(20 * gp.getTileSize());
        gp.getwObjects()[2].setWorldY(14 * gp.getTileSize());

        gp.getwObjects()[4] = new OBJ_ReviveStatue(gp);
        gp.getwObjects()[4].setWorldX(20 * gp.getTileSize());
        gp.getwObjects()[4].setWorldY(14 * gp.getTileSize());

        int[][] trapCoordinates = {
                {22, 20}, {12, 29}, {28, 31}, {34, 45}, {29, 55}, {25, 63}, {36, 65},
                {29, 89}, {37, 91}, {28, 85}, {60, 49}, {71, 62}, {83, 48}, {70, 53}, {73, 74}
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

        int portalIndex = 3; // Use a different index for portal on Map1 to avoid conflicts
        if(gp.getwObjects().length > portalIndex) {
            gp.getwObjects()[portalIndex] = new OBJ_Portal(gp, 0, 64, 37);
            gp.getwObjects()[portalIndex].setWorldX(12 * gp.getTileSize());
            gp.getwObjects()[portalIndex].setWorldY(10 * gp.getTileSize());
        }

        // NPCs
        gp.getNpc()[0] = new NPC_Princess(gp);
        gp.getNpc()[0].setWorldX(gp.getTileSize() * 74);
        gp.getNpc()[0].setWorldY(gp.getTileSize() * 84);
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

    private void spawnMonster(Monster monster, int tileX, int tileY) {
        monster.setWorldX(gp.getTileSize() * tileX);
        monster.setWorldY(gp.getTileSize() * tileY);
        gp.getMonster().add(monster);
    }
}