package projectile;

import character.Character;
import character.monster.Monster; // Để kiểm tra va chạm với Monster
import main.GamePanel;
import sound.Sound; // Để sử dụng âm thanh

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Fireball extends Projectile {


    private ArrayList<BufferedImage> imagesUp;
    private ArrayList<BufferedImage> imagesDown;
    private ArrayList<BufferedImage> imagesLeft;
    private ArrayList<BufferedImage> imagesRight;

    private int spriteCounter = 0;
    private int spriteNum = 0;
    private int numFramesPerDirection = 2; // Giả sử bạn có 2 frame cho mỗi hướng (vd: _1.png, _2.png)
    private int animationSpeed = 10;       // Số frame game trước khi chuyển sang frame hoạt ảnh tiếp theo của fireball

    public Fireball(GamePanel gp) {
        super(gp);
        // Các giá trị mặc định cho Fireball sẽ được đặt trong phương thức set()
        // hoặc khi đối tượng được tạo và cấu hình bởi caster.
        imagesUp = new ArrayList<>();
        imagesDown = new ArrayList<>();
        imagesLeft = new ArrayList<>();
        imagesRight = new ArrayList<>();
        // Tải hình ảnh Fireball
        loadDirectionalImages();
        // Đặt kích thước solidArea dựa trên một trong các ảnh (ví dụ: imageDown) hoặc một giá trị cố định nếu bạn muốn.
        if (gp != null) { // Thêm kiểm tra null cho gp
            solidArea.width = (int) (gp.getTileSize() * 0.75); // Kích thước mới cho Fireball
            solidArea.height = (int) (gp.getTileSize() * 0.75); // Kích thước mới cho Fireball
        } else { // Fallback nếu gp null (không nên xảy ra)
            solidArea.width = 36; // Giá trị mặc định nếu gp null
            solidArea.height = 36; // Giá trị mặc định nếu gp null
        }
    }
    private void loadDirectionalImages() {
        String[] directionNames = {"up", "down", "left", "right"};
        @SuppressWarnings("unchecked") // Bỏ cảnh báo type safety cho mảng ArrayList generic
        ArrayList<BufferedImage>[] imageLists = new ArrayList[]{imagesUp, imagesDown, imagesLeft, imagesRight};
        for (int i = 0; i < directionNames.length; i++) {
            for (int frame = 1; frame <= numFramesPerDirection; frame++) {
                String path = "/projectile/fireball_" + directionNames[i] + "_" + frame + ".png";
                try {
                    InputStream is = getClass().getResourceAsStream(path);
                    if (is == null) {
                        System.err.println("Cảnh báo: Không tìm thấy ảnh Fireball tại: " + path + ". Bỏ qua frame này.");
                        // Nếu frame đầu tiên của một hướng không tồn tại, thêm placeholder để tránh lỗi
                        if (frame == 1) {
                            imageLists[i].add(createPlaceholderImage()); // Thêm placeholder
                            if(numFramesPerDirection > 1) { // Nếu dự kiến có nhiều frame
                                imageLists[i].add(createPlaceholderImage()); // Thêm placeholder cho frame thứ 2 (nếu có)
                            }
                        }
                        continue; // Bỏ qua việc tải frame này
                    }
                    imageLists[i].add(ImageIO.read(is));
                } catch (IOException e) {
                    System.err.println("Lỗi khi tải ảnh Fireball: " + path + " - " + e.getMessage());
                    // Nếu có lỗi, thêm placeholder để đảm bảo danh sách không rỗng và có đủ số frame như mong đợi
                    imageLists[i].add(createPlaceholderImage());
                    if(numFramesPerDirection > 1 && imageLists[i].size() < numFramesPerDirection) {
                        imageLists[i].add(createPlaceholderImage());
                    }
                }
            }
            // Đảm bảo mỗi hướng có ít nhất một ảnh (placeholder nếu không tải được)
            if (imageLists[i].isEmpty()) {
                for(int f=0; f < numFramesPerDirection; f++){
                    imageLists[i].add(createPlaceholderImage());
                }
            }
        }
    }

    private BufferedImage createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(solidArea.width, solidArea.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();
        g.setColor(Color.ORANGE);
        g.fillOval(0, 0, solidArea.width, solidArea.height);
        g.setColor(Color.YELLOW);
        g.drawOval(0,0, solidArea.width-1, solidArea.height-1);
        g.dispose();
        return placeholder;
    }
    @Override
    public void set(int startWorldX, int startWorldY, String direction, Character caster, int damage) {
        this.worldX = startWorldX - solidArea.width / 2;   // Căn giữa fireball tại điểm xuất phát
        this.worldY = startWorldY - solidArea.height / 2;
        this.direction = direction;
        this.caster = caster;
        this.damage = damage; // Sát thương được truyền vào

        // Các thuộc tính riêng của Fireball
        this.speed = 5; // Tốc độ của Fireball
        this.maxRange = 10 * gp.getTileSize(); // Tầm bay tối đa (ví dụ: 10 ô)
        this.alive = true;
        this.distanceTraveled = 0;
        this.spriteNum = 0; // Bắt đầu từ frame đầu tiên của hoạt ảnh
        this.spriteCounter = 0; // Reset bộ đếm cho hoạt ảnh
    }
    private BufferedImage getCurrentImageFrame() {
        ArrayList<BufferedImage> currentImageList;
        switch (direction) {
            case "up":    currentImageList = imagesUp;    break;
            case "down":  currentImageList = imagesDown;  break;
            case "left":  currentImageList = imagesLeft;  break;
            case "right": currentImageList = imagesRight; break;
            default:      currentImageList = imagesDown;  break; // Mặc định
        }

        if (currentImageList != null && !currentImageList.isEmpty()) {
            int actualFramesInList = currentImageList.size();
            if (actualFramesInList == 0) return createPlaceholderImage(); // Không có ảnh nào cho hướng này
            return currentImageList.get(spriteNum % actualFramesInList); // Dùng modulo để lặp qua các frame
        }
        return createPlaceholderImage(); // Fallback
    }


    @Override
    public void update() {
        if (!alive) {
            return;
        }

        // Di chuyển projectile
        int prevWorldX = worldX;
        int prevWorldY = worldY;
        double moveAmountX = 0;
        double moveAmountY = 0;

        switch (direction) {
            case "up": moveAmountY = -speed; break;
            case "down": moveAmountY = speed; break;
            case "left": moveAmountX = -speed; break;
            case "right": moveAmountX = speed; break;
        }
        worldX += (int)moveAmountX;
        worldY += (int)moveAmountY;

        distanceTraveled += Math.sqrt(Math.pow(worldX - prevWorldX, 2) + Math.pow(worldY - prevWorldY, 2));
        // Cập nhật hoạt ảnh
        spriteCounter++;
        if (spriteCounter > animationSpeed) {
            spriteNum++;
            // Không cần lấy `actualNumFramesForCurrentDirection` ở đây vì `getCurrentImageFrame` đã xử lý modulo
            // Nếu bạn muốn spriteNum reset dựa trên numFramesPerDirection cố định:
            if (spriteNum >= numFramesPerDirection) {
                spriteNum = 0;
            }
            spriteCounter = 0;
        }
        // Kiểm tra va chạm với tile (tại tâm của projectile)
        if (checkTileCollision(worldX + solidArea.width / 2, worldY + solidArea.height / 2)) {
            // alive đã được đặt là false trong checkTileCollision nếu va chạm
            // System.out.println("Fireball hit wall"); // Debug
            return; // Không xử lý thêm nếu đã va chạm tường
        }

        // Kiểm tra va chạm với quái vật (nếu caster là Player)
//        if (caster instanceof character.Player) { // Đảm bảo import character.Player
//            for (Monster monster : gp.getMON_GreenSlime()) { // Lấy danh sách quái vật từ GamePanel
//                if (monster != null && monster.getCurrentHealth() > 0) {
//                    // Cập nhật vị trí solidArea của fireball và monster cho kiểm tra chính xác
//                    this.solidArea.x = worldX;
//                    this.solidArea.y = worldY;
//
//                    monster.solidArea.x = monster.worldX + monster.solidAreaDefaultX;
//                    monster.solidArea.y = monster.worldY + monster.solidAreaDefaultY;
//
//                    if (this.solidArea.intersects(monster.solidArea)) {
//                        // gp.getUi().showMessage("Fireball hit " + monster.getName()); // Debug
//                        gp.getCombatSystem().handleProjectileHit(this, monster);
//                        //monster.receiveDamage(this.damage, this.caster);
//                        // Phát âm thanh va chạm (nếu có)
//                        gp.playSoundEffect(Sound.SFX_FIREBALL_HIT);
//                        this.alive = false; // Fireball biến mất sau khi trúng mục tiêu
//                        return;
//                    }
//                }
//            }
//            for (Monster batMonster : gp.getMON_Bat()) {
//                if (batMonster != null && batMonster.getCurrentHealth() > 0) {
//                    // Cập nhật vị trí solidArea của fireball và batMonster cho kiểm tra chính xác
//                    this.solidArea.x = worldX;
//                    this.solidArea.y = worldY;
//
//                    batMonster.solidArea.x = batMonster.worldX + batMonster.solidAreaDefaultX;
//                    batMonster.solidArea.y = batMonster.worldY + batMonster.solidAreaDefaultY;
//
//                    if (this.solidArea.intersects(batMonster.solidArea)) {
//                        // gp.getUi().showMessage("Fireball hit " + batMonster.getName()); // Debug
//                        gp.getCombatSystem().handleProjectileHit(this, batMonster);
//                        batMonster.receiveDamage(this.damage, this.caster); // Gây sát thương cho MON_Bat
//                        // Phát âm thanh va chạm (nếu có)
//                        //gp.playSoundEffect(Sound.SFX_FIREBALL_HIT);
//                        this.alive = false; // Fireball biến mất sau khi trúng mục tiêu
//                        return; // Thoát khỏi phương thức update sau khi xử lý va chạm
//                    }
//                }
//            }
//        }
        // (Tùy chọn) Kiểm tra va chạm với Player nếu caster là Monster (chưa làm ở đây)

        // Kiểm tra tầm bay tối đa
        if (distanceTraveled > maxRange) {
            this.alive = false;
            // System.out.println("Fireball reached max range"); // Debug
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage currentFrame = getCurrentImageFrame();
        if (!alive || currentFrame == null) {
            return;
        }

        int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
        int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;

        // Chỉ vẽ nếu projectile nằm trong màn hình
        if (worldX + solidArea.width > gp.getPlayer().worldX - gp.getPlayer().screenX &&
                worldX - solidArea.width < gp.getPlayer().worldX + gp.getPlayer().screenX && // Sửa: trừ solidArea.width
                worldY + solidArea.height > gp.getPlayer().worldY - gp.getPlayer().screenY &&
                worldY - solidArea.height < gp.getPlayer().worldY + gp.getPlayer().screenY) { // Sửa: trừ solidArea.height

            // Vẽ fireball với kích thước của solidArea (đã được đặt dựa trên ảnh)
            g2.drawImage(currentFrame, screenX, screenY, solidArea.width, solidArea.height, null);
            // Nếu muốn vẽ kích thước gốc của ảnh:
            //g2.drawImage(image, screenX, screenY, image.getWidth(), image.getHeight(), null);
            //g2.drawImage(image, screenX, screenY, image.getWidth() * gp.getScale(), image.getHeight() * gp.getScale(), null);
            // Optional: Vẽ vùng solidArea để debug
             g2.setColor(Color.RED);
             g2.drawRect(screenX, screenY, solidArea.width , solidArea.height);
        }
    }
}