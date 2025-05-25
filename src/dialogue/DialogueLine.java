package dialogue;

public class DialogueLine {
    public String speakerName; // Tên người nói (tùy chọn)
    public String lineText;    // Nội dung câu thoại
    // public BufferedImage speakerAvatar; // Avatar người nói (tùy chọn)
    // public Sound lineSound; // Âm thanh cho câu thoại (tùy chọn)

    public DialogueLine(String lineText) {
        this.lineText = lineText;
        this.speakerName = null; // Hoặc một tên mặc định
    }

    public DialogueLine(String speakerName, String lineText) {
        this.speakerName = speakerName;
        this.lineText = lineText;
    }
    // Getters
    public String getLineText() { return lineText; }
    public String getSpeakerName() { return speakerName; }
}