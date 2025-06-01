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

    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    // boolean DrawPath = false; // Biến này có vẻ chưa được sử dụng
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> collisionStatus = new ArrayList<>();

    public TileManager(GamePanel gp) {
        this.gp = gp;

        // Đọc dữ liệu tile từ plain_tile.txt
        InputStream isTileData = getClass().getResourceAsStream("/maps/plain_tile.txt");
        if (isTileData == null) {
            System.err.println("LỖI NGHIÊM TRỌNG: Không thể tìm thấy tệp plain_tile.txt. Hãy kiểm tra đường dẫn trong thư mục resources/maps.");
            // Thoát hoặc ném một RuntimeException nếu đây là lỗi nghiêm trọng
            return;
        }
        BufferedReader brTileData = new BufferedReader(new InputStreamReader(isTileData));

        String line;
        try {
            while ((line = brTileData.readLine()) != null) {
                fileNames.add(line); // Tên tệp ảnh
                String collisionLine = brTileData.readLine();
                if (collisionLine != null) {
                    collisionStatus.add(collisionLine);
                } else {
                    System.err.println("Lỗi định dạng tệp plain_tile.txt: thiếu dòng collision cho tile " + line);
                    // Có thể thêm một giá trị collision mặc định (ví dụ "false") hoặc xử lý lỗi khác
                    collisionStatus.add("false"); // Ví dụ: mặc định là không va chạm nếu thiếu thông tin
                }
            }
            brTileData.close();
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc plain_tile.txt");
            e.printStackTrace();
            return; // Không thể tiếp tục nếu không đọc được thông tin tile
        }

        tile = new Tile[fileNames.size()]; // Khởi tạo mảng tile với kích thước chính xác
        getTileImage(); // Điền vào mảng tile với các đối tượng Tile đã được tải ảnh


        if (gp.getMaxWorldCol() == 0 || gp.getMaxWorldRow() == 0) {
            System.out.println("Cảnh báo: gp.maxWorldCol/Row là 0. Sử dụng kích thước mặc định 100x100 cho plain.txt. Hãy đảm bảo GamePanel cung cấp kích thước này.");
            gp.maxWorldCol = 100;
            gp.maxWorldRow = 100;
        }
        mapTileNum = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];


        loadMap("/maps/dungeon.txt"); // Tải dữ liệu map tile numbers
    }

    public BufferedImage setup(String imagePathInTilesFolder) {
        BufferedImage image = null;
        try {
            String fullPath = "/tiles/" + imagePathInTilesFolder;
            InputStream is = getClass().getResourceAsStream(fullPath);
            if (is == null) {
                System.err.println("Lỗi: Không tìm thấy tệp ảnh tile: " + fullPath + " (Đường dẫn đầy đủ từ gốc resources)");
                // Tạo ảnh báo lỗi để dễ debug
                image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image.createGraphics();
                g2.setColor(Color.MAGENTA); // Màu dễ nhận biết
                g2.fillRect(0, 0, gp.getTileSize(), gp.getTileSize());
                g2.setFont(new Font("Arial", Font.BOLD, 8));
                g2.setColor(Color.BLACK);
                g2.drawString("ERR", 5, gp.getTileSize()/2);
                g2.dispose();
                return image;
            }
            image = ImageIO.read(is);
            is.close();
        } catch (IOException e) {
            System.err.println("Lỗi IO khi đọc tệp ảnh tile: " + imagePathInTilesFolder);
            e.printStackTrace();
            // Tạo ảnh báo lỗi
            image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.setColor(Color.ORANGE); // Màu khác cho lỗi IO
            g2.fillRect(0, 0, gp.getTileSize(), gp.getTileSize());
            g2.dispose();
        }
        return image;
    }

    public void getTileImage() {
        if (fileNames.size() != collisionStatus.size()) {
            System.err.println("LỖI NGHIÊM TRỌNG: Số lượng fileNames (" + fileNames.size() + ") không khớp với collisionStatus (" + collisionStatus.size() + ") trong plain_tile.txt.");
            return; // Không thể tiếp tục an toàn
        }
        if (tile == null || tile.length != fileNames.size()) {
            System.err.println("LỖI NGHIÊM TRỌNG: Mảng 'tile' chưa được khởi tạo hoặc có kích thước không đúng.");
            tile = new Tile[fileNames.size()]; // Cố gắng khởi tạo lại nếu bị null
        }

        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);
            boolean collision = collisionStatus.get(i).equalsIgnoreCase("true"); // Dùng equalsIgnoreCase cho chắc chắn

            tile[i] = new Tile(); // Khởi tạo đối tượng Tile cho mỗi phần tử mảng

            tile[i].image = setup(fileName); // Tải hình ảnh
            tile[i].collision = collision;   // Gán thuộc tính va chạm

            if (tile[i].image == null) { // Kiểm tra sau khi gọi setup
                System.err.println("Cảnh báo: Không thể tải hình ảnh cho tile '" + fileName + "' (index " + i + "). Tile này sẽ không được vẽ đúng.");
                // Ảnh báo lỗi đã được tạo trong hàm setup nếu is == null hoặc có IOException
            }
        }
        System.out.println("TileManager: Đã xử lý " + fileNames.size() + " định nghĩa tile.");
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                System.err.println("LỖI NGHIÊM TRỌNG: Không thể tìm thấy tệp map: " + filePath);
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // Đảm bảo mapTileNum đã được khởi tạo với kích thước chính xác
            if (mapTileNum == null || mapTileNum.length != gp.getMaxWorldCol() ||
                    (gp.getMaxWorldCol() > 0 && (mapTileNum[0] == null || mapTileNum[0].length != gp.getMaxWorldRow()))) {
                System.err.println("LỖI NGHIÊM TRỌNG: mapTileNum chưa được khởi tạo đúng kích thước ("
                        + gp.getMaxWorldCol() + "x" + gp.getMaxWorldRow() + ") trong loadMap.");
                // Cố gắng khởi tạo lại nếu có thể, mặc dù lý tưởng là nó đã phải đúng từ constructor
                if (gp.getMaxWorldCol() > 0 && gp.getMaxWorldRow() > 0) {
                    mapTileNum = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
                } else {
                    System.err.println("Không thể khởi tạo mapTileNum do kích thước map không hợp lệ.");
                    br.close();
                    return;
                }
            }

            int row = 0;
            String line;
            while (row < gp.getMaxWorldRow() && (line = br.readLine()) != null) {
                String numbers[] = line.trim().split("\\s+"); // Trim và split bằng một hoặc nhiều khoảng trắng

                if (numbers.length < gp.getMaxWorldCol()) {
                    System.err.println("Cảnh báo: Dòng " + (row + 1) + " trong " + filePath + " có " + numbers.length + " cột, cần " + gp.getMaxWorldCol() + " cột. Sẽ điền phần thiếu bằng tile 0.");
                }

                for (int col = 0; col < gp.getMaxWorldCol(); col++) {
                    if (col < numbers.length && !numbers[col].isEmpty()) {
                        try {
                            int num = Integer.parseInt(numbers[col]);
                            if (num >= 0 && num < tile.length) {
                                mapTileNum[col][row] = num;
                            } else {
                                System.err.println("Cảnh báo: Tile number " + num + " (tại [" + col + "," + row + "] trong " + filePath + ") không hợp lệ (ngoài khoảng 0-" + (tile.length - 1) + "). Sử dụng tile 0.");
                                mapTileNum[col][row] = 0;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Lỗi định dạng số '" + numbers[col] + "' tại [" + col + "," + row + "] trong " + filePath + ". Sử dụng tile 0.");
                            mapTileNum[col][row] = 0;
                        }
                    } else {
                        // Nếu dòng ngắn hơn maxWorldCol, điền các ô còn thiếu
                        mapTileNum[col][row] = 0; // Tile mặc định
                    }
                }
                row++;
            }

            if (row < gp.getMaxWorldRow()) {
                System.err.println("Cảnh báo: Tệp map " + filePath + " chỉ có " + row + " dòng, cần " + gp.getMaxWorldRow() + " dòng. Các dòng còn thiếu sẽ được điền bằng tile 0.");
                for (int r = row; r < gp.getMaxWorldRow(); r++) {
                    for (int c = 0; c < gp.getMaxWorldCol(); c++) {
                        mapTileNum[c][r] = 0; // Tile mặc định
                    }
                }
            }
            br.close();
            System.out.println("TileManager: Đã tải map " + filePath + " với kích thước " + gp.getMaxWorldCol() + "x" + gp.getMaxWorldRow());
        } catch (IOException e) {
            System.err.println("Lỗi IO khi tải map: " + filePath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi tải map: " + filePath);
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        if (tile == null || mapTileNum == null) {
            System.err.println("TileManager.draw(): Mảng 'tile' hoặc 'mapTileNum' là null. Không thể vẽ map.");
            g2.setColor(Color.BLACK);
            g2.fillRect(0,0,gp.getScreenWidth(), gp.getScreenHeight());
            g2.setColor(Color.WHITE);
            g2.drawString("LỖI TẢI MAP", 50,50);
            return;
        }

        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.getMaxWorldCol() && worldRow < gp.getMaxWorldRow()) {
            int tileNum = 0; // Giá trị mặc định an toàn
            if (worldCol < mapTileNum.length && worldRow < mapTileNum[worldCol].length) {
                tileNum = mapTileNum[worldCol][worldRow];
            } else {
                System.err.println("Lỗi truy cập mapTileNum ngoài giới hạn tại draw: " + worldCol + ", " + worldRow);
            }


            int worldX = worldCol * gp.getTileSize();
            int worldY = worldRow * gp.getTileSize();
            int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
            int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;

            // Chỉ vẽ những tile trong màn hình
            if (screenX > -gp.getTileSize() && screenX < gp.getScreenWidth() &&
                    screenY > -gp.getTileSize() && screenY < gp.getScreenHeight()) {

                if (tileNum >= 0 && tileNum < tile.length && tile[tileNum] != null && tile[tileNum].image != null) {
                    g2.drawImage(tile[tileNum].image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
                } else {
                    // Vẽ tile báo lỗi nếu tileNum không hợp lệ, tile[tileNum] là null, hoặc ảnh của nó là null
                    g2.setColor(new Color(255, 0, 255, 150)); // Màu Magenta mờ
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