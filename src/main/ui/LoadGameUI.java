package main.ui;

import data.DataStorage;
import data.GameHistory;
import main.GamePanel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LoadGameUI extends UI {

    private GameHistory gameHistory;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LoadGameUI(GamePanel gp) {
        super(gp);
    }

    // Phương thức này sẽ được gọi mỗi khi vào màn hình load game
    public void refreshHistory() {
        this.gameHistory = gp.getSaveLoadManager().getSaveHistory();
    }

    @Override
    public void draw(Graphics2D g2) {
        // Vẽ nền
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        // Vẽ tiêu đề
        g2.setFont(pixelFont_Large.deriveFont(40F));
        String text = "Load Game";
        int x = getXforCenteredText(text, g2, g2.getFont());
        int y = gp.getTileSize() * 2;
        drawTextWithShadow(g2, text, x, y, Color.WHITE, menuTextShadowColor, 3);

        // Vẽ khung danh sách các điểm save
        int frameX = gp.getTileSize() * 2;
        int frameY = y + gp.getTileSize();
        int frameWidth = gp.getScreenWidth() - (gp.getTileSize() * 4);
        int frameHeight = gp.getScreenHeight() - (gp.getTileSize() * 5);
        drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 20, 20, 2);

        // Vẽ các mục trong danh sách
        g2.setFont(pixelFont_Small);
        int listY = frameY + gp.getTileSize();
        int itemHeight = gp.getTileSize() + 10;

        if (gameHistory != null && !gameHistory.getSavePoints().isEmpty()) {
            List<DataStorage> saves = gameHistory.getSavePoints();
            for (int i = 0; i < saves.size(); i++) {
                DataStorage save = saves.get(i);
                String saveText = (i + 1) + ". " + save.getDescription() + " - " + dateFormat.format(new Date(save.getTimestamp()));

                if (i == getCommandNum()) {
                    drawTextWithShadow(g2, "> " + saveText, frameX + 20, listY, menuTextColor_Selected, menuTextShadowColor, 1);
                } else {
                    drawTextWithShadow(g2, saveText, frameX + 35, listY, menuTextColor_Normal, menuTextShadowColor, 1);
                }
                listY += itemHeight;
            }
        } else {
            drawTextWithShadow(g2, "No save data found.", getXforCenteredTextInBox("No save data found.", g2, g2.getFont(), frameX, frameWidth), frameY + frameHeight/2, menuTextColor_Normal, menuTextShadowColor, 1);
        }

        // Hướng dẫn
        g2.setFont(pixelFont_XSmall);
        String helpText = "Press ESC to return to Title Screen";
        drawTextWithShadow(g2, helpText, getXforCenteredText(helpText, g2, g2.getFont()), frameY + frameHeight + 30, Color.GRAY, menuTextShadowColor, 1);
    }
}