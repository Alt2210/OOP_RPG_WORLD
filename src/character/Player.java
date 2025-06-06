package character;

import dialogue.DialogueSpeaker;
import item.Inventory;
import main.GamePanel;
import main.KeyHandler;
import sound.Sound;
import projectile.Fireball;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Character {

    public KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    private int hasKey;
    public Inventory inventory;


    // THÊM MỚI: Bộ đếm thời gian tấn công
    private int attackAnimationCounter = 0;

    private boolean canInteractWithCurrentNPC = true; // Cho phép tương tác với NPC hiện tại đang va chạm
    private Character currentlyCollidingNPC = null;   // NPC hiện tại đang va chạm
    private Character lastInteractedNPC = null;       // NPC cuối cùng đã hoàn thành hội thoại

    private int fireballManaCost;
    private int fireballCooldown;
    private final int FIREBALL_COOLDOWN_DURATION = 60; // Ví dụ: 1 giây (60 frames @ 60FPS)
    public Player(GamePanel gp, KeyHandler keyH) {

        super(gp);
        this.inventory = new Inventory(20);
        cip.setNumSprite(5);

        // Lưu tham chiếu đến KeyHandler.
        this.keyH = keyH;


        screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);
        screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);

        solidArea.x = 8; // Offset X từ góc trên bên trái sprite của Player
        solidArea.y = 16; // Offset Y từ góc trên bên trái sprite của Player
        solidArea.width = 32; // Chiều rộng vùng va chạm của Player
        solidArea.height = 32; // Chiều cao vùng va chạm của Player

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues(); // Thiết lập worldX, worldY, speed, direction ban đầu.
        cip.getImage("/player", "sodier");      // Tải hình ảnh hoạt ảnh của Player.
        setItems();
    }


    public void setItems(){
        //inventory.addItem(pickUpItem());
    }


    public void setAction(){}

    @Override // Sử dụng annotation @Override là một cách tốt để kiểm tra lỗi nếu phương thức ở lớp cha bị đổi tên hoặc xóa.
    public void setDefaultValues() {

        worldX = gp.getTileSize() * 10; // Ví dụ: Bắt đầu ở cột 30
        worldY = gp.getTileSize() * 20; // Ví dụ: Bắt đầu ở hàng 30
        speed = 4; // Tốc độ di chuyển của Player
        direction = "down"; // Hướng ban đầu của Player khi game bắt đầu
        hasKey=0; // ms vao k co key

        maxHealth = 100;
        currentHealth = maxHealth;
        attack = 10; // Giá trị tấn công của người chơi
        defense = 2; // Giá trị phòng thủ của người chơi
        attackRange = 100;
        name = "a Dung chim to";
        maxMana = 500; // Ví dụ: Player có tối đa 500 Mana
        currentMana = maxMana;
        fireballManaCost = 10; // Ví dụ: Fireball tốn 10 Mana
        fireballCooldown = 0; // Sẵn sàng sử dụng khi bắt đầu
    }
