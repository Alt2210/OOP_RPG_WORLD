package character.sideCharacter;

import character.role.Player;
import dialogue.Dialogue;
import item.Inventory;
import item.ItemStack;
import item.itemConsumable.Item_HealthPotion;
import item.itemConsumable.Item_ManaPotion;
import item.itemEquippable.Item_Book;
import item.itemEquippable.Item_Sword;
import main.GamePanel;

import java.awt.event.KeyEvent;

public class NPC_Merchant extends SideCharacter {

    private Inventory inventory;
    private Dialogue tradeConfirmationDialogue; // THÊM DÒNG NÀY

    public NPC_Merchant(GamePanel gp) {
        super(gp);

        this.direction = "down";
        this.speed = 1;
        this.inventory = new Inventory(20);

        setDefaultValues();
        initializeDialogues();
        stockItems();
    }

    @Override
    public void setDefaultValues() {
        worldX = gp.getTileSize() * 25;
        worldY = gp.getTileSize() * 25;

        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        if (this.cip != null) {
            this.cip.setNumSprite(4);
            this.cip.getImage("/npc", "merchant");
        }
        this.name = "Merchant";
    }

    @Override
    public void setAction() {
        // Để trống phương thức này để Merchant không di chuyển
    }

    @Override
    public void update() {
        if (cip != null) {
            cip.update();
        }
    }

    public void stockItems() {
        inventory.addItem(new Item_HealthPotion(gp), 20);
        inventory.addItem(new Item_ManaPotion(gp), 20);
        inventory.addItem(new Item_Sword(gp), 1);
        inventory.addItem(new Item_Book(gp), 1);
        // Thêm các vật phẩm khác bạn muốn bán ở đây
    }

    @Override
    public void initializeDialogues() {
        // Khởi tạo dialogue xác nhận giao dịch
        tradeConfirmationDialogue = new Dialogue();
        tradeConfirmationDialogue.addLine("Merchant", "Chào mừng, nhà thám hiểm! Bạn có muốn trao đổi đồ vật không?");
        // Chúng ta sẽ không thêm các lựa chọn "Yes/No" trực tiếp vào Dialogue
        // Thay vào đó, KeyHandler sẽ xử lý các phím ENTER/ESCAPE ở trạng thái dialogue này.
    }

    @Override
    public void initiateDialogue(GamePanel gpReference) {
        gp.setCurrentMerchant(this); // Đặt thương nhân hiện tại để UI và KeyHandler có thể truy cập
        // Thay vì chuyển thẳng sang tradeState, chúng ta bắt đầu dialogue xác nhận.
        gp.getDialogueManager().startDialogue(this, tradeConfirmationDialogue);
        // DialogueManager sẽ tự động chuyển gameState sang dialogueState
    }

    // Phương thức mới để bắt đầu giao dịch, sẽ được gọi từ KeyHandler
    public void startTradeSession() {
        gp.gameState = GamePanel.tradeState; // Chuyển sang trạng thái giao dịch
        gp.getUi().setUI(gp.gameState); // Cập nhật UI
        gp.getUi().setCommandNum(0); // Reset con trỏ về panel của player
        gp.getUi().setSlotCol(0);
        gp.getUi().setSlotRow(0);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void tradeItem() {
        int itemIndex = gp.getUi().getItemIndexOnSlot();
        Player player = gp.getPlayer();
        Inventory playerInv = player.getInventory();
        Inventory merchantInv = this.inventory;

        // --- PANEL BÁN ĐỒ (CỦA NGƯỜI CHƠI) ---
        if (gp.getUi().getCommandNum() == 0) {
            if (itemIndex < playerInv.getItemStack()) {
                ItemStack stackToSell = playerInv.getItemStack(itemIndex);

                if (stackToSell.getItem().getSellPrice() <= 0) {
                    gp.getUi().showMessage("This item cannot be sold.");
                    return;
                }
                if (stackToSell.getItem() == player.getCurrentWeapon()) {
                    gp.getUi().showMessage("Cannot sell an equipped weapon!");
                    return;
                }

                player.gainCoin(stackToSell.getItem().getSellPrice());
                playerInv.removeItem(stackToSell.getItem(), 1);
                gp.getUi().showMessage("Sold " + stackToSell.getItem().getName() + " for " + stackToSell.getItem().getSellPrice() + " coins.");
            }
        }
        // --- PANEL MUA ĐỒ (CỦA THƯƠNG NHÂN) ---
        else {
            if (itemIndex < merchantInv.getItemStack()) {
                ItemStack stackToBuy = merchantInv.getItemStack(itemIndex);

                if (player.getCurrentCoin() >= stackToBuy.getItem().getBuyPrice()) {
                    if (!playerInv.isFull()) {
                        player.gainCoin(-stackToBuy.getItem().getBuyPrice());
                        playerInv.addItem(stackToBuy.getItem(), 1);

                        // Giảm số lượng item của merchant, nếu có (không cần thêm vào inventory của merchant)
                        if (stackToBuy.getQuantity() > 0) {
                            merchantInv.removeItem(stackToBuy.getItem(), 1);
                        }

                        gp.getUi().showMessage("Bought " + stackToBuy.getItem().getName() + " for " + stackToBuy.getItem().getBuyPrice() + " coins.");
                    } else {
                        gp.getUi().showMessage("Your inventory is full!");
                    }
                } else {
                    gp.getUi().showMessage("Not enough coins!");
                }
            }
        }
    }
}