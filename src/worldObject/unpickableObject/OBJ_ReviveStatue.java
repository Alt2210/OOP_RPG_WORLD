package worldObject.unpickableObject;

import character.role.Player;
import main.GamePanel;
import worldObject.WorldObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class OBJ_ReviveStatue extends WorldObject {

    // không lưu liên tiếp
    private long lastSaveTime = 0;
    private static final long SAVE_COOLDOWN = 2000; // 2 giây

    public OBJ_ReviveStatue(GamePanel gp) {
        name = "Revive Statue";
        collision = true;

        try {
            InputStream is = getClass().getResourceAsStream("/objects/revive_statue.png");
            if (is != null) {
                image = ImageIO.read(is);
            } else {
                System.err.println("Warning: Cannot find /objects/revive_statue.png. Using a placeholder.");
                image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image.createGraphics();
                g2.setColor(new Color(100, 120, 150));
                g2.fillRect(0, 0, gp.getTileSize(), gp.getTileSize());
                g2.setColor(Color.YELLOW);
                g2.drawString("SAVE", 5, gp.getTileSize()/2);
                g2.dispose();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        solidArea.x = 0;
        solidArea.y = 8;
        solidArea.width = 48;
        solidArea.height = 40;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }
    @Override
    public void interactPlayer(Player player, int i, GamePanel gp) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSaveTime > SAVE_COOLDOWN) {
            gp.getSaveLoadManager().saveGame();
            lastSaveTime = currentTime;
        }
    }
}