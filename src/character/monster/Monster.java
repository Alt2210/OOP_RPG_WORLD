package character.monster;

import character.Character;
import main.GamePanel;

import java.awt.*;
import java.util.Random;

public abstract class Monster extends Character {

    protected int exp;

    public Monster(GamePanel gp) {
        super(gp);
    }

    @Override
    public void draw(Graphics2D g2) {

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
}
