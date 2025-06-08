package projectile;

import character.Character;
import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Projectile {
    protected GamePanel gp;
    public int worldX, worldY;
    public int speed;
    public String direction;
    protected BufferedImage image; // Có thể là một danh sách BufferedImage nếu có animation
    protected Character caster; // Nhân vật đã bắn ra projectile này
    protected int damage;
    public boolean alive = true; // Trạng thái của projectile (còn tồn tại hay không)
    public Rectangle solidArea = new Rectangle(0, 0, 0, 0); // Kích thước vùng va chạm, sẽ được lớp con đặt
    // int solidAreaDefaultX, solidAreaDefaultY; // Không cần thiết nếu solidArea luôn là (0,0) tương đối với worldX, worldY của projectile

    protected int maxRange; // Tầm xa tối đa
    protected int distanceTraveled = 0; // Quãng đường đã di chuyển

    public Projectile(GamePanel gp) {
        this.gp = gp;
    }

    public int getDamageValue() { // Đổi tên từ getDamage() để tránh trùng nếu có trong lớp con cụ thể với ý nghĩa khác
        return damage;
    }

    public Character getCaster() {
        return caster;
    }

    public boolean isAlive() { // Thêm getter cho trạng thái alive
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Thiết lập các giá trị ban đầu cho projectile.
     *
     * @param worldX    Tọa độ X ban đầu (thường là trung tâm của caster).
     * @param worldY    Tọa độ Y ban đầu.
     * @param direction Hướng bay.
     * @param caster    Nhân vật bắn ra.
     * @param damage    Sát thương gây ra.
     */
    public abstract void set(int worldX, int worldY, String direction, Character caster, int damage);

    /**
     * Cập nhật trạng thái của projectile (di chuyển, kiểm tra va chạm).
     */
    public abstract void update();

    /**
     * Vẽ projectile lên màn hình.
     *
     * @param g2 Đối tượng Graphics2D để vẽ.
     */
    public abstract void draw(Graphics2D g2);

    /**
     * Kiểm tra va chạm của projectile (tại một điểm x,y cụ thể, thường là tâm hoặc điểm đầu) với các tile cứng.
     *
     * @param checkX Tọa độ X để kiểm tra.
     * @param checkY Tọa độ Y để kiểm tra.
     * @return true nếu có va chạm, false nếu không.
     */
    protected boolean checkTileCollision(int checkX, int checkY) {
        int tileCol = checkX / gp.getTileSize();
        int tileRow = checkY / gp.getTileSize();

        // Kiểm tra va chạm với biên bản đồ
        if (tileCol < 0 || tileCol >= gp.getMaxWorldCol() ||
                tileRow < 0 || tileRow >= gp.getMaxWorldRow()) {
            alive = false; // Ngoài biên thì coi như biến mất
            return true;
        }

        // Kiểm tra tile có tồn tại và có thuộc tính collision không
        if (gp.getTileM().tile[gp.getTileM().mapTileNum[gp.currentMap][tileCol][tileRow]] != null &&
                gp.getTileM().tile[gp.getTileM().mapTileNum[gp.currentMap][tileCol][tileRow]].collision) {
            alive = false; // Va chạm với tile cứng
            return true;
        }
        return false;
    }

    public boolean isSingleHit() {
        return true;
    }
}