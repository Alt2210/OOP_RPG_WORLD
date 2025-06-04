package dialogue;
import character.NPC_Princess;
import main.GamePanel;
import character.Character; // Cần để biết NPC nào đang nói

public class DialogueManager {
    private GamePanel gp;
    private DialogueSpeaker currentSpeaker = null;
    private Dialogue currentDialogue = null;
    private DialogueLine currentDialogueLine = null;

    public DialogueManager(GamePanel gp) {
        this.gp = gp;
    }

    public void startDialogue(DialogueSpeaker speaker, Dialogue dialogue) {
        if (gp.gameState == gp.dialogueState && this.currentSpeaker == speaker && this.currentDialogue == dialogue) {
            return;
        }
        this.currentSpeaker = speaker;
        this.currentDialogue = dialogue;

        if (this.currentDialogue != null) {
            this.currentDialogue.reset(); // Đảm bảo bắt đầu từ đầu
            advance(); // Hiển thị dòng đầu tiên
        }
        gp.gameState = gp.dialogueState; // Chuyển game sang trạng thái hội thoại
        if (speaker instanceof Character) {
            gp.setInteractingNPC((Character) speaker); // Cho GamePanel biết ai đang nói
        }
    }

    public void advance() {
        if (currentDialogue != null && currentDialogue.hasMoreLines()) {
            currentDialogueLine = currentDialogue.getNextLine();
            if (currentDialogueLine != null) {
                // Cập nhật UI với dòng hội thoại mới
                String displayText = (currentDialogueLine.getSpeakerName() != null ? currentDialogueLine.getSpeakerName() + ": " : "") + currentDialogueLine.getLineText();
                gp.getUi().setCurrentDialogue(displayText);
            }
        } else {
            // Không còn dòng nào, kết thúc hội thoại
            endDialogue();
        }
    }

    public void endDialogue() {
        gp.getUi().setCurrentDialogue(""); // Xóa text hội thoại trên UI

        // KIỂM TRA XEM CÓ PHẢI LÀ PRINCESS VÀ QUYẾT ĐỊNH TRẠNG THÁI TIẾP THEO
        if (currentSpeaker instanceof NPC_Princess) {

            gp.gameState = gp.victoryEndState;

        } else {
            // Đối với các NPC khác, quay lại trạng thái chơi bình thường
            gp.gameState = gp.playState;
        }

        if (currentSpeaker instanceof Character) {
            gp.setInteractingNPC(null); // Không còn NPC nào đang tương tác
        }
        currentSpeaker = null;
        currentDialogue = null;
        currentDialogueLine = null;
    }

    public boolean isDialogueActive() {
        return gp.gameState == gp.dialogueState && currentDialogue != null;
    }

    public void reset() {
        currentSpeaker = null;
        currentDialogue = null;
        currentDialogueLine = null;
        if (gp.getUi() != null) { // Thêm kiểm tra null cho an toàn
            gp.getUi().setCurrentDialogue("");
        }
        // Không thay đổi gameState ở đây, GamePanel sẽ quyết định
    }
}