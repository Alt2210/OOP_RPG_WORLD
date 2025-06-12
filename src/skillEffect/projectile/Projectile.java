package skillEffect.projectile;

import character.Character;
import main.GamePanel;
import skillEffect.SkillEffect;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Projectile extends SkillEffect {
//    protected GamePanel gp;
    public int speed;
    public String direction;
    protected BufferedImage image; // Có thể là một danh sách BufferedImage nếu có animation
    protected int damage;
    public boolean alive = true; // Trạng thái của skillEffect.projectile (còn tồn tại hay không)
    // int solidAreaDefaultX, solidAreaDefaultY; // Không cần thiết nếu solidArea luôn là (0,0) tương đối với worldX, worldY của skillEffect.projectile

    protected int maxRange; // Tầm xa tối đa
    protected int distanceTraveled = 0; // Quãng đường đã di chuyển

    public int getMaxRange() {
        return maxRange;
    }

    public int getDistanceTraveled() {
        return distanceTraveled;
    }

    public Projectile(GamePanel gp) {
        super(gp);
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

    public abstract void set(int worldX, int worldY, String direction, Character caster, int damage);

    public abstract void update();

    public abstract void draw(Graphics2D g2);

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
        if (gp.getTileM().getTile()[gp.getTileM().getMapTileNum()[gp.getCurrentMap()][tileCol][tileRow]] != null &&
                gp.getTileM().getTile()[gp.getTileM().getMapTileNum()[gp.getCurrentMap()][tileCol][tileRow]].isCollision()) {
            alive = false; // Va chạm với tile cứng
            return true;
        }
        return false;
    }

    @Override
    public boolean isSingleHit() {
        return true;
    }
}