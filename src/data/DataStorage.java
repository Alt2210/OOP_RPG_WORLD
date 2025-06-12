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

    // Player Position
    int playerWorldX;
    int playerWorldY;
    String playerDirection;
    private int currentMap;

    // World State: Sử dụng mảng List để lưu trạng thái cho từng map
    // Kích thước mảng sẽ bằng GamePanel.maxMap
    List<WorldObjectState>[] objectStates;
    List<MonsterState>[] monsterStates;

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