package main.ui;

import item.Item;
import item.ItemStack;
import item.Inventory;
import main.GamePanel;
import main.ui.UI;

import java.awt.*;
import java.util.List;

/**
 * Lớp này chịu trách nhiệm vẽ giao diện khi người chơi mở rương đồ (Chest).
 * Nó hiển thị cả túi đồ của người chơi và túi đồ của rương.
 */
public class ChestUI extends UI {

    public ChestUI(GamePanel gp) {
        super(gp);
    }

    @Override
    public void draw(Graphics2D g2) {
        // --- Nền Mờ ---
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        // --- Khung Kho đồ Player (Bên trái) ---
        int playerFrameX = gp.getTileSize();
        int frameY = gp.getTileSize();
        int frameWidth = gp.getTileSize() * 6;
        int playerFrameHeight = gp.getTileSize() * 6; // Chiều cao cho túi đồ người chơi
        drawSubWindow(g2, playerFrameX, frameY, frameWidth, playerFrameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(pixelFont_Small);
        g2.drawString("Your Items", playerFrameX + 20, frameY + 35);

        // --- Khung Kho đồ Rương (Bên phải) ---
        int chestFrameX = gp.getTileSize() * 9;
        int chestFrameHeight = gp.getTileSize() * 4; // Chiều cao cho túi đồ của rương
        drawSubWindow(g2, chestFrameX, frameY, frameWidth, chestFrameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(pixelFont_Small);
        g2.drawString("Chest", chestFrameX + 20, frameY + 35);

        // --- Vẽ các ô vật phẩm (Slots) ---
        drawSlots(g2, playerFrameX, frameY, gp.getPlayer().getInventory()); // Vẽ túi đồ người chơi
        if (gp.currentChest != null) {
            drawSlots(g2, chestFrameX, frameY, gp.currentChest.getInventory()); // Vẽ túi đồ của rương
        }

        // --- Vẽ Con trỏ (Cursor) ---
        drawCursor(g2, playerFrameX, chestFrameX);

        // --- Khung mô tả ---
        drawDescriptionBox(g2, playerFrameX, frameWidth, chestFrameX);
    }

    /**
     * Phương thức tiện ích để vẽ các ô vật phẩm của một túi đồ.
     * Logic được sao chép từ drawInventoryScreen trong file UI gốc.
     */
    private void drawSlots(Graphics2D g2, int frameX, int frameY, Inventory inventory) {
        final int slotXStart = frameX + 20;
        final int slotYStart = frameY + 50;
        int slotX = slotXStart;
        int slotY = slotYStart;
        final int slotSize = gp.getTileSize() + 3;

        for (int i = 0; i < inventory.getCapacity(); i++) {
            if (i < inventory.getItemStack()) {
                ItemStack stack = inventory.getItemStack(i);
                if (stack != null && stack.getItem() != null && stack.getItem().getItp() != null) {
                    g2.drawImage(stack.getItem().getItp().getCurFrame(), slotX, slotY, gp.getTileSize(), gp.getTileSize(), null);

                    // Logic đặc biệt: Highlight vũ khí đang trang bị (chỉ áp dụng cho túi đồ của Player)
                    if (inventory == gp.getPlayer().getInventory() && gp.getPlayer().getCurrentWeapon() == stack.getItem()) {
                        g2.setColor(new Color(240, 190, 90, 150));
                        g2.fillRoundRect(slotX, slotY, gp.getTileSize(), gp.getTileSize(), 10, 10);
                    }
                }
            }
            slotX += slotSize;
            if ((i + 1) % 5 == 0) { // Giả sử 5 cột một hàng
                slotX = slotXStart;
                slotY += slotSize;
            }
        }
    }

    /**
     * Vẽ con trỏ dựa trên panel đang được chọn (commandNum).
     */
    private void drawCursor(Graphics2D g2, int playerPanelX, int chestPanelX) {
        int slotSize = gp.getTileSize() + 3;
        int slotYStart = gp.getTileSize() + 50;

        int cursorX = 0;
        int cursorY = slotYStart + (slotSize * getSlotRow());
        int cursorWidth = gp.getTileSize();
        int cursorHeight = gp.getTileSize();

        if (getCommandNum() == 0) { // Player panel
            cursorX = (playerPanelX + 20) + (slotSize * getSlotCol());
        } else { // Chest panel
            cursorX = (chestPanelX + 20) + (slotSize * getSlotCol());
        }
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);
    }

    /**
     * Vẽ khung mô tả cho vật phẩm được chọn.
     */
    private void drawDescriptionBox(Graphics2D g2, int playerFrameX, int playerFrameWidth, int chestFrameX) {
        int dFrameY = gp.getTileSize() * 7 + 10; // Đặt dưới khung lớn nhất
        int dFrameWidth = playerFrameWidth * 2 + (chestFrameX - (playerFrameX + playerFrameWidth));
        int dFrameHeight = gp.getTileSize() * 3;
        drawSubWindow(g2, playerFrameX, dFrameY, dFrameWidth, dFrameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);

        int textX = playerFrameX + 20;
        int textY = dFrameY + 30;
        g2.setFont(pixelFont_XSmall);
        g2.setColor(Color.WHITE);

        int itemIndex = getItemIndexOnSlot();
        ItemStack selectedStack = null;

        if (getCommandNum() == 0) { // Lấy item từ kho player
            if (itemIndex < gp.getPlayer().getInventory().getItemStack()) {
                selectedStack = gp.getPlayer().getInventory().getItemStack(itemIndex);
            }
        } else { // Lấy item từ kho rương
            if (gp.currentChest != null && itemIndex < gp.currentChest.getInventory().getItemStack()) {
                selectedStack = gp.currentChest.getInventory().getItemStack(itemIndex);
            }
        }

        if (selectedStack != null) {
            Item selectedItem = selectedStack.getItem();
            if (selectedItem != null && selectedItem.getDescription() != null) {
                // Tự động xuống dòng cho mô tả
                List<String> lines = wrapText(selectedItem.getDescription(), g2.getFontMetrics(), dFrameWidth - 40);
                for (String line : lines) {
                    g2.drawString(line, textX, textY);
                    textY += 24;
                }
                // Vẽ số lượng
                String msg_quantity = "Quantity: " + selectedStack.getQuantity();
                g2.drawString(msg_quantity, textX, textY);
            }
        }
    }
}