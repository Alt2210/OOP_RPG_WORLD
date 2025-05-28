package main; // Hoặc package của bạn

import character.Player;
// import worldObject.pickableObject.OBJ_Key;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class UI {
    GamePanel gp;
    Font basePixelFont;
    Font pixelFont_Large, pixelFont_Medium, pixelFont_Small, pixelFont_XSmall;

    BufferedImage keyImage;
    BufferedImage titleBgImage;
    BufferedImage titlePrincessAvatar;
    BufferedImage titlePlayerAvatar;
    BufferedImage menuBoxImage;
    BufferedImage menuCursorImage;

    public boolean messageOn = false;
    public String message = "";
    private int messageCounter = 0;
    public String currentDialogue = "";
    private double playtime = 0.0;
    private DecimalFormat dFormat = new DecimalFormat("#0.00");
    protected int commandNum = 0;

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
            pixelFont_Medium = basePixelFont.deriveFont(Font.PLAIN, 26F); // Font cho menu items
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

            titlePrincessAvatar = ImageIO.read(getClass().getResourceAsStream("/npc/princess_left1.png"));
            titlePlayerAvatar = ImageIO.read(getClass().getResourceAsStream("/player/sodier_walkright1.png")); // Đảm bảo tên file đúng

            menuBoxImage = ImageIO.read(getClass().getResourceAsStream("/ui/menu_box_pixel.png"));
            menuCursorImage = ImageIO.read(getClass().getResourceAsStream("/ui/menu_cursor_pixel.png"));

        } catch (IOException e) {
            System.err.println("UI: Error loading one or more UI images! Some UI elements might not display correctly.");
        } catch (IllegalArgumentException e) {
            System.err.println("UI: Path for an UI image is invalid or file not found!");
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
        } else if (gp.gameState == gp.playState) {
            drawPlayUI(g2);
        } else if (gp.gameState == gp.pauseState) {
            drawPauseScreen(g2);
        } else if (gp.gameState == gp.endGameState) {
            drawEndGameScreen(g2);
        } else if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen(g2);
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

        if (messageOn) {
            g2.setFont(pixelFont_Medium);
            int messageBoxWidth = gp.getScreenWidth() - gp.getTileSize() * 6;
            int messageBoxHeight = gp.getTileSize() * 2;
            int messageBoxX = gp.getScreenWidth()/2 - messageBoxWidth/2;
            int messageBoxY = gp.getScreenHeight() - gp.getTileSize() * 3 - gp.getTileSize()/2;

            drawSubWindow(g2, messageBoxX, messageBoxY, messageBoxWidth, messageBoxHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 15,15,3);

            FontMetrics fm = g2.getFontMetrics();
            int messageX = getXforCenteredTextInBox(message, g2, g2.getFont(), messageBoxX, messageBoxWidth);
            int messageY = messageBoxY + (messageBoxHeight - fm.getHeight()) / 2 + fm.getAscent();

            drawTextWithShadow(g2, message, messageX, messageY, Color.WHITE, menuTextShadowColor, 1);

            messageCounter++;
            if (messageCounter > 120) {
                messageCounter = 0;
                messageOn = false;
            }
        }
    }

    public void drawTitleScreen(Graphics2D g2) {
        if (titleBgImage != null) {
            g2.drawImage(titleBgImage, 0, 0, gp.getScreenWidth(), gp.getScreenHeight(), null);
        } else {
            g2.setColor(new Color(30, 30, 60));
            g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
        }

        // --- Bắt đầu định nghĩa cho menu ---
        int menuPadding = gp.getTileSize() / 3; // GIẢM PADDING (was /2)
        g2.setFont(pixelFont_Medium);
        FontMetrics fmMenu = g2.getFontMetrics();

        int characterDisplaySize = gp.getTileSize() * 2;
        int characterInnerSpacing = 5; // GIẢM (was gp.getTileSize() / 4) - khoảng cách giữa 2 avatar
        int spaceBetweenTextAndAvatars = gp.getTileSize() / 4; // GIẢM (was gp.getTileSize()) - khoảng cách giữa khối text và khối avatar

        String[] menuItems = {"New Game", "Load Game", "Quit"};
        int menuItemHeight = fmMenu.getHeight() + 10; // (10 là padding dọc cho mỗi item)
        int menuItemsTotalHeight = menuItemHeight * menuItems.length;

        int contentHeight = Math.max(menuItemsTotalHeight, characterDisplaySize);
        int menuHeight = contentHeight + menuPadding * 2; // menuPadding ở đây là padding dọc trên/dưới

        int widestMenuItemTextWidth = 0;
        for (String item : menuItems) {
            widestMenuItemTextWidth = Math.max(widestMenuItemTextWidth, fmMenu.stringWidth(item));
        }
        int cursorWidthAllowance = 0;
        if (menuCursorImage != null) {
            cursorWidthAllowance = menuCursorImage.getWidth() + 5; // 5 là khoảng cách nhỏ sau con trỏ
        } else {
            cursorWidthAllowance = fmMenu.stringWidth(">") + 5;
        }
        int textBlockWidth = cursorWidthAllowance + widestMenuItemTextWidth;
        int avatarBlockWidth = (characterDisplaySize * 2) + characterInnerSpacing;

        // menuPadding ở đây là padding ngang trái/phải
        int menuWidth = menuPadding + textBlockWidth + spaceBetweenTextAndAvatars + avatarBlockWidth + menuPadding;

        int menuX = gp.getTileSize() / 2; // Giữ nguyên vị trí X của menu box
        int menuY = gp.getScreenHeight() - menuHeight - (gp.getTileSize() / 2); // Giữ nguyên vị trí Y

        if (menuBoxImage != null) {
            g2.drawImage(menuBoxImage, menuX, menuY, menuWidth, menuHeight, null);
        } else {
            drawSubWindow(g2, menuX, menuY, menuWidth, menuHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 15, 15, 3);
        }

        int textBlockStartY = menuY + menuPadding;
        if (menuItemsTotalHeight < characterDisplaySize) {
            textBlockStartY = menuY + (menuHeight - menuItemsTotalHeight) / 2;
        }
        int currentItemY = textBlockStartY + fmMenu.getAscent();

        for (int i = 0; i < menuItems.length; i++) {
            String text = menuItems[i];
            int itemTextX;
            Color textColor;
            int pointerX = menuX + menuPadding; // Vị trí bắt đầu của con trỏ (hoặc ">")

            if (commandNum == i) {
                textColor = menuTextColor_Selected;
                if (menuCursorImage != null) {
                    int cursorActualY = currentItemY - fmMenu.getAscent() + (fmMenu.getHeight() - menuCursorImage.getHeight()) / 2 ;
                    g2.drawImage(menuCursorImage, pointerX, cursorActualY, null);
                    itemTextX = pointerX + menuCursorImage.getWidth() + 5;
                } else {
                    drawTextWithShadow(g2, ">", pointerX , currentItemY, textColor, menuTextShadowColor, 1);
                    itemTextX = pointerX + fmMenu.stringWidth(">") + 5;
                }
            } else {
                textColor = menuTextColor_Normal;
                // Căn chỉnh text cho các mục không được chọn
                if (menuCursorImage != null) {
                    itemTextX = pointerX + menuCursorImage.getWidth() + 5;
                } else {
                    // Để trống không gian tương đương dấu ">" cho các mục không được chọn để text thẳng hàng
                    itemTextX = pointerX + fmMenu.stringWidth(">") + 5;
                }
            }
            drawTextWithShadow(g2, text, itemTextX, currentItemY, textColor, menuTextShadowColor, 1);
            currentItemY += menuItemHeight;
        }

        int avatarsDrawY = menuY + (menuHeight - characterDisplaySize) / 2;
        int avatarBlockDrawStartX = menuX + menuPadding + textBlockWidth + spaceBetweenTextAndAvatars;

        if (titlePlayerAvatar != null) {
            g2.drawImage(titlePlayerAvatar, avatarBlockDrawStartX, avatarsDrawY, characterDisplaySize, characterDisplaySize, null);
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
        drawTextWithShadow(g2, text, x, y, Color.WHITE, new Color(0,0,0,180), 2);
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
        int y2 = gp.getScreenHeight() / 2 + gp.getTileSize() /2 + yOffset;
        drawTextWithShadow(g2, text2, x2, y2, menuTextColor_Selected, new Color(0,0,0,180), 2);

        g2.setFont(pixelFont_Small);
        String text3 = "Press ESC to Exit";
        int x3 = getXforCenteredText(text3, g2, g2.getFont());
        int y3 = gp.getScreenHeight() / 2 + gp.getTileSize() * 2 + yOffset;
        drawTextWithShadow(g2, text3, x3, y3, Color.LIGHT_GRAY, menuTextShadowColor, 1);
    }

    public void drawDialogueScreen(Graphics2D g2) {
        int x = gp.getTileSize();
        int y = gp.getScreenHeight() - gp.getTileSize() * 4 - gp.getTileSize()/2;
        int width = gp.getScreenWidth() - (gp.getTileSize() * 2);
        int height = gp.getTileSize() * 4;
        drawSubWindow(g2, x, y, width, height, menuBoxBgColor_New, menuBoxBorderColor_New, 15,15,3);

        g2.setFont(pixelFont_Small);
        g2.setColor(Color.white);

        int dialogueX = x + gp.getTileSize() / 2;
        FontMetrics fm = g2.getFontMetrics();
        int currentY = y + fm.getAscent() + gp.getTileSize()/3;
        int lineHeight = fm.getHeight() + 2;
        int availableWidth = width - gp.getTileSize();

        if (currentDialogue != null && !currentDialogue.isEmpty()) {
            for (String paragraph : currentDialogue.split("\n")) {
                String remainingParagraph = paragraph;
                while (!remainingParagraph.isEmpty()) {
                    String lineToDraw = remainingParagraph;
                    if (fm.stringWidth(lineToDraw) > availableWidth) {
                        int wrapIndex = -1;
                        for (int i = lineToDraw.length() - 1; i >= 0; i--) {
                            if (Character.isWhitespace(lineToDraw.charAt(i))) {
                                String sub = lineToDraw.substring(0, i);
                                if (fm.stringWidth(sub) <= availableWidth) {
                                    wrapIndex = i;
                                    break;
                                }
                            }
                        }
                        if (wrapIndex != -1) {
                            lineToDraw = remainingParagraph.substring(0, wrapIndex);
                            remainingParagraph = remainingParagraph.substring(wrapIndex).trim();
                        } else {
                            int charIndex = 0;
                            for (int i = 1; i <= remainingParagraph.length(); i++) {
                                String sub = remainingParagraph.substring(0, i);
                                if (fm.stringWidth(sub) > availableWidth) {
                                    charIndex = i - 1;
                                    break;
                                }
                                charIndex = i;
                            }
                            lineToDraw = remainingParagraph.substring(0, charIndex);
                            remainingParagraph = remainingParagraph.substring(charIndex).trim();
                        }
                    } else {
                        remainingParagraph = "";
                    }

                    if (currentY < y + height - gp.getTileSize() / 2) {
                        drawTextWithShadow(g2, lineToDraw, dialogueX, currentY, Color.WHITE, menuTextShadowColor,1);
                        currentY += lineHeight;
                    } else {
                        break;
                    }
                }
                if (currentY > y + height - gp.getTileSize() / 2) break;
            }
        }

        if (gp.gameState == gp.dialogueState) {
            g2.setFont(pixelFont_XSmall);
            String continueText = "Press ENTER...";
            FontMetrics fmContinue = g2.getFontMetrics();
            int continueX = x + width - fmContinue.stringWidth(continueText) - gp.getTileSize() / 2;
            int continueY = y + height - fmContinue.getHeight()/2 - gp.getTileSize()/4;
            drawTextWithShadow(g2, continueText, continueX, continueY, Color.LIGHT_GRAY, menuTextShadowColor,1);
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
        if (text == null || g2 == null ) return;
        Font currentFont = g2.getFont();
        if(currentFont == null) {
            g2.setFont(pixelFont_Small != null ? pixelFont_Small : new Font("Arial", Font.PLAIN, 12));
        }

        g2.setColor(shadowColor);
        g2.drawString(text, x + shadowOffset, y + shadowOffset);
        g2.setColor(textColor);
        g2.drawString(text, x, y);
    }

    public int getXforCenteredText(String text, Graphics2D g2, Font font) {
        if (text == null || g2 == null ) return 0;
        Font currentFont = font != null ? font : g2.getFont();
        if (currentFont == null) { currentFont = basePixelFont; }
        FontMetrics fm = g2.getFontMetrics(currentFont);
        return gp.getScreenWidth() / 2 - fm.stringWidth(text) / 2;
    }

    public int getXforCenteredTextInBox(String text, Graphics2D g2, Font font, int boxX, int boxWidth) {
        if (text == null || g2 == null ) return 0;
        Font currentFont = font != null ? font : g2.getFont();
        if (currentFont == null) { currentFont = basePixelFont; }
        FontMetrics fm = g2.getFontMetrics(currentFont);
        return boxX + (boxWidth - fm.stringWidth(text)) / 2;
    }
}