package main.ui;

import character.role.Player;
import main.GamePanel;
import main.ui.UI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayUI extends UI {

    // Các thuộc tính trạng thái chỉ dành riêng cho màn hình này
    public boolean messageOn = false;
    public String message = "";
    private int messageCounter = 0;
    public boolean textOn = false;
    public String floatingText = "";
    private int textCounter = 0;
    public String currentDialogue = "";
    private double playtime = 0.0;
    private final DecimalFormat dFormat = new DecimalFormat("#0.00");
    private BufferedImage keyImage;

    public PlayUI(GamePanel gp) {
        super(gp);
        loadPlayUIImages();
    }

    private void loadPlayUIImages() {
        try {
            keyImage = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
        } catch (IOException e) {
            System.err.println("UI: Could not load key image.");
            e.printStackTrace();
        }
    }

    // Các phương thức công khai để UIManager có thể tương tác
    public void showMessage(String text) {
        this.message = text;
        this.messageOn = true;
    }

    public void setCurrentDialogue(String dialogueText) {
        this.currentDialogue = dialogueText;
    }

    @Override
    public void draw(Graphics2D g2) {
        // Cập nhật thời gian chơi nếu game đang ở trạng thái play
        if (gp.gameState == gp.playState) {
            playtime += (double) 1.0 / gp.getFPS();
        }

        // Vẽ các thành phần của giao diện chính khi chơi
        drawPlayerHUD(g2);

        // Vẽ màn hình hội thoại nếu đang trong trạng thái dialogue
        if (gp.gameState == gp.dialogueState) {
            drawDialogueWindow(g2);
        }

        // Vẽ các thông báo pop-up (nếu có)
        drawMessage(g2);
    }

    private void drawPlayerHUD(Graphics2D g2) {
        g2.setFont(pixelFont_Small);
        g2.setColor(Color.white);

        int currentTileSize = gp.getTileSize();
        Player currentPlayer = gp.getPlayer();
        if (currentPlayer == null) return;

        // VẼ THANH EXP (SAO CHÉP TỪ CODE GỐC)
        int expBarX = currentTileSize / 2;
        int expBarY = currentTileSize / 2;
        int expBarWidth = currentTileSize * 5;
        int expBarHeight = 22;
        double expRatio = (currentPlayer.getExpToNextLevel() > 0) ? (double) currentPlayer.getCurrentExp() / currentPlayer.getExpToNextLevel() : 0;
        int currentExpBarWidth = (int) (expBarWidth * expRatio);
        g2.setColor(new Color(60, 60, 60, 200));
        g2.fillRoundRect(expBarX, expBarY, expBarWidth, expBarHeight, 10, 10);
        g2.setColor(new Color(255, 215, 0));
        g2.fillRoundRect(expBarX, expBarY, currentExpBarWidth, expBarHeight, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(expBarX, expBarY, expBarWidth, expBarHeight, 10, 10);
        g2.setFont(pixelFont_XSmall.deriveFont(12F));
        String levelText = "LV " + currentPlayer.getLevel();
        String expText = currentPlayer.getCurrentExp() + " / " + currentPlayer.getExpToNextLevel();
        drawTextWithShadow(g2, levelText, expBarX + 5, expBarY + expBarHeight - 6, Color.WHITE, menuTextShadowColor, 1);
        FontMetrics fm = g2.getFontMetrics();
        int expTextX = expBarX + (expBarWidth - fm.stringWidth(expText)) / 2;
        drawTextWithShadow(g2, expText, expTextX, expBarY + expBarHeight - 6, Color.WHITE, menuTextShadowColor, 1);

        // VẼ THỜI GIAN
        drawTextWithShadow(g2, "Time: " + dFormat.format(playtime), gp.getTileSize() * 10, currentTileSize, Color.WHITE, menuTextShadowColor, 1);

        // VẼ THANH MANA (SAO CHÉP TỪ CODE GỐC)
        int mpBarX = currentTileSize / 2;
        int mpBarY = currentTileSize / 2 + currentTileSize + 10;
        int barWidth = currentTileSize * 2;
        int barHeight = 12;
        double manaPercent = (currentPlayer.getMaxMana() > 0) ? (double) currentPlayer.getCurrentMana() / currentPlayer.getMaxMana() : 0;
        int currentManaBarWidth = (int) (barWidth * manaPercent);
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(mpBarX - 1, mpBarY - 1, barWidth + 2, barHeight + 2, 5, 5);
        g2.setColor(new Color(50, 50, 100));
        g2.fillRoundRect(mpBarX, mpBarY, barWidth, barHeight, 5, 5);
        if (currentManaBarWidth > 0) {
            g2.setColor(new Color(0, 100, 255));
            g2.fillRoundRect(mpBarX, mpBarY, currentManaBarWidth, barHeight, 5, 5);
        }
        g2.setFont(pixelFont_XSmall);
        String mpText = currentPlayer.getCurrentMana() + "/" + currentPlayer.getMaxMana();
        fm = g2.getFontMetrics(pixelFont_XSmall);
        int textX = mpBarX + barWidth + 5;
        int textY = mpBarY + fm.getAscent() - (fm.getHeight() - barHeight) / 2 + 1;
        drawTextWithShadow(g2, mpText, textX, textY, Color.WHITE, menuTextShadowColor, 1);

        // VẼ THANH STAMINA (SAO CHÉP TỪ CODE GỐC)
        int staminaBarX = currentTileSize / 2;
        int staminaBarY = (currentTileSize / 2 + 22 + 10);
        double staminaPercent = (currentPlayer.getMaxStamina() > 0) ? (double) currentPlayer.getCurrentStamina() / currentPlayer.getMaxStamina() : 0;
        int currentStaminaBarWidth = (int) (barWidth * staminaPercent);
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(staminaBarX - 1, staminaBarY - 1, barWidth + 2, barHeight + 2, 5, 5);
        g2.setColor(new Color(100, 100, 50));
        g2.fillRoundRect(staminaBarX, staminaBarY, barWidth, barHeight, 5, 5);
        if (currentStaminaBarWidth > 0) {
            g2.setColor(new Color(230, 230, 0));
            g2.fillRoundRect(staminaBarX, staminaBarY, currentStaminaBarWidth, barHeight, 5, 5);
        }
        g2.setFont(pixelFont_XSmall);
        String staText = gp.getPlayer().getCurrentStamina() + "/" + gp.getPlayer().getMaxStamina();
        fm = g2.getFontMetrics(pixelFont_XSmall);
        textX = staminaBarX + barWidth + 5;
        textY = staminaBarY + fm.getAscent() - (fm.getHeight() - barHeight) / 2 + 1;
        drawTextWithShadow(g2, staText, textX, textY, Color.WHITE, menuTextShadowColor, 1);
    }

    public void drawMessage(Graphics2D g2) {
        {
            if (messageOn) {
                int messageBoxWidth = gp.getScreenWidth() - gp.getTileSize() * 6;
                int messageBoxHeight = gp.getTileSize() * 2;
                int messageBoxX = gp.getScreenWidth() / 2 - messageBoxWidth / 2;
                int messageBoxY = gp.getScreenHeight() - gp.getTileSize() * 3 - gp.getTileSize() / 2;

                drawSubWindow(g2, messageBoxX, messageBoxY, messageBoxWidth, messageBoxHeight, new Color(0, 0, 0, 180), Color.WHITE, 20, 20, 2);

                g2.setFont(pixelFont_XSmall);
                g2.setColor(Color.WHITE);
                int textPaddingX = gp.getTileSize() / 3;
                int currentTextY = messageBoxY + textPaddingX + g2.getFontMetrics().getAscent();

                List<String> wrappedLines = wrapText(message, g2.getFontMetrics(), messageBoxWidth - (textPaddingX * 2));
                for (String lineToDraw : wrappedLines) {
                    int lineX = messageBoxX + textPaddingX;
                    drawTextWithShadow(g2, lineToDraw, lineX, currentTextY, Color.WHITE, menuTextShadowColor, 1);
                    currentTextY += g2.getFontMetrics().getHeight();
                }

                messageCounter++;
                if (messageCounter > 120) {
                    messageCounter = 0;
                    messageOn = false;
                }
            }
        }
    }

    public void showFloatingText(String text){
        this.floatingText = text;
        this.textOn  = true;
    }

    public void drawFloatingText(Graphics2D g2) {
        {
            if (textOn) {

                g2.setFont(pixelFont_XSmall);
                g2.setColor(Color.WHITE);
                g2.drawString(floatingText, 15, gp.getTileSize()*5);

                textCounter++;
                if (textCounter > 120) {
                    textCounter = 0;
                    textOn = false;
                }
            }
        }
    }

    private void drawDialogueWindow(Graphics2D g2) {
        int x = gp.getTileSize();
        int y = gp.getScreenHeight() - gp.getTileSize() * 4 - gp.getTileSize() / 2;
        int width = gp.getScreenWidth() - (gp.getTileSize() * 2);
        int height = gp.getTileSize() * 4;
        drawSubWindow(g2, x, y, width, height, menuBoxBgColor_New, menuBoxBorderColor_New, 15, 15, 3);

        g2.setFont(pixelFont_Small);
        g2.setColor(Color.white);

        int dialogueX = x + gp.getTileSize() / 2;
        FontMetrics fm = g2.getFontMetrics();
        int currentY = y + fm.getAscent() + gp.getTileSize() / 3;
        int lineHeight = fm.getHeight() + 2;

        if (currentDialogue != null && !currentDialogue.isEmpty()) {
            for (String lineToDraw : wrapText(currentDialogue, fm, width - gp.getTileSize())) {
                drawTextWithShadow(g2, lineToDraw, dialogueX, currentY, Color.WHITE, menuTextShadowColor, 1);
                currentY += lineHeight;
            }
        }

        g2.setFont(pixelFont_XSmall);
        String continueText = "Press ENTER...";
        FontMetrics fmContinue = g2.getFontMetrics();
        int continueX = x + width - fmContinue.stringWidth(continueText) - gp.getTileSize() / 2;
        int continueY = y + height - fmContinue.getHeight() / 2 - gp.getTileSize() / 4;
        drawTextWithShadow(g2, continueText, continueX, continueY, Color.LIGHT_GRAY, menuTextShadowColor, 1);
    }
}