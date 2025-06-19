package character.monster;

import character.CombatableCharacter;
import character.role.Player;
import pathfinder.PathFinder;
import character.Character;
import main.GamePanel;
import worldObject.WorldObject;

import java.awt.*;
import java.util.Random;

public abstract class Monster extends CombatableCharacter {

    protected int exp;
    protected int coinValue;
    protected PathFinder pathFinder;
    protected int contactDamageAmount;
    protected int contactDamageCooldown;
    protected final int CONTACT_DAMAGE_COOLDOWN_DURATION = 30; // Ví dụ: 0.5 giây Player miễn nhiễm sát thương chạm từ Monster này

    public Monster(GamePanel gp) {
        super(gp);
        this.contactDamageCooldown = 0;
    }
    public int getContactDamageAmount() {
        return contactDamageAmount;
    }

    // Các phương thức quản lý cooldown sát thương chạm
    public void updateContactDamageCooldown() {
        if (contactDamageCooldown > 0) {
            contactDamageCooldown--;
        }
    }

    public boolean canDealContactDamage() {
        return contactDamageCooldown == 0;
    }

    public void resetContactDamageCooldown() {
        contactDamageCooldown = CONTACT_DAMAGE_COOLDOWN_DURATION;
    }
    @Override
    public abstract void draw(Graphics2D g2);

    @Override
    public void update() {
        collisionOn = false;
        gp.getcChecker().checkTile(this);
        gp.getcChecker().checkItem(this, false); // Quái vật va chạm với vật thể nhưng không nhặt
        gp.getcChecker().checkEntity(this, gp.getCurrentMap().getNpc()); // Va chạm với NPC
        gp.getcChecker().checkEntity(this, gp.getCurrentMap().getMonster()); // Va chạm với quái vật khác
        gp.getcChecker().checkPlayer(this); // Va chạm với người chơi
        super.update(); // Gọi logic update của lớp Character (di chuyển, animation, cooldown)
        updateContactDamageCooldown();
    }

    public abstract void playerChasing();

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
    public void damageReaction(CombatableCharacter attacker) {
        actionLockCounter = 0;
//        direction = gp.player.direction;
        onPath = true;
    }

    // Phương thức thả vật phẩm tại vị trí của quái vật
    protected void dropItem(WorldObject item) {
        if (gp.getCurrentMap() != null) { // Đảm bảo có map hiện tại
            gp.getCurrentMap().getwObjects().add(item); // Thêm item vào danh sách của bản đồ
            item.setWorldX(this.worldX);
            item.setWorldY(this.worldY);
        } else {
            System.err.println("Monster.dropItem: Cannot drop item, currentMap is null.");
        }
    }
    protected void onDeath(CombatableCharacter attacker) {
        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            player.gainExp(this.exp); // this.exp đã có sẵn trong lớp Monster
            player.gainCoin(this.coinValue);
        }

        //checkDrop();
        gp.getUi().showMessage(attacker.getName() + " đã đánh bại " + getName() + "!");
    }

    protected void giveExp(CombatableCharacter attacker){
        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            player.gainExp(this.exp);
        }
    }
}
