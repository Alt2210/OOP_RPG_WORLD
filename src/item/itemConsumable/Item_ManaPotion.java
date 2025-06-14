package item.itemConsumable;

import character.role.Player;
import imageProcessor.ItemImageProcessor;
import item.Item;
import main.GamePanel;

public class Item_ManaPotion extends Item implements Consumable{
    private final int manaValue = 50; // Lượng máu hồi

    public Item_ManaPotion(GamePanel gp) {
        name = "Mana";
        id = 102; // ID duy nhất cho vật phẩm này
        type = "CONSUMABLE"; // Loại vật phẩm là "dùng một lần"
        this.buyPrice = 10;
        itp = new ItemImageProcessor(gp);
        // Giả sử bạn có ảnh health_potion.png trong res/objects/
        itp.getImage("/objects", "potion_mana");
        description = "[" + name + "]\nA magical potion that\nrestores " + manaValue + " Mana.";
    }

    @Override
    public void consumeItem(Player user){
        user.setCurrentMana(user.getCurrentMana() + manaValue);
        user.getGp().getUi().showMessage("Restore for " + manaValue + " Mana!");
    }

    public void useItem(Player user){
        consumeItem(user);
    }
}
