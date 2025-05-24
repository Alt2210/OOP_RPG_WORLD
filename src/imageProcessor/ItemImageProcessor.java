/*DANG SUY NGHI VE VIEC TEST*/

package imageProcessor;

import main.GamePanel;

import java.awt.image.BufferedImage;

public class ItemImageProcessor extends ImageProcessor{
    private BufferedImage image;

    public ItemImageProcessor(GamePanel gp) {
        super(gp);
    }

    @Override
    public BufferedImage getCurFrame(){
        return image;
    }

    public void getImage(String folder, String name){
        String url = folder + "/" + name + ".png";
        image = setup(url);
    }
}
