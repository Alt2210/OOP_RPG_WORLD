package item;

public class ItemStack {
    private Item item;
    private int quantity;

    public ItemStack(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() { return item; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void addQuantity(int amount) { this.quantity += amount; }
    // Có thể thêm: canStack(ItemStack otherStack), mergeStack(ItemStack otherStack)
}