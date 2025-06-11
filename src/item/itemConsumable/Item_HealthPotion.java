package item.itemConsumable;

import character.role.Player;
import imageProcessor.ItemImageProcessor;
import item.Item;
import main.GamePanel;

public class Item_HealthPotion extends Item implements Consumable{

    private final int healingValue = 20; // Lượng máu hồi

    public Item_HealthPotion(GamePanel gp) {
        name = "Health Potion";
        id = 101; // ID duy nhất cho vật phẩm này
        type = "CONSUMABLE"; // Loại vật phẩm là "dùng một lần"
        itp = new ItemImageProcessor(gp);
        // Giả sử bạn có ảnh health_potion.png trong res/objects/
        itp.getImage("/objects", "potion_health");
        description = "[" + name + "]\nA magical potion that\nrestores " + healingValue + " HP.";
    }

    @Override
    public void consumeItem(Player user){
        user.setCurrentHealth(user.getCurrentHealth() + healingValue);
        user.getGp().getUi().showMessage("Healed for " + healingValue + " HP!");
    }

    public void useItem(Player user){
        consumeItem(user);
    }
}