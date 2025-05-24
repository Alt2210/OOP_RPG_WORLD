package worldObject.pickableObject;

import item.Item_Key;
import main.GamePanel;
import worldObject.WorldObject;

public class OBJ_Key extends WorldObject {

    public OBJ_Key(GamePanel gp) {
        Item_Key key = new Item_Key(gp);
        name = "Key";
        image = key.getItp().getCurFrame();
    }
}
