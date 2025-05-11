package tile_interactive;

import entity.OBject; // Sửa thành OBject
import entity.GameObject;   // Có thể cần nếu isCorrectItem nhận GameObject
import main.GamePanel;

import java.awt.*;

public class InteractiveTile extends OBject { // Sửa thành Game_Object

    // GamePanel gp; // Không cần, đã có ở lớp cha (this.gp)
    public boolean destructible = false;
    // invincible và invincibleCounter sẽ được kế thừa từ Game_Object nếu bạn thêm vào đó.
    // Nếu không, bạn cần khai báo chúng ở đây:
    // public boolean invincible = false;
    // public int invincibleCounter = 0;

    // down1 (hình ảnh) sẽ được kế thừa từ GameObject (thông qua Game_Object)
    // Bạn cần một cách để tải hình ảnh này.

    public InteractiveTile(GamePanel gp, int col, int row) {
        super(gp);
        // this.gp = gp; // Không cần thiết
        this.worldX = col * gp.tileSize;
        this.worldY = row * gp.tileSize;
        // Mặc định, InteractiveTile có thể không có hình ảnh, lớp con sẽ cung cấp
        // Hoặc bạn có thể có một phương thức getImage() ở đây hoặc làm cho lớp này abstract
        // và getImage() là abstract.
        // Ví dụ:
        // getImage();
    }

    // Ví dụ một phương thức getImage() nếu InteractiveTile có hình ảnh mặc định
    // Hoặc các lớp con cụ thể sẽ override phương thức này
    public void getImage() {
        // Ví dụ: down1 = setup("/tiles_interactive/default_itile", gp.tileSize, gp.tileSize);
        // Thông thường, các lớp con như IT_DestructibleWall sẽ load ảnh riêng của nó.
    }


    // Tham số cần được quyết định: GameObject, Character, hay Game_Object (nếu item là Game_Object)
    public boolean isCorrectItem(GameObject itemUsed) {
        boolean isCorrectItem = false;
        // Logic cụ thể sẽ nằm trong các lớp con của InteractiveTile
        // Ví dụ: if (itemUsed instanceof OBJ_Pickaxe && this instanceof IT_Rock) return true;
        return isCorrectItem;
    }

    public void playSE() {
        // Âm thanh khi tương tác
    }

    // Trả về một InteractiveTile mới (hoặc null) là dạng bị phá hủy
    public InteractiveTile getDestroyedForm() {
        InteractiveTile tile = null;
        // Logic cụ thể trong lớp con
        // Ví dụ: return new IT_GroundTile(gp, worldX/gp.tileSize, worldY/gp.tileSize);
        return tile;
    }

    @Override // Ghi đè từ GameObject (thông qua Game_Object)
    public void update() {
        // Giả sử invincible và invincibleCounter có trong Game_Object hoặc InteractiveTile
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 20) { // Thời gian bất tử (ví dụ 20 frames)
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    @Override // Ghi đè từ GameObject (thông qua Game_Object)
    public void draw(Graphics2D g2) {
        if (!drawing || !inCamera()) return; // drawing được kế thừa từ GameObject

        int screenX = getScreenX(); // getScreenX() được kế thừa
        int screenY = getScreenY();

        // down1 được kế thừa từ GameObject. Nó cần được load trong getImage().
        if (down1 != null) {
            g2.drawImage(down1, screenX, screenY, gp.tileSize, gp.tileSize, null);
        } else {
            // Fallback nếu không có hình ảnh (ví dụ cho debug)
            // g2.setColor(Color.MAGENTA);
            // g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            // g2.setColor(Color.WHITE);
            // g2.drawRect(screenX, screenY, gp.tileSize, gp.tileSize);
            // System.out.println("Warning: InteractiveTile at " + worldX/gp.tileSize + "," + worldY/gp.tileSize + " has no image (down1 is null).");
        }
    }
}