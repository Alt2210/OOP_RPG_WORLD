package item;

import imageProcessor.ItemImageProcessor;
import main.GamePanel;

public class Item_Key extends Item {

    public Item_Key(GamePanel gp) {
        name = "Key";
        id = 1;
        type = "QUEST_ITEM";
        itp = new ItemImageProcessor(gp);
        itp.getImage("/objects", "key");
    }
}
