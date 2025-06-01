package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed; // Giữ lại enterPressed nếu bạn dùng cho tương tác khác
    public boolean attackPressed; // Biến theo dõi phím tấn công
    public boolean skill1Pressed; // Biến theo dõi phím kỹ năng 1

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
                // gp.playSoundEffect(Sound.SFX_CURSOR); // Ví dụ
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.getUi().commandNum++;
                if (gp.getUi().commandNum > 2) {
                    gp.getUi().commandNum = 0;
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.getUi().commandNum == 0) { // New Game
                    gp.resetGameForNewSession(); // Đảm bảo game được reset khi bắt đầu game mới
                    gp.gameState = gp.playState;
                }
                if (gp.getUi().commandNum == 1) { // Load Game
                    if (gp.getSaveLoadManager() != null) {
                        gp.getSaveLoadManager().loadGame();
                        // gameState sẽ được chuyển trong loadGame() nếu thành công
                    } else {
                        gp.getUi().showMessage("Error: Load system not available.");
                    }
                }
                if (gp.getUi().commandNum == 2) { // Quit
                    System.exit(0);
                }
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
                // enterPressed sẽ được Player.update() sử dụng nếu bạn quay lại logic "nhấn Enter để nói chuyện"
                // Đối với logic "chạm để nói chuyện", cờ này không còn quá quan trọng cho việc bắt đầu hội thoại NPC
                // nhưng vẫn có thể dùng cho các tương tác khác.

                enterPressed = true; // Player sẽ kiểm tra cờ này trong update()
            }
            // Kỹ năng 1 (ví dụ: sử dụng kỹ năng đặc biệt)
            if (code == KeyEvent.VK_U) {
                skill1Pressed = true; // Đặt cờ kỹ năng 1
                // System.out.println("Skill 1 pressed: " + skill1Pressed); // Log để debug
            }
            // Thoát (tùy chọn)
            if (code == KeyEvent.VK_ESCAPE) {
                // gp.gameState = gp.titleState; // Ví dụ: quay về màn hình chính
            }
        } else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.playState;
            }
        } else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                if (gp.getDialogueManager() != null) { // Không cần kiểm tra getInteractingNPC ở đây nữa
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
        } else if (gp.gameState == gp.victoryEndState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.resetGameForNewSession();
                gp.gameState = gp.titleState;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

<<<<<<< HEAD
        // Chỉ cần reset các cờ liên quan đến trạng thái playState
        // Các hành động trong titleState, dialogueState, etc. thường là "nhấn một lần".
        if (gp.gameState == gp.playState || gp.gameState == gp.titleState /*hoặc bất kỳ state nào dùng các phím này*/) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { upPressed = false; }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { downPressed = false; }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) { leftPressed = false; }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) { rightPressed = false; }
            if (code == KeyEvent.VK_SPACE) {
                attackPressed = false; // Đặt lại khi thả phím tấn công
                // System.out.println("Space released (attack): " + attackPressed);
            }
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = false; // Reset cờ enter
            }
            if (code == KeyEvent.VK_U) {
                skill1Pressed = false; // Đặt lại khi thả phím kỹ năng 1
                // System.out.println("Skill 1 released: " + skill1Pressed);
            }
            /*public void characterState() {
                if(code == KeyEvent.VK_C) {
                    gp.gameState = gp.playState;
                }
                if(code == KeyEvent.VK_W) {
                    if(gp.getUi().slotRow !=0){
                        gp.getUi().slotRow--;
                    }
                }
                if(code == KeyEvent.VK_A) {
                    if(gp.getUi().slotCol !=0){
                        gp.getUi().slotCol--;
                    }
                }
                if(code == KeyEvent.VK_S) {
                    if(gp.getUi().slotRow !=3){
                        gp.getUi().slotRow++;
                    }
                }
                if(code == KeyEvent.VK_D) {
                    if(gp.getUi().slotCol !=4){
                        gp.getUi().slotCol++;
                    }
                }*/
=======

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
            skill1Pressed = false; // Đặt lại khi thả phím kỹ năng 1
            // System.out.println("Skill 1 released: " + skill1Pressed);
>>>>>>> daaa7ff62ea1d5b086c8cc898443dc62fba7f6a4
        }
    }
}