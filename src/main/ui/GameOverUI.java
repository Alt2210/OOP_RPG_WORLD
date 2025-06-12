package main.ui;

import main.GamePanel;
import java.awt.*;

/**
 * Lớp này chịu trách nhiệm vẽ giao diện khi người chơi thua cuộc (Game Over).
 * Kế thừa các thuộc tính và phương thức tiện ích từ lớp UI trừu tượng.
 */
public class GameOverUI extends UI {

    public GameOverUI(GamePanel gp) {
        super(gp);
    }

    @Override
    public void draw(Graphics2D g2) {
        // Vẽ lớp nền mờ
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        String text;
        int x, y;

        // Vẽ dòng chữ "GAME OVER"
        g2.setFont(pixelFont_Large.deriveFont(Font.BOLD, 70F));
        text = "GAME OVER";
        x = getXforCenteredText(text, g2, g2.getFont());
        y = gp.getScreenHeight() / 2 - gp.getTileSize();
        drawTextWithShadow(g2, text, x, y, new Color(180, 0, 0), Color.BLACK, 3);

        // Vẽ các lựa chọn menu
        g2.setFont(pixelFont_Medium);

        // Lựa chọn "Retry"
        text = "Retry";
        x = getXforCenteredText(text, g2, g2.getFont());
        y += gp.getTileSize() * 2;
        if (getCommandNum() == 0) {
            drawTextWithShadow(g2, "> " + text + " <", x - gp.getTileSize() / 2, y, menuTextColor_Selected, menuTextShadowColor, 1);
        } else {
            drawTextWithShadow(g2, text, x, y, menuTextColor_Normal, menuTextShadowColor, 1);
        }

        // Lựa chọn "Quit"
        text = "Quit";
        x = getXforCenteredText(text, g2, g2.getFont());
        y += gp.getTileSize();
        if (getCommandNum() == 1) {
            drawTextWithShadow(g2, "> " + text + " <", x - gp.getTileSize() / 2, y, menuTextColor_Selected, menuTextShadowColor, 1);
        } else {
            drawTextWithShadow(g2, text, x, y, menuTextColor_Normal, menuTextShadowColor, 1);
        }
    }
}