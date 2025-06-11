package item;

import character.role.Player;
import item.itemConsumable.Consumable;
import item.itemEquippable.Equippable;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Inventory {
    private List<ItemStack> items;
    private int capacity; // Số ô tối đa

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public boolean addItem(Item item, int quantity) {
        if (item == null || quantity <= 0) return false;

        // Kiểm tra xem item có thể stack và đã có trong inventory chưa
        for (ItemStack stack : items) {
            if (stack.getItem().getId() == item.getId() /* && item.isStackable() */) {
                // Thêm vào stack hiện có (nếu còn chỗ trong stack đó, hoặc item cho phép stack vô hạn)
                stack.addQuantity(quantity);
                return true;
            }
        }

        // Nếu chưa có hoặc không stack được, thêm vào ô mới nếu còn chỗ
        if (items.size() < capacity) {
            items.add(new ItemStack(item, quantity));
            return true;
        }
        return false; // Inventory đầy
    }
    public int getItemStack(){
        return items.size();
    }

    public ItemStack getItemStack(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public void useItemInSlot(int slotIndex, Player player) {
        // Lấy ra ItemStack tại vị trí được chỉ định
        ItemStack selectedStack = getItemStack(slotIndex);

        // Chỉ xử lý nếu thực sự có vật phẩm ở ô này
        if (selectedStack != null) {

            // Lấy ra đối tượng Item từ ItemStack
            Item selectedItem = selectedStack.getItem();

            // Kiểm tra xem vật phẩm có phải là loại dùng được (Consumable) không
            if (selectedItem instanceof Consumable) {

                // Kích hoạt hiệu ứng của vật phẩm
                ((Consumable) selectedItem).consumeItem(player);

                // Giảm số lượng vật phẩm đi 1
                selectedStack.removeQuantity(1);

                // Nếu số lượng vật phẩm trong stack về 0, xóa nó khỏi túi đồ
                if (selectedStack.getQuantity() <= 0) {
                    removeStack(slotIndex);
                }
            } else if (selectedItem instanceof Equippable) {
                ((Equippable) selectedItem).equipItem(player);
                // Vật phẩm trang bị không bị xóa khỏi túi đồ sau khi dùng
            }

            else {
                // Nếu là vật phẩm không dùng được (ví dụ: Key), thông báo cho người chơi
                // Chúng ta cần truy cập UI của GamePanel thông qua Player
                player.getGp().getUi().showMessage("Cannot use this item.");
            }
        }
    }

    // Thêm các phương thức: removeItem, getItem, hasItem, v.v.
    public boolean removeItem(Item itemToRemove, int quantityToRemove) {
        if (itemToRemove == null || quantityToRemove <= 0) {
            return false;
        }

        Iterator<ItemStack> iterator = items.iterator();
        boolean itemFoundAndModified = false;

        while (iterator.hasNext()) {
            ItemStack currentStack = iterator.next();
            if (currentStack.getItem().getId() == itemToRemove.getId()) {
                if (currentStack.getQuantity() > quantityToRemove) {
                    currentStack.removeQuantity(quantityToRemove);
                } else {
                    // Xóa toàn bộ stack nếu số lượng cần xóa >= số lượng đang có
                    iterator.remove(); // Xóa an toàn khi đang duyệt
                }
                itemFoundAndModified = true;
                // Giả sử mỗi loại item chỉ nằm trong một stack (do logic addItem hiện tại)
                // Nếu có thể có nhiều stack cùng loại item, bạn có thể cần điều chỉnh logic này
                // để tiếp tục xóa ở các stack khác nếu quantityToRemove chưa hết.
                // Hiện tại, nó sẽ chỉ tác động lên stack đầu tiên tìm thấy.
                break;
            }
        }
        return itemFoundAndModified;
    }
    public ItemStack removeStack(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < items.size()) {
            return items.remove(slotIndex);
        }
        return null;
    }
    public boolean hasItem(Item itemToFind) {
        if (itemToFind == null) return false;
        for (ItemStack stack : items) {
            if (stack.getItem().getId() == itemToFind.getId() && stack.getQuantity() > 0) {
                return true;
            }
        }
        return false;
    }
    public boolean hasItem(Item itemToFind, int minQuantity) {
        if (itemToFind == null || minQuantity <= 0) return false;
        int count = 0;
        for (ItemStack stack : items) {
            if (stack.getItem().getId() == itemToFind.getId()) {
                count += stack.getQuantity();
                if (count >= minQuantity) {
                    return true;
                }
            }
        }
        return false;
    }
    public int getTotalQuantity(Item itemToFind) {
        if (itemToFind == null) return 0;
        int totalQuantity = 0;
        for (ItemStack stack : items) {
            if (stack.getItem().getId() == itemToFind.getId()) {
                totalQuantity += stack.getQuantity();
            }
        }
        return totalQuantity;
    }
    public ItemStack findItemStack(Item itemToFind) {
        if (itemToFind == null) return null;
        for (ItemStack stack : items) {
            if (stack.getItem().getId() == itemToFind.getId()) {
                return stack;
            }
        }
        return null;
    }
    public boolean isFull() {
        return items.size() >= capacity;
    }
    public void clear() {
        items.clear();
    }
    public List<ItemStack> getAllItemStacks() {
        // Trả về một bản sao để ngăn chặn sửa đổi từ bên ngoài
        return new ArrayList<>(items);
    }

    public int getCapacity() {
        return this.capacity;
    }
}