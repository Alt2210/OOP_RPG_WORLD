package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import character.role.*;
import item.ItemStack;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed; // Giữ lại enterPressed nếu bạn dùng cho tương tác khác
    public boolean attackPressed; // Biến theo dõi phím tấn công
    public boolean skill1Pressed; // Biến theo dõi phím kỹ năng 1
    public boolean skill2Pressed;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Không sử dụng
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Xử lý input dựa trên trạng thái game hiện tại
        if (gp.gameState == gp.titleState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.getUi().commandNum--;
                if (gp.getUi().commandNum < 0) {
                    gp.getUi().commandNum = 2; // Giả sử có 3 lựa chọn (0, 1, 2)
                }
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.getUi().commandNum++;
                if (gp.getUi().commandNum > 2) {
                    gp.getUi().commandNum = 0;
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.getUi().commandNum == 0) { // New Game
                    // FIX: Chuyển sang màn hình chọn nhân vật thay vì bắt đầu game ngay
                    gp.gameState = gp.characterSelectState;
                    gp.getUi().commandNum = 0; // Reset lựa chọn cho màn hình mới
                }
                if (gp.getUi().commandNum == 1) { // Load Game
                    if (gp.getSaveLoadManager() != null) {
                        gp.getSaveLoadManager().loadGame();
                    } else {
                        gp.getUi().showMessage("Error: Load system not available.");
                    }
                }
                if (gp.getUi().commandNum == 2) { // Quit
                    System.exit(0);
                }
            }
        } else if (gp.gameState == gp.characterSelectState) { // NEW: Thêm logic cho màn hình chọn nhân vật
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                gp.getUi().commandNum++;
                if (gp.getUi().commandNum > 1) { // Có 2 lựa chọn: Soldier (0) và Astrologer (1)
                    gp.getUi().commandNum = 0;
                }
            } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                gp.getUi().commandNum--;
                if (gp.getUi().commandNum < 0) {
                    gp.getUi().commandNum = 1;
                }
            } else if (code == KeyEvent.VK_ENTER) {
                // Tạo Player dựa trên lựa chọn
                if (gp.getUi().commandNum == 0) {
                    gp.setPlayer(new character.role.Soldier(gp, this));
                } else if (gp.getUi().commandNum == 1) {
                    gp.setPlayer(new character.role.Astrologer(gp, this));
                }
                // Sau khi Player được tạo, tiến hành setup và bắt đầu game
                gp.setupGame();
                gp.gameState = gp.playState;

            } else if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.titleState; // Quay lại màn hình chính
            }
        } else if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                upPressed = true;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                downPressed = true;
            }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                leftPressed = true;
            }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            }
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.pauseState;
            }
            if (code == KeyEvent.VK_SPACE) {
                attackPressed = true;
            }
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true;
            }
            if (code == KeyEvent.VK_U) {
                skill1Pressed = true;
            }
            if (code == KeyEvent.VK_I) {
                skill2Pressed = true;
            }
            if (code == KeyEvent.VK_C) {
                gp.gameState = gp.InventoryState;
            }
        } else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.playState;
            }
        } else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                if (gp.getDialogueManager() != null) {
                    gp.getDialogueManager().advance();
                }
            }
        } else if (gp.gameState == gp.gameOverState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.resetGameForNewSession();
                gp.gameState = gp.titleState;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.getUi().commandNum--;
                if (gp.getUi().commandNum < 0) {
                    gp.getUi().commandNum = 1; // Giả sử có 3 lựa chọn (0, 1, 2)
                }
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.getUi().commandNum++;
                if (gp.getUi().commandNum > 1) {
                    gp.getUi().commandNum = 0;
                }
            }

        } else if (gp.gameState == gp.victoryEndState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.resetGameForNewSession();
                gp.gameState = gp.titleState;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
        } else if (gp.gameState == gp.InventoryState) {
            int slotRow = gp.getUi().getSlotRow();
            int slotCol = gp.getUi().getSlotCol();
            int slotIndex = slotRow * 5 + slotCol;
            if (code == KeyEvent.VK_C) {
                gp.gameState = gp.playState;
            }
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                if(slotRow == 0){
                    slotRow = 3;
                    gp.getUi().setSlotRow(slotRow);
                } else {
                    slotRow--;
                    gp.getUi().setSlotRow(slotRow);
                }
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                if(slotRow == 3){
                    slotRow = 0;
                    gp.getUi().setSlotRow(slotRow);
                } else {
                    slotRow++;
                    gp.getUi().setSlotRow(slotRow);
                }
            }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                if(slotCol == 0){
                    slotCol = 4;
                    gp.getUi().setSlotCol(slotCol);
                } else {
                    slotCol--;
                    gp.getUi().setSlotCol(slotCol);
                }
            }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                if(slotCol == 4){
                    slotCol = 0;
                    gp.getUi().setSlotCol(slotCol);
                } else {
                    slotCol++;
                    gp.getUi().setSlotCol(slotCol);
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.getPlayer().getInventory().getItemStack(gp.getUi().getItemIndexOnSlot()) != null) {
                    if (gp.getPlayer().getInventory().getItemStack(gp.getUi().getItemIndexOnSlot()).getItem() == gp.getPlayer().getCurrentWeapon()) {
                        gp.getPlayer().unequipWeapon();
                    } else {
                        gp.getPlayer().getInventory().useItemInSlot(gp.getUi().getItemIndexOnSlot(), gp.getPlayer());
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_SPACE) {
            attackPressed = false;
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
        }
        if (code == KeyEvent.VK_U) {
            skill1Pressed = false;
        }
        if (code == KeyEvent.VK_I) {
            skill2Pressed = false;
        }
    }
}