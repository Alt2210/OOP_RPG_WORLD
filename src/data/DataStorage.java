package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataStorage implements Serializable {
    // Player Stats
    int maxHealth;
    int currentHealth;
    int maxMana;
    int currentMana;
    int hasKey;
    int level;
    int currentExp;
    int expToNextLevel;
    int attack;
    int defense;
    // Player Position
    int playerWorldX;
    int playerWorldY;
    String playerDirection;
    private int currentMap;
    String playerClassIdentifier;
    // World State
    List<WorldObjectState>[] objectStates;
    List<MonsterState>[] monsterStates;

    // MỚI: Thêm thông tin cho màn hình load game
    private long timestamp;
    private String description;


    // --- GETTERS & SETTERS (Thêm cho các trường mới) ---

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(int currentMap) {
        this.currentMap = currentMap;
    }

    @SuppressWarnings("unchecked")
    public DataStorage(int maxMap) {
        objectStates = (List<WorldObjectState>[]) new ArrayList[maxMap];
        monsterStates = (List<MonsterState>[]) new ArrayList[maxMap];
        for (int i = 0; i < maxMap; i++) {
            objectStates[i] = new ArrayList<>();
            monsterStates[i] = new ArrayList<>();
        }
    }
}