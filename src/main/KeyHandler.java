package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import character.role.*;
import item.Inventory;
import item.ItemStack;

public class KeyHandler implements KeyListener {
    private GamePanel gp;
    private boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    private boolean attackPressed;
    private boolean skill1Pressed;
    private boolean skill2Pressed;
    private boolean fPressed;
    private boolean dashPressed;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    // --- Các phương thức getter và setter của bạn không thay đổi ---
    public void setfPressed(boolean fPressed) { this.fPressed = fPressed; }
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isEnterPressed() { return enterPressed; }
    public boolean isAttackPressed() { return attackPressed; }
    public boolean isSkill1Pressed() { return skill1Pressed; }
    public boolean isSkill2Pressed() { return skill2Pressed; }
    public boolean isfPressed() { return fPressed; }
    public boolean isDashPressed() { return dashPressed; }

    @Override
    public void keyTyped(KeyEvent e) {
        // Không sử dụng
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Xử lý input dựa trên trạng thái game hiện tại
        if (gp.gameState == GamePanel.titleState) {
            handleTitleStateKeys(code);
        } else if (gp.gameState == GamePanel.characterSelectState) {
            handleCharacterSelectStateKeys(code);
        } else if (gp.gameState == GamePanel.playState) {
            handlePlayStateKeys(code);
        } else if (gp.gameState == GamePanel.pauseState) {
            handlePauseStateKeys(code);
        } else if (gp.gameState == GamePanel.dialogueState) {
            handleDialogueStateKeys(code);
        } else if (gp.gameState == GamePanel.InventoryState) {
            handleInventoryStateKeys(code);
        } else if (gp.gameState == GamePanel.chestState) {
            handleChestStateKeys(code);
        } else if (gp.gameState == GamePanel.gameOverState) {
            handleGameOverStateKeys(code);
        } else if (gp.gameState == GamePanel.victoryEndState) {
            handleVictoryStateKeys(code);
        }
    }

    // --- CÁC PHƯƠNG THỨC XỬ LÝ CHO TỪNG STATE ---

