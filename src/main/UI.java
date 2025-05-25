package main;

import worldObject.pickableObject.OBJ_Key;
import character.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class UI {
    GamePanel gp;
    // Bỏ: Graphics2D g2;
    Font arial_40, arial_80B, dialogueFont; // Thêm dialogueFont
    BufferedImage keyImage;
    public boolean messageOn = false;
    public String message = "";
    private int messageCounter = 0;
    public String currentDialogue = ""; // Khởi tạo rỗng
    private double playtime = 0.0; // Khởi tạo
    private DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);
        dialogueFont = new Font("Arial", Font.PLAIN, 28); // Font cho hội thoại

        if (this.gp != null) { // Kiểm tra null cho gp
            // Đảm bảo OBJ_Key constructor nhận GamePanel và đã được sửa lỗi StackOverflow
            OBJ_Key key = new OBJ_Key(this.gp);
            this.keyImage = key.image;
        } else {
            System.err.println("UI Constructor: GamePanel is null! Key image not loaded.");
        }
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void setCurrentDialogue(String dialogueText) {
        this.currentDialogue = dialogueText;
    }

    public void draw(Graphics2D g2) {
        // Không còn gán this.g2 = g2;

        if (gp.gameState == gp.playState) {
            drawPlayUI(g2); // Gọi phương thức vẽ cho playState
        } else if (gp.gameState == gp.pauseState) {
            // Tùy chọn: drawPlayUI(g2); // với hiệu ứng mờ
            drawPauseScreen(g2);
        } else if (gp.gameState == gp.endGameState) {
            drawEndGameScreen(g2);
        } else if (gp.gameState == gp.dialogueState) {
            // Tùy chọn: drawPlayUI(g2); // với hiệu ứng mờ
            drawDialogueScreen(g2);
        }
    }

    private void drawPlayUI(Graphics2D g2) {
        g2.setFont(arial_40);
        g2.setColor(Color.white);

        // Hiển thị số lượng chìa khóa
        int currentTileSize = gp.getTileSize();
        Player currentPlayer = gp.getPlayer();
        if (keyImage != null) {
            g2.drawImage(keyImage, currentTileSize / 2, currentTileSize / 2, currentTileSize, currentTileSize, null);
        }
        if (currentPlayer != null) {
            g2.drawString("x " + currentPlayer.getHasKey(), currentTileSize / 2 + currentTileSize + 5, currentTileSize / 2 + currentTileSize - 10);
        } else {
            // System.out.println("Player is null, cannot draw key count");
        }


        // Hiển thị thời gian
        if (gp.gameState == gp.playState) { // Chỉ cập nhật playtime khi đang chơi
            playtime += (double) 1.0 / gp.getFPS();
        }
        g2.drawString("Time: " + dFormat.format(playtime), gp.getTileSize() * 11, 65);


        // Hiển thị tin nhắn
        if (messageOn) {
            Font messageFont = arial_40.deriveFont(30F);
            g2.setFont(messageFont);
            // Căn giữa tin nhắn
            int messageX = getXforCenteredText(message, g2, messageFont);
            int messageY = gp.getScreenHeight() - gp.getTileSize() * 2; // Gần cuối màn hình
            g2.drawString(message, messageX, messageY);

            messageCounter++;
            if (messageCounter > 120) {
                messageCounter = 0;
                messageOn = false;
            }
        }
    }

    public void drawPauseScreen(Graphics2D g2) { // Nhận g2
        Font pauseFont = arial_80B; // Sử dụng font đã khai báo
        g2.setFont(pauseFont);
        g2.setColor(Color.white);
        String text = "PAUSED";
        int x = getXforCenteredText(text, g2, pauseFont); // Truyền font
        int y = gp.getScreenHeight() / 2;
        g2.drawString(text, x, y);
    }

    // Sửa getXforCenteredText để nhận Graphics2D và Font
    public int getXforCenteredText(String text, Graphics2D g2, Font font) {
        if (text == null || g2 == null || font == null) return 0; // Kiểm tra null cơ bản
        FontMetrics fm = g2.getFontMetrics(font);
        int length = fm.stringWidth(text);
        int x = gp.getScreenWidth() / 2 - length / 2;
        return x;
    }

    public void drawEndGameScreen(Graphics2D g2) { // Nhận g2
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        Font fontText1 = arial_40.deriveFont(Font.BOLD, 40f);
        g2.setFont(fontText1);
        g2.setColor(Color.white);
        String text1 = "You found the Princess!";
        int x1 = getXforCenteredText(text1, g2, fontText1);
        int y1 = gp.getScreenHeight() / 2 - (gp.getTileSize() * 2);
        g2.drawString(text1, x1, y1);

        Font fontText2 = arial_80B.deriveFont(Font.BOLD, 60f);
        g2.setFont(fontText2);
        g2.setColor(Color.yellow);
        String text2 = "CONGRATULATIONS!";
        int x2 = getXforCenteredText(text2, g2, fontText2);
        int y2 = gp.getScreenHeight() / 2 + gp.getTileSize();
        g2.drawString(text2, x2, y2);

        Font fontExit = arial_40.deriveFont(Font.PLAIN, 24f);
        g2.setFont(fontExit);
        g2.setColor(Color.white);
        String text3 = "Press ESC to Exit";
        int x3 = getXforCenteredText(text3, g2, fontExit);
        int y3 = gp.getScreenHeight() / 2 + gp.getTileSize() * 3;
        g2.drawString(text3, x3, y3);
    }

    public void drawDialogueScreen(Graphics2D g2) { // Nhận g2 làm tham số
        // 1. Vẽ khung hội thoại (SubWindow)
        int x = gp.getTileSize() * 2;
        int y = gp.getTileSize() / 2;
        int width = gp.getScreenWidth() - (gp.getTileSize() * 4);
        int height = gp.getTileSize() * 4; // Có thể tăng chiều cao nếu cần nhiều dòng hơn
        drawSubWindow(x, y, width, height, g2);

        // 2. Vẽ nội dung hội thoại (currentDialogue) với word wrapping
        g2.setFont(dialogueFont); // Sử dụng dialogueFont đã khai báo
        g2.setColor(Color.white);

        int dialogueX = x + gp.getTileSize() / 2; // Padding bên trái cho text
        int currentY = y + gp.getTileSize();   // Vị trí Y ban đầu cho dòng text đầu tiên (padding trên)
        int lineHeight = g2.getFontMetrics(dialogueFont).getHeight(); // Chiều cao của một dòng text
        int availableWidth = width - gp.getTileSize(); // Chiều rộng tối đa cho text (trừ padding 2 bên)

        if (currentDialogue != null && !currentDialogue.isEmpty()) {
            // Tách các đoạn văn bản được định sẵn bằng "\n"
            for (String paragraph : currentDialogue.split("\n")) {
                String remainingParagraph = paragraph;
                while (!remainingParagraph.isEmpty()) {
                    String lineToDraw = remainingParagraph;
                    // Kiểm tra xem dòng hiện tại có vừa không
                    if (g2.getFontMetrics(dialogueFont).stringWidth(lineToDraw) > availableWidth) {
                        // Nếu không vừa, tìm vị trí ngắt dòng hợp lý (tại dấu cách)
                        int wrapIndex = -1;
                        for (int i = lineToDraw.length() - 1; i >= 0; i--) {
                            // Ưu tiên ngắt ở dấu cách gần nhất trong phạm vi chiều rộng cho phép
                            if (Character.isWhitespace(lineToDraw.charAt(i))) {
                                String sub = lineToDraw.substring(0, i);
                                if (g2.getFontMetrics(dialogueFont).stringWidth(sub) <= availableWidth) {
                                    wrapIndex = i;
                                    break;
                                }
                            }
                        }

                        if (wrapIndex != -1) { // Tìm thấy vị trí ngắt dòng hợp lý
                            lineToDraw = remainingParagraph.substring(0, wrapIndex);
                            remainingParagraph = remainingParagraph.substring(wrapIndex).trim(); // Phần còn lại, bỏ dấu cách ở đầu
                        } else {
                            // Nếu không tìm thấy dấu cách để ngắt (từ rất dài)
                            // hoặc dòng vẫn quá dài dù đã ngắt ở từ cuối cùng có thể,
                            // chúng ta sẽ phải cắt cứng ký tự.
                            // Đây là một cách đơn giản, có thể cần cải thiện để không cắt giữa từ.
                            int charIndex = 0;
                            for (int i = 1; i <= remainingParagraph.length(); i++) {
                                String sub = remainingParagraph.substring(0, i);
                                if (g2.getFontMetrics(dialogueFont).stringWidth(sub) > availableWidth) {
                                    charIndex = i - 1; // Lùi lại một ký tự
                                    break;
                                }
                                charIndex = i; // Nếu vừa hết chuỗi
                            }
                            lineToDraw = remainingParagraph.substring(0, charIndex);
                            remainingParagraph = remainingParagraph.substring(charIndex).trim();
                        }
                    } else {
                        remainingParagraph = ""; // Đánh dấu đã xử lý hết đoạn này
                    }

                    // Vẽ dòng đã được xử lý ngắt dòng
                    if (currentY < y + height - gp.getTileSize() / 2) { // Chỉ vẽ nếu còn trong khung
                        g2.drawString(lineToDraw, dialogueX, currentY);
                        currentY += lineHeight; // Di chuyển xuống dòng tiếp theo
                    } else {
                        break; // Ngừng vẽ nếu đã vượt quá chiều cao khung
                    }
                }
                if (currentY > y + height - gp.getTileSize() / 2) break; // Ngừng xử lý các đoạn paragraph khác nếu hết chỗ
            }
        }

        // Tùy chọn: Thêm chỉ dẫn "Press Enter to continue..."
        // Đặt sau vòng lặp vẽ text để nó luôn ở dưới cùng của khung dialogue
        if (gp.gameState == gp.dialogueState) { // Chỉ vẽ khi đang hội thoại
            Font continueFont = dialogueFont.deriveFont(Font.ITALIC, 20f);
            g2.setFont(continueFont);
            g2.setColor(Color.lightGray); // Màu khác một chút cho đỡ rối
            String continueText = "Press ENTER to continue...";
            FontMetrics fm = g2.getFontMetrics(continueFont);
            int continueX = x + width - fm.stringWidth(continueText) - gp.getTileSize() / 2;
            int continueY = y + height - gp.getTileSize() / 2 - 5; // Đẩy lên một chút so với đáy
            g2.drawString(continueText, continueX, continueY);
        }
    }


    public void drawSubWindow(int x, int y, int width, int height, Graphics2D g2) { // Nhận g2
        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }
}