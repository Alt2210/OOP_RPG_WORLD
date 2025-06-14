package skillEffect;

import character.Character;
import character.CombatableCharacter;
import main.GamePanel;
import java.awt.*;

public abstract class SkillEffect {
    protected GamePanel gp;
    protected int worldX, worldY;
    protected CombatableCharacter caster;
    protected int damage;
    protected boolean alive = true;
    protected Rectangle solidArea = new Rectangle(0, 0, 0, 0);
    protected int damageTickCounter; // Đếm ngược thời gian giữa các lần gây sát thương


    public SkillEffect(GamePanel gp) {
        this.gp = gp;
    }




    public abstract void update();
    public abstract void draw(Graphics2D g2);

    // Getters
    public CombatableCharacter getCaster() { return caster; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public int getDamageValue() { return damage; }
    public boolean isSingleHit(){
        return true;
    }

    public Rectangle getSolidArea() {
        return solidArea;
    }

    public int getWorldX() {
        return worldX;
    }

    public void setWorldX(int worldX) {
        this.worldX = worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public void setWorldY(int worldY) {
        this.worldY = worldY;
    }
}