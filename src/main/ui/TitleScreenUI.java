package main.ui;

import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TitleScreenUI extends UI {
    // Các ảnh chỉ dành cho màn hình này
    private BufferedImage titleBgImage;
    private BufferedImage titlePrincessAvatar;
    private BufferedImage sodierAvatar;
    private BufferedImage menuBoxImage;
    private BufferedImage menuCursorImage;

    public TitleScreenUI(GamePanel gp) {
        super(gp);
        loadImages();
    }

    private void loadImages() {
        try {
            titleBgImage = ImageIO.read(getClass().getResourceAsStream("/ui/title_background.png"));
            titlePrincessAvatar = ImageIO.read(getClass().getResourceAsStream("/npc/princess_walkleft1.png"));
            sodierAvatar = ImageIO.read(getClass().getResourceAsStream("/player/sodier_walkleft1.png"));
            // Các ảnh này có thể null, logic vẽ sẽ xử lý
            // menuBoxImage = ImageIO.read(getClass().getResourceAsStream("/ui/menu_box_pixel.png"));
            // menuCursorImage = ImageIO.read(getClass().getResourceAsStream("/ui/menu_cursor_pixel.png"));
        } catch (Exception e) {
            System.err.println("UI: Lỗi khi tải ảnh cho Title Screen.");
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // SAO CHÉP Y HỆT logic từ phương thức drawTitleScreen() cũ của bạn
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

            if (getCommandNum() == i) {
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
}