package projectile;

import character.Character;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LaserBeam extends Projectile {
    private ArrayList<BufferedImage> imagesLeft;
    private ArrayList<BufferedImage> imagesRight;

    private boolean isCharging; // Trạng thái tụ lực
    private int chargeCounter; // Đếm thời gian tụ lực
    private final int CHARGE_DURATION = 60; // 1 giây (60 FPS)
    private int spriteCounter = 0;
    private int spriteNum = 0;
    private final int NUM_FRAMES_PER_DIRECTION = 14; // 1-8: tụ lực, 9-14: bắn
    private final int ANIMATION_SPEED = 5; // Nhanh hơn Fireball một chút (10 -> 5)

    public LaserBeam(GamePanel gp) {
        super(gp);
        isCharging = true;
        chargeCounter = 0;
        imagesLeft = new ArrayList<>();
        imagesRight = new ArrayList<>();
        loadDirectionalImages();
        solidArea.width = gp.getTileSize() * 8; // Chiều dài của laser
        solidArea.height = gp.getTileSize(); // Chiều cao
    }

    private void loadDirectionalImages() {
        String[] directionNames = {"left", "right"};
        @SuppressWarnings("unchecked")
        ArrayList<BufferedImage>[] imageLists = new ArrayList[]{imagesLeft, imagesRight};
        for (int i = 0; i < directionNames.length; i++) {
            for (int frame = 1; frame <= NUM_FRAMES_PER_DIRECTION; frame++) {
                String path = "/projectile/laserbeam_" + directionNames[i] + frame + ".png";
                try {
                    InputStream is = getClass().getResourceAsStream(path);
                    if (is == null) {
                        System.err.println("Cảnh báo: Không tìm thấy ảnh LaserBeam tại: " + path + ". Bỏ qua frame này.");
                        if (frame == 1) {
                            imageLists[i].add(createPlaceholderImage());
                            for (int f = 2; f <= NUM_FRAMES_PER_DIRECTION; f++) {
                                imageLists[i].add(createPlaceholderImage());
                            }
                        }
                        continue;
                    }
                    imageLists[i].add(ImageIO.read(is));
                } catch (IOException e) {
                    System.err.println("Lỗi khi tải ảnh LaserBeam: " + path + " - " + e.getMessage());
                    imageLists[i].add(createPlaceholderImage());
                    if (imageLists[i].size() < NUM_FRAMES_PER_DIRECTION) {
                        for (int f = imageLists[i].size() + 1; f <= NUM_FRAMES_PER_DIRECTION; f++) {
                            imageLists[i].add(createPlaceholderImage());
                        }
                    }
                }
            }
            if (imageLists[i].isEmpty()) {
                for (int f = 0; f < NUM_FRAMES_PER_DIRECTION; f++) {
                    imageLists[i].add(createPlaceholderImage());
                }
            }
        }
    }

    private BufferedImage createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(solidArea.width, solidArea.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();
        g.setColor(new Color(255, 0, 255, 100)); // Màu tím mờ để dễ nhận biết
        g.fillRect(0, 0, solidArea.width, solidArea.height);
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, solidArea.width - 1, solidArea.height - 1);
        g.dispose();
        return placeholder;
    }

    private BufferedImage getCurrentImageFrame() {
        ArrayList<BufferedImage> currentImageList;
        switch (direction) {
            case "left": currentImageList = imagesLeft; break;
            case "right": currentImageList = imagesRight; break;
            default: currentImageList = imagesRight; break;
        }

        if (currentImageList != null && !currentImageList.isEmpty()) {
            int actualFramesInList = currentImageList.size();
            if (actualFramesInList == 0) return createPlaceholderImage();
            return currentImageList.get(spriteNum % actualFramesInList);
        }
        return createPlaceholderImage();
    }

    @Override
    public void set(int startWorldX, int startWorldY, String direction, Character caster, int damage) {
        this.worldX = startWorldX + (direction.equals("right") ? caster.solidArea.width : -solidArea.width);
        this.worldY = startWorldY - solidArea.height / 2;
        this.direction = direction;
        this.caster = caster;
        this.damage = damage;
        this.speed = 10;
        this.maxRange = gp.getScreenWidth();
        this.alive = true;
        this.distanceTraveled = 0;
        this.spriteNum = 0;
        this.spriteCounter = 0;
        this.isCharging = true;
        this.chargeCounter = 0;
    }

    @Override
    public void update() {
        if (!alive) return;

        if (isCharging) {
            chargeCounter++;
            if (chargeCounter >= CHARGE_DURATION) {
                isCharging = false;
                System.out.println("LaserBeam finished charging, now firing");
            }
        } else {
            switch (direction) {
                case "right": worldX += speed; break;
                case "left": worldX -= speed; break;
            }
            distanceTraveled += speed;
            if (distanceTraveled > maxRange) {
                alive = false;
                System.out.println("LaserBeam reached max range, despawned");
            }

            if (checkTileCollision(worldX + solidArea.width / 2, worldY + solidArea.height / 2)) {
                return;
            }
        }

        spriteCounter++;
        if (spriteCounter > ANIMATION_SPEED) {
            spriteNum++;
            if (spriteNum >= NUM_FRAMES_PER_DIRECTION) {
                spriteNum = isCharging ? 0 : 8; // Lặp lại frame 1-8 khi tụ lực, 9-14 khi bắn
            }
            spriteCounter = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!alive) return;

        BufferedImage currentFrame = getCurrentImageFrame();
        if (currentFrame == null) return;

        int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
        int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;

        if (worldX + solidArea.width > gp.getPlayer().worldX - gp.getPlayer().screenX &&
                worldX - solidArea.width < gp.getPlayer().worldX + gp.getPlayer().screenX &&
                worldY + solidArea.height > gp.getPlayer().worldY - gp.getPlayer().screenY &&
                worldY - solidArea.height < gp.getPlayer().worldY + gp.getPlayer().screenY) {
            g2.drawImage(currentFrame, screenX, screenY, solidArea.width, solidArea.height, null);
        }
    }
}