package worldObject.unpickableObject;

import character.role.Player;
import main.GamePanel;
// import sound.Sound; // tiếng?
import worldObject.WorldObject;
import java.awt.Rectangle;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;

public class OBJ_Portal extends WorldObject {
    public int targetMap;
    public int playerTargetWorldX_onNewMap; // Tọa độ X của Player trên map mới (tính bằng pixel)
    public int playerTargetWorldY_onNewMap; // Tọa độ Y của Player trên map mới (tính bằng pixel)
    private GamePanel gp; // Giữ tham chiếu để truy cập GamePanel nếu cần

    public OBJ_Portal(GamePanel gp, int targetMap, int playerTileX_onNewMap, int playerTileY_onNewMap) {
        this.gp = gp; // Lưu tham chiếu GamePanel
        this.name = "Portal";
        this.targetMap = targetMap;
        this.playerTargetWorldX_onNewMap = playerTileX_onNewMap * gp.getTileSize();
        this.playerTargetWorldY_onNewMap = playerTileY_onNewMap * gp.getTileSize();
        this.collision = false; // Player đi qua để kích hoạt portal

        try {
            InputStream is = getClass().getResourceAsStream("/objects/door.png");
            if (is == null) {
                System.err.println("Lỗi: Không tìm thấy ảnh cho Portal (/objects/portal.png). Sử dụng placeholder.");
                image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2_placeholder = image.createGraphics();
                g2_placeholder.setColor(new Color(0, 200, 200, 150)); // Màu Cyan mờ
                g2_placeholder.fillRect(0, 0, gp.getTileSize(), gp.getTileSize());
                g2_placeholder.setColor(Color.BLACK);
                g2_placeholder.setFont(new Font("Arial", Font.BOLD, gp.getTileSize()/2));
                g2_placeholder.drawString("P", gp.getTileSize() / 3, gp.getTileSize() * 2 / 3);
                g2_placeholder.dispose();
            } else {
                image = ImageIO.read(is);
            }
        } catch (IOException e) {
            System.err.println("Lỗi IO khi tải ảnh Portal: " + e.getMessage());
            e.printStackTrace();
            // Tạo placeholder nếu có lỗi IO
            image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2_placeholder_io = image.createGraphics();
            g2_placeholder_io.setColor(Color.PINK); // Màu khác cho lỗi IO
            g2_placeholder_io.fillRect(0,0,gp.getTileSize(),gp.getTileSize());
            g2_placeholder_io.dispose();
        }
        // Vùng va chạm của Portal là toàn bộ tile
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = gp.getTileSize();
        solidArea.height = gp.getTileSize();
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void interactPlayer(Player player, int worldObjectIndex, GamePanel gpRef) {
        // portal.collision = false => logic gọi từ Player.pickUpItem()
        // portal.collision = true, cần một cách khác để kích hoạt ( sau này sẽ thêm key đặc biệt để mở portal)

        if (gpRef.gameState == gpRef.playState) {
            Rectangle playerBounds = new Rectangle(player.getWorldX() + player.getSolidArea().x, player.getWorldY() + player.getSolidArea().y, player.getSolidArea().width, player.getSolidArea().height);
            Rectangle portalBounds = new Rectangle(this.worldX + this.solidArea.x, this.worldY + this.solidArea.y, this.solidArea.width, this.solidArea.height);

            if (playerBounds.intersects(portalBounds)) {
                gpRef.getUi().showMessage("Bước qua cổng dịch chuyển đến map " + targetMap + "...");

                gpRef.currentMap = this.targetMap;
                player.setWorldX(this.playerTargetWorldX_onNewMap);
                player.setWorldY(this.playerTargetWorldY_onNewMap);

                gpRef.clearEntitiesForMapChange();
                gpRef.getaSetter().setupMapAssets(gpRef.currentMap);

                System.out.println("Player teleported to map: " + gpRef.currentMap + " at (" + player.getWorldX() + "," + player.getWorldY() + ")");

            }
        }
    }

    // Ghi đè phương thức draw để portal luôn được vẽ, ngay cả khi collision = false
    @Override
    public void draw(Graphics2D g2, GamePanel gp) { //
        if (image == null) return;

        int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX(); //
        int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY(); //

        if(worldX + gp.getTileSize() > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() && //
                worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() && //
                worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() && //
                worldY - gp.getTileSize() < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) { //
            g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null); //
        }
    }
}