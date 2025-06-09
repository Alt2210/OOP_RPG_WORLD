package main;

import character.role.Player;
// import worldObject.pickableObject.OBJ_Key;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UI {
    GamePanel gp;
    Font basePixelFont;
    Font pixelFont_Large, pixelFont_Medium, pixelFont_Small, pixelFont_XSmall;

    BufferedImage keyImage;
    // --- Các ảnh cho màn hình tiêu đề và chọn nhân vật ---
    BufferedImage titleBgImage;
    BufferedImage titlePrincessAvatar;
    BufferedImage sodierAvatar; // Đổi tên từ titlePlayerAvatar để rõ ràng hơn
    BufferedImage astrologerAvatar; // Ảnh đại diện cho Astrologer
    BufferedImage menuBoxImage;
    BufferedImage menuCursorImage;

    public boolean messageOn = false;
    public String message = "";
    private int messageCounter = 0;
    public String currentDialogue = "";
    private double playtime = 0.0;
    private DecimalFormat dFormat = new DecimalFormat("#0.00");
    protected int commandNum = 0;
    public int slotCol = 0;
    public int slotRow = 0;

    Color menuTextColor_Normal = Color.WHITE;
    Color menuTextColor_Selected = new Color(255, 220, 100);
    Color menuTextShadowColor = new Color(50, 50, 50, 180);
    Color menuBoxBgColor_New = new Color(45, 30, 65, 230);
    Color menuBoxBorderColor_New = new Color(100, 70, 130, 255);


    public UI(GamePanel gp) {
        this.gp = gp;
        loadFonts();
        loadUIImages();
    }

    private void loadFonts() {
        try {
            InputStream is_pixel = getClass().getResourceAsStream("/font/pressstaart2P.ttf");
            if (is_pixel == null) {
                throw new IOException("Custom font not found: PressStart2P-Regular.ttf");
            }
            basePixelFont = Font.createFont(Font.TRUETYPE_FONT, is_pixel);

            pixelFont_Large = basePixelFont.deriveFont(Font.BOLD, 48F);
            pixelFont_Medium = basePixelFont.deriveFont(Font.PLAIN, 26F);
            pixelFont_Small = basePixelFont.deriveFont(Font.PLAIN, 20F);
            pixelFont_XSmall = basePixelFont.deriveFont(Font.ITALIC, 16F);

        } catch (FontFormatException | IOException e) {
            System.err.println("UI: Error loading custom font! Falling back to Arial.");
            e.printStackTrace();
            basePixelFont = new Font("Arial", Font.PLAIN, 12);
            pixelFont_Large = new Font("Arial", Font.BOLD, 48);
            pixelFont_Medium = new Font("Arial", Font.PLAIN, 26);
            pixelFont_Small = new Font("Arial", Font.PLAIN, 20);
            pixelFont_XSmall = new Font("Arial", Font.ITALIC, 16);
        }
    }

    private void loadUIImages() {
        try {
            keyImage = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
            titleBgImage = ImageIO.read(getClass().getResourceAsStream("/ui/title_background.png"));
            titlePrincessAvatar = ImageIO.read(getClass().getResourceAsStream("/npc/princess_walkleft1.png"));

            // Tải ảnh đại diện cho các lớp nhân vật
            // Đảm bảo các file này tồn tại trong /res/player/
            sodierAvatar = ImageIO.read(getClass().getResourceAsStream("/player/sodier_walkleft1.png"));
            astrologerAvatar = ImageIO.read(getClass().getResourceAsStream("/player/astrologist_walkleft1.png"));

            menuBoxImage = ImageIO.read(getClass().getResourceAsStream("/ui/menu_box_pixel.png"));
            menuCursorImage = ImageIO.read(getClass().getResourceAsStream("/ui/menu_cursor_pixel.png"));

        } catch (Exception e) {
            System.err.println("UI: Lỗi khi tải một hoặc nhiều ảnh UI! Một số thành phần có thể không hiển thị.");
            e.printStackTrace();
        }
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void setCurrentDialogue(String dialogueText) {
        this.currentDialogue = dialogueText;
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        if (gp.gameState == gp.titleState) {
            drawTitleScreen(g2);
        } else if (gp.gameState == gp.characterSelectState) {
            drawCharacterSelectScreen(g2);
        } else if (gp.gameState == gp.playState) {
            drawPlayUI(g2);
        } else if (gp.gameState == gp.pauseState) {
            drawPauseScreen(g2);
        } else if (gp.gameState == gp.victoryEndState) {
            drawEndGameScreen(g2);
        } else if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen(g2);
        } else if (gp.gameState == gp.gameOverState){
            drawGameOverScreen(g2);
        } else if (gp.gameState == gp.InventoryState){
            drawIventory(g2);
        }
    }

    public void drawCharacterSelectScreen(Graphics2D g2) {
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
        if (commandNum == 0) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(soldierFrameX - 3, frameY - 3, frameWidth + 6, frameHeight + 6, 25, 25);
        }

        // Ô chọn Astrologer (commandNum == 1)
        int astrologerFrameX = soldierFrameX + frameWidth + frameSpacing;
        drawSubWindow(g2, astrologerFrameX, frameY, frameWidth, frameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 20, 20, 2);
        if (commandNum == 1) {
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
            g2.drawImage(sodierAvatar, soldierFrameX + (frameWidth - gp.getTileSize())/2, frameY + gp.getTileSize() + 20, gp.getTileSize(), gp.getTileSize(), null);
        }
        g2.setFont(pixelFont_XSmall);
        drawTextInBox("A brave warrior excelling in close combat. Wields a powerful Fireball skill.", soldierFrameX, frameY + gp.getTileSize() * 3, frameWidth, g2);

        // Thông tin Astrologer
        g2.setFont(pixelFont_Medium);
        String nameAstrologer = "Astrologer";
        int nameAstrologerX = getXforCenteredTextInBox(nameAstrologer, g2, g2.getFont(), astrologerFrameX, frameWidth);
        drawTextWithShadow(g2, nameAstrologer, nameAstrologerX, frameY + gp.getTileSize(), Color.WHITE, menuTextShadowColor, 2);
        if (astrologerAvatar != null) {
            g2.drawImage(astrologerAvatar, astrologerFrameX + (frameWidth - gp.getTileSize())/2, frameY + gp.getTileSize() + 20, gp.getTileSize(), gp.getTileSize(), null);
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

    private void drawTextInBox(String text, int boxX, int boxY, int boxWidth, Graphics2D g2) {
        int padding = 15;
        int availableWidth = boxWidth - (padding * 2);
        int currentY = boxY;

        FontMetrics fm = g2.getFontMetrics();
        List<String> lines = wrapText(text, fm, availableWidth);
        for(String line : lines) {
            int lineX = getXforCenteredTextInBox(line, g2, g2.getFont(), boxX, boxWidth);
            g2.drawString(line, lineX, currentY);
            currentY += fm.getHeight() * 0.9;
        }
    }

    private void drawPlayUI(Graphics2D g2) {
        g2.setFont(pixelFont_Small);
        g2.setColor(Color.white);

        int currentTileSize = gp.getTileSize();
        Player currentPlayer = gp.getPlayer();
        if (keyImage != null && currentPlayer != null) {
            g2.drawImage(keyImage, currentTileSize / 2, currentTileSize / 2, currentTileSize, currentTileSize, null);
            drawTextWithShadow(g2, "x " + currentPlayer.getHasKey(), currentTileSize / 2 + currentTileSize + 10, currentTileSize / 2 + currentTileSize - 5, Color.WHITE, menuTextShadowColor, 1);
        }

        if (gp.gameState == gp.playState) {
            playtime += (double) 1.0 / gp.getFPS();
        }
        drawTextWithShadow(g2, "Time: " + dFormat.format(playtime), gp.getTileSize() * 10, currentTileSize, Color.WHITE, menuTextShadowColor, 1);

        if (currentPlayer != null) {
            int mpBarX = currentTileSize / 2;
            int mpBarY = currentTileSize / 2 + currentTileSize + 10;
            int barWidth = currentTileSize * 2;
            int barHeight = 12;
            int barOutlineWidth = barWidth + 2;
            int barOutlineHeight = barHeight + 2;

            double manaPercent = (currentPlayer.getMaxMana() > 0) ? (double) currentPlayer.getCurrentMana() / currentPlayer.getMaxMana() : 0;
            int currentManaBarWidth = (int) (barWidth * manaPercent);

            g2.setColor(Color.BLACK);
            g2.fillRoundRect(mpBarX - 1, mpBarY - 1, barOutlineWidth, barOutlineHeight, 5, 5);

            g2.setColor(new Color(50, 50, 100));
            g2.fillRoundRect(mpBarX, mpBarY, barWidth, barHeight, 5, 5);

            if (currentManaBarWidth > 0) {
                g2.setColor(new Color(0, 100, 255));
                g2.fillRoundRect(mpBarX, mpBarY, currentManaBarWidth, barHeight, 5, 5);
            }

            g2.setFont(pixelFont_XSmall);
            String mpText = currentPlayer.getCurrentMana() + "/" + currentPlayer.getMaxMana();
            FontMetrics fm = g2.getFontMetrics(pixelFont_XSmall);
            int textX = mpBarX + barWidth + 5;
            int textY = mpBarY + fm.getAscent() - (fm.getHeight() - barHeight) / 2 + 1;
            drawTextWithShadow(g2, mpText, textX, textY, Color.WHITE, menuTextShadowColor, 1);
        }

        if (messageOn) {
            int messageBoxWidth = gp.getScreenWidth() - gp.getTileSize() * 6;
            int messageBoxHeight = gp.getTileSize() * 2;
            int messageBoxX = gp.getScreenWidth() / 2 - messageBoxWidth / 2;
            int messageBoxY = gp.getScreenHeight() - gp.getTileSize() * 3 - gp.getTileSize() / 2;

            drawSubWindow(g2, messageBoxX, messageBoxY, messageBoxWidth, messageBoxHeight, new Color(0, 0, 0, 180), Color.WHITE, 20, 20, 2);

            g2.setFont(pixelFont_Medium);
            g2.setColor(Color.WHITE);
            int textPaddingX = gp.getTileSize() / 3;
            int textPaddingY = gp.getTileSize() / 3;
            int availableTextWidth = messageBoxWidth - (textPaddingX * 2);
            int currentTextY = messageBoxY + textPaddingY + g2.getFontMetrics().getAscent();

            if (message != null && !message.isEmpty()) {
                List<String> wrappedLines = wrapText(message, g2.getFontMetrics(), availableTextWidth);
                for (String lineToDraw : wrappedLines) {
                    if (currentTextY < messageBoxY + messageBoxHeight - textPaddingY) {
                        int lineX = messageBoxX + textPaddingX;
                        drawTextWithShadow(g2, lineToDraw, lineX, currentTextY, Color.WHITE, menuTextShadowColor, 1);
                        currentTextY += g2.getFontMetrics().getHeight();
                    } else break;
                }
            }

            // messageCounter vẫn hoạt động như cũ
            messageCounter++;
            if (messageCounter > 120) {
                messageCounter = 0;
                messageOn = false;
            }
        }
    }

    // Phương thức wrapText (đảm bảo bạn đã có nó trong lớp UI)
    private List<String> wrapText(String text, FontMetrics fm, int availableWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty() || fm == null || availableWidth <= 0) {
            lines.add(text != null ? text : "");
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (fm.stringWidth(currentLine.toString() + word) > availableWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }
        if (lines.isEmpty() && text.isEmpty()) {
            lines.add("");
        }
        return lines;
    }

    public void drawTitleScreen(Graphics2D g2) {
        if (titleBgImage != null) {
            g2.drawImage(titleBgImage, 0, 0, gp.getScreenWidth(), gp.getScreenHeight(), null);
        } else {
            g2.setColor(new Color(30, 30, 60));
            g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
        }

        int menuPadding = gp.getTileSize() / 3;
        g2.setFont(pixelFont_Medium);
        FontMetrics fmMenu = g2.getFontMetrics();

        int characterDisplaySize = gp.getTileSize() * 2;
        int characterInnerSpacing = 5;
        int spaceBetweenTextAndAvatars = gp.getTileSize() / 4;
        String[] menuItems = {"New Game", "Load Game", "Quit"};
        int menuItemHeight = fmMenu.getHeight() + 10;
        int menuItemsTotalHeight = menuItemHeight * menuItems.length;

        int contentHeight = Math.max(menuItemsTotalHeight, characterDisplaySize);
        int menuHeight = contentHeight + menuPadding * 2;

        int widestMenuItemTextWidth = 0;
        for (String item : menuItems) {
            widestMenuItemTextWidth = Math.max(widestMenuItemTextWidth, fmMenu.stringWidth(item));
        }
        int cursorWidthAllowance = (menuCursorImage != null) ? menuCursorImage.getWidth() + 5 : fmMenu.stringWidth(">") + 5;
        int textBlockWidth = cursorWidthAllowance + widestMenuItemTextWidth;
        int avatarBlockWidth = (characterDisplaySize * 2) + characterInnerSpacing;
        int menuWidth = menuPadding * 2 + textBlockWidth + spaceBetweenTextAndAvatars + avatarBlockWidth;

        int menuX = gp.getTileSize() / 2;
        int menuY = gp.getScreenHeight() - menuHeight - (gp.getTileSize() / 2);

        if (menuBoxImage != null) {
            g2.drawImage(menuBoxImage, menuX, menuY, menuWidth, menuHeight, null);
        } else {
            drawSubWindow(g2, menuX, menuY, menuWidth, menuHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 15, 15, 3);
        }

        int textBlockStartY = menuY + (menuHeight - menuItemsTotalHeight) / 2;
        int currentItemY = textBlockStartY + fmMenu.getAscent();

        for (int i = 0; i < menuItems.length; i++) {
            String text = menuItems[i];
            int itemTextX;
            Color textColor;
            int pointerX = menuX + menuPadding;

            if (commandNum == i) {
                textColor = menuTextColor_Selected;
                int cursorYOffset = currentItemY - fmMenu.getAscent() + (fmMenu.getHeight() - ((menuCursorImage != null) ? menuCursorImage.getHeight() : fmMenu.getHeight())) / 2;
                if (menuCursorImage != null) {
                    g2.drawImage(menuCursorImage, pointerX, cursorYOffset, null);
                    itemTextX = pointerX + menuCursorImage.getWidth() + 5;
                } else {
                    drawTextWithShadow(g2, ">", pointerX, currentItemY, textColor, menuTextShadowColor, 1);
                    itemTextX = pointerX + fmMenu.stringWidth(">") + 5;
                }
            } else {
                textColor = menuTextColor_Normal;
                itemTextX = pointerX + cursorWidthAllowance;
            }
            drawTextWithShadow(g2, text, itemTextX, currentItemY, textColor, menuTextShadowColor, 1);
            currentItemY += menuItemHeight;
        }

        int avatarsDrawY = menuY + (menuHeight - characterDisplaySize) / 2;
        int avatarBlockDrawStartX = menuX + menuPadding + textBlockWidth + spaceBetweenTextAndAvatars;

        if (sodierAvatar != null) {
            g2.drawImage(sodierAvatar, avatarBlockDrawStartX, avatarsDrawY, characterDisplaySize, characterDisplaySize, null);
        }
        if (titlePrincessAvatar != null) {
            g2.drawImage(titlePrincessAvatar, avatarBlockDrawStartX + characterDisplaySize + characterInnerSpacing, avatarsDrawY, characterDisplaySize, characterDisplaySize, null);
        }
    }


    public void drawPauseScreen(Graphics2D g2) {
        g2.setFont(pixelFont_Large);
        String text = "PAUSED";
        int x = getXforCenteredText(text, g2, g2.getFont());
        int y = gp.getScreenHeight() / 2;
        drawTextWithShadow(g2, text, x, y, Color.WHITE, new Color(0, 0, 0, 180), 2);
    }

    public void drawEndGameScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        int yOffset = -gp.getTileSize();

        g2.setFont(pixelFont_Medium);
        String text1 = "You found the Princess!";
        int x1 = getXforCenteredText(text1, g2, g2.getFont());
        int y1 = gp.getScreenHeight() / 2 - gp.getTileSize() + yOffset;
        drawTextWithShadow(g2, text1, x1, y1, Color.WHITE, menuTextShadowColor, 1);

        g2.setFont(pixelFont_Large);
        String text2 = "CONGRATULATIONS!";
        int x2 = getXforCenteredText(text2, g2, g2.getFont());
        int y2 = gp.getScreenHeight() / 2 + gp.getTileSize() / 2 + yOffset;
        drawTextWithShadow(g2, text2, x2, y2, menuTextColor_Selected, new Color(0, 0, 0, 180), 2);

        g2.setFont(pixelFont_Small);
        String textEnter = "Press ENTER to return to Title Screen";
        int xEnter = getXforCenteredText(textEnter, g2, g2.getFont());
        int yEnter = gp.getScreenHeight() / 2 + gp.getTileSize() * 2 + yOffset;
        drawTextWithShadow(g2, textEnter, xEnter, yEnter, Color.WHITE, menuTextShadowColor, 1);
    }

    public void drawDialogueScreen(Graphics2D g2) {
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
        int availableWidth = width - gp.getTileSize();

        if (currentDialogue != null && !currentDialogue.isEmpty()) {
            for (String lineToDraw : wrapText(currentDialogue, fm, availableWidth)) {
                if (currentY < y + height - gp.getTileSize() / 2) {
                    drawTextWithShadow(g2, lineToDraw, dialogueX, currentY, Color.WHITE, menuTextShadowColor, 1);
                    currentY += lineHeight;
                } else break;
            }
        }

        if (gp.gameState == gp.dialogueState) {
            g2.setFont(pixelFont_XSmall);
            String continueText = "Press ENTER...";
            FontMetrics fmContinue = g2.getFontMetrics();
            int continueX = x + width - fmContinue.stringWidth(continueText) - gp.getTileSize() / 2;
            int continueY = y + height - fmContinue.getHeight() / 2 - gp.getTileSize() / 4;
            drawTextWithShadow(g2, continueText, continueX, continueY, Color.LIGHT_GRAY, menuTextShadowColor, 1);
        }
    }

    public void drawSubWindow(int x, int y, int width, int height, Graphics2D g2) {
        drawSubWindow(g2, x, y, width, height, new Color(0, 0, 0, 210), Color.WHITE, 25, 25, 4);
    }

    public void drawSubWindow(int x, int y, int width, int height, Graphics2D g2, Color bgColor, Color borderColor) {
        drawSubWindow(g2, x, y, width, height, bgColor, borderColor, 25, 25, 4);
    }

    private void drawSubWindow(Graphics2D g2, int x, int y, int width, int height, Color bgColor, Color borderColor, int arcWidth, int arcHeight, int borderStroke) {
        g2.setColor(bgColor);
        g2.fillRoundRect(x, y, width, height, arcWidth, arcHeight);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderStroke));
        g2.drawRoundRect(x + borderStroke / 2, y + borderStroke / 2, width - borderStroke, height - borderStroke, arcWidth, arcHeight);
    }

    private void drawTextWithShadow(Graphics2D g2, String text, int x, int y, Color textColor, Color shadowColor, int shadowOffset) {
        if (text == null || g2 == null) return;
        Font currentFont = g2.getFont();
        if (currentFont == null) {
            g2.setFont(pixelFont_Small != null ? pixelFont_Small : new Font("Arial", Font.PLAIN, 12));
        }

        g2.setColor(shadowColor);
        g2.drawString(text, x + shadowOffset, y + shadowOffset);
        g2.setColor(textColor);
        g2.drawString(text, x, y);
    }

    public int getXforCenteredText(String text, Graphics2D g2, Font font) {
        if (text == null || g2 == null) return 0;
        Font currentFont = font != null ? font : g2.getFont();
        if (currentFont == null) { currentFont = basePixelFont; }
        FontMetrics fm = g2.getFontMetrics(currentFont);
        return gp.getScreenWidth() / 2 - fm.stringWidth(text) / 2;
    }

    public int getXforCenteredTextInBox(String text, Graphics2D g2, Font font, int boxX, int boxWidth) {
        if (text == null || g2 == null) return 0;
        Font currentFont = font != null ? font : g2.getFont();
        if (currentFont == null) { currentFont = basePixelFont; }
        FontMetrics fm = g2.getFontMetrics(currentFont);
        return boxX + (boxWidth - fm.stringWidth(text)) / 2;
    }

    public void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        String text;
        int x, y;

        g2.setFont(pixelFont_Large.deriveFont(Font.BOLD, 70F));
        text = "GAME OVER";
        x = getXforCenteredText(text, g2, g2.getFont());
        y = gp.getScreenHeight() / 2 - gp.getTileSize();
        drawTextWithShadow(g2, text, x, y, new Color(180,0,0), Color.BLACK, 3);

        g2.setFont(pixelFont_Medium);
        text = "Retry";
        x = getXforCenteredText(text, g2, g2.getFont());
        y += gp.getTileSize()*2;
        if(commandNum == 0) {
            drawTextWithShadow(g2, "> " + text + " <", x - gp.getTileSize()/2, y, menuTextColor_Selected, menuTextShadowColor, 1);
        } else {
            drawTextWithShadow(g2, text, x, y, menuTextColor_Normal, menuTextShadowColor, 1);
        }

        text = "Quit";
        x = getXforCenteredText(text, g2, g2.getFont());
        y += gp.getTileSize();
        if(commandNum == 1) {
            drawTextWithShadow(g2, "> " + text + " <", x - gp.getTileSize()/2, y, menuTextColor_Selected, menuTextShadowColor, 1);
        } else {
            drawTextWithShadow(g2, text, x, y, menuTextColor_Normal, menuTextShadowColor, 1);
        }
    }

    public void drawIventory(Graphics2D g2) {
        //Frame
        int frameX = gp.getTileSize() * 9;
        int frameY = gp.getTileSize();
        int frameWidth = gp.getTileSize() * 6;
        int frameHeight = gp.getTileSize() * 5;
        drawSubWindow(frameX,frameY,frameWidth,frameHeight, g2);

        //Slot
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotsSize = gp.getTileSize()+3;

        //Draw player's item
        for(int i=0;i < gp.getPlayer().getInventory().getItemStack();i++) {
            g2.drawImage(gp.getPlayer().getInventory().getItemStack(i).getItem().getItp().getCurFrame(),slotX,slotY,slotsSize, slotsSize,null);

            slotX += slotsSize;

            if(i == 4 || i == 9 || i == 14) {
                slotX = slotXstart;
                slotY += slotsSize;

            }
        }

        //CURSOR
        int cursorX = slotXstart + (gp.getTileSize() * slotCol);
        int cursorY = slotYstart + (gp.getTileSize() * slotRow);
        int cursorWidth = gp.getTileSize();
        int cursorHeight = gp.getTileSize();
        //Draw Cursor
        g2.setColor(Color.white);
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight,10,10);
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);
        //Description Frame
        int dFrameX = frameX;
        int dFrameY = frameY + frameHeight;
        int dFrameWidth = frameWidth;
        int dFrameHeight = gp.getTileSize() * 3;
        drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight,g2);
        //Draw Description
        int textX = dFrameX + 20;
        int textY = dFrameY + gp.getTileSize();
        g2.setFont(g2.getFont().deriveFont(28F));

        int itemIndex = getItemIndexOnSlot();

        if(itemIndex < gp.getPlayer().getInventory().getItemStack()){
            for(String line: gp.getPlayer().getInventory().getItemStack(itemIndex).getItem().getDescription().split("\n")){
                g2.drawString(line, textX, textY);
                textY += 32;
            }
        }

    }

    public int getItemIndexOnSlot() {
        int itemIndex = slotCol + (slotCol*5);
        return itemIndex;
    }
}