    private void handleTitleStateKeys(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.getUi().setCommandNum((gp.getUi().getCommandNum() - 1 + 3) % 3);
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.getUi().setCommandNum((gp.getUi().getCommandNum() + 1) % 3);
        }
        if (code == KeyEvent.VK_ENTER) {
            if (gp.getUi().getCommandNum() == 0) { // New Game
                gp.gameState = GamePanel.characterSelectState;
                gp.getUi().setUI(gp.gameState);
            }
            if (gp.getUi().getCommandNum() == 1) { // Load Game
                gp.getSaveLoadManager().loadGame();
                // Chỉ chuyển UI nếu load thành công và gameState đã được đổi thành playState
                if (gp.gameState == GamePanel.playState) {
                    gp.getUi().setUI(gp.gameState);
                }
            }
            if (gp.getUi().getCommandNum() == 2) { // Quit
                System.exit(0);
            }
        }
    }

    private void handleCharacterSelectStateKeys(int code) {
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            gp.getUi().setCommandNum((gp.getUi().getCommandNum() + 1) % 2);
        } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            gp.getUi().setCommandNum((gp.getUi().getCommandNum() - 1 + 2) % 2);
        } else if (code == KeyEvent.VK_ENTER) {
            if (gp.getUi().getCommandNum() == 0) {
                gp.setPlayer(new Soldier(gp, this));
            } else if (gp.getUi().getCommandNum() == 1) {
                gp.setPlayer(new Astrologer(gp, this));
            }
            gp.setupGame();
            gp.gameState = GamePanel.playState;
            gp.getUi().setUI(gp.gameState); // Cập nhật UI sang màn hình chơi game
        } else if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = GamePanel.titleState;
            gp.getUi().setUI(gp.gameState); // Cập nhật UI về màn hình tiêu đề
        }
    }

    private void handlePlayStateKeys(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_F) fPressed = true;
        if (code == KeyEvent.VK_SHIFT) dashPressed = true;
        if (code == KeyEvent.VK_SPACE) attackPressed = true;
        if (code == KeyEvent.VK_ENTER) enterPressed = true;
        if (code == KeyEvent.VK_U) skill1Pressed = true;
        if (code == KeyEvent.VK_I) skill2Pressed = true;

        if (code == KeyEvent.VK_P) {
            gp.gameState = GamePanel.pauseState;
            gp.getUi().setUI(gp.gameState);
        }
        if (code == KeyEvent.VK_C) {
            gp.gameState = GamePanel.InventoryState;
            gp.getUi().setUI(gp.gameState);
        }
    }

    private void handlePauseStateKeys(int code) {
        if (code == KeyEvent.VK_P) {
            gp.gameState = GamePanel.playState;
            gp.getUi().setUI(gp.gameState);
        }
    }

    private void handleDialogueStateKeys(int code) {
        if (code == KeyEvent.VK_ENTER) {
            gp.getDialogueManager().advance();
        }
    }

    private void handleInventoryStateKeys(int code) {
        if (code == KeyEvent.VK_C || code == KeyEvent.VK_ESCAPE) {
            gp.gameState = GamePanel.playState;
            gp.getUi().setUI(gp.gameState);
        }
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) gp.getUi().setSlotRow((gp.getUi().getSlotRow() - 1 + 4) % 4);
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) gp.getUi().setSlotRow((gp.getUi().getSlotRow() + 1) % 4);
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) gp.getUi().setSlotCol((gp.getUi().getSlotCol() - 1 + 5) % 5);
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) gp.getUi().setSlotCol((gp.getUi().getSlotCol() + 1) % 5);

        if (code == KeyEvent.VK_ENTER) {
            int itemIndex = gp.getUi().getItemIndexOnSlot();
            gp.getPlayer().getInventory().useItemInSlot(itemIndex, gp.getPlayer());
        }
    }

    private void handleChestStateKeys(int code) {
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_C) {
            gp.gameState = GamePanel.playState;
            gp.getUi().setUI(gp.gameState);
            gp.currentChest = null;
        }

        int playerPanel = 0;
        int chestPanel = 1;

        // Di chuyển lên/xuống
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (gp.getUi().getSlotRow() > 0) {
                gp.getUi().setSlotRow(gp.getUi().getSlotRow() - 1);
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (gp.getUi().getCommandNum() == playerPanel) {
                if (gp.getUi().getSlotRow() < 3) {
                    gp.getUi().setSlotRow(gp.getUi().getSlotRow() + 1);
                }
            } else {
                if (gp.getUi().getSlotRow() < 1) {
                    gp.getUi().setSlotRow(gp.getUi().getSlotRow() + 1);
                }
            }
        }

        // Di chuyển trái/phải và chuyển panel
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (gp.getUi().getCommandNum() == playerPanel) {
                if (gp.getUi().getSlotCol() > 0) {
                    gp.getUi().setSlotCol(gp.getUi().getSlotCol() - 1);
                }
            } else if (gp.getUi().getCommandNum() == chestPanel) {
                if (gp.getUi().getSlotCol() > 0) {
                    gp.getUi().setSlotCol(gp.getUi().getSlotCol() - 1);
                } else {
                    gp.getUi().setSlotCol(4);
                    gp.getUi().setCommandNum(playerPanel);
                }
            }
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (gp.getUi().getCommandNum() == playerPanel) {
                if (gp.getUi().getSlotCol() < 4) {
                    gp.getUi().setSlotCol(gp.getUi().getSlotCol() + 1);
                } else {
                    if (gp.getUi().getSlotRow() >= 2) {
                        gp.getUi().setSlotRow(1);
                    }
                    gp.getUi().setSlotCol(0);
                    gp.getUi().setCommandNum(chestPanel);
                }
            } else if (gp.getUi().getCommandNum() == chestPanel) {
                if (gp.getUi().getSlotCol() < 4) {
                    gp.getUi().setSlotCol(gp.getUi().getSlotCol() + 1);
                }
            }
        }

        if (code == KeyEvent.VK_ENTER) {
            transferItem();
        }
    }

    private void handleGameOverStateKeys(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.getUi().setCommandNum((gp.getUi().getCommandNum() - 1 + 2) % 2);
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.getUi().setCommandNum((gp.getUi().getCommandNum() + 1) % 2);
        }
        if (code == KeyEvent.VK_ENTER) {
            if (gp.getUi().getCommandNum() == 0) { // Retry
                gp.resetGameForNewSession();
                gp.gameState = GamePanel.titleState;
                gp.getUi().setUI(gp.gameState); // Cập nhật UI
            } else { // Quit
                System.exit(0);
            }
        }
    }

    private void handleVictoryStateKeys(int code) {
        if (code == KeyEvent.VK_ENTER) {
            gp.resetGameForNewSession();
            gp.gameState = GamePanel.titleState;
            gp.getUi().setUI(gp.gameState); // Cập nhật UI
        }
    }

    private void transferItem() {
        if (gp.currentChest == null) return;
        int slotIndex = gp.getUi().getItemIndexOnSlot();
        Inventory playerInv = gp.getPlayer().getInventory();
        Inventory chestInv = gp.currentChest.getInventory();

        if (gp.getUi().getCommandNum() == 0) { // Từ Player -> Rương
            ItemStack stackToMove = playerInv.getItemStack(slotIndex);
            if (stackToMove != null) {
                if (stackToMove.getItem() == gp.getPlayer().getCurrentWeapon()) {
                    gp.getUi().showMessage("Unequip the weapon first!");
                    return;
                }
                if (chestInv.addItem(stackToMove.getItem(), stackToMove.getQuantity())) {
                    playerInv.removeStack(slotIndex);
                    gp.getUi().showMessage("Moved " + stackToMove.getItem().getName() + " to chest.");
                    gp.currentChest.setOpened(false);
                } else {
                    gp.getUi().showMessage("Chest is full!");
                }
            }
        } else { // Từ Rương -> Player
            ItemStack stackToMove = chestInv.getItemStack(slotIndex);
            if (stackToMove != null) {
                if (playerInv.addItem(stackToMove.getItem(), stackToMove.getQuantity())) {
                    chestInv.removeStack(slotIndex);
                    gp.getUi().showMessage("Took " + stackToMove.getItem().getName() + " from chest.");
                    if (chestInv.getItemStack() == 0) {
                        gp.currentChest.setOpened(true);
                    }
                } else {
                    gp.getUi().showMessage("Your inventory is full!");
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_F) fPressed = false;
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
        if (code == KeyEvent.VK_SPACE) attackPressed = false;
        if (code == KeyEvent.VK_ENTER) enterPressed = false;
        if (code == KeyEvent.VK_U) skill1Pressed = false;
        if (code == KeyEvent.VK_I) skill2Pressed = false;
        if (code == KeyEvent.VK_SHIFT) dashPressed = false;
    }
}