package tile;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TileManager {

    private GamePanel gp;
    private Tile[] tile;
    private int mapTileNum[][][]; // Mảng 3 chiều cho các map
    private ArrayList<String> fileNames = new ArrayList<>();
    private ArrayList<String> collisionStatus = new ArrayList<>();

    public int[][][] getMapTileNum() {
        return mapTileNum;
    }

    public void setMapTileNum(int[][][] mapTileNum) {
        this.mapTileNum = mapTileNum;
    }

    public Tile[] getTile() {
        return tile;
    }

    public void setTile(Tile[] tile) {
        this.tile = tile;
    }

    public TileManager(GamePanel gp) {
        this.gp = gp;

        // Đọc dữ liệu tile từ plain_tile.txt (giữ nguyên)
        InputStream isTileData = getClass().getResourceAsStream("/maps/plain_tile.txt");
        if (isTileData == null) {
            System.err.println("LỖI NGHIÊM TRỌNG: Không thể tìm thấy tệp plain_tile.txt.");
            // Thoát hoặc ném một RuntimeException nếu đây là lỗi nghiêm trọng
            return;
        }
        BufferedReader brTileData = new BufferedReader(new InputStreamReader(isTileData));
        String line;
        try {
            while ((line = brTileData.readLine()) != null) {
                fileNames.add(line);
                String collisionLine = brTileData.readLine();
                if (collisionLine != null) {
                    collisionStatus.add(collisionLine);
                } else {
                    System.err.println("Lỗi định dạng tệp plain_tile.txt: thiếu dòng collision cho tile " + line);
                    collisionStatus.add("false"); // Mặc định không va chạm
                }
            }
            brTileData.close();
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc plain_tile.txt");
            e.printStackTrace();
            return;
        }

        tile = new Tile[fileNames.size()];
        getTileImage();

        // Khởi tạo mảng 3 chiều cho map data
        mapTileNum = new int[gp.getMaxMap()][gp.getMaxWorldCol()][gp.getMaxWorldRow()];

        // Tải dữ liệu cho từng map
        loadMap("/maps/plain.txt", 0); // Map 0
        loadMap("/maps/dungeon.txt", 1); // Map 1
        // Thêm các lời gọi loadMap khác nếu bạn có nhiều hơn 2 map
    }

    public BufferedImage setup(String imagePathInTilesFolder) {
        BufferedImage image = null;
        try {
            String fullPath = "/tiles/" + imagePathInTilesFolder;
            InputStream is = getClass().getResourceAsStream(fullPath);
            if (is == null) {
                System.err.println("Lỗi: Không tìm thấy tệp ảnh tile: " + fullPath);
                image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image.createGraphics();
                g2.setColor(Color.MAGENTA);
                g2.fillRect(0, 0, gp.getTileSize(), gp.getTileSize());
                g2.setFont(new Font("Arial", Font.BOLD, 8));
                g2.setColor(Color.BLACK);
                g2.drawString("ERR", 5, gp.getTileSize() / 2);
                g2.dispose();
                return image;
            }
            image = ImageIO.read(is);
            is.close();
        } catch (IOException e) {
            System.err.println("Lỗi IO khi đọc tệp ảnh tile: " + imagePathInTilesFolder);
            e.printStackTrace();
            image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.setColor(Color.ORANGE);
            g2.fillRect(0, 0, gp.getTileSize(), gp.getTileSize());
            g2.dispose();
        }
        return image;
    }

    public void getTileImage() {
        if (fileNames.size() != collisionStatus.size()) {
            System.err.println("LỖI NGHIÊM TRỌNG: Số lượng fileNames không khớp với collisionStatus.");
            return;
        }
        if (tile == null || tile.length != fileNames.size()) {
            tile = new Tile[fileNames.size()];
        }

        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);
            boolean collision = collisionStatus.get(i).equalsIgnoreCase("true");
            tile[i] = new Tile();
            tile[i].setImage(setup(fileName));
            tile[i].setCollision(collision);
            if (tile[i].getImage() == null) {
                System.err.println("Cảnh báo: Không thể tải hình ảnh cho tile '" + fileName + "'.");
            }
        }
        System.out.println("TileManager: Đã xử lý " + fileNames.size() + " định nghĩa tile.");
    }

    public void loadMap(String filePath, int mapIndex) {
        if (mapIndex < 0 || mapIndex >= gp.getMaxMap()) {
            System.err.println("Lỗi: mapIndex " + mapIndex + " không hợp lệ. Phải nằm trong khoảng 0 đến " + (gp.getMaxMap() - 1));
            return;
        }
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                System.err.println("LỖI NGHIÊM TRỌNG: Không thể tìm thấy tệp map: " + filePath + " cho map index " + mapIndex);
                // Điền map này bằng tile 0 (hoặc tile mặc định) để tránh NullPointerException khi vẽ
                for (int r = 0; r < gp.getMaxWorldRow(); r++) {
                    for (int c = 0; c < gp.getMaxWorldCol(); c++) {
                        mapTileNum[mapIndex][c][r] = 0;
                    }
                }
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int row = 0;
            String mapLine; // Đổi tên biến 'line' để tránh xung đột với biến 'line' trong constructor
            while (row < gp.getMaxWorldRow() && (mapLine = br.readLine()) != null) {
                String numbers[] = mapLine.trim().split("\\s+");

                if (numbers.length < gp.getMaxWorldCol()) {
                    System.err.println("Cảnh báo: Dòng " + (row + 1) + " trong " + filePath + " (map " + mapIndex + ") có " + numbers.length + " cột, cần " + gp.getMaxWorldCol() + " cột. Sẽ điền phần thiếu bằng tile 0.");
                }

                for (int col = 0; col < gp.getMaxWorldCol(); col++) {
                    if (col < numbers.length && !numbers[col].isEmpty()) {
                        try {
                            int num = Integer.parseInt(numbers[col]);
                            if (num >= 0 && num < tile.length) {
                                mapTileNum[mapIndex][col][row] = num;
                            } else {
                                System.err.println("Cảnh báo: Tile number " + num + " (tại [" + col + "," + row + "] trong " + filePath + ", map " + mapIndex + ") không hợp lệ. Sử dụng tile 0.");
                                mapTileNum[mapIndex][col][row] = 0;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Lỗi định dạng số '" + numbers[col] + "' tại [" + col + "," + row + "] trong " + filePath + ", map " + mapIndex + ". Sử dụng tile 0.");
                            mapTileNum[mapIndex][col][row] = 0;
                        }
                    } else {
                        mapTileNum[mapIndex][col][row] = 0; // Điền các ô còn thiếu nếu dòng ngắn
                    }
                }
                row++;
            }

            if (row < gp.getMaxWorldRow()) {
                System.err.println("Cảnh báo: Tệp map " + filePath + " (map " + mapIndex + ") chỉ có " + row + " dòng, cần " + gp.getMaxWorldRow() + " dòng. Các dòng còn thiếu sẽ được điền bằng tile 0.");
                for (int r = row; r < gp.getMaxWorldRow(); r++) {
                    for (int c = 0; c < gp.getMaxWorldCol(); c++) {
                        mapTileNum[mapIndex][c][r] = 0;
                    }
                }
            }
            br.close();
            System.out.println("TileManager: Đã tải map " + filePath + " vào index " + mapIndex);
        } catch (IOException e) {
            System.err.println("Lỗi IO khi tải map: " + filePath + " cho map index " + mapIndex);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi tải map: " + filePath + " cho map index " + mapIndex);
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        if (tile == null || mapTileNum == null || gp.getPlayer() == null) {
            System.err.println("TileManager.draw(): Một trong các thành phần (tile, mapTileNum, player) là null. Không thể vẽ map.");
            g2.setColor(Color.BLACK);
            g2.fillRect(0,0,gp.getScreenWidth(), gp.getScreenHeight());
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("LỖI TẢI DỮ LIỆU MAP HOẶC PLAYER", 50,50);
            return;
        }

        int worldCol = 0;
        int worldRow = 0;

        int currentMapIndex = gp.getCurrentMap();
        if (currentMapIndex < 0 || currentMapIndex >= gp.getMaxMap()) {
            System.err.println("Lỗi: currentMapIndex (" + currentMapIndex + ") không hợp lệ trong TileManager.draw(). Vẽ map 0 mặc định.");
            currentMapIndex = 0; // Fallback về map 0
            if (currentMapIndex >= gp.getMaxMap()) { // Nếu maxMap cũng không hợp lệ (ví dụ 0) thì không thể vẽ
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(0,0,gp.getScreenWidth(), gp.getScreenHeight());
                g2.setColor(Color.YELLOW);
                g2.drawString("LỖI: Không có map nào để vẽ.", 50, 100);
                return;
            }
        }


        while (worldCol < gp.getMaxWorldCol() && worldRow < gp.getMaxWorldRow()) {
            int tileNum = 0;
            // Truy cập mảng 3 chiều với map hiện tại
            try {
                tileNum = mapTileNum[currentMapIndex][worldCol][worldRow];
            } catch(ArrayIndexOutOfBoundsException e) {
                System.err.println("Lỗi nghiêm trọng: Truy cập mapTileNum["+currentMapIndex+"]["+worldCol+"]["+worldRow+"] ngoài giới hạn! Kích thước: " +
                        mapTileNum.length + "x" + (mapTileNum.length > 0 ? mapTileNum[0].length : "N/A") + "x" +
                        (mapTileNum.length > 0 && mapTileNum[0].length > 0 ? mapTileNum[0][0].length : "N/A"));
                // Không làm gì thêm để tránh crash vòng lặp
                worldCol++;
                if (worldCol == gp.getMaxWorldCol()) {
                    worldCol = 0;
                    worldRow++;
                }
                continue;
            }


            int worldX = worldCol * gp.getTileSize();
            int worldY = worldRow * gp.getTileSize();
            int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
            int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

            if (screenX > -gp.getTileSize() && screenX < gp.getScreenWidth() &&
                    screenY > -gp.getTileSize() && screenY < gp.getScreenHeight()) {

                if (tileNum >= 0 && tileNum < tile.length && tile[tileNum] != null && tile[tileNum].getImage() != null) {
                    g2.drawImage(tile[tileNum].getImage(), screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
                } else {
                    g2.setColor(new Color(255, 0, 255, 150));
                    g2.fillRect(screenX, screenY, gp.getTileSize(), gp.getTileSize());
                    g2.setColor(Color.BLACK);
                    g2.drawRect(screenX, screenY, gp.getTileSize()-1, gp.getTileSize()-1);

                }
            }

            worldCol++;
            if (worldCol == gp.getMaxWorldCol()) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}