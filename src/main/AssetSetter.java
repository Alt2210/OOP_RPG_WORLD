package main;

import map.*;


public class AssetSetter {
    private GamePanel gp;
    private GameMap[] gameMaps;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
        // Initialize map instances
        gameMaps = new GameMap[gp.getMaxMap()];
        gameMaps[0] = new Map0(gp);
        gameMaps[1] = new Map1(gp);
        // Add more maps here if needed: gameMaps[2] = new Map2(gp);
    }

    public GameMap getMap(int mapIndex) { // Thêm phương thức getter mới
        if (mapIndex >= 0 && mapIndex < gp.getMaxMap()) {
            return gameMaps[mapIndex]; //
        }
        System.err.println("AssetSetter: Invalid map index requested: " + mapIndex);
        return null;
    }

    public void setupMapAssets(int mapIndex) {
        System.out.println("AssetSetter: Setting up assets for map " + mapIndex);

        if (mapIndex >= 0 && mapIndex < gp.getMaxMap() && gameMaps[mapIndex] != null) {
            // Call the initialize method of the specific map instance
            gameMaps[mapIndex].initialize();
        } else {
            System.err.println("AssetSetter: Invalid map index or map not initialized: " + mapIndex);
        }
    }
}