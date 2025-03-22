package Entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

public class Player extends Entity{

    GamePanel gp;
    KeyHandler KeyH;

    public final int ScreenX;
    public final int ScreenY;

    public Player(GamePanel gp, KeyHandler KeyH){
        this.gp = gp;
        this.KeyH = KeyH;

        ScreenX = gp.screenWidth/2 - (gp.tileSize/2);
        ScreenY = gp.screenHeight/2 - (gp.tileSize/2);

        this.solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.height = 32;
        solidArea.width = 32;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){
        WorldX = gp.tileSize * 50;
        WorldY = gp.tileSize * 50;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage(){
        try{

            up1 = ImageIO.read(getClass().getResourceAsStream("/Player/walk_u1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/Player/walk_u2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/Player/walk_d1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/Player/walk_d2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/Player/walk_l1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/Player/walk_l2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/Player/walk_r1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/Player/walk_r2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void update(){

        if(KeyH.upPressed == true || KeyH.downPressed == true ||
                KeyH.leftPressed == true || KeyH.rightPressed == true){
            if(KeyH.upPressed){
                direction = "up";
            }
            if(KeyH.downPressed){
                direction = "down";
            }
            if(KeyH.leftPressed){
                direction = "left";
            }
            if(KeyH.rightPressed){
                direction = "right";
            }

            collisionOn = false;
            gp.cChecker.checkTile(this);

            if(collisionOn == false){
                switch(direction){
                    case "up":
                        WorldY -= speed;
                        break;
                    case "down":
                        WorldY += speed;
                        break;
                    case "left":
                        WorldX -= speed;
                        break;
                    case "right":
                        WorldX += speed;
                        break;
                }
            }

            spriteCounter++;
            if(spriteCounter > 10){
                if(spriteNum == 1){
                    spriteNum = 2;
                }
                else if(spriteNum == 2){
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }

    }

    public void draw(Graphics2D g2){

//        g2.setColor(Color.white);
//        g2.fillRect(x, y, gp.tileSize, gp.tileSize);

        BufferedImage image = null;

        switch(direction) {
            case "up":
                if(spriteNum == 1){
                    image = up1;
                }
                if(spriteNum == 2){
                   image = up2;
                }
                break;
            case "down":
                if(spriteNum == 1){
                    image = down1;
                }
                if(spriteNum == 2){
                    image = down2;
                }
                break;
            case "left":
                if(spriteNum == 1){
                    image = left1;
                }
                if(spriteNum == 2){
                    image = left2;
                }
                break;
            case "right":
                if(spriteNum == 1){
                    image = right1;
                }
                if(spriteNum == 2){
                    image = right2;
                }
                break;
        }

        g2.drawImage(image, ScreenX, ScreenY, gp.tileSize, gp.tileSize, null);
    }
}
