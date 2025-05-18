package entity;

import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

// Lớp cơ sở trừu tượng cho tất cả các nhân vật có thể di chuyển và có hoạt ảnh
// abstract: Không thể tạo đối tượng trực tiếp từ lớp này.
// Phải thông qua lớp con (Player, Monster, NPC di chuyển...).
public abstract class Character {

    // --- Thuộc tính chung của Nhân vật ---

    // Cần tham chiếu đến GamePanel để truy cập các hệ thống game (va chạm, bản đồ, cài đặt...)
    protected GamePanel gp;

    // Vị trí và chuyển động trong thế giới game (tọa độ thế giới)
    public int worldX, worldY;
    public int speed;
    public String direction; // Hướng hiện tại ("up", "down", "left", "right", hoặc "standing" nếu có)

    // Hoạt ảnh (Animation)
    // Lưu trữ các khung hình hoạt ảnh cho mỗi hướng
    protected BufferedImage up1, up2, up3, up4, up5;
    protected BufferedImage down1, down2, down3, down4, down5;
    protected BufferedImage left1, left2, left3, left4, left5;
    protected BufferedImage right1, right2, right3, right4, right5;

    // Biến điều khiển hoạt ảnh
    protected int spriteCounter = 0; // Bộ đếm thời gian để chuyển frame
    protected int spriteNum = 1;     // Số thứ tự của frame hoạt ảnh hiện tại đang hiển thị (1-5)

    // Va chạm (Collision)
    public Rectangle solidArea; // Định nghĩa vùng va chạm của nhân vật (offset so với góc trên bên trái sprite)
    public int solidAreaDefaultX, solidAreaDefaultY; // Lưu lại offset mặc định của solidArea
    public boolean collisionOn = false; // Cờ báo hiệu có va chạm trong lần cập nhật này
    public int actionLockCounter = 0;

    // --- Constructor ---
    // Mọi lớp con khi được tạo phải gọi constructor này và truyền GamePanel.
    public Character(GamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle(); // Khởi tạo đối tượng Rectangle cho vùng va chạm
        // Kích thước và offset cụ thể của solidArea sẽ được thiết lập trong constructor của lớp con.
    }

    // --- Phương thức trừu tượng (Bắt buộc lớp con phải triển khai) ---

    // Phương thức trừu tượng để các lớp con tải hình ảnh hoạt ảnh CỤ THỂ của chúng.
    // Lớp Character cần các biến BufferedImage (up1...right5) nhưng không biết ảnh trông như thế nào.
    protected abstract void getImages();
    public abstract void setAction();
    // Phương thức trừu tượng để vẽ nhân vật lên màn hình.
    // Cách tính toán vị trí vẽ trên màn hình khác nhau giữa Player và Monster.
    public abstract void draw(Graphics2D g2);

    // --- Phương thức chung (Lớp con có thể ghi đè nếu cần) ---

    // Thiết lập các giá trị ban đầu cho nhân vật khi game bắt đầu hoặc khi sinh ra.
    // Lớp con sẽ ghi đè để thiết lập vị trí khởi tạo, tốc độ mặc định, hướng ban đầu...
    public void setDefaultValues() {
        // Lớp Character không có giá trị mặc định cụ thể cho mọi nhân vật,
        // nên phương thức này ở đây chỉ là một "khung" để lớp con ghi đè.
    }

    // --- Logic Cập nhật chung (Được gọi bởi lớp con) ---

