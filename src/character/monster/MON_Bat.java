package character.monster;

import character.Character;
import character.Player;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MON_Bat extends Monster {
    private int dashCounter;
    private final int DASH_INTERVAL = 180; // Lướt mỗi 2 giây
    private final int DASH_SPEED = 5;
    private final int NORMAL_SPEED = 1;
    private boolean isDashing = false;      // Trạng thái đang lướt
    private int dashDurationCounter = 0;    // Đếm thời gian của cú lướt hiện tại
    private final int DASH_DURATION_MAX = 45; // Thời gian tối đa cho một cú lướt (ví dụ: 0.75 giây ở 60FPS)

    private boolean hasDamagedPlayerThisDash = false; // Đảm bảo chỉ gây sát thương 1 lần/lướt
    private String lockedDashDirection;     // Hướng lướt được "khóa" khi bắt đầu lướt

    public MON_Bat(GamePanel gp) {
        super(gp);
        cip.setNumSprite(2);
        direction = "down";
        speed = 1;
        dashCounter = 0;
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 27;
        worldY = gp.getTileSize() * 33;
        direction = "down";
        setName("Bat");
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxHealth = 50;
        currentHealth = maxHealth;
        attack = 4;
        defense = 0;
        exp = 2;
        attackRange = gp.getTileSize() * 2;
        ATTACK_COOLDOWN_DURATION = 30;
        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        cip.getImage("/monster", "bat");
    }

    @Override
    public void update() {
        if (isDashing) {
            // --- LOGIC KHI ĐANG LƯỚT ---
            this.direction = lockedDashDirection; // Giữ nguyên hướng lướt
            this.speed = DASH_SPEED; // Sử dụng tốc độ lướt
            dashDurationCounter++;

            // 1. KIỂM TRA VA CHẠM VỚI TILE (TƯỜNG)
            collisionOn = false; // Reset trạng thái va chạm trước mỗi lần kiểm tra
            gp.getcChecker().checkTile(this); // Kiểm tra va chạm với tile

            // 2. NẾU KHÔNG VA CHẠM TƯỜNG, DI CHUYỂN BAT
            if (!collisionOn) {
                switch (lockedDashDirection) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            } else {
                // Nếu đụng tường, dừng lướt ngay lập tức
                isDashing = false;
                }

            // 3. KIỂM TRA VA CHẠM VỚI PLAYER ĐỂ GÂY SÁT THƯƠNG (NẾU CHƯA DỪNG LƯỚT VÌ ĐỤNG TƯỜNG)
            if (isDashing) { // Kiểm tra lại vì có thể isDashing đã bị set false do đụng tường
                Player player = gp.getPlayer();
                // Tạo một Rectangle tạm thời cho vùng va chạm của Bat tại vị trí hiện tại
                // để kiểm tra với solidArea của Player.
                Rectangle batAttackCheckArea = new Rectangle(
                        worldX + solidArea.x,
                        worldY + solidArea.y,
                        solidArea.width,
                        solidArea.height
                );
                int attackReachPadding = 2; // Ví dụ: thêm 2 pixel "tầm với"
                batAttackCheckArea.grow(attackReachPadding, attackReachPadding); // Mở rộng về mọi phía

                if (!hasDamagedPlayerThisDash && batAttackCheckArea.intersects(player.solidArea)) {
                    int dashAttackPower = this.attack + 2; // Ví dụ
                    player.receiveDamage(dashAttackPower, this);
                    gp.getUi().showMessage(getName() + " lướt trúng " + player.getName() + "!");
                    hasDamagedPlayerThisDash = true;
                    // isDashing = false; // Cân nhắc dừng lướt sau khi trúng để tránh đa sát thương
                }
            }

            // 4. KẾT THÚC LƯỚT NẾU HẾT THỜI GIAN HOẶC ĐÃ BỊ DỪNG DO ĐỤNG TƯỜNG
            if (dashDurationCounter >= DASH_DURATION_MAX || !isDashing) { // !isDashing là để xử lý trường hợp đụng tường
                isDashing = false;
                this.speed = defaultSpeed; // Trả Bat về tốc độ bình thường
            }
            cip.update(); //

        } else {
            // --- LOGIC KHI KHÔNG LƯỚT (HÀNH VI BÌNH THƯỜNG CỦA BAT) ---
            dashCounter++; // Đếm thời gian đến lần lướt tiếp theo

            if (dashCounter >= DASH_INTERVAL) {
                // Đủ điều kiện để bắt đầu một cú lướt mới
                isDashing = true;
                hasDamagedPlayerThisDash = false; // Reset lại cho cú lướt mới
                dashDurationCounter = 0;        // Reset bộ đếm thời gian lướt

                // Xác định hướng lướt dựa trên vị trí hiện tại của Player
                Player target = gp.getPlayer();
                int dx = target.getCenterX() - this.getCenterX(); //
                int dy = target.getCenterY() - this.getCenterY(); //

                if (Math.abs(dx) > Math.abs(dy)) {
                    lockedDashDirection = (dx > 0) ? "right" : "left";
                } else {
                    lockedDashDirection = (dy > 0) ? "down" : "up";
                }
                this.direction = lockedDashDirection; // Cập nhật hướng hiện tại để cip có thể chọn animation ban đầu cho cú lướt

                // Nếu bạn có cơ chế chuyển animation trong CharacterImageProcessor (cip):
                // cip.setAnimationMode("dash"); // Chuyển sang animation lướt
                // gp.playSoundEffect(Sound.BAT_DASH_SOUND); // Phát âm thanh khi Bat bắt đầu lướt

                dashCounter = 0; // Reset bộ đếm khoảng thời gian giữa các cú lướt
            } else {
                // Khi không lướt và chưa đến lúc lướt, Bat thực hiện hành vi bình thường
                // Ví dụ: di chuyển ngẫu nhiên hoặc đứng yên
                getRandomDirection(120); // Thay đổi hướng ngẫu nhiên mỗi 2 giây

                collisionOn = false; // Reset va chạm
                gp.getcChecker().checkTile(this); // Kiểm tra va chạm với tile

                // Quan trọng: Khi không lướt, Bat NÊN bị chặn bởi Player.
                // Phương thức checkPlayer sẽ đặt entity.collisionOn = true nếu có va chạm.
                boolean contactWithPlayer = gp.getcChecker().checkPlayer(this); //

                if (!collisionOn) { // Nếu không bị chặn bởi tile hoặc Player (khi không lướt)
                    switch (direction) {
                        case "up":
                            worldY -= speed; //
                            break;
                        case "down":
                            worldY += speed; //
                            break;
                        case "left":
                            worldX -= speed; //
                            break;
                        case "right":
                            worldX += speed; //
                            break;
                    }
                }
                cip.update(); // Cập nhật animation bình thường của Bat
            }
        }
    }


    public void flyAttack(Character target) {
        if (canAttack()) {
            target.receiveDamage(attack, this);
            resetAttackCooldown();
            gp.getUi().showMessage(getName() + " lướt vào " + target.getName() + "!");
        }
    }

    private String getDirectionFromAngle(double angle) {
        angle = Math.toDegrees(angle);
        if (angle >= -45 && angle < 45) return "right";
        if (angle >= 45 && angle < 135) return "down";
        if (angle >= 135 || angle < -135) return "left";
        return "up";
    }

    /* @Override
    protected void dropItems() {
        int i = new Random().nextInt(100) + 1;
        if (i < 50) {
            dropItem(new OBJ_Coin_Bronze(gp));
        } else if (i < 75) {
            dropItem(new OBJ_Heart(gp));
        } else {
            dropItem(new OBJ_ManaCrystal(gp));
        }
    } */

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = cip.getCurFrame(); // Lấy frame hiện tại từ lớp Character
        if (image != null) {
            // Tính toán vị trí vẽ trên màn hình tương tự như cách vẽ Tile hoặc SuperItem
            int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
            int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;

            // Chỉ vẽ NPC nếu nó nằm trong tầm nhìn của camera
            if (worldX + gp.getTileSize() > gp.getPlayer().worldX - gp.getPlayer().screenX &&
                    worldX - gp.getTileSize() < gp.getPlayer().worldX + gp.getPlayer().screenX &&
                    worldY + gp.getTileSize() > gp.getPlayer().worldY - gp.getPlayer().screenY &&
                    worldY - gp.getTileSize() < gp.getPlayer().worldY + gp.getPlayer().screenY) {
                g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
            }

            drawHealthBar(g2, screenX, screenY);
            g2.setColor(Color.RED); // Hoặc một màu khác để phân biệt
// screenX và screenY là tọa độ vẽ của sprite trên màn hình
            g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
        }
    }
}