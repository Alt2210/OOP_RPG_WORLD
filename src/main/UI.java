package main; // Hoặc package của bạn

import character.Player;
// import worldObject.pickableObject.OBJ_Key;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList; // Thêm import này
import java.util.List;    // Thêm import này

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
        } else if (gp.gameState == gp.victoryEndState) {
            drawEndGameScreen(g2);
        } else if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen(g2);
        } else if (gp.gameState == gp.gameOverState){
            drawGameOverScreen(g2);
        }
    }

    private void drawPlayUI(Graphics2D g2) {
        g2.setFont(pixelFont_Small); // Hoặc font bạn muốn cho các thông tin chung
        g2.setColor(Color.white);

        // ... (code vẽ key và time như cũ) ...
        int currentTileSize = gp.getTileSize();
        Player currentPlayer = gp.getPlayer();
        if (keyImage != null && currentPlayer != null) {
            g2.drawImage(keyImage, currentTileSize / 2, currentTileSize / 2, currentTileSize, currentTileSize, null);
            drawTextWithShadow(g2, "x " + currentPlayer.getHasKey(), currentTileSize / 2 + currentTileSize + 10, currentTileSize / 2 + currentTileSize - 5, Color.WHITE, menuTextShadowColor, 1);
        }
        // Vẽ thời gian chơi
        if (gp.gameState == gp.playState) {
            playtime += (double) 1.0 / gp.getFPS();
        }
        drawTextWithShadow(g2, "Time: " + dFormat.format(playtime), gp.getTileSize() * 10, currentTileSize, Color.WHITE, menuTextShadowColor, 1);

        // Vẽ thanh Mana (MP Bar)
        if (currentPlayer != null) {
            int mpBarX = currentTileSize / 2;
            // Đặt thanh Mana ngay dưới thanh Máu (đã được vẽ bởi Character.drawHealthBar -> Player.draw -> Player.drawHealthBar)
            // Giả sử thanh máu của player vẽ tại player.screenY - healthBarHeight - 5
            // Thanh mana sẽ ở dưới đó một chút hoặc ở vị trí khác tùy bạn.
            // Ví dụ: đặt ngay dưới thông tin chìa khóa
            int mpBarY = currentTileSize / 2 + currentTileSize + 10; // Vị trí Y cho thanh MP
            int barOutlineWidth = currentTileSize * 2 + 2; // Thêm viền nhỏ
            int barOutlineHeight = 12 + 2;
            int barWidth = currentTileSize * 2;
            int barHeight = 12;

            double manaPercent = 0;
            if (currentPlayer.getMaxMana() > 0) { // Tránh chia cho 0
                manaPercent = (double) currentPlayer.getCurrentMana() / currentPlayer.getMaxMana();
            }
            int currentManaBarWidth = (int) (barWidth * manaPercent);

            // Vẽ viền cho thanh mana (tùy chọn)
            g2.setColor(Color.BLACK);
            g2.fillRoundRect(mpBarX -1, mpBarY -1 , barOutlineWidth, barOutlineHeight, 5, 5);


            // Vẽ nền cho phần mana đã mất
            g2.setColor(new Color(50, 50, 100)); // Màu nền tối cho mana
            g2.fillRoundRect(mpBarX, mpBarY, barWidth, barHeight, 5, 5);

            // Vẽ phần mana hiện có
            g2.setColor(new Color(0, 100, 255)); // Màu xanh dương cho mana
            if (currentManaBarWidth > 0) { // Chỉ vẽ nếu có mana
                g2.fillRoundRect(mpBarX, mpBarY, currentManaBarWidth, barHeight, 5, 5);
            }

            // Vẽ text MP (ví dụ: "MP: 30/50")
            g2.setFont(pixelFont_XSmall); // Font nhỏ cho text
            g2.setColor(Color.WHITE);
            String mpText = currentPlayer.getCurrentMana() + "/" + currentPlayer.getMaxMana();
            FontMetrics fm = g2.getFontMetrics(pixelFont_XSmall);
            int textX = mpBarX + barWidth + 5; // Cách thanh MP một chút
            int textY = mpBarY + fm.getAscent() - (fm.getHeight() - barHeight)/2 +1; // Căn giữa text với thanh MP
            drawTextWithShadow(g2, mpText, textX, textY, Color.WHITE, menuTextShadowColor, 1);
        }
        // Hiển thị tin nhắn (messageOn) với word wrapping
        if (messageOn) {
            // 1. Định nghĩa kích thước và vị trí cho ô chứa message
            // Bạn đã có sẵn logic này, rất tốt!
            int messageBoxWidth = gp.getScreenWidth() - gp.getTileSize() * 6; //
            int messageBoxHeight = gp.getTileSize() * 2; //
            int messageBoxX = gp.getScreenWidth() / 2 - messageBoxWidth / 2; //
            int messageBoxY = gp.getScreenHeight() - gp.getTileSize() * 3 - gp.getTileSize() / 2; //

            // 2. Vẽ ô chứa (subWindow) cho message
            // Bạn đã có: drawSubWindow(g2, messageBoxX, messageBoxY, messageBoxWidth, messageBoxHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 15,15,3);
            // Đảm bảo màu sắc và kiểu dáng phù hợp
            Color messageBgColor = new Color(0, 0, 0, 180); // Nền đen mờ ví dụ
            Color messageBorderColor = Color.WHITE;
            drawSubWindow(g2, messageBoxX, messageBoxY, messageBoxWidth, messageBoxHeight,
                    messageBgColor, messageBorderColor, 20, 20, 2); // Điều chỉnh bo góc, độ dày viền


            // 3. Xử lý và vẽ text message với word wrapping
            Font messageFont = pixelFont_Medium; // Hoặc pixelFont_Small, tùy bạn chọn
            g2.setFont(messageFont);
            g2.setColor(Color.WHITE);

            int textPaddingX = gp.getTileSize() / 3; // Khoảng đệm ngang cho text bên trong message box
            int textPaddingY = gp.getTileSize() / 3; // Khoảng đệm trên cho text
            int availableTextWidth = messageBoxWidth - (textPaddingX * 2); // Chiều rộng thực cho text
            int currentTextY = messageBoxY + textPaddingY + g2.getFontMetrics(messageFont).getAscent(); // Vị trí Y cho dòng đầu tiên
            int lineHeight = g2.getFontMetrics(messageFont).getHeight();

            if (message != null && !message.isEmpty()) {
                List<String> wrappedLines = wrapText(message, g2.getFontMetrics(messageFont), availableTextWidth);

                for (String lineToDraw : wrappedLines) {
                    // Chỉ vẽ nếu còn không gian trong messageBoxHeight
                    if (currentTextY < messageBoxY + messageBoxHeight - textPaddingY) {
                        // Căn giữa text trong messageBox (nếu muốn)
                        // int lineX = messageBoxX + (messageBoxWidth - g2.getFontMetrics(messageFont).stringWidth(lineToDraw)) / 2;
                        // Hoặc căn trái với padding
                        int lineX = messageBoxX + textPaddingX;
                        drawTextWithShadow(g2, lineToDraw, lineX, currentTextY, Color.WHITE, menuTextShadowColor, 1);
                        currentTextY += lineHeight;
                    } else {
                        break; // Ngừng vẽ nếu vượt quá chiều cao box
                    }
                }
            }

            // messageCounter vẫn hoạt động như cũ
            messageCounter++;
            if (messageCounter > 120) { // Thời gian hiển thị message (ví dụ: 2 giây)
                messageCounter = 0;
                messageOn = false;
            }
        }
    }

    // Phương thức wrapText (đảm bảo bạn đã có nó trong lớp UI)
    private List<String> wrapText(String text, FontMetrics fm, int availableWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty() || fm == null || availableWidth <= 0) {
            lines.add(text == null ? "" : text);
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            int wordWidth = fm.stringWidth(word + " ");
            if (currentLine.length() > 0 && fm.stringWidth(currentLine.toString()) + wordWidth > availableWidth) {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
            }

            // Xử lý từ đơn lẻ dài hơn availableWidth
            if (fm.stringWidth(word) > availableWidth && currentLine.length() == 0) {
                String remainingWord = word;
                while (fm.stringWidth(remainingWord) > availableWidth) {
                    int breakPoint = 0;
                    for (int i = 1; i <= remainingWord.length(); i++) {
                        if (fm.stringWidth(remainingWord.substring(0, i)) > availableWidth) {
                            breakPoint = i - 1;
                            break;
                        }
                        breakPoint = i;
                    }
                    if (breakPoint > 0) {
                        lines.add(remainingWord.substring(0, breakPoint));
                        remainingWord = remainingWord.substring(breakPoint);
                    } else {
                        lines.add(remainingWord); // Thêm cả từ (sẽ bị tràn nếu không xử lý được nữa)
                        remainingWord = "";
                        break;
                    }
                }
                if (!remainingWord.isEmpty()) {
                    currentLine.append(remainingWord); // Nối phần còn lại của từ (nếu có) vào dòng hiện tại
                    // (hoặc bắt đầu dòng mới nếu currentLine rỗng)
                    if (fm.stringWidth(remainingWord) > 0) currentLine.append(" ");
                }
            } else {
                currentLine.append(word).append(" ");
            }
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

        g2.setFont(pixelFont_Small); // Sử dụng font nhỏ hơn
        g2.setColor(Color.WHITE); // Hoặc Color.LIGHT_GRAY
        String textEnter = "Press ENTER to return";
        int xEnter = getXforCenteredText(textEnter, g2, g2.getFont());
        // Đặt vị trí Y cho dòng này phía trên dòng "Press ESC to Exit"
        int yEnter = gp.getScreenHeight() / 2 + gp.getTileSize() * 2 + yOffset - gp.getTileSize()/2; // Dịch lên một chút so với text3
        drawTextWithShadow(g2, textEnter, xEnter, yEnter, Color.WHITE, menuTextShadowColor, 1);

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
    public void drawGameOverScreen(Graphics2D g2) {
        // 1. Vẽ một lớp phủ màu tối (hoặc đỏ mờ) để làm nổi bật text "GAME OVER"
        g2.setColor(new Color(0, 0, 0, 200)); // Màu đen với độ trong suốt alpha 200
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        String gameOverText;
        int x, y;
        // 2. Vẽ chữ "GAME OVER" lớn ở giữa
        g2.setFont(pixelFont_Large.deriveFont(Font.BOLD, 70F)); // Sử dụng font pixel lớn, đậm
        g2.setColor(new Color(180, 0, 0)); // Màu đỏ đậm cho "GAME OVER"
        gameOverText = "GAME OVER";
        x = getXforCenteredText(gameOverText, g2, g2.getFont());
        y = gp.getScreenHeight() / 2 - gp.getTileSize(); // Hơi dịch lên trên
        drawTextWithShadow(g2, gameOverText, x, y, new Color(180,0,0), Color.BLACK, 3); // Thêm bóng đen

        // 4. Hướng dẫn người chơi
        g2.setFont(pixelFont_Small); // Font nhỏ hơn cho hướng dẫn
        g2.setColor(Color.WHITE);

        String continueText = "Press ENTER to return to Title Screen";
        x = getXforCenteredText(continueText, g2, g2.getFont());
        y = gp.getScreenHeight() / 2 + gp.getTileSize() * 2;
        drawTextWithShadow(g2, continueText, x, y, Color.WHITE, menuTextShadowColor, 1);

        String exitText = "Press ESC to Exit Game";
        x = getXforCenteredText(exitText, g2, g2.getFont());
        y += gp.getTileSize(); // Dịch xuống một chút
        drawTextWithShadow(g2, exitText, x, y, Color.WHITE, menuTextShadowColor, 1);
    }
}