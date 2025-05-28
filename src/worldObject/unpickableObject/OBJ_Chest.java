package worldObject.unpickableObject;

import character.Player;
import worldObject.WorldObject;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Chest extends WorldObject {
    public OBJ_Chest() {
        name = "Chest";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/chest.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;
    }

    public void interactPlayer(Player player, int i){
        System.out.println("This is a chest");
    }
}
