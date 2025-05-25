package worldObject.pickableObject;
import item.Item;
import main.GamePanel;

public interface Pickable {
    Item convertToItem(GamePanel gp);
}
