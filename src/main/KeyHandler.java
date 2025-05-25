package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_P) {
            if(gp.gameState == gp.playState){
                gp.gameState = gp.pauseState;
            }
            else if(gp.gameState == gp.pauseState){
                gp.gameState = gp.playState;
            }
        }
        if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.getDialogueManager().advance(); // Yêu cầu DialogueManager tiến tới câu thoại tiếp theo
            }
        }
        if (code == KeyEvent.VK_ESCAPE) {
            if (gp.gameState == gp.endGameState) {
                System.out.println("Exiting game from end game state.");
                System.exit(0);
            } else if (gp.gameState == gp.playState || gp.gameState == gp.pauseState) {
                // Có thể thêm chức năng mở menu hoặc thoát game từ các trạng thái này
                // Ví dụ: gp.gameState = gp.titleState; // Quay về màn hình chính
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
}
