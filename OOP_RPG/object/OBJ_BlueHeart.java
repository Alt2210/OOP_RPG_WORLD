package object;

import entity.Character;
import entity.OBject;
import main.GamePanel;

public class OBJ_BlueHeart extends OBject {

    public static final String objName = "Blue Heart";
    public OBJ_BlueHeart(GamePanel gp)
    {
        super(gp);

        this.gp = gp;

        type = type_pickupOnly;
        name = objName;
        down1 = setup("/objects/blueheart", gp.tileSize, gp.tileSize);
//        setDialogues();
    }
//    public void setDialogues()
//    {
//        dialogues[0][0] = "You pick up a beautiful blue gem.";
//        dialogues[0][1] = "You find the Blue Heart, the legendary treasure!";
//    }
    public boolean use(Character entity) //when pickup this method will be called
    {
        gp.gameState = gp.cutsceneState;
        gp.csManager.sceneNum = gp.csManager.ending;
        return true;
    }

}
