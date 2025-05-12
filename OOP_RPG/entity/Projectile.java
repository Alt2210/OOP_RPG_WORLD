package entity;

import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Projectile extends OBject { // Kế thừa từ OBject

    public Character user; // Character đã bắn ra projectile này
    public boolean alive = false; // Trạng thái sống/chết của projectile

    // Các thuộc tính cơ bản của projectile, sẽ được thiết lập bởi các lớp con
    // hoặc có giá trị mặc định ở đây nếu tất cả projectile đều giống nhau ban đầu.
    public int life;         // Thời gian tồn tại hiện tại (đếm ngược)
    public int maxLife;      // Thời gian tồn tại tối đa (số frame)
    public int speed;        // Tốc độ di chuyển
    public String direction;  // Hướng di chuyển của Projectile (QUAN TRỌNG)
     public int attackValue;    // Sát thương - Kế thừa từ OBject
     public int knockBackPower; // Lực đẩy lùi - Kế thừa từ OBject
     public int useCost;        // Chi phí sử dụng (mana, ammo) - Kế thừa từ OBject

//     spriteNum và spriteCounter được kế thừa từ GameObject,
//     chúng sẽ được dùng nếu projectile có animation.
     public int spriteNum = 1;
     public int spriteCounter = 0;

    public Projectile(GamePanel gp) {
        super(gp);
        this.type = type_projectile; // type_projectile cần được định nghĩa trong GameObject
        this.collision = true;     // Projectile thường có va chạm
        this.alive = false;        // Mặc định là chưa active
        // Các giá trị mặc định có thể được đặt ở đây hoặc trong constructor của lớp con
    }

    // Phương thức này sẽ được gọi bởi các lớp con cụ thể (ví dụ: OBJ_Fireball)
    // để load hình ảnh riêng của chúng.
    public abstract void getImage();

    public void set(int worldX, int worldY, String directionOfUser, boolean aliveStatus, Character shooter) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = directionOfUser; // Hướng của projectile sẽ là hướng của người bắn
        this.alive = aliveStatus;
        this.user = shooter;
        this.life = this.maxLife;       // Reset thời gian sống khi được bắn lại
        this.collisionOn = false;     // Reset trạng thái va chạm

        // Thiết lập solidArea dựa trên kích thước hình ảnh nếu cần
        // Ví dụ, nếu bạn có 1 hình ảnh cố định 'image' cho projectile:
        // if (image != null) {
        //     solidArea.width = image.getWidth();
        //     solidArea.height = image.getHeight();
        // } else { // Kích thước mặc định nếu không có ảnh
        //     solidArea.width = 8;
        //     solidArea.height = 8;
        // }
        // solidArea.x = 0; // Hoặc căn giữa nếu cần
        // solidArea.y = 0;
        // solidAreaDefaultX = solidArea.x;
        // solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void update() {
        if (!alive || user == null) {
            drawing = false; // Kế thừa từ GameObject
            return;
        }
        drawing = true;

        // 1. Xử lý va chạm (Collision Handling)
        // Va chạm với Player (nếu projectile này không phải do player bắn)
        if (user != gp.player) {
            boolean contactPlayer = gp.cChecker.checkPlayer(this); // 'this' là Projectile
            if (!gp.player.invincible && contactPlayer) {
                // gp.player.takeDamage(this.attackValue, this.user); // Ví dụ: Player có phương thức nhận sát thương
                // Tạm thời:
                if (this.attackValue > 0) { // Chỉ gây sát thương nếu có attackValue
                    int damageDealt = Math.max(1, this.attackValue - gp.player.defense); // Player có defense
                    gp.player.life -= damageDealt;
                    gp.player.invincible = true;
                    gp.player.transparent = true;
                    // Cân nhắc thêm knockback cho Player nếu projectile này có knockBackPower
                    // gp.player.setKnockBack(gp.player, this.user, this.knockBackPower);
                }
                generateParticle(this, gp.player); // Tạo hạt khi trúng player
                alive = false;
            }
        }
        // Va chạm với Monster (nếu projectile này do player bắn)
        else if (user == gp.player) {
            // int monsterIndex = gp.cChecker.checkCreatures(this, gp.monster); // Cần phương thức checkCreatures
            // if (monsterIndex != 999) {
            //     Character targetMonster = gp.monster[gp.currentMap][monsterIndex];
            //     gp.player.damageMonster(monsterIndex, this, this.attackValue, this.knockBackPower);
            //     generateParticle(this, targetMonster);
            //     alive = false;
            // }
        }

        // Va chạm với Tile (tường)
        collisionOn = false; // Reset trước khi kiểm tra
        gp.cChecker.checkTile(this);
        if (collisionOn) {
            generateParticle(this, this); // Tạo hạt tại điểm va chạm
            alive = false;
        }

        // Va chạm với InteractiveTile (ví dụ: các đối tượng có thể phá hủy)
        // int iTileIndex = gp.cChecker.checkInteractiveTiles(this, gp.iTile);
        // if (iTileIndex != 999) {
        //     InteractiveTile tile = gp.iTile[gp.currentMap][iTileIndex];
        //     if (tile.destructible) {
        //         // tile.takeDamage(this.attackValue); // Giả sử ITile có phương thức này
        //         // gp.playSE(âm thanh phá vỡ);
        //         generateParticle(this, tile);
        //         alive = false;
        //     } else { // Nếu không phá hủy được nhưng vẫn là vật cản
        //         alive = false;
        //         generateParticle(this, tile);
        //     }
        // }

        // Nếu sau các kiểm tra va chạm, projectile vẫn còn 'alive' thì mới di chuyển
        if (alive) {
            // 2. Di chuyển (Movement)
            switch (this.direction) { // Sử dụng hướng đã được lưu của projectile
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }

            // 3. Giảm thời gian sống (Life Countdown)
            life--;
            if (life <= 0) {
                alive = false;
            }
        }

        // 4. Animation (Sprite Animation) (nếu projectile có nhiều frame)
        // spriteNum và spriteCounter được kế thừa từ GameObject
        spriteCounter++;
        if (spriteCounter > 12) { // Tốc độ animation (ví dụ: 12 frame game đổi 1 frame sprite)
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }

        if (!alive) {
            drawing = false;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!drawing || !inCamera() || !alive) {
            return;
        }

        BufferedImage imageToDraw = null;

        // Logic chọn sprite dựa trên direction và spriteNum
        // Các biến up1, down1, left1, right1, up2, down2, left2, right2
        // được kế thừa từ GameObject và sẽ được load bởi lớp con cụ thể (OBJ_Fireball)
        switch (direction) {
            case "up":
                imageToDraw = (spriteNum == 1) ? up1 : up2;
                break;
            case "down":
                imageToDraw = (spriteNum == 1) ? down1 : down2;
                break;
            case "left":
                imageToDraw = (spriteNum == 1) ? left1 : left2;
                break;
            case "right":
                imageToDraw = (spriteNum == 1) ? right1 : right2;
                break;
            default: // Nếu không có hướng hoặc chỉ có 1 hình ảnh chung
                imageToDraw = image; // Biến 'image' từ GameObject
                break;
        }

        // Nếu sau logic trên imageToDraw vẫn null (ví dụ lớp con chưa load ảnh đúng)
        // thì vẽ một hình thay thế để dễ debug
        if (imageToDraw == null && image != null) { // Ưu tiên sprite theo hướng, nếu không có thì dùng image chung
            imageToDraw = image;
        }


        if (imageToDraw != null) {
            // Vẽ với kích thước thực của sprite, hoặc một kích thước cố định cho projectile
            int drawWidth = gp.tileSize / 2;  // Ví dụ: nửa tile
            int drawHeight = gp.tileSize / 2; // Ví dụ: nửa tile
            // Nếu muốn dùng kích thước thật của ảnh:
            // drawWidth = imageToDraw.getWidth();
            // drawHeight = imageToDraw.getHeight();
            g2.drawImage(imageToDraw, getScreenX(), getScreenY(), drawWidth, drawHeight, null);
        } else {
            // Fallback nếu không có ảnh nào được load
            g2.setColor(Color.ORANGE); // Màu cho projectile mặc định
            g2.fillOval(getScreenX() + gp.tileSize / 2 - 5, getScreenY() + gp.tileSize / 2 - 5, 10, 10); // Vẽ hình tròn nhỏ
            System.err.println("Warning: Projectile '" + name + "' is trying to draw with a null image.");
        }
    }

    // Các phương thức này nên được override bởi các lớp con cụ thể
    // nếu chúng có chi phí tài nguyên khác nhau.
    public boolean haveResource(Character user) {
        if (this.useCost <= 0) return true; // Không tốn gì cả
        // Ví dụ:
        // if (name.equals("Fireball") && user.mana >= this.useCost) return true;
        // if (name.equals("Arrow") && user.ammo >= this.useCost) return true;
        return false; // Mặc định là không đủ nếu có useCost
    }

    public void subtractResource(Character user) {
        if (this.useCost > 0) {
            // Ví dụ:
            // if (name.equals("Fireball")) user.mana -= this.useCost;
            // if (name.equals("Arrow")) user.ammo -= this.useCost;
        }
    }
}