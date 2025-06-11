package data;

import java.io.Serializable;

public class MonsterState implements Serializable {
    private String name;
    private int worldX;
    private int worldY;
    private int currentHealth;
    private boolean onPath;

    public MonsterState(String name, int worldX, int worldY, int currentHealth, boolean onPath) {
        this.name = name;
        this.worldX = worldX;
        this.worldY = worldY;
        this.currentHealth = currentHealth;
        this.onPath = onPath;
    }

    public int getWorldX() {
        return worldX;
    }

    public void setWorldX(int worldX) {
        this.worldX = worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public void setWorldY(int worldY) {
        this.worldY = worldY;
    }

    public boolean isOnPath() {
        return onPath;
    }

    public String getName() {
        return name;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }
}