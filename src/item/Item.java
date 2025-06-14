package item;

import character.role.Player;
import imageProcessor.ItemImageProcessor;
import main.GamePanel;

public abstract class Item {
    protected String name;
    protected String type;
    protected int id;
    protected ItemImageProcessor itp;
    protected String description;

    protected int buyPrice;
    protected boolean sellable = true;

    // Thêm 2 getters này
    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return buyPrice / 2;
    }

    public boolean isSellable() {
        return sellable;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void useItem(Player user){}

    public Item(){
        name = "Null";
        id = -1;
        type = "UNKNOWN";
        itp = null;
        description = "Null";
    }


}
