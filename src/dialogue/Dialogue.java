package dialogue;

import java.util.ArrayList;
import java.util.List;

public class Dialogue {
    private List<DialogueLine> lines;
    private int currentLineIndex = -1; // Bắt đầu từ -1, advance sẽ làm nó thành 0

    public Dialogue() {
        this.lines = new ArrayList<>();
    }

    public void addLine(String text) {
        this.lines.add(new DialogueLine(text));
    }

    public void addLine(String speakerName, String text) {
        this.lines.add(new DialogueLine(speakerName, text));
    }

    public DialogueLine getNextLine() {
        if (hasMoreLines()) {
            currentLineIndex++;
            return lines.get(currentLineIndex);
        }
        return null; // Hoặc ném Exception nếu không còn dòng nào
    }

    public DialogueLine getCurrentLine() {
        if (currentLineIndex >= 0 && currentLineIndex < lines.size()) {
            return lines.get(currentLineIndex);
        }
        return null;
    }

    public boolean hasMoreLines() {
        return currentLineIndex < lines.size() - 1;
    }

    public void reset() {
        currentLineIndex = -1;
    }
}