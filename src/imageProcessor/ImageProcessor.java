package imageProcessor;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public abstract class ImageProcessor {
    private GamePanel gp;
    protected int numSprite;
    protected int spriteCounter = 0;
    protected int spriteNum = 0;

    public ImageProcessor(GamePanel gp) {
        this.gp = gp;
    }

    public int getNumSprite() {
        return numSprite;
    }

    public void setSpriteNum(int spriteNum) {
        this.spriteNum = spriteNum;
    }

    public void setNumSprite(int numSprite) {
        this.numSprite = numSprite;
    }

    public BufferedImage getCurFrame(){
        BufferedImage image = null;
        return image;
    };

    public BufferedImage setup(String url){
        BufferedImage image = null;
        InputStream is = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void getImage(){}

    public void update(){
        spriteCounter++;
        if(spriteCounter > gp.getFPS()/10) {
            spriteNum++;
            if (spriteNum >= numSprite) {
                spriteNum = 0;
            }
            spriteCounter = 0;
        }
    }

}
