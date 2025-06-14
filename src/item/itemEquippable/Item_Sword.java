package item.itemEquippable;

import character.role.Player;
import imageProcessor.ItemImageProcessor;
import item.Item;
import item.itemEquippable.Equippable;
import item.itemEquippable.Item_Weapon;
import main.GamePanel;

public class Item_Sword extends Item_Weapon implements Equippable {

    public Item_Sword(GamePanel gp) {
        name = "Normal Sword";
        id = 201; // Chọn một ID mới chưa được sử dụng
        type = "WEAPON";
        weaponType = "Sword";
        description = "[" + name + "]\nMột thanh kiếm cũ kỹ.\nTăng 5 sát thương tấn công.";

        this.attackBonus = 5;
        this.buyPrice = 20;
        itp = new ItemImageProcessor(gp);
        itp.getImage("/weapon", "sword_normal");
    }

    @Override
    public void equipItem(Player user) {
        useItem(user);
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    @Override
    public void useItem(Player user) {
        // Logic chính: kiểm tra và trang bị/tháo ra
        if (user.getCurrentWeapon() == this) {
            // Nếu vũ khí này đã được trang bị -> tháo nó ra
            user.unequipWeapon();
        } else {
            // Nếu chưa -> trang bị nó
            user.equipWeapon(this);
        }
    }

    @Override
    public void specialBuff(Player user) {
        this.attackBonus = 20;
    }

}