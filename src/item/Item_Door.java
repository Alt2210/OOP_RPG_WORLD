package item;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Item_Door extends SuperItem {
    public Item_Door() {
        name = "Door";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/door.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;
    }
}
