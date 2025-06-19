package dialogue;
import character.sideCharacter.NPC_Merchant; // THÊM DÒNG NÀY
import character.sideCharacter.NPC_Princess;
import main.GamePanel;
import character.Character;

public class DialogueManager {
    private GamePanel gp;
    private DialogueSpeaker currentSpeaker = null;
    private Dialogue currentDialogue = null;
    private DialogueLine currentDialogueLine = null;

    public DialogueManager(GamePanel gp) {
        this.gp = gp;
    }

    public void startDialogue(DialogueSpeaker speaker, Dialogue dialogue) {
        // Nếu đã ở trạng thái dialogue với cùng một speaker và dialogue, không làm gì.
        // Điều này ngăn việc khởi tạo lại dialogue khi spam phím F
        if (gp.gameState == gp.dialogueState && this.currentSpeaker == speaker && this.currentDialogue == dialogue) {
            return;
        }
        this.currentSpeaker = speaker;
        this.currentDialogue = dialogue;

        if (this.currentDialogue != null) {
            this.currentDialogue.reset();
            advance(); // Tải dòng đầu tiên
        }
        gp.gameState = gp.dialogueState; // Đặt game state thành dialogue
        if (speaker instanceof Character) {
            gp.setInteractingNPC((Character) speaker);
        }
    }

    public void advance() {
        if (currentDialogue != null && currentDialogue.hasMoreLines()) {
            currentDialogueLine = currentDialogue.getNextLine();
            if (currentDialogueLine != null) {
                String displayText = (currentDialogueLine.getSpeakerName() != null ? currentDialogueLine.getSpeakerName() + ": " : "") + currentDialogueLine.getLineText();
                gp.getUi().setCurrentDialogue(displayText);
            }
        } else {
            // Không còn dòng nào, hội thoại đã "hoàn tất".
            // Giờ đây, thay vì tự động chuyển trạng thái, chúng ta kiểm tra loại NPC.
            if (currentSpeaker instanceof NPC_Merchant) {
                // Với Merchant, giữ nguyên dialogueState và hiển thị các lựa chọn cho KeyHandler
                if (currentDialogueLine != null) { // Đảm bảo currentDialogueLine không null khi kết thúc
                    gp.getUi().setCurrentDialogue(currentDialogueLine.getLineText() + "\n(ENTER: Trade / ESC: Escape)");
                } else {
                    // Trường hợp dialogue rỗng hoặc lỗi, hiển thị mặc định
                    gp.getUi().setCurrentDialogue("Merchant: Ready to trade?\n(ENTER: Trade / ESC: Escape)");
                }
                // KHÔNG thay đổi gp.gameState, nó vẫn là dialogueState
            } else if (currentSpeaker instanceof NPC_Princess) {
                gp.Victory(); // Nếu là Princess, game kết thúc chiến thắng
            } else {
                // Với các NPC khác (ví dụ: OldMan), kết thúc dialogue và trở về playState
                gp.gameState = gp.playState;
                gp.getUi().setUI(gp.playState);
                // Sau khi chuyển state, mới clear các tham chiếu
                if (currentSpeaker instanceof Character) {
                    gp.setInteractingNPC(null);
                }
                currentSpeaker = null;
                currentDialogue = null;
                currentDialogueLine = null;
            }
        }
    }

    public void endDialogue() { // Phương thức này được gọi khi người chơi nhấn ESC để thoát dialogue
        gp.getUi().setCurrentDialogue(""); // Xóa text dialogue trên UI
        // Nếu là Princess, kết thúc game
        if (currentSpeaker instanceof NPC_Princess) {
            gp.Victory();
        } else {
            // Quay về playState cho các NPC khác
            gp.gameState = gp.playState;
            gp.getUi().setUI(gp.playState);
        }

        // Luôn clear các tham chiếu khi kết thúc dialogue bằng ESC
        if (currentSpeaker instanceof Character) {
            gp.setInteractingNPC(null);
        }
        currentSpeaker = null;
        currentDialogue = null;
        currentDialogueLine = null;
    }

    public boolean isDialogueActive() {
        // Dialogue được coi là "active" nếu game đang ở dialogueState
        // và có một dialogue được thiết lập (hoặc đang hiển thị dòng cuối cùng của merchant)
        return gp.gameState == gp.dialogueState && currentDialogue != null;
    }

    public void reset() {
        currentSpeaker = null;
        currentDialogue = null;
        currentDialogueLine = null;
        if (gp.getUi() != null) {
            gp.getUi().setCurrentDialogue("");
        }
    }

    // THÊM PHƯƠNG THỨC NÀY
    public boolean hasMoreLinesInCurrentDialogue() {
        return currentDialogue != null && currentDialogue.hasMoreLines();
    }

    public DialogueSpeaker getInteractingNPC(){
        return this.currentSpeaker;
    }
}