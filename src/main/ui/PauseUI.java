package main.ui;

import main.GamePanel;
import main.ui.UI;

import java.awt.*;


public class PauseUI extends UI {

    public PauseUI(GamePanel gp) {
        super(gp);
    }

    @Override
    public void draw(Graphics2D g2) {

        g2.setFont(pixelFont_Large);
        String text = "PAUSED";

        // Lấy tọa độ X và Y để căn chữ ra giữa màn hình
        int x = getXforCenteredText(text, g2, g2.getFont());
        int y = gp.getScreenHeight() / 2;

        // Vẽ chữ có bóng đổ
        drawTextWithShadow(g2, text, x, y, Color.WHITE, new Color(0, 0, 0, 180), 2);
    }
}