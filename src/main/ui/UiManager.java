package main.ui;

import main.GamePanel;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
public class UiManager {
    private final GamePanel gp;
    private UI currentUI;

    // Giữ một instance của mỗi loại UI
    private final UI titleUI;
    private final UI characterSelectUI;
    private final PlayUI playUI; // Cần là PlayUI để gọi showMessage
    private final UI pauseUI;
    private final UI gameOverUI;
    private final UI victoryUI;
    private final InventoryUI inventoryUI;
    private final ChestUI chestUI;
    private final LoadGameUI loadGameUI;
    private final UI tradeUI;
    public UiManager(GamePanel gp) {
        this.gp = gp;

        // Khởi tạo tất cả các đối tượng UI
        this.titleUI = new TitleScreenUI(gp);
        this.characterSelectUI = new CharacterSelectUI(gp);
        this.playUI = new PlayUI(gp);
        this.pauseUI = new PauseUI(gp);
        this.gameOverUI = new GameOverUI(gp);
        this.victoryUI = new VictoryUI(gp);
        this.inventoryUI = new InventoryUI(gp);
        this.chestUI = new ChestUI(gp);
        this.loadGameUI = new LoadGameUI(gp);
        this.tradeUI = new TradeUI(gp);
        // Thiết lập UI mặc định ban đầu
        this.currentUI = titleUI;
    }

    public void setUI(int gameState) {
        switch (gameState) {
            case GamePanel.titleState: currentUI = titleUI; break;
            case GamePanel.characterSelectState: currentUI = characterSelectUI; break;
            case GamePanel.playState: currentUI = playUI; break;
            case GamePanel.pauseState: currentUI = pauseUI; break;
            case GamePanel.dialogueState: currentUI = playUI; break;
            case GamePanel.InventoryState: currentUI = inventoryUI; break;
            case GamePanel.chestState: currentUI = chestUI; break;
            case GamePanel.gameOverState: currentUI = gameOverUI; break;
            case GamePanel.victoryEndState: currentUI = victoryUI; break;
            case GamePanel.loadGameState: loadGameUI.refreshHistory(); currentUI = loadGameUI;  break;
            case GamePanel.tradeState: currentUI = tradeUI; break;
        }
    }

    public void draw(Graphics2D g2) {
        // Luôn tắt khử răng cưa cho pixel art
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        if (currentUI != null) {
            currentUI.draw(g2);
        }

        if (gp.gameState != GamePanel.titleState && gp.gameState != GamePanel.characterSelectState && playUI != null) {
            playUI.drawMessage(g2);
            playUI.drawFloatingText(g2);
        }
    }

    // --- Các phương thức ủy nhiệm (delegate methods) ---
    // Các lớp khác sẽ gọi đến UIManager, và UIManager sẽ gọi đến lớp UI con phù hợp.

    public void showMessage(String text) {
        playUI.showMessage(text);
    }
    public void showFloatingText(String text){playUI.showFloatingText(text);}

    public void setCurrentDialogue(String text) {
        playUI.setCurrentDialogue(text);
    }

    public int getCommandNum() { return currentUI.getCommandNum(); }
    public void setCommandNum(int num) { currentUI.setCommandNum(num); }

    public int getSlotCol() {
        if(currentUI instanceof InventoryUI) return ((InventoryUI) currentUI).getSlotCol();
        if(currentUI instanceof ChestUI) return ((ChestUI) currentUI).getSlotCol();
        if(currentUI instanceof TradeUI) return ((TradeUI) currentUI).getSlotCol(); // THÊM DÒNG NÀY
        return 0;
    }
    public void setSlotCol(int col) {
        if(currentUI instanceof ChestUI)  ((ChestUI) currentUI).setSlotCol(col);
        if(currentUI instanceof InventoryUI) ((InventoryUI) currentUI).setSlotCol(col);
        if(currentUI instanceof TradeUI) ((TradeUI) currentUI).setSlotCol(col); // THÊM DÒNG NÀY
    }

    public int getSlotRow() {
        if(currentUI instanceof InventoryUI) return ((InventoryUI) currentUI).getSlotRow();
        if(currentUI instanceof ChestUI) return ((ChestUI) currentUI).getSlotRow();
        if(currentUI instanceof TradeUI) return ((TradeUI) currentUI).getSlotRow(); // THÊM DÒNG NÀY
        return 0;
    }
    public void setSlotRow(int row) {
        if(currentUI instanceof ChestUI) ((ChestUI) currentUI).setSlotRow(row);
        if(currentUI instanceof InventoryUI) ((InventoryUI) currentUI).setSlotRow(row);
        if(currentUI instanceof TradeUI) ((TradeUI) currentUI).setSlotRow(row); // THÊM DÒNG NÀY
    }

    public int getItemIndexOnSlot() {
        if(currentUI instanceof InventoryUI) return ((InventoryUI) currentUI).getItemIndexOnSlot();
        if(currentUI instanceof ChestUI) return ((ChestUI) currentUI).getItemIndexOnSlot();
        if(currentUI instanceof TradeUI) return ((TradeUI) currentUI).getItemIndexOnSlot(); // THÊM DÒNG NÀY
        return 0;
    }

    public boolean hasMoreLinesInCurrentDialogue() {
        return gp.getDialogueManager().hasMoreLinesInCurrentDialogue();
    }
}