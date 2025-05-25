# 🚀 VERSION 1.2 🎮 (Hoặc phiên bản phù hợp với bạn)

---

## 📜 Giới Thiệu Chung

Phiên bản mới nhất của game RPG, tiếp tục phát triển các tính năng cốt lõi và cải thiện cấu trúc dự án theo hướng đối tượng.

---

## ✨ Sửa đổi mới (Tổng kết từ Version 1.1 đến nay)

* **Hệ thống Giao diện Người dùng (UI):**
    * Tạo và phát triển lớp `UI` để hiển thị thông tin game (số chìa khóa, thời gian, tin nhắn).
    * Triển khai các màn hình cho các trạng thái game khác nhau: `playState`, `pauseState`, `dialogueState`, `endGameState`.
    * Cải thiện logic vẽ text, bao gồm cả việc căn giữa và tự động ngắt dòng cho hội thoại.
* **Quản lý Trạng thái Game (`gameState`):**
    * `GamePanel` giờ đây quản lý các trạng thái game khác nhau (`playState`, `pauseState`, `dialogueState`, `endGameState`).
    * Logic `update()` và `draw()` trong `GamePanel` và `UI` được điều chỉnh để hoạt động dựa trên `gameState` hiện tại.
* **Hệ thống Hội thoại (Dialogue System) - Bước đầu:**
    * Tạo package `dialogue` với các thành phần cốt lõi:
        * Interface `DialogueSpeaker` (định nghĩa đối tượng có khả năng nói chuyện).
        * Lớp `Dialogue` (quản lý một chuỗi các `DialogueLine`).
        * Lớp `DialogueLine` (đại diện cho một câu thoại).
        * Lớp `DialogueManager` (điều phối các cuộc hội thoại).
    * Các NPC (`NPC_OldMan`, `NPC_Princess`) bắt đầu implement `DialogueSpeaker` và sử dụng `DialogueManager` để xử lý hội thoại.
    * `Player` tương tác với `DialogueSpeaker` để kích hoạt hội thoại.
    * `KeyHandler` xử lý input để người chơi chuyển tiếp các câu thoại.
* **NPC (Nhân vật không phải người chơi):**
    * Tạo lớp `NPC_Princess` với logic di chuyển và hội thoại riêng.
    * Tinh chỉnh cách `NPC_Princess` hiển thị hoạt ảnh dựa trên các sprite chỉ có sẵn cho hướng trái/phải, ngay cả khi logic di chuyển là 4 hướng.
    * Logic kết thúc game khi tương tác với `NPC_Princess` (ban đầu) đã được thay đổi thành một cuộc hội thoại, sau đó có thể quay lại `playState` hoặc một kịch bản khác (do `DialogueManager` quyết định).

---

## 🤝 Làm thêm trong tương lai

* **Hoàn thiện Hệ thống Hội thoại:**
    * Phát triển đầy đủ chức năng cho `DialogueManager`.
    * Cho phép NPC có nhiều đoạn hội thoại khác nhau tùy theo ngữ cảnh, trạng thái nhiệm vụ.
    * Thêm tính năng lựa chọn hội thoại cho người chơi.
    * Tải nội dung hội thoại từ file (text, JSON, XML) thay vì hardcode.
* **Hệ thống Nhiệm vụ (Quest System):**
    * Thiết kế và triển khai hệ thống nhiệm vụ mà người chơi có thể nhận và hoàn thành từ NPC.
    * Tích hợp hội thoại với hệ thống nhiệm vụ.
* **Hoàn thiện Hệ thống Item và Inventory:**
    * Triển khai đầy đủ lớp `Inventory` và `ItemStack`.
    * Cho phép Player nhặt nhiều loại `WorldObject` khác nhau và chuyển thành `Item` trong `Inventory`.
    * Thêm chức năng sử dụng item.
* **Thêm Class Monster và Hệ thống Chiến đấu:**
    * Tạo lớp `Monster` với AI và thuộc tính chiến đấu.
    * Xây dựng cơ chế chiến đấu cơ bản.
* **Cải thiện AI cho NPC và Monster:**
    * Làm cho hành vi của NPC và Monster trở nên thông minh và đa dạng hơn.
* **Tiếp tục Cải thiện Encapsulation và Tái cấu trúc (Refactoring):**
    * Luôn rà soát code để đảm bảo tính đóng gói và các nguyên tắc OOP khác được tuân thủ.
    * Tái cấu trúc các phần code lớn khi cần thiết để dễ quản lý và mở rộng.
* **Thêm Âm thanh và Hiệu ứng:**
    * Tích hợp âm thanh nền, hiệu ứng âm thanh cho hành động, hội thoại.
* **Lưu và Tải Game (Save/Load System):**
    * Một tính năng quan trọng cho game RPG.
* **Mở rộng Bản đồ và Nội dung Game:**
    * Thêm các khu vực mới, NPC mới, item mới, và cốt truyện.

---

# Cảm ơn bạn đã đọc