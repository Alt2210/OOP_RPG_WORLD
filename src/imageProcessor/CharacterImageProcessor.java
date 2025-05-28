package imageProcessor;

import character.Character;
import character.Player;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class CharacterImageProcessor extends ImageProcessor {
    private ArrayList<BufferedImage> up;
    private ArrayList<BufferedImage> down;
    private ArrayList<BufferedImage> left;
    private ArrayList<BufferedImage> right;
    private ArrayList<BufferedImage> attackRight; // THÊM MỚI: Sprite tấn công bên phải
    private ArrayList<BufferedImage> attackLeft;  // THÊM MỚI: Sprite tấn công bên trái
    private Character character;

    public CharacterImageProcessor(GamePanel gp, Character character) {
        super(gp);
        this.character = character;
        up = new ArrayList<>();
        down = new ArrayList<>();
        left = new ArrayList<>();
        right = new ArrayList<>();
        attackRight = new ArrayList<>();
        attackLeft = new ArrayList<>();
    }

    @Override
    public BufferedImage getCurFrame() {
        if (character instanceof Player && ((Player) character).isAttacking()) {
            if (character.direction.equals("right") && attackRight.size() > 0) {
                System.out.println("Displaying attack right sprite, frame: " + (spriteNum % attackRight.size()) + ", attackRight size: " + attackRight.size());
                return attackRight.get(spriteNum % attackRight.size());
            } else if (character.direction.equals("left") && attackLeft.size() > 0) {
                System.out.println("Displaying attack left sprite, frame: " + (spriteNum % attackLeft.size()) + ", attackLeft size: " + attackLeft.size());
                return attackLeft.get(spriteNum % attackLeft.size());
            } else if (attackRight.size() > 0) { // Mặc định dùng attackRight nếu hướng là "up" hoặc "down"
                System.out.println("Displaying attack right sprite (default), frame: " + (spriteNum % attackRight.size()) + ", attackRight size: " + attackRight.size());
                return attackRight.get(spriteNum % attackRight.size());
            } else if (attackLeft.size() > 0) {
                System.out.println("Displaying attack left sprite (fallback), frame: " + (spriteNum % attackLeft.size()) + ", attackLeft size: " + attackLeft.size());
                return attackLeft.get(spriteNum % attackLeft.size());
            } else {
                System.out.println("Attack sprites not loaded, falling back to default");
                if (right.size() > 0) {
                    return right.get(spriteNum % right.size());
                }
            }
        }

        switch (character.direction) {
            case "up":
                if (up.size() > 0) {
                    return up.get(spriteNum % up.size());
                }
            case "down":
                if (down.size() > 0) {
                    return down.get(spriteNum % down.size());
                }
            case "left":
                if (left.size() > 0) {
                    return left.get(spriteNum % left.size());
                }
            case "right":
                if (right.size() > 0) {
                    return right.get(spriteNum % right.size());
                }
            default:
                if (down.size() > 0) {
                    return down.get(0);
                }
                BufferedImage defaultImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = defaultImage.createGraphics();
                g2.setColor(Color.RED);
                g2.fillRect(0, 0, 48, 48);
                g2.dispose();
                return defaultImage;
        }
    }

    public void getImage(String folder, String name) {
        int walkSpriteCount = 2;
        if (name.equals("sodier")) walkSpriteCount = 5;
        else if (name.equals("Princess")) walkSpriteCount = 7;

        for (int i = 0; i < walkSpriteCount; i++) {
            BufferedImage image;
            String baseName = name + "_walkright";
            String UP_IMAGE_PATH = folder + "/" + baseName + (i + 1) + ".png";
            String RIGHT_IMAGE_PATH = folder + "/" + baseName + (i + 1) + ".png";
            String LEFT_IMAGE_PATH = folder + "/" + name + "_walkleft" + (i + 1) + ".png";
            String DOWN_IMAGE_PATH = folder + "/" + name + "_walkleft" + (i + 1) + ".png";

            System.out.println("Loading: " + UP_IMAGE_PATH);
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
            // Tải sprite tấn công bên phải
            for (int i = 0; i < attackSpriteCount; i++) {
                String ATTACK_RIGHT_PATH = folder + "/" + name + "_attackright" + (i + 1) + ".png";
                System.out.println("Attempting to load: " + ATTACK_RIGHT_PATH);
                BufferedImage image = setup(ATTACK_RIGHT_PATH);
                if (image != null) {
                    attackRight.add(image);
                    System.out.println("Successfully loaded: " + ATTACK_RIGHT_PATH + ", size: " + image.getWidth() + "x" + image.getHeight());
                } else {
                    System.err.println("Failed to load attack right sprite: " + ATTACK_RIGHT_PATH);
                }
            }
            System.out.println("Total loaded attack right sprites: " + attackRight.size());

            // Tải sprite tấn công bên trái
            for (int i = 0; i < attackSpriteCount; i++) {
                String ATTACK_LEFT_PATH = folder + "/" + name + "_attackleft" + (i + 1) + ".png";
                System.out.println("Attempting to load: " + ATTACK_LEFT_PATH);
                BufferedImage image = setup(ATTACK_LEFT_PATH);
                if (image != null) {
                    attackLeft.add(image);
                    System.out.println("Successfully loaded: " + ATTACK_LEFT_PATH + ", size: " + image.getWidth() + "x" + image.getHeight());
                } else {
                    System.err.println("Failed to load attack left sprite: " + ATTACK_LEFT_PATH);
                }
            }
            System.out.println("Total loaded attack left sprites: " + attackLeft.size());

            this.numSprite = (attackRight.size() > 0 || attackLeft.size() > 0) ? attackSpriteCount : walkSpriteCount;
        } else {
            this.numSprite = walkSpriteCount;
        }

        this.numSprite = Math.min(this.numSprite, Math.min(up.size(), Math.min(down.size(), Math.min(left.size(), right.size()))));
        System.out.println("Adjusted numSprite: " + this.numSprite);
    }

}