    // Phương thức này chứa logic cập nhật trạng thái di chuyển và hoạt ảnh chung
    // cho mọi nhân vật. Lớp con gọi phương thức này sau khi đã xác định
    // hướng di chuyển (direction) và các hành động khác.
    public void update() {

        // --- Logic Di chuyển và Va chạm ---
        // Phương thức này giả định 'direction' đã được thiết lập bởi lớp con (qua input hoặc AI).
        // Nếu direction là null hoặc không khớp các case, nhân vật sẽ không di chuyển.

        // Đặt lại cờ va chạm trước mỗi lần kiểm tra
        collisionOn = false;
        // Gọi hệ thống kiểm tra va chạm của GamePanel để xem nhân vật có đụng độ với tile không.
        // CollisionChecker sẽ sử dụng worldX, worldY, speed, direction, solidArea của 'this'.
        gp.cChecker.checkTile(this);
        if (!(this instanceof Player)) { // Quan trọng: Chỉ NPC/Monster mới checkPlayer theo cách này
            boolean contactWithPlayer = gp.cChecker.checkPlayer(this);
            // Phương thức checkPlayer(Character entity) trong CollisionChecker sẽ đặt
            // entity.collisionOn = true nếu entity (NPC) sắp va chạm Player.
        }

        // Nếu không có va chạm (sau khi checkTile), cho phép nhân vật di chuyển thực sự.
        if(!collisionOn) {
            switch(direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
                // Các case khác như "standing", hoặc  direction = null sẽ không làm thay đổi worldX/worldY.
            }
        }

        // --- Logic Cập nhật Hoạt ảnh ---
        // Logic này điều khiển việc chuyển đổi giữa các khung hình hoạt ảnh (spriteNum)
        // để tạo hiệu ứng chuyển động.
        // Nó chạy mỗi khi phương thức update() này được gọi (tức là mỗi frame game
        // nếu lớp con gọi super.update() mỗi frame).

        spriteCounter++; // Tăng bộ đếm frame game
        // Điều chỉnh số 3 để thay đổi tốc độ hoạt ảnh (số càng nhỏ, chuyển động càng nhanh).
        // Nên dùng một giá trị dựa trên FPS của game để tốc độ hoạt ảnh nhất quán trên các máy khác nhau.
        // Ví dụ: gp.fps / số frame trên giây bạn muốn chuyển động (ví dụ 10 frame/s -> gp.fps/10)
        if(spriteCounter > gp.FPS/10) { // Chuyển frame sau một số frame game nhất định
            spriteNum++; // Chuyển sang frame hoạt ảnh tiếp theo
            if (spriteNum > 5) { // Nếu vượt quá frame cuối (5), quay lại frame đầu (1)
                spriteNum = 1;
            }
            spriteCounter = 0; // Đặt lại bộ đếm sau khi chuyển frame
        }
        // Note: Logic animation này sẽ chạy ngay cả khi nhân vật không di chuyển nếu direction vẫn được set
        // và super.update() được gọi. Logic dừng hoạt ảnh khi đứng yên cần được xử lý ở lớp con (ví dụ Player).
    }

    // --- Phương thức Helper ---

    // Trả về hình ảnh (BufferedImage) của frame hoạt ảnh hiện tại dựa trên hướng và spriteNum.
    // Được sử dụng bởi phương thức draw() của lớp con.
    public BufferedImage getCurrentFrame() {
        BufferedImage image = null; // Mặc định là null
        switch (direction) {
            case "up":
                if(spriteNum == 1) image = up1;
                else if(spriteNum == 2) image = up2;
                else if(spriteNum == 3) image = up3;
                else if(spriteNum == 4) image = up4;
                else if(spriteNum == 5) image = up5;
                break;
            case "down":
                if(spriteNum == 1) image = down1;
                else if(spriteNum == 2) image = down2;
                else if(spriteNum == 3) image = down3;
                else if(spriteNum == 4) image = down4;
                else if(spriteNum == 5) image = down5;
                break;
            case "left":
                if(spriteNum == 1) image = left1;
                else if(spriteNum == 2) image = left2;
                else if(spriteNum == 3) image = left3;
                else if(spriteNum == 4) image = left4;
                else if(spriteNum == 5) image = left5;
                break;
            case "right":
                if(spriteNum == 1) image = right1;
                else if(spriteNum == 2) image = right2;
                else if(spriteNum == 3) image = right3;
                else if(spriteNum == 4) image = right4;
                else if(spriteNum == 5) image = right5;
                break;
            // Optional: Thêm case "standing" hoặc default để trả về ảnh đứng yên
            default:
                // Nếu direction không phải 4 hướng chính, mặc định hiển thị ảnh đứng yên (ví dụ: down1)
                image = down1; // Có thể thay bằng ảnh đứng yên riêng nếu có.
                break;
        }
        return image; // Trả về hình ảnh của frame hiện tại (có thể là null hoặc ảnh mặc định nếu lỗi/không khớp hướng)
    }

    // Phương thức helper để tải hình ảnh từ resource folder một cách an toàn, xử lý lỗi.
    // Được sử dụng bởi phương thức getImages() của các lớp con.
    protected BufferedImage setup(String imagePath) {
        BufferedImage image = null;
        try {
            // getClass().getResourceAsStream() là cách chuẩn để tải resource (ảnh, tệp bản đồ...) từ classpath của ứng dụng Java.
            // Điều này hoạt động tốt khi đóng gói ứng dụng thành JAR.
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
        } catch (IOException e) {
            // In lỗi ra console nếu không tìm thấy hoặc không đọc được tệp ảnh.
            e.printStackTrace();
        }
        return image; // Trả về ảnh đã tải (hoặc null nếu lỗi).
    }
}