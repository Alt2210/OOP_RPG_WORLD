package item.itemEquippable;

import character.role.Player;
import item.Item;

public abstract class Item_Weapon extends Item {
    protected int attackBonus;
    protected int manaBonus;

    protected String weaponType;

    public int getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    public String getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(String weaponType) {
        this.weaponType = weaponType;
    }

    public void specialBuff(Player user){   };
}
