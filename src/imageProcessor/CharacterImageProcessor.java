package imageProcessor;

import character.Character;
import character.Player;
import character.monster.MON_Bat;
import character.monster.MON_GolemBoss;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CharacterImageProcessor extends ImageProcessor {
    private ArrayList<BufferedImage> up;
    private ArrayList<BufferedImage> down;
    private ArrayList<BufferedImage> left;
    private ArrayList<BufferedImage> right;
    private ArrayList<BufferedImage> attackRight;
    private ArrayList<BufferedImage> attackLeft;
    private ArrayList<BufferedImage> chargeRight;
    private ArrayList<BufferedImage> chargeLeft;
    private ArrayList<BufferedImage> laserRight;
    private ArrayList<BufferedImage> laserLeft;
    private ArrayList<BufferedImage> laserUp;
    private ArrayList<BufferedImage> laserDown;
    private ArrayList<BufferedImage> armShotRight;
    private ArrayList<BufferedImage> armShotLeft; // Thêm danh sách cho hướng trái
    private int laserSpriteCounter = 0;
    private int laserSpriteNum = 0;
    private int armShotSpriteCounter = 0;
    private int armShotSpriteNum = 0;
    private final int LASER_FRAMES_PER_ANIMATION = 4;
    private final int ARM_SHOT_FRAMES_PER_ANIMATION = 6; // 60 frame cho 9 khung hình: 60/9 ≈ 6.67
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
        chargeRight = new ArrayList<>();
        chargeLeft = new ArrayList<>();
        laserRight = new ArrayList<>();
        laserLeft = new ArrayList<>();
        laserUp = new ArrayList<>();
        laserDown = new ArrayList<>();
        armShotRight = new ArrayList<>();
        armShotLeft = new ArrayList<>(); // Khởi tạo danh sách
    }

    @Override
    public BufferedImage getCurFrame() {
        if (character instanceof Player && ((Player) character).isAttacking()) {
            if (character.direction.equals("right") && attackRight.size() > 0) {
                return attackRight.get(spriteNum % attackRight.size());
            } else if (character.direction.equals("left") && attackLeft.size() > 0) {
                return attackLeft.get(spriteNum % attackLeft.size());
            } else if (attackRight.size() > 0) {
                return attackRight.get(spriteNum % attackRight.size());
            } else if (attackLeft.size() > 0) {
                return attackLeft.get(spriteNum % attackLeft.size());
            }
        } else if (character instanceof MON_GolemBoss) {
            MON_GolemBoss golemBoss = (MON_GolemBoss) character;
            if (golemBoss.isChargingLaser()) {
                if (character.direction.equals("right") && chargeRight.size() > 0) {
                    return chargeRight.get(spriteNum % chargeRight.size());
                } else if (character.direction.equals("left") && chargeLeft.size() > 0) {
                    return chargeLeft.get(spriteNum % chargeLeft.size());
                } else if (chargeRight.size() > 0) {
                    return chargeRight.get(spriteNum % chargeRight.size());
                } else if (chargeLeft.size() > 0) {
                    return chargeLeft.get(spriteNum % chargeLeft.size());
                }
            } else if (golemBoss.isChargingArmShot()) {
                return getArmShotFrame(); // Trả về khung hình hoạt ảnh "tháo cánh tay"
            }
        } else if (character instanceof MON_Bat && ((MON_Bat) character).isDashing()) {
            if (character.direction.equals("right") && right.size() > 0) {
                return right.get(spriteNum % right.size());
            } else if (character.direction.equals("left") && left.size() > 0) {
                return left.get(spriteNum % left.size());
            } else if (right.size() > 0) {
                return right.get(spriteNum % right.size());
            } else if (left.size() > 0) {
                return left.get(spriteNum % left.size());
            }
        }

        switch (character.direction) {
            case "up":
                if (up.size() > 0) return up.get(spriteNum % up.size());
                break;
            case "down":
                if (down.size() > 0) return down.get(spriteNum % down.size());
                break;
            case "left":
                if (left.size() > 0) return left.get(spriteNum % left.size());
                break;
            case "right":
                if (right.size() > 0) return right.get(spriteNum % right.size());
                break;
        }

        if (down.size() > 0) return down.get(0);
        BufferedImage defaultImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = defaultImage.createGraphics();
        g2.setColor(Color.RED);
        g2.fillRect(0, 0, 48, 48);
        g2.dispose();
        return defaultImage;
    }

    public BufferedImage getLaserFrame(String direction) {
        laserSpriteCounter++;
        if (laserSpriteCounter >= LASER_FRAMES_PER_ANIMATION) {
            laserSpriteNum++;
            if (laserSpriteNum >= 7) {
                laserSpriteNum = 0;
            }
            laserSpriteCounter = 0;
        }

        int adjustedSpriteNum = laserSpriteNum + 7;
        if (direction.equals("right") && laserRight.size() > adjustedSpriteNum) {
            return laserRight.get(adjustedSpriteNum);
        } else if (direction.equals("left") && laserLeft.size() > adjustedSpriteNum) {
            return laserLeft.get(adjustedSpriteNum);
        } else if (direction.equals("up") && laserUp.size() > adjustedSpriteNum) {
            return laserUp.get(adjustedSpriteNum);
        } else if (direction.equals("down") && laserDown.size() > adjustedSpriteNum) {
            return laserDown.get(adjustedSpriteNum);
        }
        return null;
    }

    public BufferedImage getArmShotFrame() {
        armShotSpriteCounter++;
        if (armShotSpriteCounter >= ARM_SHOT_FRAMES_PER_ANIMATION) {
            armShotSpriteNum++;
            if (character.direction.equals("right") && armShotSpriteNum >= armShotRight.size()) {
                armShotSpriteNum = 0;
            } else if (character.direction.equals("left") && armShotSpriteNum >= armShotLeft.size()) {
                armShotSpriteNum = 0;
            }
            armShotSpriteCounter = 0;
        }

        if (character.direction.equals("right") && armShotRight.size() > armShotSpriteNum) {
            return armShotRight.get(armShotSpriteNum);
        } else if (character.direction.equals("left") && armShotLeft.size() > armShotSpriteNum) {
            return armShotLeft.get(armShotSpriteNum);
        } else if (armShotRight.size() > armShotSpriteNum) { // Mặc định trả về right nếu hướng không hợp lệ
            return armShotRight.get(armShotSpriteNum);
        }
        return null; // Tránh lỗi nếu danh sách rỗng
    }

    public void getImage(String folder, String name) {
        int walkSpriteCount = 4;
        int chargeSpriteCount = 7;
        int attackSpriteCount = 0;
        int laserSpriteCount = 14;

        if (name.equals("sodier")) {
            walkSpriteCount = 5;
            attackSpriteCount = 6;
        } else if (name.equals("Princess")) {
            walkSpriteCount = 7;
        } else if (name.equals("golemboss")) {
            walkSpriteCount = 4;
            chargeSpriteCount = 7;
        } else if (name.equals("oldman")) {
            walkSpriteCount = 2;
        } else if (name.equals("greenslime")) {
            walkSpriteCount = 2;
        } else if (name.equals("bat")) {
            walkSpriteCount = 2;
        }

        // Tải sprite đi bộ
        for (int i = 0; i < walkSpriteCount; i++) {
            String baseName = name + "_walkright";
            String UP_IMAGE_PATH = folder + "/" + baseName + (i + 1) + ".png";
            String RIGHT_IMAGE_PATH = folder + "/" + baseName + (i + 1) + ".png";
            String LEFT_IMAGE_PATH = folder + "/" + name + "_walkleft" + (i + 1) + ".png";
            String DOWN_IMAGE_PATH = folder + "/" + name + "_walkleft" + (i + 1) + ".png";

            BufferedImage image = setup(UP_IMAGE_PATH);
            if (image != null) up.add(image);
            else System.err.println("Không tải được: " + UP_IMAGE_PATH);

            image = setup(RIGHT_IMAGE_PATH);
            if (image != null) right.add(image);
            else System.err.println("Không tải được: " + RIGHT_IMAGE_PATH);

            image = setup(LEFT_IMAGE_PATH);
            if (image != null) left.add(image);
            else System.err.println("Không tải được: " + LEFT_IMAGE_PATH);

            image = setup(DOWN_IMAGE_PATH);
            if (image != null) down.add(image);
            else System.err.println("Không tải được: " + DOWN_IMAGE_PATH);
        }

        // Tải sprite tấn công (nếu có)
        if (attackSpriteCount > 0) {
            for (int i = 0; i < attackSpriteCount; i++) {
                String ATTACK_RIGHT_PATH = folder + "/" + name + "_attackright" + (i + 1) + ".png";
                String ATTACK_LEFT_PATH = folder + "/" + name + "_attackleft" + (i + 1) + ".png";
                BufferedImage image = setup(ATTACK_RIGHT_PATH);
                if (image != null) attackRight.add(image);
                else System.err.println("Không tải được: " + ATTACK_RIGHT_PATH);

                image = setup(ATTACK_LEFT_PATH);
                if (image != null) attackLeft.add(image);
                else System.err.println("Không tải được: " + ATTACK_LEFT_PATH);
            }
        }

        // Tải sprite tụ lực, tia laser và "tháo cánh tay" (nếu là golemboss)
        if (name.equals("golemboss")) {
            for (int i = 0; i < chargeSpriteCount; i++) {
                String CHARGE_RIGHT_PATH = folder + "/" + name + "_laserright" + (i + 1) + ".png";
                String CHARGE_LEFT_PATH = folder + "/" + name + "_laserleft" + (i + 1) + ".png";
                BufferedImage image = setup(CHARGE_RIGHT_PATH);
                if (image != null) chargeRight.add(image);
                else System.err.println("Không tải được: " + CHARGE_RIGHT_PATH);

                image = setup(CHARGE_LEFT_PATH);
                if (image != null) chargeLeft.add(image);
                else System.err.println("Không tải được: " + CHARGE_LEFT_PATH);
            }

            // Tải sprite cho tia laser
            for (int i = 0; i < laserSpriteCount; i++) {
                String LASER_RIGHT_PATH = folder + "/laserbeam_right" + (i + 1) + ".png";
                String LASER_LEFT_PATH = folder + "/laserbeam_left" + (i + 1) + ".png";
                String LASER_UP_PATH = folder + "/laserbeam_up" + (i + 1) + ".png";
                String LASER_DOWN_PATH = folder + "/laserbeam_down" + (i + 1) + ".png";
                BufferedImage image = setup(LASER_RIGHT_PATH);
                if (image != null) laserRight.add(image);
                else System.err.println("Không tải được: " + LASER_RIGHT_PATH);

                image = setup(LASER_LEFT_PATH);
                if (image != null) laserLeft.add(image);
                else System.err.println("Không tải được: " + LASER_LEFT_PATH);

                image = setup(LASER_UP_PATH);
                if (image != null) laserUp.add(image);
                else System.err.println("Không tải được: " + LASER_UP_PATH);

                image = setup(LASER_DOWN_PATH);
                if (image != null) laserDown.add(image);
                else System.err.println("Không tải được: " + LASER_DOWN_PATH);
            }

            // Tải sprite cho hoạt ảnh "tháo cánh tay" (hướng phải)
            for (int i = 0; i < 9; i++) {
                String ARM_SHOT_RIGHT_PATH = folder + "/golemboss_shotright" + (i + 1) + ".png";
                BufferedImage image = setup(ARM_SHOT_RIGHT_PATH);
                if (image != null) {
                    armShotRight.add(image);
                    System.out.println("Đã tải: " + ARM_SHOT_RIGHT_PATH);
                } else {
                    System.err.println("Không tải được: " + ARM_SHOT_RIGHT_PATH);
                }
            }
            if (armShotRight.isEmpty()) {
                System.err.println("Danh sách armShotRight rỗng! Hoạt ảnh tháo cánh tay (right) sẽ không hiển thị.");
            }

            // Tải sprite cho hoạt ảnh "tháo cánh tay" (hướng trái)
            for (int i = 0; i < 9; i++) {
                String ARM_SHOT_LEFT_PATH = folder + "/golemboss_shotleft" + (i + 1) + ".png";
                BufferedImage image = setup(ARM_SHOT_LEFT_PATH);
                if (image != null) {
                    armShotLeft.add(image);
                    System.out.println("Đã tải: " + ARM_SHOT_LEFT_PATH);
                } else {
                    System.err.println("Không tải được: " + ARM_SHOT_LEFT_PATH);
                }
            }
            if (armShotLeft.isEmpty()) {
                System.err.println("Danh sách armShotLeft rỗng! Hoạt ảnh tháo cánh tay (left) sẽ không hiển thị.");
            }
        }
    }
}