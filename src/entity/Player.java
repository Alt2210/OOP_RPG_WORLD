package entity;

import main.GamePanel;
import main.KeyHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
// Không cần import IOException và ImageIO ở đây nữa vì dùng phương thức setup() từ Character

// Lớp Player, kế thừa từ Character.
// Chứa các thuộc tính và hành vi riêng của người chơi.
public class Player extends Character {

    // Player cần KeyHandler để đọc input từ bàn phím.
    // Khai báo public để các lớp khác (nếu cần) có thể truy cập trạng thái phím của player.
    public KeyHandler keyH;

    // Tọa độ cố định của người chơi trên MÀN HÌNH.
    // Trong game 2D dạng này, player thường được giữ cố định ở giữa màn hình,
    // và thế giới game (bản đồ) sẽ di chuyển xung quanh player.
    public final int screenX;
    public final int screenY;

    // --- Constructor ---
    // Được gọi từ lớp GamePanel khi tạo đối tượng Player.
    // Nhận tham chiếu đến GamePanel và KeyHandler.
    public Player(GamePanel gp, KeyHandler keyH) {
        // Gọi constructor của lớp cha (Character) và truyền tham chiếu GamePanel.
        super(gp);

        // Lưu tham chiếu đến KeyHandler.
        this.keyH = keyH;

        // Tính toán vị trí cố định của Player ở giữa màn hình.
        // (Kích thước màn hình / 2) - (Kích thước sprite / 2) để căn giữa chính xác.
        screenX = gp.ScreenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.ScreenHeight / 2 - (gp.tileSize / 2);

        // --- Cài đặt Vùng Va chạm (Solid Area) riêng cho Player ---
        // solidArea được khởi tạo trong constructor của lớp cha Character.
        // Ở đây, ta thiết lập kích thước và offset cụ thể cho Player.
        solidArea.x = 8; // Offset X từ góc trên bên trái sprite của Player
        solidArea.y = 16; // Offset Y từ góc trên bên trái sprite của Player
        solidArea.width = 32; // Chiều rộng vùng va chạm của Player
        solidArea.height = 32; // Chiều cao vùng va chạm của Player
        // Lưu lại offset mặc định để có thể reset khi cần thiết (ví dụ: sau đòn tấn công làm thay đổi solidArea).
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // --- Thiết lập Giá trị ban đầu và Tải ảnh ---
        // Gọi các phương thức để thiết lập trạng thái khởi tạo và tải hình ảnh của Player.
        setDefaultValues(); // Thiết lập worldX, worldY, speed, direction ban đầu.
        getImages();      // Tải hình ảnh hoạt ảnh của Player.
    }

    // --- Ghi đè các Phương thức chung từ Character ---

    // Ghi đè phương thức setDefaultValues() từ lớp Character.
    // Thiết lập các giá trị khởi tạo riêng cho Player khi bắt đầu game.
    @Override // Sử dụng annotation @Override là một cách tốt để kiểm tra lỗi nếu phương thức ở lớp cha bị đổi tên hoặc xóa.
    public void setDefaultValues() {
        // Vị trí ban đầu của Player trong thế giới game (worldX, worldY).
        // Sử dụng gp.tileSize để đặt vị trí theo hệ lưới ô vuông.
        worldX = gp.tileSize * 30; // Ví dụ: Bắt đầu ở cột 22
        worldY = gp.tileSize * 30; // Ví dụ: Bắt đầu ở hàng 24
        speed = 4; // Tốc độ di chuyển của Player
        direction = "down"; // Hướng ban đầu của Player khi game bắt đầu
    }

