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
            System.out.println("Dialogue.getNextLine: New Index=" + currentLineIndex);

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
        boolean moreLines = (currentLineIndex + 1 < lines.size());
        System.out.println("Dialogue.hasMoreLines: Index=" + currentLineIndex + ", Size=" + lines.size() + ", More=" + moreLines);
        return moreLines;
    }

    public void reset() {
        currentLineIndex = -1;
        System.out.println("Dialogue: RESET called. currentLineIndex = " + currentLineIndex);

    }
}