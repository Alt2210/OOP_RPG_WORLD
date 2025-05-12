package object;

import entity.OBject;
import main.GamePanel;

public class OBJ_Lantern extends OBject {

    public static final String objName = "Lantern";

    public OBJ_Lantern(GamePanel gp)
    {
        super(gp);

        type = type_light;
        name = objName;
        down1 = setup("/objects/lantern",gp.tileSize,gp.tileSize);
        description = "[Lantern]\nIlluminates your \nsurroundings.";
        price = 200;
        lightRadius = 350;
    }
}
