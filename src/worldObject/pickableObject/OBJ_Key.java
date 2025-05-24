package worldObject.pickableObject;

import item.Item_Key;
import worldObject.WorldObject;

public class OBJ_Key extends WorldObject {
    Item_Key key = new Item_Key();

    public OBJ_Key() {
        name = "Key";
        image = key.getItp().getCurFrame();
    }
}
