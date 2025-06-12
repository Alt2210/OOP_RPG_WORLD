package worldObject.unpickableObject;

import character.role.Player;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Spike extends EnvironmentalHazard {

    public OBJ_Spike(GamePanel gp) {
        super(gp);
        name = "Spike Trap";
        this.damageAmount = 5;
        this.damageCooldown = 500;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/spike.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = gp.getTileSize();
        solidArea.height = gp.getTileSize();
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }
    @Override
    protected void applyEffect(Player player, GamePanel gp) {
        gp.getUi().showMessage("Bạn đã giẫm phải gai! -" + this.damageAmount + " HP");
        // Gây sát thương cho người chơi, truyền 'null' vì bẫy là môi trường
        player.receiveDamage(this.damageAmount, null);
    }
}