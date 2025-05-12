package entity;

import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public abstract class Character extends GameObject {

    // Sprites
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2;
    public BufferedImage guardUp, guardDown, guardLeft, guardRight;

    // State
    public String direction = "down";
    public int spriteNum = 1;
    public boolean attacking = false;
    public boolean guarding = false;
    public boolean alive = true;
    public boolean dying = false;
    public boolean invincible = false;
    public boolean knockBack = false;
    public String knockBackDirection;
    public boolean onPath = false;
    public boolean hpBarOn = false;
    public boolean transparent = false;
    public boolean offBalance = false;
    public boolean sleep = false; // If creatures can sleep

    // Counters
    public int spriteCounter = 0;
    public int actionLockCounter = 0;
    public int invincibleCounter = 0;
    public int shotAvailableCounter = 0; // For projectile users
    public int dyingCounter = 0;
    public int hpBarCounter = 0;
    public int knockBackCounter = 0;
    public int guardCounter = 0;
    public int offBalanceCounter = 0;

    // Attributes
    public int defaultSpeed;
    public int speed;
    public int maxLife;
    public int life;
    public int maxMana;
    public int mana;
    public int ammo; // If used by this creature
    public int level;
    public int strength;
    public int dexterity;
    public int attack;
    public int defense;
    public int exp;
    public int nextLevelExp;
    public int coin;
    public int motion1_duration;
    public int motion2_duration;
    public int knockBackPower = 1;
    // Equipment & Items
    public OBject currentWeapon; // Will be an Object type, e.g., Weapon
    public OBject currentShield; // Will be an Object type, e.g., Shield
    public GameObject currentLight;  // Will be an Object type, e.g., LightSource
    public Projectile projectile; // Specific projectile type this creature uses
    public OBject loot;
    public ArrayList<GameObject> inventory = new ArrayList<>(); // Inventory holds Objects
    public final int maxInventorySize = 20;

    // Interaction
    public String[][] dialogues = new String[20][20];
    public int dialogueSet = 0;
    public int dialogueIndex = 0;
    public Character attacker; // The creature that last attacked this one
    public GameObject linkedEntity; // Can be GameObject if it links to non-creatures

    public boolean boss = false;


    public Character(GamePanel gp) {
        super(gp);
    }

    public abstract void getImage(); // Subclasses must load their specific sprites
    public abstract void getAttackImage(); // If they have attack sprites
    public void getGuardImage(){/* default empty or specific implementation */};


    public void setAction() {
        // Default AI action, to be overridden by NPCs/Monsters
    }

    public void damageReaction() {
        // Default reaction to damage
    }

    public void resetCounter() {
        spriteCounter = 0;
        actionLockCounter = 0;
        invincibleCounter = 0;
        shotAvailableCounter = 0;
        dyingCounter = 0;
        hpBarCounter = 0;
        knockBackCounter = 0;
        guardCounter = 0;
        offBalanceCounter = 0;
    }
    public void checkDrop() {
        // Logic để quyết định có drop 'loot' hay không
        // Ví dụ: có tỉ lệ drop, hoặc luôn drop nếu loot != null
        if (this.loot != null) {
            // Có thể thêm logic tỉ lệ ở đây
            dropItem(this.loot);
        }
        // Hoặc nếu Character có thể drop nhiều loại item từ một danh sách:
        // for (OBject itemToDrop : potentialLootList) {
        //     if (Math.random() < itemToDrop.dropRate) { // Giả sử OBject có dropRate
        //         dropItem(gp.eGenerator.getObject(itemToDrop.name)); // Tạo instance mới của item
        //     }
        // }
    }

    public void dropItem(OBject droppedItem) { // Phương thức này đã có trong Entity gốc
        if (droppedItem == null) return;
        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] == null) {
                gp.obj[gp.currentMap][i] = droppedItem; // Thêm item vào mảng obj của GamePanel
                // Đặt vị trí của item là vị trí của Character đã chết
                gp.obj[gp.currentMap][i].worldX = this.worldX;
                gp.obj[gp.currentMap][i].worldY = this.worldY;
                // Nếu item là stackable và đã có trong túi người chơi gần đó, có thể xử lý khác
                break;
            }
        }
    }
    @Override
    public void speak() {
        // Default speak action
        if (dialogues[dialogueSet][dialogueIndex] != null) {
            gp.ui.currentDialogue = dialogues[dialogueSet][dialogueIndex];
            dialogueIndex++;
            // Logic to advance or end dialogue
        }
        facePlayer(); // Example common action
    }

    public void startDialogue(Character character, int setNum) {
        gp.gameState = gp.dialogueState;
        gp.ui.npc = character; // UI needs to know which character is talking
        dialogueSet = setNum;
        dialogueIndex = 0; // Reset index for the new set
    }


    public void facePlayer() {
        if (gp.player == null) return;
        switch (gp.player.direction) {
            case "up": direction = "down"; break;
            case "down": direction = "up"; break;
            case "left": direction = "right"; break;
            case "right": direction = "left"; break;
        }
    }

    public void checkCollision() {
        collisionOn = false;
        gp.cChecker.checkTile(this);
        // gp.cChecker.checkObject(this, false); // 'false' might mean 'don't pick up' for non-player
        // gp.cChecker.checkCreature(this, gp.npc); // New method to check against other creatures
        // gp.cChecker.checkCreature(this, gp.monster);
        // gp.cChecker.checkInteractiveTile(this, gp.iTile); // If ITiles are separate
        boolean contactPlayer = gp.cChecker.checkPlayer(this); // If this creature is not the player

        if (this.type == type_monster && contactPlayer && this != gp.player) {
            if (gp.player.invincible == false) {
                damagePlayer(attack); // Monster attacks player
            }
        }
    }


    @Override
    public void update() {
        if (sleep) return;

        if (knockBack) {
            // Knockback logic (copied and adapted from original Entity)
            checkCollision(); // Check collision while being knocked back
            if (collisionOn) {
                knockBackCounter = 0;
                knockBack = false;
                speed = defaultSpeed;
            } else {
                switch (knockBackDirection) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }
            knockBackCounter++;
            if (knockBackCounter >= 10) { // Adjusted to >= for safety
                knockBackCounter = 0;
                knockBack = false;
                speed = defaultSpeed;
            }
        } else if (attacking) {
            attacking();
        } else {
            setAction(); // AI or player input driven
            checkCollision();

            if (!collisionOn) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }
            // Sprite animation
            spriteCounter++;
            if (spriteCounter > 24) { // Adjust frame rate as needed
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }

        // Invincibility
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 40) { // Duration of invincibility
                invincible = false;
                transparent = false; // Reset transparency
                invincibleCounter = 0;
            }
        }
        if(shotAvailableCounter < 30) { // Cooldown for shooting
            shotAvailableCounter++;
        }
        if(offBalance) {
            offBalanceCounter++;
            if(offBalanceCounter > 60) {
                offBalance = false;
                offBalanceCounter = 0;
            }
        }
        if (life <= 0) {
            dying = true;
            alive = false; // Or handle death sequence
        }
    }

    public void attacking() {
        spriteCounter++;

        if (spriteCounter <= motion1_duration) {
            spriteNum = 1;
        }
        if (spriteCounter > motion1_duration && spriteCounter <= motion2_duration) {
            spriteNum = 2;
            // Save current state
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Adjust for attack area
            if (currentWeapon != null && currentWeapon.attackArea != null) {
                switch (direction) {
                    case "up": worldY -= currentWeapon.attackArea.height; break;
                    case "down": worldY += gp.tileSize; break; // Or currentWeapon.attackArea.height
                    case "left": worldX -= currentWeapon.attackArea.width; break;
                    case "right": worldX += gp.tileSize; break; // Or currentWeapon.attackArea.width
                }
                solidArea.width = currentWeapon.attackArea.width;
                solidArea.height = currentWeapon.attackArea.height;
            }


            // Check for hits
            if (type == type_monster) { // Monster attacking
                if (gp.cChecker.checkPlayer(this)) {
                    damagePlayer(attack); // Monster damages player
                }
            } else { // Player or NPC attacking
                // int monsterIndex = gp.cChecker.checkCreature(this, gp.monster);
                // if(monsterIndex != 999) {
                //     gp.player.damageMonster(monsterIndex, this, attack, currentWeapon != null ? currentWeapon.knockBackPower : 0);
                // }
                // int iTileIndex = gp.cChecker.checkInteractiveTile(this, gp.iTile);
                // if(iTileIndex != 999) {
                //    gp.player.damageInteractiveTile(iTileIndex);
                // }
                // int projectileIndex = gp.cChecker.checkObject(this, gp.projectile); // Projectiles are objects
                // if(projectileIndex != 999){
                //    gp.player.damageProjectile(projectileIndex);
                // }
                // This part is highly dependent on how Player.java handles damage dealing
                // For now, this creature is the attacker.
                // The Player class will have specific logic for its attacks.
            }

            // Restore original state
            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;
        }
        if (spriteCounter > motion2_duration) {
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
    }

    public void damagePlayer(int damage) { // When this creature (e.g. monster) damages the player
        if (gp.player.invincible) return;

        int actualDamage = damage - gp.player.defense;
        String canGuardDirection = getOppositeDirection(direction);

        if(gp.player.guarding && gp.player.direction.equals(canGuardDirection)){
            if(gp.player.guardCounter < 10){ // Parry
                actualDamage = 0;
                gp.playSE(16);
                setKnockBack(this, gp.player, knockBackPower); // Knockback attacker
                offBalance = true; // Attacker becomes offBalance
                spriteCounter = -60; // Stun effect for attacker
            } else { // Normal guard
                actualDamage /= 2;
                gp.playSE(15);
            }
        } else { // Not guarding
            gp.playSE(6);
            if (actualDamage < 1) actualDamage = 1;
        }

        if(actualDamage != 0) {
            gp.player.transparent = true; // Player becomes transparent on hit
            setKnockBack(gp.player, this, knockBackPower); // Knockback player
        }

        gp.player.life -= actualDamage;
        gp.player.invincible = true;
    }

    public void setKnockBack(Character target, Character attacker, int knockBackPwr) {
        if (target == null || attacker == null) return;
        target.attacker = attacker; // Record who attacked
        target.knockBackDirection = attacker.direction;
        target.speed += knockBackPwr; // Increase speed temporarily for knockback effect
        target.knockBack = true;
    }

    public String getOppositeDirection(String dir) {
        switch (dir) {
            case "up": return "down";
            case "down": return "up";
            case "left": return "right";
            case "right": return "left";
            default: return "";
        }
    }


    public void dyingAnimation(Graphics2D g2) {
        dyingCounter++;
        int i = 5; // Interval for flicker

        if (dyingCounter <= i) changeAlpha(g2, 0f);
        else if (dyingCounter <= i * 2) changeAlpha(g2, 1f);
        else if (dyingCounter <= i * 3) changeAlpha(g2, 0f);
        else if (dyingCounter <= i * 4) changeAlpha(g2, 1f);
        else if (dyingCounter <= i * 5) changeAlpha(g2, 0f);
        else if (dyingCounter <= i * 6) changeAlpha(g2, 1f);
        else if (dyingCounter <= i * 7) changeAlpha(g2, 0f);
        else if (dyingCounter <= i * 8) changeAlpha(g2, 1f);
        else {
            // alive = false; // Already set when dying is true
            // TODO: Handle actual removal from game or corpse state
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!drawing || !inCamera()) return;

        BufferedImage imageToDraw = null;
        int tempScreenX = getScreenX();
        int tempScreenY = getScreenY();

        // Determine image based on state (walking, attacking, guarding)
        if (attacking) {
            switch (direction) {
                case "up": imageToDraw = (spriteNum == 1) ? attackUp1 : attackUp2; tempScreenY -= (attackUp1 != null ? attackUp1.getHeight() - gp.tileSize : 0) ; break;
                case "down": imageToDraw = (spriteNum == 1) ? attackDown1 : attackDown2; break;
                case "left": imageToDraw = (spriteNum == 1) ? attackLeft1 : attackLeft2; tempScreenX -= (attackLeft1 !=null ? attackLeft1.getWidth() - gp.tileSize:0); break;
                case "right": imageToDraw = (spriteNum == 1) ? attackRight1 : attackRight2; break;
            }
        } else if (guarding) {
            switch (direction) {
                case "up": imageToDraw = guardUp; break;
                case "down": imageToDraw = guardDown; break;
                case "left": imageToDraw = guardLeft; break;
                case "right": imageToDraw = guardRight; break;
            }
        } else { // Walking or idle
            switch (direction) {
                case "up": imageToDraw = (spriteNum == 1) ? up1 : up2; break;
                case "down": imageToDraw = (spriteNum == 1) ? down1 : down2; break;
                case "left": imageToDraw = (spriteNum == 1) ? left1 : left2; break;
                case "right": imageToDraw = (spriteNum == 1) ? right1 : right2; break;
            }
        }
        if (imageToDraw == null) imageToDraw = down1; // Fallback image

        // HP Bar for monsters/NPCs
        if (this != gp.player && hpBarOn) {
            double oneScale = (double) gp.tileSize / maxLife;
            double hpBarValue = oneScale * life;

            g2.setColor(new Color(35, 35, 35));
            g2.fillRect(tempScreenX - 1, tempScreenY - 16, gp.tileSize + 2, 12);
            g2.setColor(new Color(255, 0, 30));
            g2.fillRect(tempScreenX, tempScreenY - 15, (int) hpBarValue, 10);

            hpBarCounter++;
            if (hpBarCounter > 600) { // 10 seconds
                hpBarOn = false;
                hpBarCounter = 0;
            }
        }

        if (invincible || transparent) { // Distinction: invincible by game mechanic, transparent by hit
            hpBarOn = true; // Show HP bar when hit
            hpBarCounter = 0;
            changeAlpha(g2, 0.4F);
        }

        if (dying) {
            dyingAnimation(g2);
        }

        g2.drawImage(imageToDraw, tempScreenX, tempScreenY, null);
        changeAlpha(g2, 1F); // Reset alpha

        // Debug solidArea
        // g2.setColor(Color.red);
        // g2.drawRect(tempScreenX + solidArea.x, tempScreenY + solidArea.y, solidArea.width, solidArea.height);
    }

    // AI-related methods (can be overridden or expanded)
    public void searchPath(int goalCol, int goalRow) {
        int startCol = (worldX + solidArea.x) / gp.tileSize;
        int startRow = (worldY + solidArea.y) / gp.tileSize;
        gp.pFinder.setNodes(startCol, startRow, goalCol, goalRow, this); // 'this' should be Character
        if (gp.pFinder.search()) {
            int nextX = gp.pFinder.pathList.get(0).col * gp.tileSize;
            int nextY = gp.pFinder.pathList.get(0).row * gp.tileSize;

            int enLeftX = worldX + solidArea.x;
            int enRightX = worldX + solidArea.x + solidArea.width;
            int enTopY = worldY + solidArea.y;
            int enBottomY = worldY + solidArea.y + solidArea.height;

            if (enTopY > nextY && enLeftX >= nextX && enRightX < nextX + gp.tileSize) direction = "up";
            else if (enTopY < nextY && enLeftX >= nextX && enRightX < nextX + gp.tileSize) direction = "down";
            else if (enTopY >= nextY && enBottomY < nextY + gp.tileSize) {
                if (enLeftX > nextX) direction = "left";
                if (enLeftX < nextX) direction = "right";
            } else if (enTopY > nextY && enLeftX > nextX) { // up or left
                direction = "up"; checkCollision(); if (collisionOn) direction = "left";
            } else if (enTopY > nextY && enLeftX < nextX) { // up or right
                direction = "up"; checkCollision(); if (collisionOn) direction = "right";
            } else if (enTopY < nextY && enLeftX > nextX) { // down or left
                direction = "down"; checkCollision(); if (collisionOn) direction = "left";
            } else if (enTopY < nextY && enLeftX < nextX) { // down or right
                direction = "down"; checkCollision(); if (collisionOn) direction = "right";
            }
            // Path completion check (optional for continuous following)
            // int nextCol = gp.pFinder.pathList.get(0).col;
            // int nextRow = gp.pFinder.pathList.get(0).row;
            // if(nextCol == goalCol && nextRow == goalRow) onPath = false;
        }
    }

    public void checkShootOrNot(int rate, int shotInterval) {
        if (projectile == null || projectile.alive || shotAvailableCounter < shotInterval) return;

        int i = new Random().nextInt(rate);
        if (i == 0) {
            projectile.set(worldX, worldY, direction, true, this); // 'this' is the Character user
            // Add to GamePanel's projectile list (gp.projectile[gp.currentMap][...])
            for(int ii = 0; ii < gp.projectile[gp.currentMap].length;ii++) { // Assuming gp.projectile is now GameObject[][]
                if(gp.projectile[gp.currentMap][ii] == null) {
                    gp.projectile[gp.currentMap][ii] = projectile;
                    break;
                }
            }
            shotAvailableCounter = 0;
            // Play sound effect gp.playSE(...)
        }
    }
    // Other AI helper methods like checkAttackOrNot, checkStartChasingOrNot, etc.
    // would be similar, just ensure types are correct (e.g., targets are Creatures).

}