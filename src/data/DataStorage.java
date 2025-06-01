package data;

import java.io.Serializable;

public class DataStorage implements Serializable {
    // Player Stats
    int level;
    int maxHealth; // Đổi từ maxLife để nhất quán với Player.java (nếu có)
    int currentHealth; // Đổi từ life
    int maxMana;    // Sửa lỗi cú pháp, thêm dấu chấm phẩy
    int currentMana;
    int strength; // Sức mạnh (có thể là base attack)
    int dexterity; // Khéo léo (có thể ảnh hưởng defense, accuracy)
    int exp;

    // Player Position and Direction
    int playerWorldX;
    int playerWorldY;
    String playerDirection;

    // Player Inventory/Quest Items (Ví dụ đơn giản)
    int hasKey; // Số lượng chìa khóa Player có

    // Game World State (Ví dụ)
    double playtime; // Thời gian chơi (từ UI)
    // String currentMapName; // Nếu game có nhiều map

    // TODO: Thêm các trường dữ liệu khác nếu cần:
    // - Thông tin chi tiết về inventory (ví dụ: một ArrayList<String> tên item, hoặc một đối tượng Inventory phức tạp hơn)
    // - Trạng thái nhiệm vụ (Quest progress)
    // - Vị trí/trạng thái của các NPC quan trọng nếu chúng thay đổi
    // - Trạng thái của các đối tượng tương tác trong thế giới (ví dụ: cửa đã mở, rương đã lấy)
}