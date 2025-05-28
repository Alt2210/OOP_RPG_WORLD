package imageProcessor;

import character.Character;
import character.Player;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class CharacterImageProcessor extends ImageProcessor {
    private ArrayList<BufferedImage> up;
    private ArrayList<BufferedImage> down;
    private ArrayList<BufferedImage> left;
    private ArrayList<BufferedImage> right;
    private ArrayList<BufferedImage> attack;
    private Character character;

    public CharacterImageProcessor(GamePanel gp, Character character) {
        super(gp);
        this.character = character;
        up = new ArrayList<>();
        down = new ArrayList<>();
        left = new ArrayList<>();
        right = new ArrayList<>();
        attack = new ArrayList<>();
    }

    @Override
    public BufferedImage getCurFrame() {
        // THAY ĐỔI: Thêm log để debug
        if (character instanceof Player && ((Player) character).keyH.attackPressed && attack.size() > 0) {
            System.out.println("Displaying attack sprite, frame: " + (spriteNum % attack.size()));
            return attack.get(spriteNum % attack.size());
        } else if (character instanceof Player && ((Player) character).keyH.attackPressed) {
            System.out.println("Attack sprites not loaded, falling back to default");
        }

        switch (character.direction) {
            case "up":
                for (int i = 0; i < numSprite; i++) {
                    if (spriteNum == i) {
                        return up.get(i);
                    }
                }
            case "down":
                for (int i = 0; i < numSprite; i++) {
                    if (spriteNum == i) {
                        return down.get(i);
                    }
                }
            case "left":
                for (int i = 0; i < numSprite; i++) {
                    if (spriteNum == i) {
                        return left.get(i);
                    }
                }
            case "right":
                for (int i = 0; i < numSprite; i++) {
                    if (spriteNum == i) {
                        return right.get(i);
                    }
                }
            default:
                return down.get(0);
        }
    }

    public void getImage(String folder, String name) {
        int walkSpriteCount = 2;
        if (name.equals("sodier")) walkSpriteCount = 5;

        for (int i = 0; i < walkSpriteCount; i++) {
            BufferedImage image;
            String UP_IMAGE_PATH = folder + "/" + name + "right" + (i + 1) + ".png";
            String RIGHT_IMAGE_PATH = folder + "/" + name + "right" + (i + 1) + ".png";
            String LEFT_IMAGE_PATH = folder + "/" + name + "left" + (i + 1) + ".png";
            String DOWN_IMAGE_PATH = folder + "/" + name + "left" + (i + 1) + ".png";

            System.out.println("Loading: " + UP_IMAGE_PATH);
            // XỬ LÍ NGOẠI LỆ (thêm vào cho có yêu cầu dùng exception thôi)
            try {
                image = setup(UP_IMAGE_PATH);
                if (image == null) throw new IOException("Null image: " + UP_IMAGE_PATH);
                up.add(image);

                image = setup(RIGHT_IMAGE_PATH);
                if (image == null) throw new IOException("Null image: " + RIGHT_IMAGE_PATH);
                right.add(image);

                image = setup(LEFT_IMAGE_PATH);
                if (image == null) throw new IOException("Null image: " + LEFT_IMAGE_PATH);
                left.add(image);

                image = setup(DOWN_IMAGE_PATH);
                if (image == null) throw new IOException("Null image: " + DOWN_IMAGE_PATH);
                down.add(image);
            } catch (IOException e) {
                System.err.println("Error loading walk sprite: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (name.equals("sodier")) {
            int attackSpriteCount = 6;
            for (int i = 0; i < attackSpriteCount; i++) {
                String ATTACK_IMAGE_PATH = folder + "/" + name + "attack" + (i + 1) + ".png";
                System.out.println("Loading: " + ATTACK_IMAGE_PATH);
                try {
                    BufferedImage image = setup(ATTACK_IMAGE_PATH);
                    if (image == null) {
                        System.err.println("Failed to load attack sprite: " + ATTACK_IMAGE_PATH);
                        continue; // Bỏ qua nếu không tải được
                    }
                    attack.add(image);
                } catch (Exception e) {
                    System.err.println("Error loading attack sprite: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            // THÊM MỚI: Kiểm tra danh sách attack
            System.out.println("Loaded attack sprites: " + attack.size());
            this.numSprite = attack.size() > 0 ? attackSpriteCount : walkSpriteCount;
        } else {
            this.numSprite = walkSpriteCount;
        }
    }
}