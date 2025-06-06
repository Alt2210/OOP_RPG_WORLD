package main;

// Thêm các import cho các lớp nhân vật cụ thể
import character.Role.Astrologer;
import character.Role.Soldier;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    public boolean attackPressed; // Phím Space cho đòn đánh thường
    public boolean skill1Pressed; // Phím U cho kỹ năng

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
        int currentState = gp.gameState;

        // --- TITLE STATE ---
        if (currentState == gp.titleState) {
            handleTitleState(code);
        }
        // --- CHARACTER SELECT STATE ---
        else if (currentState == gp.characterSelectState) {
            handleCharacterSelectState(code);
        }
        // --- PLAY STATE ---
        else if (currentState == gp.playState) {
            handlePlayState(code);
        }
        // --- PAUSE STATE ---
        else if (currentState == gp.pauseState) {
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.playState;
            }
        }
        // --- DIALOGUE STATE ---
        else if (currentState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                if (gp.getDialogueManager() != null) {
                    gp.getDialogueManager().advance();
                }
            }
        }
        // --- GAME OVER STATE ---
        else if (currentState == gp.gameOverState) {
            handleGameOverState(code);
        }
        // --- VICTORY STATE ---
        else if (currentState == gp.victoryEndState) {
            handleGameOverState(code); // Dùng chung logic với Game Over
        }
    }


    private void handleTitleState(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.getUi().commandNum--;
            if (gp.getUi().commandNum < 0) {
                gp.getUi().commandNum = 2; // 0: New Game, 1: Load, 2: Quit
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.getUi().commandNum++;
            if (gp.getUi().commandNum > 2) {
                gp.getUi().commandNum = 0;
            }
        }
        if (code == KeyEvent.VK_ENTER) {
            switch (gp.getUi().commandNum) {
                case 0: // New Game
                    gp.gameState = gp.characterSelectState; // Chuyển sang màn hình chọn nhân vật
                    gp.getUi().commandNum = 0; // Reset commandNum cho màn hình mới
                    break;
                case 1: // Load Game
                    gp.getSaveLoadManager().loadGame();
                    // Game state sẽ được chuyển trong loadGame() nếu thành công
                    break;
                case 2: // Quit
                    System.exit(0);
                    break;
            }
        }
    }

    private void handleCharacterSelectState(int code) {
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            gp.getUi().commandNum++;
            if (gp.getUi().commandNum > 1) { // Giả sử có 2 lựa chọn: 0 (Soldier), 1 (Astrologer)
                gp.getUi().commandNum = 0;
            }
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            gp.getUi().commandNum--;
            if (gp.getUi().commandNum < 0) {
                gp.getUi().commandNum = 1;
            }
        }
        if (code == KeyEvent.VK_ENTER) {
            // Khởi tạo lớp nhân vật dựa trên lựa chọn
            if (gp.getUi().commandNum == 0) {
                System.out.println("Soldier selected!");
                gp.setPlayer(new Soldier(gp, this));
            } else if (gp.getUi().commandNum == 1) {
                System.out.println("Astrologer selected!");
                gp.setPlayer(new Astrologer(gp, this));
            }

            // Sau khi đã có đối tượng player, setup game và chuyển sang playState
            gp.resetGameForNewSession();
            gp.gameState = gp.playState;
        }
        if (code == KeyEvent.VK_ESCAPE) { // Quay lại màn hình tiêu đề
            gp.gameState = gp.titleState;
            gp.getUi().commandNum = 0;
        }
    }

    private void handlePlayState(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_P) gp.gameState = gp.pauseState;
        if (code == KeyEvent.VK_SPACE) attackPressed = true;
        if (code == KeyEvent.VK_ENTER) enterPressed = true;
        if (code == KeyEvent.VK_U) skill1Pressed = true;
        // if (code == KeyEvent.VK_ESCAPE) gp.gameState = gp.titleState; // Tùy chọn: quay về title
    }

    private void handleGameOverState(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.getUi().commandNum--;
            if(gp.getUi().commandNum < 0) gp.getUi().commandNum = 1;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.getUi().commandNum++;
            if(gp.getUi().commandNum > 1) gp.getUi().commandNum = 0;
        }
        if (code == KeyEvent.VK_ENTER) {
            if (gp.getUi().commandNum == 0) { // Retry
                gp.resetGameForNewSession(); // Hoặc một phương thức retry khác
                gp.gameState = gp.playState;
            } else if (gp.getUi().commandNum == 1) { // Quit
                gp.gameState = gp.titleState;
                gp.resetGameForNewSession(); // Reset để lần sau vào New Game là game mới
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
        }

}