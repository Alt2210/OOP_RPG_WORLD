package imageProcessor;

import character.Character;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CharacterImageProcessor extends ImageProcessor{
    private ArrayList<BufferedImage> up;
    private ArrayList<BufferedImage> down;
    private ArrayList<BufferedImage> left;
    private ArrayList<BufferedImage> right;

    private Character character;

    public CharacterImageProcessor(GamePanel gp, Character character) {
        super(gp);

        this.character = character;
        up = new ArrayList<BufferedImage>();
        down = new ArrayList<BufferedImage>();
        left = new ArrayList<BufferedImage>();
        right = new ArrayList<BufferedImage>();
    }

    @Override
    public BufferedImage getCurFrame(){
        switch (character.direction) {
            case "up":
                for(int i = 0; i < numSprite; i++){
                    if(spriteNum == i) {
                        return up.get(i);
                    }
                }
            case "down":
                for(int i = 0; i < numSprite; i++){
                    if(spriteNum == i) {
                        return down.get(i);
                    }
                }
            case "left":
                for(int i = 0; i < numSprite; i++){
                    if(spriteNum == i) {
                        return left.get(i);
                    }
                }
            case "right":
                for(int i = 0; i < numSprite; i++){
                    if(spriteNum == i) {
                        return right.get(i);
                    }
                }
            default:
                return down.get(0);
        }
    }

    public void getImage(String folder, String name){

        for(int i = 0; i < numSprite; i++){
            BufferedImage image;
            String UP_IMAGE_PATH = folder + "/" + name + "right" + String.valueOf(i+1) + ".png";
            System.out.println(UP_IMAGE_PATH);
            image = setup(UP_IMAGE_PATH);
            up.add(image);
            String RIGHT_IMAGE_PATH = folder + "/" + name + "right" + String.valueOf(i+1) + ".png";
            image = setup(RIGHT_IMAGE_PATH);
            right.add(image);
            String LEFT_IMAGE_PATH = folder + "/" + name + "left" + String.valueOf(i+1) + ".png";
            image = setup(LEFT_IMAGE_PATH);
            left.add(image);
            String DOWN_IMAGE_PATH = folder + "/" + name + "left" + String.valueOf(i+1) + ".png";
            image = setup(DOWN_IMAGE_PATH);
            down.add(image);
        }
    }
}
