package character.role;
import item.Inventory;

import character.Character;
import character.NPC_Princess;
import dialogue.DialogueSpeaker;
import item.Item;
import item.itemEquippable.Equippable;
import item.itemEquippable.Item_Weapon;
import main.GamePanel;
import main.KeyHandler;
import skill.Skill;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Player extends character.Character {
    protected boolean attackDamageAppliedThisSwing = false;
    public KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    protected int hasKey = 0;
    protected String characterClassIdentifier;
    protected Inventory inventory;

    private boolean canInteractWithCurrentNPC = true; // Cho phép tương tác với NPC hiện tại đang va chạm
    private Character currentlyCollidingNPC = null;   // NPC hiện tại đang va chạm
    private Character lastInteractedNPC = null;       // NPC cuối cùng đã hoàn thành hội thoại

    protected int attackStateCounter = 0;
    protected int currentAttackStateDuration = 30;

    protected int baseAttack;
    protected Item_Weapon currentWeapon;

    public int level;
    public int currentExp;
    public int expToNextLevel;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.inventory = new Inventory(20);
        this.keyH = keyH;
        this.screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);
        this.screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public Inventory getInventory() {
        return inventory;
    }

    protected abstract void loadCharacterSprites();
    protected abstract void performNormalAttackAction();
    protected abstract void drawCharacterSpecifics(Graphics2D g2);

    @Override
    public boolean isAttacking() {
        return this.attackStateCounter > 0;
    }


    @Override
    public void update() {

        coolDown();

        if (!isAttacking()) { // Chỉ xử lý input mới khi không đang trong hành động tấn công
            // ... (xử lý input di chuyển và tấn công thường)

            // Thay thế handleSkillInputs() bằng logic mới
            if (keyH.skill1Pressed) {
                activateSkill(0); // Kích hoạt kỹ năng ở vị trí số 0 (ví dụ Fireball)
            }
            if (keyH.skill2Pressed){
                activateSkill(1);
            }

            // Bạn có thể thêm else if (keyH.skill2Pressed) { activateSkill(1); } sau này
        }

        // ====================================================================
        //  PHẦN 1: XỬ LÝ LOGIC KHI PLAYER ĐANG TRONG TRẠNG THÁI TẤN CÔNG
        // ====================================================================
        if (isAttacking()) {
            // Đếm ngược thời gian của animation tấn công
            attackStateCounter--;

            // Xác định "khung hình" sẽ gây sát thương
            int hitFrame = currentAttackStateDuration / 2;

            // Tại đúng khung hình đó, gọi phương thức checkAttack() để gây sát thương
            if (attackStateCounter == hitFrame && !attackDamageAppliedThisSwing) {
                checkAttack();
                attackDamageAppliedThisSwing = true;
            }

            // Khi animation kết thúc, thoát khỏi trạng thái tấn công
            if (attackStateCounter <= 0) {
                attackStateCounter = 0;
                // Không cần làm gì thêm, vòng lặp tiếp theo isAttacking() sẽ là false
            }

            // ====================================================================
            //  PHẦN 2: XỬ LÝ LOGIC KHI PLAYER KHÔNG TẤN CÔNG (SẴN SÀNG HÀNH ĐỘNG)
            // ====================================================================
        } else {
            if (actionLockCounter > 0) {
                actionLockCounter--;
            }
            else {
                // --- 2a. Xử lý Input cho Hành động (Tấn công, Dùng skill) ---
                if (keyH.attackPressed && canAttack()) {
                    performNormalAttackAction();
                }


                // --- 2b. Xử lý Input cho Di chuyển ---
                boolean isTryingToMove = (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed);
                if (isTryingToMove) {
                    if (keyH.upPressed) direction = "up";
                    else if (keyH.downPressed) direction = "down";
                    else if (keyH.leftPressed) direction = "left";
                    else if (keyH.rightPressed) direction = "right";

                    // Kiểm tra va chạm với Tile, Item, NPC
                    collisionOn = false;
                    gp.getcChecker().checkTile(this);
                    int itemIndex = gp.getcChecker().checkItem(this, true);
                    pickUpItem(itemIndex);
                    int npcIndex = gp.getcChecker().checkEntity(this, gp.getNpc());

                    if (npcIndex != 999) { // Player đang va chạm với một NPC
                        Character newlyCollidedNPC = gp.getNpc()[npcIndex];
                        this.collisionOn = true; // Player bị chặn bởi NPC này

                        if (this.currentlyCollidingNPC != newlyCollidedNPC) {
                            this.currentlyCollidingNPC = newlyCollidedNPC;
                            this.canInteractWithCurrentNPC = true;
                        }

                        if (gp.gameState == gp.playState && this.canInteractWithCurrentNPC && this.currentlyCollidingNPC != null) {
                            interactWithNPC(npcIndex); // Phương thức này sẽ đặt canInteractWithCurrentNPC = false
                        }
                    } else { // Player KHÔNG va chạm với bất kỳ NPC nào
                        if (this.currentlyCollidingNPC != null) {
                            this.canInteractWithCurrentNPC = true;
                            this.currentlyCollidingNPC = null;
                        }
                    }

                    // Cập nhật vị trí nếu không có va chạm
                    if (!collisionOn) {
                        switch (direction) {
                            case "up":
                                worldY -= speed;
                                break;
                            case "down":
                                worldY += speed;
                                break;
                            case "left":
                                worldX -= speed;
                                break;
                            case "right":
                                worldX += speed;
                                break;
                        }
                    }
                }
            }
        }

        // ====================================================================
        //  PHẦN 3: CẬP NHẬT CÁC TRẠNG THÁI CHUNG & ANIMATION
        // ====================================================================

        // Luôn giảm cooldown tấn công mỗi frame
        if (attackCooldown > 0) {
            attackCooldown--;
        }

        // Cập nhật sprite animation
        // Animation sẽ chạy nếu Player đang tấn công, hoặc đang di chuyển mà không bị chặn
        boolean isActuallyMoving = (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) && !collisionOn;
        if (isAttacking() || isActuallyMoving) {
            cip.update();
        } else {
            // Nếu không làm gì cả, quay về sprite đứng yên
            cip.setSpriteNum(0);
        }
    }
    public abstract void  checkAttack();
    @Override
    public abstract void draw(Graphics2D g2);

    @Override
    protected void onDeath(character.Character attacker) {
        gp.getUi().showMessage("You have been defeated by " + attacker.getName() + "!");
        gp.gameState = gp.gameOverState;
    }

    // Các phương thức chung khác cho Player
    public String getCharacterClassIdentifier() { return characterClassIdentifier; }
    public int getHasKey() { return hasKey; }
    public void setHasKey(int count) { this.hasKey = Math.max(0, count); }
    public void incrementKeyCount() { this.hasKey++; }
    public void decrementKeyCount() { if (this.hasKey > 0) this.hasKey--; }

    public void pickUpItem(int i) {
        if (i != 999 && gp.getwObjects()[i] != null) {
            gp.getwObjects()[i].interactPlayer(this, i, this.gp);
        }
    }
    public void interactWithNPC(int npcIndex) {
        if (npcIndex != 999) {
            if (gp.gameState == gp.playState) {
                Character npcCharacter = gp.getNpc()[npcIndex];

                if (npcCharacter instanceof DialogueSpeaker) {
                    ((DialogueSpeaker) npcCharacter).initiateDialogue(gp); // Điều này sẽ chuyển gameState sang dialogueState

                    if (!(npcCharacter instanceof NPC_Princess && gp.gameState == gp.victoryEndState) ) {
                        this.canInteractWithCurrentNPC = false;
                    }
                } else if (npcCharacter instanceof NPC_Princess) {
                    gp.gameState = gp.victoryEndState;
                }
            }
        }
    }

    @Override
    public int getAttack() {
        int totalAttack = baseAttack;
        if (currentWeapon != null) {
            totalAttack += currentWeapon.getAttackBonus();
        }
        return totalAttack;
    }

    public void equipWeapon(Item_Weapon weapon) {
        if(weapon instanceof Equippable){
            this.currentWeapon = weapon;
            gp.getUi().showMessage("Equipped: " + weapon.getName());
            // Không cần cập nhật sát thương ở đây vì getAttack() đã tự động tính toán
        }
        else {
            gp.getUi().showMessage("Can not equip " + weapon.getName());
            return;
        }
    }

    public item.itemEquippable.Item_Weapon getCurrentWeapon() {
        return this.currentWeapon;
    }

    public void unequipWeapon() {
        if (this.currentWeapon != null) {
            Item equippedItem = (Item) this.currentWeapon;
            gp.getUi().showMessage("Unequipped: " + equippedItem.getName());

            this.currentWeapon = null;
        } else {
            // Thông báo nếu không có gì để gỡ
            gp.getUi().showMessage("No weapon equipped.");
        }
    }

    public void levelUp() {
        level++;
        currentExp = currentExp - expToNextLevel;
        expToNextLevel = (int)(expToNextLevel * 1.5);

        maxHealth += 10;
        maxMana += 5;
        baseAttack += 2;
        defense += 1;

        currentHealth = maxHealth;
        currentMana = maxMana;

        gp.playSoundEffect(4); // Ví dụ: âm thanh SFX_FANFARE
        gp.getUi().showMessage("LEVEL UP! You are now level " + level + "!");
    }

    public void gainExp(int expGained) {
        this.currentExp += expGained;
        gp.getUi().showMessage("Gained " + expGained + " EXP!");


        while (currentExp >= expToNextLevel) {
            levelUp();
        }
    }

    protected void setInitLevel(){
        this.level = 1;
        this.currentExp = 0;
        this.expToNextLevel = 10;
    }
}