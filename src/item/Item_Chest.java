package item;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Item_Chest extends SuperItem {
    public Item_Chest() {
        name = "Chest";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/chest.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
