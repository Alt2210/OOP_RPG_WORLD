package item;

import java.util.ArrayList;
import java.util.List;

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

    // Thêm các phương thức: removeItem, getItem, hasItem, v.v.
}