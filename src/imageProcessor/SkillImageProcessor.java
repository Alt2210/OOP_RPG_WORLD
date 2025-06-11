package imageProcessor;

import character.Character;
import main.GamePanel;
import main.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SkillImageProcessor extends ImageProcessor {

    private List<BufferedImage> up = new ArrayList<>();
    private List<BufferedImage> down = new ArrayList<>();
    private List<BufferedImage> left = new ArrayList<>();
    private List<BufferedImage> right = new ArrayList<>();

    private List<BufferedImage> spin = new ArrayList<>();

    private Character caster;
    private boolean hasDirection;

    public SkillImageProcessor(GamePanel gp, Character caster, boolean hasDirection) {
        super(gp);
        this.caster = caster;
        // Mặc định tốc độ animation, có thể điều chỉnh sau
        this.numSprite = 0; // Sẽ được cập nhật khi tải ảnh
        this.hasDirection = hasDirection;
    }

    public void getImage(String folder, String identifier, int numSprite) {

        right.clear();
        up.clear();
        down.clear();
        left.clear();
        for (int i = 1; i <= numSprite; i++) {
            // Giả sử tên file có dạng prefix_0.png, prefix_1.png, ...
            String fullPath = "/" + folder + "/" + identifier + i + ".png";
            System.out.println(fullPath);
            BufferedImage image = setup(fullPath); // Phương thức setup() kế thừa từ ImageProcessor
            if (image != null && this.hasDirection) {
                setDirection(image);
            }
            else{
                right.add(image);
            }
        }
        // Đặt lại số lượng sprite cho animation
        setNumSprite(numSprite);
    }

    private void setDirection(BufferedImage image){
        right.add(ImageUtils.rotateImage(image, 0));
        left.add(ImageUtils.rotateImage(image, 180));
        up.add(ImageUtils.rotateImage(image, -90));
        down.add(ImageUtils.rotateImage(image, 90));
    }

    private void setSpin(BufferedImage image){
        for (int i = 0; i <= 360; i = i + 15) {
            spin.add(ImageUtils.rotateImageToSquareCanvas(image, i));
            System.out.println(ImageUtils.rotateImageToSquareCanvas(image, i).getHeight());
        }
    }

    @Override
    public BufferedImage getCurFrame() {
        return getCurFrame(caster.direction);
    }

    public BufferedImage getCurFrame(String direction) {
        List<BufferedImage> frames = new ArrayList<>();
        if (hasDirection) {
            switch (direction) {
                case "up":
                    frames = up;
                    break;
                case "down":
                    frames = down;
                    break;
                case "right":
                    frames = right;
                    break;
                case "left":
                    frames = left;
                    break;
                default:
                    // Mặc định có thể là down hoặc right tùy vào sprite của bạn
                    frames = down;
                    break;
            }
        } else {
            frames = right; // Dành cho các skill không có hướng
        }

        setNumSprite(frames.size());
        if (frames != null && !frames.isEmpty() && numSprite > 0) {
            int index = spriteNum % numSprite;
            return frames.get(index);
        }
        return null; // Trả về null nếu không có frame hợp lệ
    }
}