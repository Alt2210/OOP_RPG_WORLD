package character.monster;

import ai.Node;
import ai.PathFinder;
import character.Character;
import character.Player;
import main.GamePanel;
import worldObject.WorldObject;

import java.awt.*;
import java.util.Random;

public abstract class Monster extends Character {

    protected int exp;
    PathFinder pathFinder;

    public Monster(GamePanel gp) {
        super(gp);
    }

    @Override
    public abstract void draw(Graphics2D g2);

    @Override
    public void update() {
        super.update(); // Gọi logic update của lớp Character (di chuyển, animation, cooldown)

    }
    public void checkStartChasingOrNot(Character target, int distance, int rate) {
        if (getTileDistance(target) < distance) {
            int i = new Random().nextInt(rate);

            if (i == 0) {
                onPath = true;
            }
        }
    }

    public void checkStopChasingOrNot(Character target, int distance, int rate) {
        if (getTileDistance(target) > distance) {
            int i = new Random().nextInt(rate);

            if (i == 0) {
                onPath = false;
            }
        }
    }

    public void getRandomDirection(int interval) {
        actionLockCounter++;

        if (actionLockCounter > interval) {
            Random random = new Random();
            int i = random.nextInt(100) + 1; // pick up a number from 1 to 100

            if (i <= 25) { direction = "up"; }
            if (i > 25 && i <= 50) { direction = "down"; }
            if (i > 50 && i <= 75) { direction = "left"; }
            if (i > 75) { direction = "right"; }

            actionLockCounter = 0;
        }
    }

    public void damageReaction() {
        actionLockCounter = 0;
//        direction = gp.player.direction;
        onPath = true;
    }
    public void damageReaction(Character attacker) {
        actionLockCounter = 0;
//        direction = gp.player.direction;
        onPath = true;
    }

    // Phương thức thả vật phẩm tại vị trí của quái vật
    protected void dropItem(WorldObject item) {
        for (int i = 0; i < gp.getwObjects().length; i++) {
            if (gp.getwObjects()[i] == null) {
                gp.getwObjects()[i] = item;
                item.worldX = this.worldX;
                item.worldY = this.worldY;
                break;
            }
        }
    }





}
