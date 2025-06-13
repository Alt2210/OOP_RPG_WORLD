package main.ui;

import character.role.Player;
import item.Item;
import item.ItemStack;
import main.GamePanel;
import main.ui.UI;

import java.awt.*;
import java.util.List;

/**
 * Lớp này chịu trách nhiệm vẽ giao diện màn hình Túi đồ & Trạng thái nhân vật.
 * Kế thừa các thuộc tính và phương thức tiện ích từ lớp UI trừu tượng.
 */
public class InventoryUI extends UI {

    public InventoryUI(GamePanel gp) {
        super(gp);
    }

    @Override
    public void draw(Graphics2D g2) {
        // Vẽ toàn bộ màn hình Inventory, được chia thành hai phần chính.
        drawStatusPanel(g2);
        drawInventoryPanel(g2);
    }

    /**
     * Vẽ khung trạng thái nhân vật ở bên trái.
     * Sao chép logic từ file UI.java gốc.
     */
    private void drawStatusPanel(Graphics2D g2) {
        int frameX = gp.getTileSize();
        int frameY = gp.getTileSize();
        int frameWidth = gp.getTileSize() * 6;
        int frameHeight = gp.getTileSize() * 10;
        drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);

        g2.setColor(Color.WHITE);
        g2.setFont(pixelFont_Small);

        int textX = frameX + 20;
        int textY = frameY + gp.getTileSize();
        final int lineHeight = 45;

        g2.drawString("Level", textX, textY); textY += lineHeight;
        g2.drawString("Coin", textX, textY); textY += lineHeight;
        g2.drawString("Health", textX, textY); textY += lineHeight;
        g2.drawString("Mana", textX, textY); textY += lineHeight;
        g2.drawString("Stamina", textX, textY); textY += lineHeight;
        g2.drawString("Attack", textX, textY); textY += lineHeight;
        g2.drawString("Defense", textX, textY); textY += lineHeight;
        g2.drawString("Exp", textX, textY); textY += lineHeight;
        g2.drawString("Next Level", textX, textY); textY += lineHeight;
        g2.drawString("Weapon", textX, textY);

        int tailX = (frameX + frameWidth) - 30;
        textY = frameY + gp.getTileSize(); // Reset Y
        Player player = gp.getPlayer();

        String value = String.valueOf(player.getLevel());
        g2.drawString(value, tailX - g2.getFontMetrics().stringWidth(value), textY); textY += lineHeight;

        value = String.valueOf(player.getCurrentCoin()) ;
        g2.drawString(value, tailX - g2.getFontMetrics().stringWidth(value), textY); textY += lineHeight;

        value = player.getCurrentHealth() + "/" + player.getMaxHealth();
        g2.drawString(value, tailX - g2.getFontMetrics().stringWidth(value), textY); textY += lineHeight;

        value = player.getCurrentMana() + "/" + player.getMaxMana();
        g2.drawString(value, tailX - g2.getFontMetrics().stringWidth(value), textY); textY += lineHeight;

        value = player.getCurrentStamina() + "/" + player.getMaxStamina();
        g2.drawString(value, tailX - g2.getFontMetrics().stringWidth(value), textY); textY += lineHeight;

        value = String.valueOf(player.getAttack());
        g2.drawString(value, tailX - g2.getFontMetrics().stringWidth(value), textY); textY += lineHeight;

        value = String.valueOf(player.getDefense());
        g2.drawString(value, tailX - g2.getFontMetrics().stringWidth(value), textY); textY += lineHeight;

        value = String.valueOf(player.getCurrentExp());
        g2.drawString(value, tailX - g2.getFontMetrics().stringWidth(value), textY); textY += lineHeight;

        value = String.valueOf(player.getExpToNextLevel());
        g2.drawString(value, tailX - g2.getFontMetrics().stringWidth(value), textY); textY += lineHeight;

        if (player.getCurrentWeapon() != null) {
            int weaponIconSize = gp.getTileSize() + 10;
            int iconX = tailX - weaponIconSize;
            int iconY = textY - (weaponIconSize / 2) - 8;
            g2.drawImage(player.getCurrentWeapon().getItp().getCurFrame(), iconX, iconY, weaponIconSize, weaponIconSize, null);
        } else {
            g2.drawString("None", tailX - g2.getFontMetrics().stringWidth("None"), textY);
        }
    }

    /**
     * Vẽ khung túi đồ và mô tả vật phẩm ở bên phải.
     * Sao chép logic từ file UI.java gốc.
     */
    private void drawInventoryPanel(Graphics2D g2) {
        int frameX = gp.getTileSize() * 8;
        int frameY = gp.getTileSize();
        int frameWidth = gp.getTileSize() * 6;
        int frameHeight = gp.getTileSize() * 5;
        drawSubWindow(g2, frameX, frameY, frameWidth, frameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);

        // Vẽ các ô vật phẩm (Slots)
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotsSize = gp.getTileSize() + 3;

        for (int i = 0; i < gp.getPlayer().getInventory().getItemStack(); i++) {
            ItemStack currentStack = gp.getPlayer().getInventory().getItemStack(i);
            if (currentStack != null && currentStack.getItem() != null && currentStack.getItem().getItp() != null) {
                g2.drawImage(currentStack.getItem().getItp().getCurFrame(), slotX, slotY, slotsSize, slotsSize, null);
                if (gp.getPlayer().getCurrentWeapon() != null && currentStack.getItem() == gp.getPlayer().getCurrentWeapon()) {
                    g2.setColor(new Color(240, 190, 90, 150));
                    g2.fillRoundRect(slotX, slotY, gp.getTileSize(), gp.getTileSize(), 10, 10);
                }
            }
            slotX += slotsSize;
            if (i == 4 || i == 9 || i == 14) {
                slotX = slotXstart;
                slotY += slotsSize;
            }
        }

        // Vẽ con trỏ (Cursor)
        int cursorX = slotXstart + (slotsSize * getSlotCol());
        int cursorY = slotYstart + (slotsSize * getSlotRow());
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cursorX, cursorY, gp.getTileSize(), gp.getTileSize(), 10, 10);

        // Vẽ khung mô tả (Description Frame)
        int dFrameX = frameX;
        int dFrameY = frameY + frameHeight + 10;
        int dFrameWidth = frameWidth;
        int dFrameHeight = gp.getTileSize() * 3;
        drawSubWindow(g2, dFrameX, dFrameY, dFrameWidth, dFrameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);

        // Vẽ chữ mô tả
        int descTextX = dFrameX + 20;
        int descTextY = dFrameY + 30;
        g2.setFont(pixelFont_XSmall);
        g2.setColor(Color.WHITE);

        int itemIndex = getItemIndexOnSlot();
        if (itemIndex < gp.getPlayer().getInventory().getItemStack()) {
            ItemStack selectedStack = gp.getPlayer().getInventory().getItemStack(itemIndex);
            if (selectedStack != null) {
                Item selectedItem = selectedStack.getItem();
                if (selectedItem != null && selectedItem.getDescription() != null) {
                    for (String line : selectedItem.getDescription().split("\n")) {
                        g2.drawString(line, descTextX, descTextY);
                        descTextY += 30;
                    }
                    String msg_quantity = "Quantity: " + selectedStack.getQuantity();
                    g2.drawString(msg_quantity, descTextX, descTextY);
                }
            }
        }
    }
}