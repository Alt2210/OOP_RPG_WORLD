package main.ui;

import character.role.Player;
import item.Item;
import item.ItemStack;
import item.Inventory;
import main.GamePanel;

import java.awt.*;
import java.util.List;

public class TradeUI extends UI {

    public TradeUI(GamePanel gp) {
        super(gp);
    }

    @Override
    public void draw(Graphics2D g2) {
        if (gp.getCurrentMerchant() == null) return;

        // --- Nền Mờ ---
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        // --- Khung Kho đồ Player (Bên trái) ---
        int playerFrameX = gp.getTileSize();
        int frameY = gp.getTileSize();
        int frameWidth = gp.getTileSize() * 6;
        int playerFrameHeight = gp.getTileSize() * 6;
        drawSubWindow(g2, playerFrameX, frameY, frameWidth, playerFrameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(pixelFont_Small);
        g2.drawString("Your Inventory", playerFrameX + 20, frameY + 35);

        // --- Khung Hàng hóa của Thương nhân (Bên phải) ---
        int merchantFrameX = gp.getTileSize() * 9;
        int merchantFrameHeight = gp.getTileSize() * 6; // Cao bằng khung người chơi
        drawSubWindow(g2, merchantFrameX, frameY, frameWidth, merchantFrameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(pixelFont_Small);
        g2.drawString(gp.getCurrentMerchant().getName() + "'s Wares", merchantFrameX + 20, frameY + 35);

        // --- Vẽ các ô vật phẩm (Slots) ---
        drawSlots(g2, playerFrameX, frameY, gp.getPlayer().getInventory());
        drawSlots(g2, merchantFrameX, frameY, gp.getCurrentMerchant().getInventory());

        // --- Vẽ Con trỏ (Cursor) ---
        drawCursor(g2, playerFrameX, merchantFrameX, playerFrameHeight);

        // --- Khung mô tả và giá ---
        drawDescriptionAndPriceBox(g2);

        // --- Khung hiển thị tiền của người chơi ---
        drawPlayerCoinBox(g2);
    }

    private void drawSlots(Graphics2D g2, int frameX, int frameY, Inventory inventory) {
        final int slotXStart = frameX + 20;
        final int slotYStart = frameY + 50;
        int slotX = slotXStart;
        int slotY = slotYStart;
        final int slotSize = gp.getTileSize() + 3;

        for (int i = 0; i < inventory.getCapacity(); i++) {
            if (i < inventory.getItemStack()) {
                ItemStack stack = inventory.getItemStack(i);
                Item item = stack.getItem();

                if (item != null && item.getItp() != null) {
                    g2.drawImage(item.getItp().getCurFrame(), slotX, slotY, gp.getTileSize(), gp.getTileSize(), null);

                    // Hiển thị giá mua trên đồ của thương nhân
                    if (inventory == gp.getCurrentMerchant().getInventory()) {
                        g2.setFont(pixelFont_XSmall.deriveFont(12f));
                        g2.setColor(Color.YELLOW);
                        String priceText = String.valueOf(item.getBuyPrice());
                        int priceX = slotX + gp.getTileSize() - g2.getFontMetrics().stringWidth(priceText) - 2;
                        int priceY = slotY + gp.getTileSize() - 2;
                        drawTextWithShadow(g2, priceText, priceX, priceY, Color.YELLOW, Color.BLACK, 1);
                    }
                }
            }
            slotX += slotSize;
            if ((i + 1) % 5 == 0) {
                slotX = slotXStart;
                slotY += slotSize;
            }
        }
    }

    private void drawCursor(Graphics2D g2, int playerPanelX, int merchantPanelX, int panelHeight) {
        int slotSize = gp.getTileSize() + 3;
        int slotYStart = gp.getTileSize() + 50;

        int cursorX = 0;
        int cursorY = slotYStart + (slotSize * getSlotRow());
        int cursorWidth = gp.getTileSize();
        int cursorHeight = gp.getTileSize();

        int maxRowPlayer = 3; // 4 hàng item
        int maxRowMerchant = 3; // Giả sử thương nhân cũng có 4 hàng

        // Logic chuyển panel
        if (getCommandNum() == 0) { // Player panel
            cursorX = (playerPanelX + 20) + (slotSize * getSlotCol());
        } else { // Merchant panel
            cursorX = (merchantPanelX + 20) + (slotSize * getSlotCol());
            // Đảm bảo con trỏ không ở hàng không có item
            if (getSlotRow() > maxRowMerchant) setSlotRow(maxRowMerchant);
        }
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);
    }

    private void drawDescriptionAndPriceBox(Graphics2D g2) {
        int dFrameX = gp.getTileSize();
        int dFrameY = gp.getTileSize() * 7 + 10;
        int dFrameWidth = gp.getScreenWidth() - (gp.getTileSize() * 2);
        int dFrameHeight = gp.getTileSize() * 3;
        drawSubWindow(g2, dFrameX, dFrameY, dFrameWidth, dFrameHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);

        int textX = dFrameX + 20;
        int textY = dFrameY + 30;
        g2.setFont(pixelFont_XSmall);
        g2.setColor(Color.WHITE);

        int itemIndex = getItemIndexOnSlot();
        ItemStack selectedStack = null;
        boolean isSelling = false;

        if (getCommandNum() == 0) { // Player's inventory
            if (itemIndex < gp.getPlayer().getInventory().getItemStack()) {
                selectedStack = gp.getPlayer().getInventory().getItemStack(itemIndex);
                isSelling = true;
            }
        } else { // Merchant's inventory
            if (gp.getCurrentMerchant() != null && itemIndex < gp.getCurrentMerchant().getInventory().getItemStack()) {
                selectedStack = gp.getCurrentMerchant().getInventory().getItemStack(itemIndex);
            }
        }

        if (selectedStack != null) {
            Item selectedItem = selectedStack.getItem();
            if (selectedItem != null && selectedItem.getDescription() != null) {
                List<String> lines = wrapText(selectedItem.getDescription(), g2.getFontMetrics(), dFrameWidth - 40);
                lines.add("Quantity: " + selectedStack.getQuantity());
                for (String line : lines) {
                    g2.drawString(line, textX, textY);
                    textY += 24;
                }
                // Hiển thị giá bán hoặc mua
                int price = isSelling ? selectedItem.getSellPrice() : selectedItem.getBuyPrice();
                String priceText = (isSelling ? "Sell Price: " : "Buy Price: ") + price;
                g2.setColor(Color.YELLOW);
                g2.drawString(priceText, textX, textY + 20);
            }
        }
    }

    private void drawPlayerCoinBox(Graphics2D g2) {
        int boxX = gp.getTileSize() * 9;
        int boxY = gp.getTileSize() * 10;
        int boxWidth = gp.getTileSize() * 6;
        int boxHeight = gp.getTileSize();
        drawSubWindow(g2, boxX, boxY, boxWidth, boxHeight, menuBoxBgColor_New, menuBoxBorderColor_New, 25, 25, 4);

        g2.setColor(Color.YELLOW);
        g2.setFont(pixelFont_Small);
        String coinText = "Your Coins: " + gp.getPlayer().getCurrentCoin();
        int textX = getXforCenteredTextInBox(coinText, g2, g2.getFont(), boxX, boxWidth);
        int textY = boxY + 35;
        drawTextWithShadow(g2, coinText, textX, textY, Color.YELLOW, Color.BLACK, 2);
    }
}