package worldObject.pickableObject;

import character.role.Player;
import item.Item;
import item.Item_Key;
import main.GamePanel;
import sound.Sound;
import worldObject.WorldObject;

public class OBJ_Key extends WorldObject implements Pickable {

    public OBJ_Key(GamePanel gp) {
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

    @Override
    public void interactPlayer(Player player, int i, GamePanel gp){
        Item itemToAdd = convertToItem(gp);
        if (player.getInventory().addItem(itemToAdd, 1)) {
            gp.getCurrentMap().getwObjects().remove(i); // Xóa đối tượng khỏi bản đồ
            gp.getUi().showMessage("Picked up a " + name + "!");
            gp.playSoundEffect(Sound.SFX_PICKUP_KEY);
        } else {
            gp.getUi().showMessage("Inventory is full!");
        }
    }
}
