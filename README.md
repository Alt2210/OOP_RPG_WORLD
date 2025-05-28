# MyNewMMOGame - RPG Adventure

🚀 **Version 1.3** 🎮  
*Last updated: 02:12 PM +07, Wednesday, May 28, 2025*

## 📜 Introduction
Welcome to the latest version of our RPG game! This release focuses on enhancing core gameplay mechanics, optimizing sprite rendering, and introducing a basic combat system to deliver a smoother and more engaging player experience.

## ✨ Changelog (Version 1.3 vs. Version 1.2)

### Hệ thống Giao diện Người dùng (UI)
- **Version 1.2:** Displayed key count, time, messages, and supported game states (playState, pauseState, dialogueState, endGameState).
- **Version 1.3:**
    - **Added health bar:** Implemented `drawHealthBar` for `Player` and `MON_GreenSlime`, visually showing health percentage for better tracking.

### Quản lý Trạng thái Game (Game State Management)
- **Version 1.2:** GamePanel managed game states with basic `update()` and `draw()` logic; game ended on interaction with `NPC_Princess`.
- **Version 1.3:**
    - **Improved game over logic:** Added `onDeath` for `Player`, displaying a message (e.g., "You were defeated by Green Slime!") and switching to `endGameState`.

### Hệ thống Hội thoại (Dialogue System)
- **Version 1.2:** Basic DialogueManager supported `NPC_OldMan` and `NPC_Princess` conversations; `Player` triggered dialogues via `DialogueSpeaker`.
- **Version 1.3:**
    - **Enhanced interaction:** Ensured smooth return to `playState` after dialogue completion.

### Hệ thống Nhân vật (Character System)
- **Version 1.2:** `NPC_OldMan` and `NPC_Princess` featured movement and dialogue; `NPC_Princess` used left/right sprites despite 4-direction movement logic.
- **Version 1.3:**
    - **Player:**
        - **Fixed attack animation:** Attack sprites (`sodier_attack*.png`) now display when standing still, not just while moving.
        - **Direction-based attacks:** Added separate sprites for right (`sodier_attackright*.png`) and left (`sodier_attackleft*.png`), displayed based on `direction`.
        - **Optimized sprite rendering:** Fixed image distortion by scaling sprites with preserved aspect ratio, centered within tiles (`gp.getTileSize()`).
    - **NPC:**
        - **Optimized rendering:** `NPC_OldMan` and `NPC_Princess` now render with preserved sprite aspect ratio, avoiding distortion.
    - **Monster (New):**
        - **Introduced `MON_GreenSlime`:** First monster with combat logic, including health, attack, defense, and chasing behavior (`playerChasing`).
        - **Added health bar:** Monsters display health bars for player convenience.

### Hệ thống Chiến đấu (Combat System) - New
- **Version 1.2:** No combat system.
- **Version 1.3:**
    - **Basic implementation:** Added attributes `attack`, `defense`, `maxHealth`, `currentHealth`, and `attackCooldown` to `Character`.
    - **Combat mechanics:** `Player` attacks with the Space key, dealing damage to monsters; monster death triggers a message (e.g., "Đạt đẹp trai defeated Green Slime!").
    - **Interaction support:** `MON_GreenSlime` takes damage and dies (`onDeath`), with basic drop logic (`checkDrop`).

### Hệ thống Hình ảnh và Hoạt ảnh (Image Processing)
- **Version 1.2:** `CharacterImageProcessor` handled basic movement sprites and animations.
- **Version 1.3:**
    - **Direction-based attacks:** Added `attackRight` and `attackLeft` lists for directional attack sprites.
    - **Fixed distortion:** Scaled sprites to maintain aspect ratio, centered in tiles, eliminating stretching.
    - **Improved frame rate:** Reduced frame delay during attacks for smoother animations.

### Hệ thống Item
- **Version 1.2:** `Player` could pick up keys and open doors.
- **Version 1.3:**
    - Retained core pick-up and usage logic for `Key` and `Door`.

### Tái cấu trúc và Tối ưu hóa
- **Version 1.2:** Focused on basic encapsulation.
- **Version 1.3:**
    - **Enhanced encapsulation:** Improved `Character`, `Player`, and `Monster` with clearer attributes and methods.
    - **Better debugging:** Added detailed logs in `ImageProcessor` and `CharacterImageProcessor` for easier sprite loading error detection.

## 🤝 Future Plans

### Hoàn thiện Hệ thống Hội thoại
- Enable NPCs to have multiple dialogue branches based on context or quest status.
- Add player dialogue choices.
- Load dialogue content from files (text, JSON, XML) instead of hardcoding.

### Hệ thống Nhiệm vụ (Quest System)
- Design and implement a quest system for players to accept and complete from NPCs.
- Integrate dialogues with quest progression.

### Hoàn thiện Hệ thống Item và Inventory
- Fully implement `Inventory` and `ItemStack` classes.
- Allow `Player` to pick up various `WorldObject` types and convert them to `Item` in `Inventory`.
- Add item usage functionality (e.g., health restoration, power boosts).

### Mở rộng Hệ thống Chiến đấu
- Introduce new monster types with diverse behaviors and attributes.
- Implement an EXP and level system for `Player`.
- Add combat effects (screen shake, sound effects).

### Cải thiện AI cho NPC và Monster
- Enhance NPC and Monster behavior (e.g., ranged attacks, context-aware NPC reactions).
- Optimize `PathFinder` for complex maps.

### Thêm Âm thanh và Hiệu ứng
- Integrate background music and sound effects for actions (attacks, item pickups, dialogues).
- Add visual effects (screen shake, particles) for attacks or damage.

### Lưu và Tải Game (Save/Load System)
- Develop a system to save and load game states (player position, key count, quest progress).

### Mở rộng Bản đồ và Nội dung Game
- Add new areas, NPCs, items, and a richer storyline.
- Increase environmental interaction (e.g., breaking obstacles, discovering secrets).

### Tiếp tục Tái cấu trúc
- Review code to ensure encapsulation and OOP principles.
- Optimize performance as map size and entity count grow.

## 🙌 Acknowledgments
Thank you for reading and supporting the project! Stay tuned for more updates! 🎉