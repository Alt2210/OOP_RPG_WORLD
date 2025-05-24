package worldObject.unpickableObject;

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
}
