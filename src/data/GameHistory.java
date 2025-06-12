package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameHistory implements Serializable {
    private static final long serialVersionUID = 1L; // Good practice for Serializable classes
    private List<DataStorage> savePoints;

    public GameHistory() {
        this.savePoints = new ArrayList<>();
    }

    public List<DataStorage> getSavePoints() {
        return savePoints;
    }

    public void addSavePoint(DataStorage data) {
        this.savePoints.add(data);
    }
}