package item;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Item_Key extends SuperItem {
    public Item_Key() {
        name = "Key";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
