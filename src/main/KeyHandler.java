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
        if (gp.gameState == gp.titleState) { // TRẠNG THÁI TITLE SCREEN
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
                    gp.gameState = gp.playState;
                }
                if (gp.getUi().commandNum == 1) { // Load Game
                    // Logic load game (chưa triển khai)
                    System.out.println("Load Game selected - Not implemented yet.");
                }
                if (gp.getUi().commandNum == 2) { // Quit
                    System.exit(0);
                }
            }
        } else if (gp.gameState == gp.playState) { // TRẠNG THÁI PLAYING
            // Di chuyển
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { upPressed = true; }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { downPressed = true; }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) { leftPressed = true; } // Thêm VK_LEFT
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) { rightPressed = true; } // Thêm VK_RIGHT

            // Tạm dừng
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.pauseState;
            }
            // Tấn công
            if (code == KeyEvent.VK_SPACE) {
                attackPressed = true; // Đặt cờ tấn công
                // System.out.println("Space pressed (attack): " + attackPressed); // Log để debug
            }
            // Tương tác (ví dụ: nói chuyện, mở rương)
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true; // Player sẽ kiểm tra cờ này trong update()
            }
            // Thoát (tùy chọn)
            if (code == KeyEvent.VK_ESCAPE) {
                // gp.gameState = gp.titleState; // Ví dụ: quay về màn hình chính
            }

        } else if (gp.gameState == gp.pauseState) { // TRẠNG THÁI PAUSED
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.playState; // Quay lại chơi
            }
            if (code == KeyEvent.VK_ESCAPE) {
                // gp.gameState = gp.titleState; // Ví dụ: quay về màn hình chính từ pause
            }
        } else if (gp.gameState == gp.dialogueState) { // TRẠNG THÁI DIALOGUE
            if (code == KeyEvent.VK_ENTER) {
                if (gp.getDialogueManager() != null) {
                    gp.getDialogueManager().advance();
                }
            }

        } else if (gp.gameState == gp.gameOverState) { // TRẠNG THÁI GAME OVER
            if (code == KeyEvent.VK_ENTER) {
                // Logic để chơi lại (reset game và quay về title hoặc play state)
                gp.resetGameForNewSession(); // Cần một phương thức reset trong GamePanel
                gp.gameState = gp.titleState;
                System.out.println("Enter pressed on Game Over screen.");
            }
            if (code == KeyEvent.VK_ESCAPE) {
                System.out.println("Exiting game from game over state.");
                System.exit(0);
            }
        }
        else if (gp.gameState == gp.victoryEndState) { // Giả sử bạn đã đổi tên endGameState thành victoryEndState
            if (code == KeyEvent.VK_ENTER) {
                gp.resetGameForNewSession();
                gp.gameState = gp.titleState;

            }
            if (code == KeyEvent.VK_ESCAPE) {
                System.out.println("Exiting game from victory screen.");
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

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
        }
    }
}