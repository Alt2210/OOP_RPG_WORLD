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
    int currentMap;

    // World State: Sử dụng mảng List để lưu trạng thái cho từng map
    // Kích thước mảng sẽ bằng GamePanel.maxMap
    List<WorldObjectState>[] objectStates;
    List<MonsterState>[] monsterStates;

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