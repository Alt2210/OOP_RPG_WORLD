package character;

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
        cip.setNumSprite(5);

        // Lưu tham chiếu đến KeyHandler.
        this.keyH = keyH;

        // Tính toán vị trí cố định của Player ở giữa màn hình.
        // (Kích thước màn hình / 2) - (Kích thước sprite / 2) để căn giữa chính xác.
        screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);
        screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);

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
        cip.getImage("/player", "sodier_walk");      // Tải hình ảnh hoạt ảnh của Player.
    }

    // --- Ghi đè các Phương thức chung từ Character ---

    // Ghi đè phương thức setDefaultValues() từ lớp Character.
    // Thiết lập các giá trị khởi tạo riêng cho Player khi bắt đầu game.
    @Override
    public void setAction(){}
    @Override // Sử dụng annotation @Override là một cách tốt để kiểm tra lỗi nếu phương thức ở lớp cha bị đổi tên hoặc xóa.
    public void setDefaultValues() {
        // Vị trí ban đầu của Player trong thế giới game (worldX, worldY).
        // Sử dụng gp.getTileSize() để đặt vị trí theo hệ lưới ô vuông.
        worldX = gp.getTileSize() * 30; // Ví dụ: Bắt đầu ở cột 30
        worldY = gp.getTileSize() * 30; // Ví dụ: Bắt đầu ở hàng 30
        speed = 4; // Tốc độ di chuyển của Player
        direction = "down"; // Hướng ban đầu của Player khi game bắt đầu

        maxHealth = 100;
        currentHealth = maxHealth;
    }

    // Ghi đè phương thức update() từ lớp Character.
    // Thêm logic xử lý input riêng của Player trước khi gọi logic cập nhật chung.
    @Override
    public void update() {
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            // 1. Xác định hướng di chuyển dựa trên input
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            // 2. Reset cờ va chạm cho frame hiện tại của Player
            collisionOn = false;

            // 3. Thực hiện tất cả các kiểm tra va chạm CẦN THIẾT cho Player
            //    Thứ tự kiểm tra có thể quan trọng tùy theo logic game của bạn.
            //    Ở đây, chúng ta kiểm tra tile trước, sau đó đến NPC.

            // 3a. Kiểm tra va chạm với Tile
            gp.getcChecker().checkTile(this); // Phương thức này sẽ đặt this.collisionOn = true nếu có va chạm tile

            // 3b. Kiểm tra va chạm với Objects (nếu có) - ví dụ:
            // int objectIndex = gp.getcChecker(.checkObject(this, true); // true nghĩa là Player có thể tương tác
            // if (objectIndex != 999) {
            //     pickUpObject(objectIndex); // Phương thức riêng của Player
            //     // Có thể không cần đặt collisionOn = true nếu player có thể nhặt và đi qua
            // }

            int itemIndex = gp.getcChecker().checkItem(this, true);
            pickUpItem(itemIndex);

            // 3c. Kiểm tra va chạm với NPCs
            int npcIndex = gp.getcChecker().checkEntity(this, gp.getNpc());
            if (npcIndex != 999) {
                // Nếu va chạm NPC, đặt collisionOn của Player là true để ngăn di chuyển
                // và sau đó xử lý tương tác.
                this.collisionOn = true;
                interactWithNPC(npcIndex);
            }

            // 3d. Kiểm tra va chạm với Monsters (tương tự NPCs)
            // int monsterIndex = gp.getcChecker(.checkEntity(this, gp.monsters); // Giả sử có mảng gp.monsters
            // if (monsterIndex != 999) {
            //     this.collisionOn = true;
            //     // interactWithMonster(monsterIndex); // Phương thức riêng của Player
            // }


            // 4. Di chuyển Player NẾU KHÔNG có bất kỳ va chạm nào (từ tile, NPC, monster, object...)
            if (!collisionOn) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }



            // 5. Cập nhật hoạt ảnh (animation)
            // Logic này có thể được lấy từ Character.update() hoặc bạn có thể tạo
            // một phương thức protected updateAnimation() trong Character để Player gọi.
            cip.update();

        } else {
            // Người chơi không nhấn phím di chuyển -> xử lý animation đứng yên
            cip.setSpriteNum(0); // Đặt về frame đầu tiên (hoặc frame đứng yên cụ thể)
            // Hoặc bạn có thể không làm gì ở đây để giữ nguyên frame cuối cùng của hành động trước.
            // Tùy thuộc vào hiệu ứng bạn muốn.
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
        BufferedImage image = cip.getCurFrame();

        // Vẽ hình ảnh lên màn hình.
        // Player được vẽ tại vị trí CỐ ĐỊNH trên màn hình (screenX, screenY).
        // Kích thước vẽ được scale bằng gp.getTileSize().
        // null là tham số ImageObserver, thường dùng null khi vẽ trực tiếp lên Graphics.
        g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);


        drawHealthBar(g2, screenX, screenY); // Truyền screenX và screenY vào
        // --- Tùy chọn: Vẽ vùng va chạm (solidArea) để debug ---
        // Điều này giúp bạn thấy rõ vùng va chạm của nhân vật trên màn hình.
        // Vị trí vẽ vùng va chạm: Tọa độ màn hình của Player + offset của solidArea.
        // g2.setColor(Color.red); // Đặt màu vẽ là đỏ
        // g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height); // Vẽ hình chữ nhật
    }

    public void interactWithNPC(int npcIndex) {
        if (npcIndex != 999) {
            // Logic khi Player tương tác với NPC: ví dụ mở hội thoại
            System.out.println("Interacting with NPC: " + gp.getNpc()[npcIndex].toString());
            // gp.gameState = gp.dialogueState;
            // gp.npc[npcIndex].speak(); // Giả sử NPC có phương thức speak()
        }
    }




    public void pickUpItem(int i) {
        // the object array's index
        if (i != 999) {
            String itemName = gp.getwObjects()[i].name;

            switch (itemName) {
                case "Key":

                    gp.getwObjects()[i] = null;
                    System.out.println("Key:" );
                    break;
                case "Door":
                    /*if (hasKey > 0) {
                        gp.item[i] = null;
                        hasKey--;
                    }*/
                    System.out.println("Touch Door");
                    break;
            }
        }
    }



}