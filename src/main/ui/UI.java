package main.ui;

import main.GamePanel;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class UI {
    // --- CÁC THUỘC TÍNH CHUNG (Không thay đổi) ---
    protected GamePanel gp;
    protected Font basePixelFont, pixelFont_Large, pixelFont_Medium, pixelFont_Small, pixelFont_XSmall;
    protected Color menuTextColor_Normal = Color.WHITE;
    protected Color menuTextColor_Selected = new Color(255, 220, 100);
    protected Color menuTextShadowColor = new Color(50, 50, 50, 180);
    protected Color menuBoxBgColor_New = new Color(45, 30, 65, 230);
    protected Color menuBoxBorderColor_New = new Color(100, 70, 130, 255);

    private int commandNum = 0;
    private int slotCol = 0;
    private int slotRow = 0;

    public UI(GamePanel gp) {
        this.gp = gp;
        loadFonts();
    }

    // --- GETTER & SETTER (Không thay đổi) ---
    public int getCommandNum() { return commandNum; }
    public void setCommandNum(int commandNum) { this.commandNum = commandNum; }
    public int getSlotCol() { return slotCol; }
    public void setSlotCol(int slotCol) { this.slotCol = slotCol; }
    public int getSlotRow() { return slotRow; }
    public void setSlotRow(int slotRow) { this.slotRow = slotRow; }
    public int getItemIndexOnSlot() { return slotRow * 5 + slotCol; }

    // --- PHƯƠNG THỨC TRỪU TƯỢNG (Không thay đổi) ---
    public abstract void draw(Graphics2D g2);

    // --- CÁC PHƯƠNG THỨC TIỆN ÍCH CHUNG (SỬA LẠI ACCESS MODIFIER) ---
    private void loadFonts() {
        try {
            InputStream is_pixel = getClass().getResourceAsStream("/font/FVF_Fernando_08.ttf");
            if (is_pixel == null) throw new IOException("Custom font not found");
            basePixelFont = Font.createFont(Font.TRUETYPE_FONT, is_pixel);
            pixelFont_Large = basePixelFont.deriveFont(Font.BOLD, 48F);
            pixelFont_Medium = basePixelFont.deriveFont(Font.PLAIN, 26F);
            pixelFont_Small = basePixelFont.deriveFont(Font.PLAIN, 20F);
            pixelFont_XSmall = basePixelFont.deriveFont(Font.ITALIC, 16F);
        } catch (Exception e) {
            System.err.println("UI: Error loading custom font! Falling back to Arial.");
            pixelFont_Large = new Font("Arial", Font.BOLD, 48);
            pixelFont_Medium = new Font("Arial", Font.PLAIN, 26);
            pixelFont_Small = new Font("Arial", Font.PLAIN, 20);
            pixelFont_XSmall = new Font("Arial", Font.ITALIC, 16);
        }
    }

    // SỬA THÀNH public hoặc protected
    public void drawSubWindow(int x, int y, int width, int height, Graphics2D g2) {
        drawSubWindow(g2, x, y, width, height, new Color(0, 0, 0, 210), Color.WHITE, 25, 25, 4);
    }

    // SỬA THÀNH public hoặc protected
    public void drawSubWindow(int x, int y, int width, int height, Graphics2D g2, Color bgColor, Color borderColor) {
        drawSubWindow(g2, x, y, width, height, bgColor, borderColor, 25, 25, 4);
    }

    // SỬA THÀNH protected
    protected void drawSubWindow(Graphics2D g2, int x, int y, int width, int height, Color bgColor, Color borderColor, int arcWidth, int arcHeight, int borderStroke) {
        g2.setColor(bgColor);
        g2.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderStroke));
        g2.drawRoundRect(x + borderStroke / 2, y + borderStroke / 2, width - borderStroke, height - borderStroke, arcWidth, arcHeight);
    }

    protected void drawTextWithShadow(Graphics2D g2, String text, int x, int y, Color textColor, Color shadowColor, int shadowOffset) {
        g2.setColor(shadowColor);
        g2.drawString(text, x + shadowOffset, y + shadowOffset);
        g2.setColor(textColor);
        g2.drawString(text, x, y);
    }

    protected int getXforCenteredText(String text, Graphics2D g2, Font font) {
        FontMetrics fm = g2.getFontMetrics(font);
        return gp.getScreenWidth() / 2 - fm.stringWidth(text) / 2;
    }

    protected int getXforCenteredTextInBox(String text, Graphics2D g2, Font font, int boxX, int boxWidth) {
        FontMetrics fm = g2.getFontMetrics(font);
        return boxX + (boxWidth - fm.stringWidth(text)) / 2;
    }

    protected List<String> wrapText(String text, FontMetrics fm, int availableWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (fm.stringWidth(currentLine.toString() + word) > availableWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString().trim());
        return lines;
    }
}