    // Ghi đè phương thức trừu tượng getImages() từ lớp Character.
    // Triển khai việc tải ảnh CỤ THỂ cho Player bằng phương thức helper setup() của lớp cha.
    @Override
    protected void getImages() {
        // Sử dụng phương thức helper setup() từ lớp cha (Character) để tải từng tệp ảnh.
        // Đường dẫn ảnh được giả định là đúng như cấu trúc thư mục resource của bạn (/player/...).
        // Lưu ý: Dựa theo đường dẫn ảnh trong code gốc, ảnh đi lên/phải dùng chung và xuống/trái dùng chung.
        // Nếu muốn hoạt ảnh khác nhau cho 4 hướng, bạn cần các tệp ảnh riêng biệt và cập nhật đường dẫn ở đây.

        up1 = setup("/player/sodier_walkright1.png"); // Ảnh đi lên frame 1 (có thể là ảnh đi phải?)
        up2 = setup("/player/sodier_walkright2.png");
        up3 = setup("/player/sodier_walkright3.png");
        up4 = setup("/player/sodier_walkright4.png");
        up5 = setup("/player/sodier_walkright5.png");

        down1 = setup("/player/sodier_walkleft1.png"); // Ảnh đi xuống frame 1 (có thể là ảnh đi trái?)
        down2 = setup("/player/sodier_walkleft2.png");
        down3 = setup("/player/sodier_walkleft3.png");
        down4 = setup("/player/sodier_walkleft4.png");
        down5 = setup("/player/sodier_walkleft5.png");

        left1 = setup("/player/sodier_walkleft1.png"); // Ảnh đi trái frame 1
        left2 = setup("/player/sodier_walkleft2.png");
        left3 = setup("/player/sodier_walkleft3.png");
        left4 = setup("/player/sodier_walkleft4.png");
        left5 = setup("/player/sodier_walkleft5.png");

        right1 = setup("/player/sodier_walkright1.png"); // Ảnh đi phải frame 1
        right2 = setup("/player/sodier_walkright2.png");
        right3 = setup("/player/sodier_walkright3.png");
        right4 = setup("/player/sodier_walkright4.png");
        right5 = setup("/player/sodier_walkright5.png");
    }

    // Ghi đè phương thức update() từ lớp Character.
    // Thêm logic xử lý input riêng của Player trước khi gọi logic cập nhật chung.
    @Override
    public void update() {

        // --- Player-specific Logic: Xử lý Input từ KeyHandler ---
        // Chỉ xử lý input để đặt hướng di chuyển (direction) nếu có phím di chuyển được nhấn.
        // Điều này giúp nhân vật đứng yên và dừng hoạt ảnh khi không nhấn phím nào.
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {

            // Xác định hướng di chuyển dựa trên phím được nhấn.
            // Sử dụng 'else if' để ưu tiên phím được kiểm tra sau nếu nhiều phím nhấn cùng lúc.
            if (keyH.upPressed){
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            // --- Gọi Logic Cập nhật chung của Lớp cha ---
            // Sau khi đã xác định được hướng di chuyển (direction) từ input,
            // gọi phương thức update() của lớp cha Character để thực hiện logic:
            // 1. Kiểm tra va chạm với tile (sử dụng direction, speed)
            // 2. Cập nhật vị trí thế giới (worldX, worldY) nếu không có va chạm.
            // 3. Cập nhật bộ đếm và số frame hoạt ảnh (spriteCounter, spriteNum).
            super.update();

        } else {
            // --- Xử lý khi không có phím di chuyển nào được nhấn ---
            // Nếu không có phím di chuyển nào, chúng ta không cập nhật direction dựa trên input mới.
            // Đồng thời, chúng ta KHÔNG gọi super.update(). Điều này có tác dụng:
            // 1. Vị trí thế giới của player không thay đổi (dừng lại).
            // 2. Logic hoạt ảnh trong super.update() không chạy, làm hoạt ảnh dừng lại ở frame cuối cùng.
            // Nếu bạn muốn nhân vật quay về frame đứng yên cụ thể khi dừng, bạn cần thêm logic ở đây,
            // ví dụ: spriteNum = 1; hoặc đặt direction về "standing" và trong getCurrentFrame() xử lý case "standing".
        }
    }

    // Triển khai phương thức trừu tượng draw(Graphics2D g2) từ lớp Character.
    // Định nghĩa cách vẽ Player lên màn hình.
    @Override
    public void draw(Graphics2D g2) {

        // --- Logic Vẽ Player ---
        // Lấy hình ảnh (BufferedImage) của frame hoạt ảnh hiện tại.
        // Phương thức getCurrentFrame() từ lớp cha Character sẽ trả về ảnh phù hợp
        // dựa trên direction và spriteNum hiện tại (đã được cập nhật trong update()).
        BufferedImage image = getCurrentFrame();

        // Vẽ hình ảnh lên màn hình.
        // Player được vẽ tại vị trí CỐ ĐỊNH trên màn hình (screenX, screenY).
        // Kích thước vẽ được scale bằng gp.tileSize.
        // null là tham số ImageObserver, thường dùng null khi vẽ trực tiếp lên Graphics.
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        // --- Tùy chọn: Vẽ vùng va chạm (solidArea) để debug ---
        // Điều này giúp bạn thấy rõ vùng va chạm của nhân vật trên màn hình.
        // Vị trí vẽ vùng va chạm: Tọa độ màn hình của Player + offset của solidArea.
        // g2.setColor(Color.red); // Đặt màu vẽ là đỏ
        // g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height); // Vẽ hình chữ nhật
    }

}