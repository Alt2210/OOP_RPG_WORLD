package data;

import java.io.Serializable;

public class MonsterState implements Serializable {
    public String name;
    public int worldX;
    public int worldY;
    public int currentHealth;
    public boolean onPath;

    public MonsterState(String name, int worldX, int worldY, int currentHealth, boolean onPath) {
        this.name = name;
        this.worldX = worldX;
        this.worldY = worldY;
        this.currentHealth = currentHealth;
        this.onPath = onPath;
    }
}