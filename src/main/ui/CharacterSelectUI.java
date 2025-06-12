package main.ui;

import main.GamePanel;
import main.ui.UI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Lớp này chịu trách nhiệm vẽ giao diện màn hình Chọn Nhân Vật.
 * Kế thừa các thuộc tính và phương thức tiện ích từ lớp UI trừu tượng.
 */
public class CharacterSelectUI extends UI {

    // Các ảnh chỉ dành riêng cho màn hình này
    private BufferedImage sodierAvatar;
    private BufferedImage astrologerAvatar;
    // Các ảnh menuBoxImage, menuCursorImage có thể được tải ở đây nếu cần,
    // nhưng theo code gốc của bạn, chúng không được sử dụng trong màn hình này.

    public CharacterSelectUI(GamePanel gp) {
        super(gp);
        loadImages();
    }

    private void loadImages() {
        try {
            // Tải ảnh đại diện cho các lớp nhân vật từ file tài nguyên
            sodierAvatar = ImageIO.read(getClass().getResourceAsStream("/player/sodier_walkleft1.png"));
            astrologerAvatar = ImageIO.read(getClass().getResourceAsStream("/player/astrologist_walkleft1.png"));
        } catch (Exception e) {
            System.err.println("UI: Lỗi khi tải ảnh cho màn hình chọn nhân vật.");
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // Sao chép 100% logic từ phương thức drawCharacterSelectScreen() gốc

        // Vẽ nền
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        // Vẽ tiêu đề
        g2.setFont(pixelFont_Large.deriveFont(40F));
        String text = "Choose Your Hero";
        int x = getXforCenteredText(text, g2, g2.getFont());
        int y = gp.getTileSize() * 2;
        drawTextWithShadow(g2, text, x, y, Color.WHITE, menuTextShadowColor, 3);

        // --- Vẽ các ô lựa chọn ---
        int frameWidth = gp.getTileSize() * 5;
        int frameHeight = gp.getTileSize() * 7;
        int frameSpacing = gp.getTileSize() * 2;
        int totalWidth = (frameWidth * 2) + frameSpacing;
        int startX = (gp.getScreenWidth() - totalWidth) / 2;
        int frameY = gp.getScreenHeight() / 2 - frameHeight / 2;

        // Ô chọn Soldier (commandNum == 0)
        int soldierFrameX = startX;
        drawSubWindow(g2, soldierFrameX, frameY, frameWidth, frameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 20, 20, 2);
        if (getCommandNum() == 0) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(soldierFrameX - 3, frameY - 3, frameWidth + 6, frameHeight + 6, 25, 25);
        }

        // Ô chọn Astrologer (commandNum == 1)
        int astrologerFrameX = soldierFrameX + frameWidth + frameSpacing;
        drawSubWindow(g2, astrologerFrameX, frameY, frameWidth, frameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 20, 20, 2);
        if (getCommandNum() == 1) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(astrologerFrameX - 3, frameY - 3, frameWidth + 6, frameHeight + 6, 25, 25);
        }

        // --- Vẽ thông tin bên trong các ô ---
        // Thông tin Soldier
        g2.setFont(pixelFont_Medium);
        String nameSoldier = "Soldier";
        int nameSoldierX = getXforCenteredTextInBox(nameSoldier, g2, g2.getFont(), soldierFrameX, frameWidth);
        drawTextWithShadow(g2, nameSoldier, nameSoldierX, frameY + gp.getTileSize(), Color.WHITE, menuTextShadowColor, 2);
        if (sodierAvatar != null) {
            g2.drawImage(sodierAvatar, soldierFrameX + (frameWidth - gp.getTileSize()) / 2, frameY + gp.getTileSize() + 20, gp.getTileSize(), gp.getTileSize(), null);
        }
        g2.setFont(pixelFont_XSmall);
        drawTextInBox("A brave warrior excelling in close combat. Wields a powerful Fireball skill.", soldierFrameX, frameY + gp.getTileSize() * 3, frameWidth, g2);

        // Thông tin Astrologer
        g2.setFont(pixelFont_Medium);
        String nameAstrologer = "Astrologer";
        int nameAstrologerX = getXforCenteredTextInBox(nameAstrologer, g2, g2.getFont(), astrologerFrameX, frameWidth);
        drawTextWithShadow(g2, nameAstrologer, nameAstrologerX, frameY + gp.getTileSize(), Color.WHITE, menuTextShadowColor, 2);
        if (astrologerAvatar != null) {
            g2.drawImage(astrologerAvatar, astrologerFrameX + (frameWidth - gp.getTileSize()) / 2, frameY + gp.getTileSize() + 20, gp.getTileSize(), gp.getTileSize(), null);
        }
        g2.setFont(pixelFont_XSmall);
        drawTextInBox("A wise mage who channels celestial power. Normal attacks create magical effects.", astrologerFrameX, frameY + gp.getTileSize() * 3, frameWidth, g2);

        // --- Hướng dẫn ---
        g2.setFont(pixelFont_Small);
        String helpText = "Use LEFT/RIGHT to select. Press ENTER to confirm.";
        int helpX = getXforCenteredText(helpText, g2, g2.getFont());
        int helpY = frameY + frameHeight + gp.getTileSize();
        drawTextWithShadow(g2, helpText, helpX, helpY, Color.LIGHT_GRAY, menuTextShadowColor, 1);

        String backText = "Press ESC to go back";
        int backX = getXforCenteredText(backText, g2, g2.getFont());
        drawTextWithShadow(g2, backText, backX, helpY + gp.getTileSize(), Color.GRAY, menuTextShadowColor, 1);
    }

    /**
     * Helper method để vẽ text được căn giữa và tự động xuống dòng trong một khu vực hình chữ nhật.
     * Phương thức này được sao chép từ lớp UI gốc vì nó cần thiết cho màn hình này.
     */
    private void drawTextInBox(String text, int boxX, int boxY, int boxWidth, Graphics2D g2) {
        int padding = 15;
        int availableWidth = boxWidth - (padding * 2);
        int currentY = boxY;

        FontMetrics fm = g2.getFontMetrics();
        // Phương thức wrapText được kế thừa từ lớp cha UI
        List<String> lines = wrapText(text, fm, availableWidth);
        for (String line : lines) {
            // Phương thức getXforCenteredTextInBox cũng được kế thừa từ lớp cha UI
            int lineX = getXforCenteredTextInBox(line, g2, g2.getFont(), boxX, boxWidth);
            g2.drawString(line, lineX, currentY);
            currentY += fm.getHeight() * 0.9;
        }
    }
}