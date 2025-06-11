package item.itemEquippable;

import character.role.Player;
import imageProcessor.ItemImageProcessor;
import main.GamePanel;

public class Item_Book extends Item_Weapon implements Equippable{
    public Item_Book(GamePanel gp) {
        name = "Normal Book";
        id = 202; // Chọn một ID mới chưa được sử dụng
        type = "WEAPON";
        weaponType = "Book";
        description = "[" + name + "]\nMột quyển sách cũ kỹ.\nTăng 5 sát thương tấn công.";

        this.attackBonus = 5;
        itp = new ItemImageProcessor(gp);
        itp.getImage("/weapon", "book_normal");
    }

    @Override
    public void equipItem(Player user) {
        if("astrologist".equals(user.getCharacterClassIdentifier())){
            specialBuff(user);
        }
        user.equipWeapon(this);
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    @Override
    public void useItem(Player user) {
        equipItem(user);
    }

    @Override
    public void specialBuff(Player user) {
        user.setMaxMana(user.getMaxMana() + 30);
        user.setCurrentMana(user.getMaxMana());
    }
}
