package worldObject.pickableObject;

import character.role.Player;
import item.Item;
import item.itemConsumable.Item_HealthPotion;
import main.GamePanel;
import sound.Sound;
import worldObject.WorldObject;
import worldObject.pickableObject.Pickable;
// import sound.Sound; // Bỏ comment nếu bạn muốn thêm âm thanh

public class OBJ_HealthPotion extends WorldObject implements Pickable {

    public OBJ_HealthPotion(GamePanel gp) {
        name = "Health Potion";
        // Lấy hình ảnh từ chính đối tượng Item của nó
        Item_HealthPotion item = new Item_HealthPotion(gp);
        image = item.getItp().getCurFrame();
    }

    @Override
    public Item convertToItem(GamePanel gp) {
        return new Item_HealthPotion(gp);
    }

    @Override
    public void interactPlayer(Player player, int i, GamePanel gp) {
        Item itemToAdd = convertToItem(gp);
        if (player.getInventory().addItem(itemToAdd, 1)) {
            gp.getCurrentMap().getwObjects().remove(i); // Xóa đối tượng khỏi bản đồ
            gp.getUi().showMessage("Picked up a " + name + "!");
            gp.playSoundEffect(Sound.SFX_POWERUP);
        } else {
            gp.getUi().showMessage("Inventory is full!");
        }
    }
}