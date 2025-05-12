package object;

import entity.OBject;
import main.GamePanel;

public class OBJ_Boots extends OBject {
    public static final String objName = "Boots";
    public OBJ_Boots(GamePanel gp)
    {
        super(gp);
        name = objName;
        down1 = setup("/objects/boots",gp.tileSize,gp.tileSize);
        price = 75;
    }
}
