package map;
import main.GamePanel;

public abstract class GameMap {
    protected GamePanel gp;
    public GameMap(GamePanel gp) { this.gp = gp; }
    public abstract void initialize();
}