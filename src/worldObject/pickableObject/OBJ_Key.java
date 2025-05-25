package worldObject.pickableObject;

import item.Item;
import item.Item_Key;
import main.GamePanel;
import worldObject.WorldObject;

<<<<<<< HEAD
public class OBJ_Key extends WorldObject implements Pickable {

    public OBJ_Key(GamePanel gp) {
=======
public class OBJ_Key extends WorldObject {

    public OBJ_Key(GamePanel gp) {
        Item_Key key = new Item_Key(gp);
>>>>>>> d6cb7e6f79b020b8591cdc199f625524acf87184
        name = "Key";
        Item_Key key = new Item_Key(gp);
        image = key.getItp().getCurFrame();
    }
    @Override // Triển khai phương thức từ interface Pickable
    public Item convertToItem(GamePanel gp) {
        // Tạo một đối tượng Item_Key mới để đưa vào inventory
        // Truyền gp vào constructor của Item_Key
        return new Item_Key(gp);
    }
}
