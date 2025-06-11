package data;

import java.io.Serializable;

public class WorldObjectState implements Serializable {
    public String name;
    public int worldX;
    public int worldY;
    public boolean exists; // Cờ để xác định đối tượng có còn trên bản đồ không

    public WorldObjectState(String name, int worldX, int worldY, boolean exists) {
        this.name = name;
        this.worldX = worldX;
        this.worldY = worldY;
        this.exists = exists;
    }
}