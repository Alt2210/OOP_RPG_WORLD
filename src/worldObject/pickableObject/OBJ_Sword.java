package worldObject.pickableObject;

import character.role.Player;
import item.Item;
import item.itemEquippable.*;
import main.GamePanel;
import worldObject.WorldObject;

public class OBJ_Sword extends WorldObject implements Pickable {

    public OBJ_Sword(GamePanel gp) {
        name = "Normal Sword";
        Item_Sword item = new Item_Sword(gp);
        image = item.getItp().getCurFrame();
    }

    @Override
    public Item convertToItem(GamePanel gp) {
        return new Item_Sword(gp);
    }

    @Override
    public void interactPlayer(Player player, int i, GamePanel gp) {
        Item itemToAdd = convertToItem(gp);
        if (player.getInventory().addItem(itemToAdd, 1)) {
            gp.getCurrentMap().getwObjects().remove(i); // Xóa đối tượng khỏi bản đồ
            gp.getUi().showMessage("Picked up a " + name + "!");
            // gp.playSoundEffect(...); // Thêm âm thanh nếu có
        } else {
            gp.getUi().showMessage("Inventory is full!");
        }
    }
}