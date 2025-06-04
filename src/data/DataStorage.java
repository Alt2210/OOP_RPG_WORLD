package data;

import java.io.Serializable;

public class DataStorage implements Serializable {
    // Player Stats
    int level; //
    int maxHealth; //
    int currentHealth; //
    int maxMana;    //
    int currentMana; //
    int strength; //
    int dexterity; //
    int exp; //

    // Player Position and Direction
    int playerWorldX; //
    int playerWorldY; //
    String playerDirection; //

    // Player Inventory/Quest Items
    int hasKey; //

    // Game World State
    double playtime; //
    // String currentMapName; // Không cần nếu dùng currentMap index

    // THÊM MỚI
    int currentMap; // Lưu chỉ số map hiện tại của người chơi
}