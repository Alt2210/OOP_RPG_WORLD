package worldObject.unpickableObject;

import character.role.Player;
import item.Item_Key;
import main.GamePanel;
import sound.Sound;
import worldObject.WorldObject;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Door extends WorldObject {
    public OBJ_Door() {
        name = "Door";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/door_iron.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;
    }

    public void interactPlayer(Player player, int i, GamePanel gp){
        // Tạo một đối tượng Item_Key tạm thời để kiểm tra
        Item_Key keyToFind = new Item_Key(gp);

        // Kiểm tra xem người chơi có Item_Key trong túi đồ không
        if (player.getInventory().hasItem(keyToFind)) {
            // Nếu có, xóa cửa
            gp.getwObjects()[i] = null;

            // Xóa 1 chìa khóa khỏi túi đồ
            player.getInventory().removeItem(keyToFind, 1);

            player.getGp().getUi().showMessage("You opened a door");
            gp.playSoundEffect(Sound.SFX_UNLOCK_DOOR);
        }
        else{
            player.getGp().getUi().showMessage("You need a key");
        }
    }
}
