// Gợi ý package: main/ImageUtils.java
package main;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Lớp tiện ích chứa các phương thức để xử lý hình ảnh.
 */
public class ImageUtils {

    /**
     * Xoay một đối tượng BufferedImage một góc theo độ.
     * Phương thức này sẽ tạo ra một ảnh mới đủ lớn để chứa ảnh đã xoay mà không bị cắt xén.
     *
     * @param image   Ảnh gốc cần xoay.
     * @param degrees Góc xoay theo độ (số dương là xoay theo chiều kim đồng hồ).
     * @return Một đối tượng BufferedImage mới đã được xoay. Trả về null nếu ảnh đầu vào là null.
     */
    public static BufferedImage rotateImage(BufferedImage image, double degrees) {
        if (image == null) {
            return null;
        }

        // Chuyển đổi độ sang radian vì các hàm lượng giác trong Java dùng radian
        double radians = Math.toRadians(degrees);

        // Lấy kích thước ảnh gốc
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // Tính toán sin và cos của góc xoay
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));

        // Tính toán kích thước mới của ảnh để đảm bảo ảnh xoay không bị cắt
        int newWidth = (int) Math.floor(originalWidth * cos + originalHeight * sin);
        int newHeight = (int) Math.floor(originalHeight * cos + originalWidth * sin);

        // Tạo một ảnh mới với kích thước đã tính toán và hỗ trợ kênh alpha (trong suốt)
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();

        // Tạo một đối tượng AffineTransform để thực hiện việc xoay
        AffineTransform at = new AffineTransform();

        // Dịch chuyển tâm xoay đến giữa ảnh mới
        // Điều này đảm bảo ảnh gốc sẽ được vẽ vào giữa không gian mới trước khi xoay
        at.translate((newWidth - originalWidth) / 2.0, (newHeight - originalHeight) / 2.0);

        // Thực hiện phép xoay quanh tâm của ảnh gốc
        at.rotate(radians, originalWidth / 2.0, originalHeight / 2.0);

        // Áp dụng phép biến đổi (xoay) vào đối tượng Graphics2D
        g2d.setTransform(at);

        // Vẽ ảnh gốc lên ảnh mới (lúc này đã có hiệu ứng xoay)
        g2d.drawImage(image, 0, 0, null);

        // Giải phóng tài nguyên của Graphics2D
        g2d.dispose();

        return rotatedImage;
    }

    public static BufferedImage rotateImageToSquareCanvas(BufferedImage image, double degrees) {
        if (image == null) {
            return null;
        }

        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // 1. Tính toán kích thước canvas tối đa (đường chéo của ảnh gốc)
        int diagonal = (int) Math.sqrt(originalWidth * originalWidth + originalHeight * originalHeight);
        int canvasSize = diagonal; // Canvas sẽ là hình vuông

        // 2. Tạo một canvas hình vuông mới với kích thước cố định
        BufferedImage rotatedImage = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();

        // 3. Thực hiện phép biến đổi để xoay ảnh quanh tâm của canvas
        AffineTransform at = new AffineTransform();
        // Dịch đến tâm canvas
        at.translate(canvasSize / 2.0, canvasSize / 2.0);
        // Xoay
        at.rotate(Math.toRadians(degrees));
        // Dịch ngược lại để tâm của ảnh gốc trùng với tâm canvas
        at.translate(-originalWidth / 2.0, -originalHeight / 2.0);

        g2d.setTransform(at);

        // 4. Vẽ ảnh gốc lên canvas đã biến đổi
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotatedImage;
    }
}