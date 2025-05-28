package worldObject.unpickableObject;

import character.Player;
import worldObject.WorldObject;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Door extends WorldObject {
    public OBJ_Door() {
        name = "Door";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/door.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;
    }

    public void interactPlayer(Player player, int i){
        if (player.getHasKey() > 0) {
            player.getGp().getwObjects()[i] = null;
            player.decrementKeyCount();
            player.getGp().getUi().showMessage("You opened a door");
        }
        else{
            player.getGp().getUi().showMessage("You need a key");
        }
    }
}
