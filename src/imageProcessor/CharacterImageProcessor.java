package imageProcessor;

import character.Character;
import character.CombatableCharacter;
import character.role.Player;
import character.monster.MON_GolemBoss;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CharacterImageProcessor extends ImageProcessor {
    // Danh sách cho đi bộ 4 hướng
    private ArrayList<BufferedImage> up;
    private ArrayList<BufferedImage> down;
    private ArrayList<BufferedImage> left;
    private ArrayList<BufferedImage> right;

    // Danh sách cho tấn công trên cơ thể (sodier, Orc)
    private ArrayList<BufferedImage> attackUp;
    private ArrayList<BufferedImage> attackDown;
    private ArrayList<BufferedImage> attackRight;
    private ArrayList<BufferedImage> attackLeft;

    // Danh sách cho hiệu ứng tấn công tách rời (Astrologer)
    private ArrayList<BufferedImage> loadedDetachedAttackEffect;

    // Các danh sách riêng cho GolemBoss
    private ArrayList<BufferedImage> chargeRight;
    private ArrayList<BufferedImage> chargeLeft;
    private ArrayList<BufferedImage> laserRight;
    private ArrayList<BufferedImage> laserLeft;
    private ArrayList<BufferedImage> laserUp;
    private ArrayList<BufferedImage> laserDown;
    private ArrayList<BufferedImage> armShotRight;
    private ArrayList<BufferedImage> armShotLeft;


    // Các biến quản lý animation cho GolemBoss
    private int laserSpriteCounter = 0;
    private int laserSpriteNum = 0;
    private int armShotSpriteCounter = 0;
    private int armShotSpriteNum = 0;
    private final int LASER_FRAMES_PER_ANIMATION = 4;
    private final int ARM_SHOT_FRAMES_PER_ANIMATION = 6;

    private Character character;

    public CharacterImageProcessor(GamePanel gp, Character character) {
        super(gp);
        this.character = character;
        // Khởi tạo tất cả các ArrayList
        up = new ArrayList<>();
        down = new ArrayList<>();
        left = new ArrayList<>();
        right = new ArrayList<>();
        attackUp = new ArrayList<>();
        attackDown = new ArrayList<>();
        attackRight = new ArrayList<>();
        attackLeft = new ArrayList<>();
        loadedDetachedAttackEffect = new ArrayList<>();
        chargeRight = new ArrayList<>();
        chargeLeft = new ArrayList<>();
        laserRight = new ArrayList<>();
        laserLeft = new ArrayList<>();
        laserUp = new ArrayList<>();
        laserDown = new ArrayList<>();
        armShotRight = new ArrayList<>();
        armShotLeft = new ArrayList<>();
    }

    public ArrayList<BufferedImage> getLoadedDetachedAttackFrames() {
        return loadedDetachedAttackEffect;
    }

    @Override
    public BufferedImage getCurFrame() {
        ArrayList<BufferedImage> listToUse = null;
        String characterIdentifier = "unknown";
        if (character instanceof Player) {
            characterIdentifier = ((Player) character).getCharacterClassIdentifier();
        } else if (character != null) {
            characterIdentifier = character.getName().toLowerCase();
        }

        String currentDirection = (character != null) ? character.getDirection() : "down";
        boolean currentIsAttacking = (character != null) && character.isAttacking();

        if (character instanceof MON_GolemBoss) {
            MON_GolemBoss golemBoss = (MON_GolemBoss) character;
            if (golemBoss.isChargingLaser()) {
                if (golemBoss.getDirection().equals("right") && chargeRight.size() > 0) listToUse = chargeRight;
                else if (golemBoss.getDirection().equals("left") && chargeLeft.size() > 0) listToUse = chargeLeft;
                else listToUse = (chargeRight.size() > 0) ? chargeRight : chargeLeft;
            } else if (golemBoss.isChargingArmShot()) {
                return getArmShotFrame();
            }
        }

        if (listToUse == null && currentIsAttacking && !"astrologer".equals(characterIdentifier)) {
            switch (currentDirection) {
                case "up":    listToUse = attackUp;    break;
                case "down":  listToUse = attackDown;  break;
                case "left":  listToUse = attackLeft;  break;
                case "right": listToUse = attackRight; break;
                default:
                    if ("sodier".equals(characterIdentifier)) {
                        listToUse = (attackRight.size() > 0) ? attackRight : attackLeft;
                    } else if (attackDown.size() > 0) {
                        listToUse = attackDown;
                    }
                    break;
            }
        }

        if (listToUse == null || listToUse.isEmpty()) {
            switch (currentDirection) {
                case "up":    listToUse = up;    break;
                case "down":  listToUse = down;  break;
                case "left":  listToUse = left;  break;
                case "right": listToUse = right; break;
                default:      listToUse = down;  break;
            }
        }

        if (listToUse != null && !listToUse.isEmpty()) {
            return listToUse.get(spriteNum % listToUse.size());
        }

        System.err.println("CIP.getCurFrame: FALLBACK - Không tìm thấy danh sách ảnh hợp lệ cho " +
                characterIdentifier + " hướng " + currentDirection +
                ", isAttacking: " + currentIsAttacking);
        if (down != null && !down.isEmpty()) return down.get(0);

        return createPlaceholderImage();
    }

    public void getImage(String folder, String characterIdentifier) {
        // 0. Xóa tất cả các danh sách cũ để đảm bảo không bị lẫn lộn ảnh
        up.clear(); down.clear(); left.clear(); right.clear();
        attackUp.clear(); attackDown.clear(); attackLeft.clear(); attackRight.clear();
        loadedDetachedAttackEffect.clear();
        chargeRight.clear(); chargeLeft.clear(); laserRight.clear(); laserLeft.clear();
        laserUp.clear(); laserDown.clear(); armShotRight.clear(); armShotLeft.clear();

        // 1. Cấu hình số lượng frame cho từng loại nhân vật
        int walkSpriteCount = 0;
        int bodyAttackSpriteCount = 0;
        int detachedAttackFrameCount = 0;
        int golemChargeFrames = 0;
        int golemLaserFrames = 0;
        int golemArmShotFrames = 0;

        String baseIdentifier = characterIdentifier;
        if (characterIdentifier.startsWith("skeletonlord_phase2")) {
            baseIdentifier = "skeletonlord_phase2"; // Sử dụng baseIdentifier để tải ảnh phase 2
        } else if (characterIdentifier.startsWith("skeletonlord")) {
            baseIdentifier = "skeletonlord";
        }

        switch (characterIdentifier) {
            case "sodier": // Giữ nguyên "sodier" theo yêu cầu
                walkSpriteCount = 5;
                bodyAttackSpriteCount = 6;
                break;
            case "astrologist": // Dùng "astrologist" cho khớp với tên file ảnh của bạn
                walkSpriteCount = 16;
                detachedAttackFrameCount = 4;
                break;
            case "orc":
                walkSpriteCount = 2;
                bodyAttackSpriteCount = 2;
                break;
            case "princess":
                walkSpriteCount = 7;
                break;
            case "merchant":
                BufferedImage img;
                for(int i = 1; i <= 8; i++){
                    String path = folder + "/" + characterIdentifier + "_down" + i + ".png";
                    System.out.println(path);
                    img = setup(path);
                    down.add(img);
                }
                up.addAll(down);
                left.addAll(down);
                right.addAll(down);
                break;
            case "oldman":
            case "greenslime":
            case "bat":
                walkSpriteCount = 2;
                break;
            case "golemboss":
                walkSpriteCount = 4;
                golemChargeFrames = 7;
                golemLaserFrames = 14;
                golemArmShotFrames = 9;
                break;
            case "skeletonlord":
                walkSpriteCount = 2; // Số frame cho đi bộ
                bodyAttackSpriteCount = 2; // Số frame cho tấn công cận chiến
                break;
            case "skeletonlord_phase2":
                walkSpriteCount = 2; // Số frame cho đi bộ Phase 2
                bodyAttackSpriteCount = 2; // Số frame cho tấn công cận chiến Phase 2
                break;
            default:
                System.err.println("CIP.getImage: Tên định danh nhân vật không xác định: '" + characterIdentifier + "'.");
                return;
        }

        // 2. Tải sprite đi bộ
        if (walkSpriteCount > 0) {
            // Logic mới: Áp dụng cho TẤT CẢ các lớp con của Player
            if (character instanceof Player) {
                for (int i = 1; i <= walkSpriteCount; i++) {
                    String lPath = folder + "/" + characterIdentifier + "_walkleft" + i + ".png";
                    BufferedImage imgL = setup(lPath);
                    if (imgL != null) left.add(imgL);

                    String rPath = folder + "/" + characterIdentifier + "_walkright" + i + ".png";
                    BufferedImage imgR = setup(rPath);
                    if (imgR != null) right.add(imgR);
                }
                // Áp dụng logic theo yêu cầu của bạn: Lên -> Trái, Xuống -> Phải
                up.addAll(left);
                down.addAll(right);
            }else if ("princess".equals(characterIdentifier)) {
                // Princess chỉ có sprite left/right, tái sử dụng cho up/down
                for (int i = 1; i <= walkSpriteCount; i++) {
                    String lPath = folder + "/" + characterIdentifier + "_walkleft" + i + ".png";
                    BufferedImage imgL = setup(lPath);
                    if (imgL != null) left.add(imgL);

                    String rPath = folder + "/" + characterIdentifier + "_walkright" + i + ".png";
                    BufferedImage imgR = setup(rPath);
                    if (imgR != null) right.add(imgR);
                }
                // Tái sử dụng: up dùng ảnh left, down dùng ảnh right
                up.addAll(left);
                down.addAll(right);
            }
            else if("golemboss".equals(characterIdentifier)){
                for (int i = 1; i <= walkSpriteCount; i++) {
                    String lPath = folder + "/" + characterIdentifier + "_walkleft" + i + ".png";
                    BufferedImage imgL = setup(lPath);
                    if (imgL != null) left.add(imgL);

                    String rPath = folder + "/" + characterIdentifier + "_walkright" + i + ".png";
                    BufferedImage imgR = setup(rPath);
                    if (imgR != null) right.add(imgR);
                }
                // Tái sử dụng: up dùng ảnh left, down dùng ảnh right
                up.addAll(left);
                down.addAll(right);
            }
            else { // Logic cũ cho NPC/Monster (có thể có đủ 4 hướng)
                String[] directions = {"up", "down", "left", "right"};
                @SuppressWarnings("unchecked")
                ArrayList<BufferedImage>[] walkImageLists = new ArrayList[]{up, down, left, right};
                for (int d = 0; d < directions.length; d++) {
                    for (int i = 1; i <= walkSpriteCount; i++) {
                        String imagePath = folder + "/" + characterIdentifier + "_walk" + directions[d] + i + ".png";
                        System.out.println(imagePath + "\n");
                        BufferedImage image = setup(imagePath);
                        if (image != null) ((ArrayList<BufferedImage>)walkImageLists[d]).add(image);
                    }
                }
            }
        }

        // 3. Tải sprite tấn công GẮN LIỀN CƠ THỂ
        if (bodyAttackSpriteCount > 0) {
            // Logic mới: Áp dụng cho TẤT CẢ các lớp con của Player
            if (character instanceof Player) {
                for (int i = 1; i <= bodyAttackSpriteCount; i++) {
                    // Tên file attack của bạn không có left/right, chỉ có số
                    // Dựa trên ảnh, ta thấy có attackleft và attackright
                    String lPath = folder + "/" + characterIdentifier + "_attackleft" + i + ".png";
                    BufferedImage imgL = setup(lPath);
                    if (imgL != null) attackLeft.add(imgL);

                    String rPath = folder + "/" + characterIdentifier + "_attackright" + i + ".png";
                    BufferedImage imgR = setup(rPath);
                    if (imgR != null) attackRight.add(imgR);
                }
                // Áp dụng logic tương tự: Lên -> Trái, Xuống -> Phải
                attackUp.addAll(attackLeft);
                attackDown.addAll(attackRight);
            } else { // Logic cho Monster (Orc)
                String[] directions = {"up", "down", "left", "right"};
                @SuppressWarnings("unchecked")
                ArrayList<BufferedImage>[] attackImageLists = new ArrayList[]{attackUp, attackDown, attackLeft, attackRight};
                for (int d = 0; d < directions.length; d++) {
                    for (int i = 1; i <= bodyAttackSpriteCount; i++) {
                        String imagePath = folder + "/" + characterIdentifier + "_attack" + directions[d] + i + ".png";
                        BufferedImage image = setup(imagePath);
                        if (image != null) ((ArrayList<BufferedImage>)attackImageLists[d]).add(image);
                    }
                }
            }
        }

        // 4. Tải sprite hiệu ứng TÁCH RỜI (cho Astrologist)
        if (detachedAttackFrameCount > 0 && "astrologist".equals(characterIdentifier)) {
            for (int i = 0; i < detachedAttackFrameCount; i++) {
                String imagePath = "/skill/astrologist_1_skill0_effect_" + i + ".png";
                BufferedImage image = setup(imagePath);
                if (image != null) {
                    loadedDetachedAttackEffect.add(image);
                }
            }
        }

        // 5. Tải sprite đặc biệt cho GolemBoss
        if ("golemboss".equals(characterIdentifier)) {
            // Tải ảnh charge
            for (int i = 1; i <= golemChargeFrames; i++) {
                String rPath = folder + "/" + characterIdentifier + "_laserright" + i + ".png";
                String lPath = folder + "/" + characterIdentifier + "_laserleft" + i + ".png";
                BufferedImage imgR = setup(rPath); if (imgR != null) chargeRight.add(imgR);
                BufferedImage imgL = setup(lPath); if (imgL != null) chargeLeft.add(imgL);
            }
            // Tải ảnh laser
            for (int i = 9; i <= golemLaserFrames; i++) {
                String[] laserDirs = {"right", "left","up","down"};
                @SuppressWarnings("unchecked")
                ArrayList<BufferedImage>[] laserLists = new ArrayList[]{laserRight, laserLeft, laserUp, laserDown};
                for (int d = 0; d < laserDirs.length; d++) {
                    String path = "/skill/laserbeam_" + laserDirs[d] + i + ".png";
                    System.out.println(path);
                    BufferedImage img = setup(path);
                    if (img != null) laserLists[d].add(img);
                }
            }
            // Tải ảnh bắn tay
            for (int i = 1; i <= golemArmShotFrames; i++) {
                String rPath = folder + "/" + characterIdentifier + "_shotright" + i + ".png";
                BufferedImage imgR = setup(rPath);
                if (imgR != null) armShotRight.add(imgR);

                String lPath = folder + "/" + characterIdentifier + "_shotleft" + i + ".png";
                BufferedImage imgL = setup(lPath);
                if (imgL != null) armShotLeft.add(imgL);
            }
        }
    }


    public BufferedImage getLaserFrame(String direction) {
        laserSpriteCounter++;
        if (laserSpriteCounter >= LASER_FRAMES_PER_ANIMATION) {
            laserSpriteNum++;
            if (laserSpriteNum >= 7) { // Giả sử laser có 7 frame
                laserSpriteNum = 0;
            }
            laserSpriteCounter = 0;
        }

        ArrayList<BufferedImage> targetLaserList = null;
        switch(direction){
            case "up": targetLaserList = laserUp; break;
            case "down": targetLaserList = laserDown; break;
            case "left": targetLaserList = laserLeft; break;
            case "right": targetLaserList = laserRight; break;
        }

        if(targetLaserList != null && !targetLaserList.isEmpty() && targetLaserList.size() > laserSpriteNum){
            return targetLaserList.get(laserSpriteNum);
        }
        return null;
    }

    public BufferedImage getArmShotFrame() {
        armShotSpriteCounter++;
        if (armShotSpriteCounter >= ARM_SHOT_FRAMES_PER_ANIMATION) {
            armShotSpriteNum++;
            int listSize = (character.getDirection().equals("right")) ? armShotRight.size() : armShotLeft.size();
            if (listSize > 0 && armShotSpriteNum >= listSize) {
                armShotSpriteNum = 0;
            } else if (listSize == 0) {
                armShotSpriteNum = 0;
            }
            armShotSpriteCounter = 0;
        }

        if (character.getDirection().equals("right") && armShotRight.size() > armShotSpriteNum) {
            return armShotRight.get(armShotSpriteNum);
        } else if (character.getDirection().equals("left") && armShotLeft.size() > armShotSpriteNum) {
            return armShotLeft.get(armShotSpriteNum);
        } else if (armShotRight.size() > armShotSpriteNum) {
            return armShotRight.get(armShotSpriteNum);
        }
        return null;
    }

    private BufferedImage createPlaceholderImage() {
        BufferedImage defaultImage = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = defaultImage.createGraphics();
        g2.setColor(new Color(255, 0, 255, 200));
        g2.fillRect(0, 0, gp.getTileSize(), gp.getTileSize());
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.setColor(Color.WHITE);
        g2.drawString("NoIMG", 2, gp.getTileSize() / 2 + 4);
        g2.dispose();
        return defaultImage;
    }
}