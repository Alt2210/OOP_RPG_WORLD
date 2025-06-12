package main.ui;

import main.GamePanel;
import main.ui.UI;

import java.awt.*;

/**
 * Lớp này chịu trách nhiệm vẽ giao diện khi người chơi chiến thắng.
 * Kế thừa các thuộc tính và phương thức tiện ích từ lớp UI trừu tượng.
 */
public class VictoryUI extends UI {

    public VictoryUI(GamePanel gp) {
        super(gp);
    }

    @Override
    public void draw(Graphics2D g2) {
        // Sao chép 100% logic từ phương thức drawEndGameScreen() gốc

        // Vẽ lớp nền mờ
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        int yOffset = -gp.getTileSize();

        // Vẽ dòng chữ "You found the Princess!"
        g2.setFont(pixelFont_Medium);
        String text1 = "You found the Princess!";
        int x1 = getXforCenteredText(text1, g2, g2.getFont());
        int y1 = gp.getScreenHeight() / 2 - gp.getTileSize() + yOffset;
        drawTextWithShadow(g2, text1, x1, y1, Color.WHITE, menuTextShadowColor, 1);

        // Vẽ dòng chữ "CONGRATULATIONS!"
        g2.setFont(pixelFont_Large);
        String text2 = "CONGRATULATIONS!";
        int x2 = getXforCenteredText(text2, g2, g2.getFont());
        int y2 = gp.getScreenHeight() / 2 + gp.getTileSize() / 2 + yOffset;
        drawTextWithShadow(g2, text2, x2, y2, menuTextColor_Selected, new Color(0, 0, 0, 180), 2);

        // Vẽ hướng dẫn quay về màn hình chính
        g2.setFont(pixelFont_Small);
        String textEnter = "Press ENTER to return to Title Screen";
        int xEnter = getXforCenteredText(textEnter, g2, g2.getFont());
        int yEnter = gp.getScreenHeight() / 2 + gp.getTileSize() * 2 + yOffset;
        drawTextWithShadow(g2, textEnter, xEnter, yEnter, Color.WHITE, menuTextShadowColor, 1);
    }
}