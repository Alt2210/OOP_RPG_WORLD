package item;

import imageProcessor.ItemImageProcessor;
import main.GamePanel;

public class Item {
    protected String name;
    protected String type;
    protected int id;
    protected ItemImageProcessor itp;

    public enum ItemType {
        WEAPON,
        ARMOR,
        ACCESSORY,
        CONSUMABLE,
        MATERIAL,
        QUEST_ITEM,
        CURRENCY,
        MISC,
        UNKNOWN;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ItemImageProcessor getItp() {
        return itp;
    }

    public void setItp(ItemImageProcessor itp) {
        this.itp = itp;
    }

    public void useItem(){}

    public Item(){
        name = "Null";
        id = -1;
        type = "UNKNOWN";
        itp = null;
    }


}
