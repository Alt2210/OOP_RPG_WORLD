package map;
import character.monster.Monster;
import character.Character;
import main.GamePanel;
import worldObject.WorldObject;
import java.util.ArrayList;

import java.util.List;

public abstract class GameMap {
    protected GamePanel gp;
    protected List<WorldObject> worldObjects; //
    protected List<Character> npcs; //
    protected List<Monster> monsters; //

    public GameMap(GamePanel gp) {
        this.gp = gp;
        this.worldObjects = new ArrayList<>(); //
        this.npcs = new ArrayList<>(); //
        this.monsters = new ArrayList<>(); //
    }

    public abstract void initialize();

    public List<WorldObject> getwObjects() {
        return worldObjects;
    }

    public List<Character> getNpc() {
        return npcs;
    }

    public List<Monster> getMonster() {
        return monsters;
    }
}