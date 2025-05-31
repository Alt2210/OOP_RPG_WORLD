package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed; // Giữ lại enterPressed nếu bạn dùng cho tương tác khác
    public boolean attackPressed; // Biến theo dõi phím tấn công

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
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { upPressed = true; }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { downPressed = true; }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) { leftPressed = true; }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) { rightPressed = true; }
            if (code == KeyEvent.VK_P) { gp.gameState = gp.pauseState; }
            if (code == KeyEvent.VK_SPACE) { attackPressed = true; }
            if (code == KeyEvent.VK_ENTER) {
                // enterPressed sẽ được Player.update() sử dụng nếu bạn quay lại logic "nhấn Enter để nói chuyện"
                // Đối với logic "chạm để nói chuyện", cờ này không còn quá quan trọng cho việc bắt đầu hội thoại NPC
                // nhưng vẫn có thể dùng cho các tương tác khác.
                enterPressed = true;
            }
        } else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) { gp.gameState = gp.playState; }
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
            if (code == KeyEvent.VK_ESCAPE) { System.exit(0); }
        } else if (gp.gameState == gp.victoryEndState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.resetGameForNewSession();
                gp.gameState = gp.titleState;
            }
            if (code == KeyEvent.VK_ESCAPE) { System.exit(0); }
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
        // Nếu có các phím hành động khác đặt cờ boolean, cũng reset chúng ở đây.
    }
}