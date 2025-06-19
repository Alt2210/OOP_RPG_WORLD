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
        this.buyPrice = 20;
        itp = new ItemImageProcessor(gp);
        itp.getImage("/weapon", "book_normal");
    }

    @Override
    public void equipItem(Player user) {
        // Phương thức này không còn được sử dụng trực tiếp từ Inventory nữa,
        // nhưng chúng ta có thể giữ lại để tuân thủ interface Equippable
        // hoặc gọi useItem từ đây.
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
        user.setMaxMana(user.getMaxMana() + 30);
        user.setCurrentMana(user.getMaxMana());
    }
    @Override
    public void unapplySpecialBuff(Player user) {
        // LOGIC GỠ BUFF
        if("astrologist".equals(user.getCharacterClassIdentifier())){
            // **QUAN TRỌNG: Đảm bảo đây là phép trừ (-)**
            user.setMaxMana(user.getMaxMana() - 30);
            // Đảm bảo mana hiện tại không vượt quá max mana mới sau khi đã trừ
            if (user.getCurrentMana() > user.getMaxMana()) {
                user.setCurrentMana(user.getMaxMana());
            }
        }
    }
}
