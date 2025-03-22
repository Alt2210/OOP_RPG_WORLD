package tile;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];

        mapTileNum = new int[gp.maxWorldRow][gp.maxWorldCol];
        getTileImage();
        loadMap("/Maps/world_map.txt");
    }

    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/water.png"));
            tile[0].collision = true;

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/earth.png"));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/sand.png"));

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/grass.png"));

            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/wall.png"));
            tile[4].collision = true;

            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/tree.png"));
            tile[5].collision = true;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String map){
        try{
            InputStream is = getClass().getResourceAsStream(map);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {

                String line = br.readLine();
                while (col < gp.maxWorldCol) {
                    String numbers[] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[row][col] = num;
                    col++;
                }
                if(col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {

        }
    }

    public void draw(Graphics2D g2){
        //g2.drawImage(tile[0].image, 0, 0, gp.tileSize, gp.tileSize, null);
        int WorldCol = 0;
        int WorldRow = 0;

        while (WorldCol < gp.maxWorldCol && WorldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[WorldRow][WorldCol];

            int WorldX = WorldCol * gp.tileSize;
            int WorldY = WorldRow * gp.tileSize;
            int ScreenX = WorldX - gp.player.WorldX + gp.player.ScreenX;
            int ScreenY = WorldY - gp.player.WorldY + gp.player.ScreenY;

            if (WorldX + gp.tileSize > gp.player.WorldX - gp.player.ScreenX &&
                    WorldX - gp.tileSize < gp.player.WorldX + gp.player.ScreenX &&
                    WorldY + gp.tileSize > gp.player.WorldY - gp.player.ScreenY &&
                    WorldY - gp.tileSize < gp.player.WorldY + gp.player.ScreenY) {

                g2.drawImage(tile[tileNum].image, ScreenX, ScreenY, gp.tileSize, gp.tileSize, null);
            }
            WorldCol++;

            if (WorldCol == gp.maxWorldCol) {
                WorldCol = 0;
                WorldRow++;
            }
        }

    }
}
