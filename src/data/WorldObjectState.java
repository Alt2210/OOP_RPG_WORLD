package data;

import java.io.Serializable;

public class WorldObjectState implements Serializable {
    private String name;
    private int worldX;
    private int worldY;
    private boolean exists; // Cờ để xác định đối tượng có còn trên bản đồ không

    public WorldObjectState(String name, int worldX, int worldY, boolean exists) {
        this.name = name;
        this.worldX = worldX;
        this.worldY = worldY;
        this.exists = exists;
    }

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public String getName() {
        return name;
    }

    public boolean isExists() {
        return exists;
    }
}