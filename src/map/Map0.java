package map;

import main.GamePanel;
import character.monster.*;
import character.sideCharacter.NPC_OldMan;
import character.sideCharacter.NPC_Princess;
import item.Item_Key;
import item.itemConsumable.Item_HealthPotion;
import item.itemConsumable.Item_ManaPotion;
import item.itemEquippable.Item_Book;
import item.itemEquippable.Item_Sword;
import worldObject.unpickableObject.OBJ_Chest;
import worldObject.unpickableObject.OBJ_Portal;
import worldObject.unpickableObject.OBJ_ReviveStatue;

public class Map0 extends GameMap{ // Or implements MapInitializer
    public Map0(GamePanel gp) {
        super(gp);
    }

    @Override
    public void initialize() {

        // OBJECTS
        gp.getwObjects()[1] = new OBJ_ReviveStatue(gp);
        gp.getwObjects()[1].setWorldX(49 * gp.getTileSize());
        gp.getwObjects()[1].setWorldY(54 * gp.getTileSize());

        gp.getwObjects()[2] = new OBJ_Portal(gp, 1, 10, 12);
        gp.getwObjects()[2].setWorldX(67 * gp.getTileSize());
        gp.getwObjects()[2].setWorldY(40 * gp.getTileSize());

        OBJ_Chest chest = new OBJ_Chest(gp);
        chest.getInventory().addItem(new Item_Sword(gp), 1);
        chest.getInventory().addItem(new Item_Book(gp), 1);
        chest.getInventory().addItem(new Item_HealthPotion(gp), 3);
        chest.getInventory().addItem(new Item_ManaPotion(gp), 3);
        chest.getInventory().addItem(new Item_Key(gp),1);
        gp.getwObjects()[3] = chest;
        gp.getwObjects()[3].setWorldX(12 * gp.getTileSize());
        gp.getwObjects()[3].setWorldY(91 * gp.getTileSize());

        // NPCs
        gp.getNpc()[0] = new NPC_OldMan(gp);
        gp.getNpc()[0].setWorldX(gp.getTileSize() * 50);
        gp.getNpc()[0].setWorldY(gp.getTileSize() * 92);

        // MONSTERS
        gp.getMonster().add(new MON_GreenSlime(gp));
        gp.getMonster().get(0).setWorldX(43 * gp.getTileSize());
        gp.getMonster().get(0).setWorldY(86 * gp.getTileSize());
        // ... (thêm các quái vật khác tương tự)
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

    private void spawnMonster(Monster monster, int tileX, int tileY) {
        monster.setWorldX(gp.getTileSize() * tileX);
        monster.setWorldY(gp.getTileSize() * tileY);
        gp.getMonster().add(monster);
    }
}