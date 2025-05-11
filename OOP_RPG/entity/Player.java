package entity;

import main.GamePanel;
import main.KeyHandler;
// import object.*; // Bạn sẽ cần import các lớp Object cụ thể

import java.awt.*;
import java.awt.image.BufferedImage;
// import java.util.ArrayList; // Character đã có inventory

public class Player extends Character { // Kế thừa từ Character

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    int standCounter = 0;
    public boolean attackCanceled = false;
    public boolean lightUpdated = false;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp); // Gọi constructor của Character
        this.keyH = keyH;
        this.name = "Player"; // Or some default player name
        this.type = type_player;


        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        // solidArea đã được khởi tạo trong GameObject, bạn có thể tùy chỉnh ở đây
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        // gp.currentMap = 0; // Quản lý map trong GamePanel
        // gp.currentArea = gp.outside; // Quản lý area trong GamePanel

        defaultSpeed = 4;
        speed = defaultSpeed;
        direction = "down";

        level = 1;
        maxLife = 10;
        life = maxLife;
        maxMana = 8;
        mana = maxMana;
        ammo = 10;
        strength = 1;
        dexterity = 1;
        exp = 0;
        nextLevelExp = 4;
        coin = 40;

        // Ví dụ khởi tạo vũ khí, shield, projectile (đây phải là các lớp kế thừa từ OBject)
        // currentWeapon = new OBJ_Sword_Normal(gp); // OBJ_Sword_Normal phải extends OBject
        // currentShield = new OBJ_Shield_Wood(gp); // OBJ_Shield_Wood phải extends OBject
        // projectile = new OBJ_Fireball(gp);     // OBJ_Fireball phải extends Projectile

        attack = getAttack();
        defense = getDefense();

        getImage();
        getAttackImage();
        getGuardImage();
        setItems();
        setDialogue(); // Player can have dialogues (e.g. level up)
    }

    @Override
    public void setDialogue() { // Example for player
        dialogues[0][0] = "You are level " + level + " now!\n" + "You feel stronger!";
    }


    public void setDefaultPositions() {
        // gp.currentMap = 0;
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        direction = "down";
    }

    public void restoreStatus() {
        life = maxLife;
        mana = maxMana;
        speed = defaultSpeed;
        invincible = false;
        transparent = false;
        attacking = false;
        guarding = false;
        knockBack = false;
        lightUpdated = true; // For lighting system
    }

    public void setItems() {
        inventory.clear();
        if (currentWeapon != null) inventory.add(currentWeapon);
        if (currentShield != null) inventory.add(currentShield);
        // Add other starting items
        // inventory.add(new OBJ_Potion_Red(gp));
    }

    @Override
    public int getAttack() { // Ghi đè từ Character nếu cần logic khác
        if (currentWeapon == null) return strength; // Base attack if no weapon
        // currentWeapon phải là OBject và có attackArea, motion durations
        attackArea = currentWeapon.attackArea != null ? currentWeapon.attackArea : new Rectangle(0,0,0,0);
        motion1_duration = currentWeapon.motion1_duration;
        motion2_duration = currentWeapon.motion2_duration;
        return strength * currentWeapon.attackValue;
    }

    @Override
    public int getDefense() { // Ghi đè từ Character
        if (currentShield == null) return dexterity; // Base defense if no shield
        return dexterity * currentShield.defenseValue;
    }

    // getImage, getAttackImage, getGuardImage (đã có trong Character, bạn cần triển khai chúng)
    @Override
    public void getImage() {
        up1 = setup("/player/boy_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/player/boy_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/player/boy_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/player/boy_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/player/boy_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/player/boy_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/player/boy_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/player/boy_right_2", gp.tileSize, gp.tileSize);
    }
    public void getSleepingImage(BufferedImage image) // Specific to player potentially
    {
        up1 = image; up2 = image; down1 = image; down2 = image;
        left1 = image; left2 = image; right1 = image; right2 = image;
    }

    @Override
    public void getAttackImage() {
        if (currentWeapon == null) return;
        String weaponTypeFolder = "";
        if (currentWeapon.type == type_sword) weaponTypeFolder = "boy_attack";
        else if (currentWeapon.type == type_axe) weaponTypeFolder = "boy_axe";
        else if (currentWeapon.type == type_pickaxe) weaponTypeFolder = "boy_pick";
        else return; // No specific attack image for this weapon type

        attackUp1 = setup("/player/" + weaponTypeFolder + "_up_1", gp.tileSize, gp.tileSize * 2);
        attackUp2 = setup("/player/" + weaponTypeFolder + "_up_2", gp.tileSize, gp.tileSize * 2);
        attackDown1 = setup("/player/" + weaponTypeFolder + "_down_1", gp.tileSize, gp.tileSize * 2);
        attackDown2 = setup("/player/" + weaponTypeFolder + "_down_2", gp.tileSize, gp.tileSize * 2);
        attackLeft1 = setup("/player/" + weaponTypeFolder + "_left_1", gp.tileSize * 2, gp.tileSize);
        attackLeft2 = setup("/player/" + weaponTypeFolder + "_left_2", gp.tileSize * 2, gp.tileSize);
        attackRight1 = setup("/player/" + weaponTypeFolder + "_right_1", gp.tileSize * 2, gp.tileSize);
        attackRight2 = setup("/player/" + weaponTypeFolder + "_right_2", gp.tileSize * 2, gp.tileSize);
    }
    @Override
    public void getGuardImage(){
        guardUp = setup("/player/boy_guard_up",gp.tileSize,gp.tileSize);
        guardDown = setup("/player/boy_guard_down",gp.tileSize,gp.tileSize);
        guardLeft = setup("/player/boy_guard_left",gp.tileSize,gp.tileSize);
        guardRight = setup("/player/boy_guard_right",gp.tileSize,gp.tileSize);
    }


    @Override
    public void update() { // Player's specific update logic
        if (knockBack) {
            super.update(); // Handle knockback from Character class
            return;
        }
        if (attacking) {
            attacking(); // Call player's attacking method or Character's
        } else if (keyH.spacePressed) {
            guarding = true;
            guardCounter++;
        } else if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed || keyH.enterPressed) {
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            // Collision checking (needs to be adapted for new types)
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // int objIndex = gp.cChecker.checkObject(this, true); // Now checks OBject
            // pickUpObject(objIndex);

            // int npcIndex = gp.cChecker.checkCreature(this, gp.npc); // Checks Creatures
            // interactNPC(npcIndex);

            // int monsterIndex = gp.cChecker.checkCreature(this, gp.monster);
            // contactMonster(monsterIndex);

            // int iTileIndex = gp.cChecker.checkInteractiveTile(this, gp.iTile);
            // contactInteractiveTile(iTileIndex); // Example

            gp.eHandler.checkEvent(); // Event handler

            if (!collisionOn && !keyH.enterPressed) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }

            if (keyH.enterPressed && !attackCanceled) {
                gp.playSE(7);
                attacking = true;
                spriteCounter = 0;
            }

            attackCanceled = false;
            gp.keyH.enterPressed = false; // Reset enter key
            guarding = false; // Stop guarding if moved/attacked
            guardCounter = 0;

            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else { // Standing still
            standCounter++;
            if (standCounter >= 20) { // Use >= for safety
                spriteNum = 1;
                standCounter = 0;
            }
            guarding = false;
            guardCounter = 0;
        }

        // Projectile shooting
        if (gp.keyH.shotKeyPressed && projectile != null && !projectile.alive &&
                shotAvailableCounter >= 30 && projectile.haveResource(this)) { // Use >=

            projectile.set(worldX, worldY, direction, true, this);
            projectile.subtractResource(this);

            // Add to GamePanel's projectile list
            for(int i = 0; i < gp.projectile[gp.currentMap].length; i++) {
                if(gp.projectile[gp.currentMap][i] == null) {
                    gp.projectile[gp.currentMap][i] = projectile;
                    break;
                }
            }
            shotAvailableCounter = 0;
            gp.playSE(10);
        }


        // Invincibility, mana/life checks (can use super.update() or reimplement)
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                transparent = false;
                invincibleCounter = 0;
            }
        }
        if (shotAvailableCounter < 30) shotAvailableCounter++;
        if (life > maxLife) life = maxLife;
        if (mana > maxMana) mana = maxMana;

        if (!keyH.godModeOn && life <= 0) {
            gp.gameState = gp.gameOverState;
            gp.ui.commandNum = -1;
            gp.stopMusic();
            gp.playSE(12);
        }
    }

    @Override
    public void attacking() { // Player's specific attacking logic
        spriteCounter++;

        if (spriteCounter <= motion1_duration) {
            spriteNum = 1;
        }
        if (spriteCounter > motion1_duration && spriteCounter <= motion2_duration) {
            spriteNum = 2;
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Adjust player's worldX/Y for attackArea
            if (currentWeapon != null && currentWeapon.attackArea != null) {
                switch (direction) {
                    case "up": worldY -= currentWeapon.attackArea.height; break;
                    case "down": worldY += gp.tileSize; break;
                    case "left": worldX -= currentWeapon.attackArea.width; break;
                    case "right": worldX += gp.tileSize; break;
                }
                solidArea.width = currentWeapon.attackArea.width;
                solidArea.height = currentWeapon.attackArea.height;
            }


            // Check monster collision
            // int monsterIndex = gp.cChecker.checkCreature(this, gp.monster);
            // damageMonster(monsterIndex, this, attack, currentWeapon != null ? currentWeapon.knockBackPower : 0);

            // int iTileIndex = gp.cChecker.checkInteractiveTile(this, gp.iTile);
            // damageInteractiveTile(iTileIndex);

            // int projectileIndex = gp.cChecker.checkObject(this, gp.projectile); // Check against OBject array
            // damageProjectile(projectileIndex);


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

    public void pickUpObject(int objIndex) { // objIndex is for gp.obj (OBject[])
        if (objIndex != 999) {
            OBject pickedObject = gp.obj[gp.currentMap][objIndex];
            if (pickedObject == null) return;

            if (pickedObject.type == type_pickupOnly) {
                pickedObject.use(this); // use() now takes Character
                gp.obj[gp.currentMap][objIndex] = null;
            } else if (pickedObject.type == type_obstacle) {
                if(keyH.enterPressed){
                    attackCanceled = true;
                    pickedObject.interact();
                }
            }
            else { // Inventory items
                String text;
                if (canObtainItem(pickedObject)) {
                    gp.playSE(1);
                    text = "Got a " + pickedObject.name + "!";
                } else {
                    text = "You cannot carry any more!";
                }
                gp.ui.addMessage(text);
                if(canObtainItem(pickedObject) || pickedObject.stackable == false) { // remove if obtained or not stackable and full
                    gp.obj[gp.currentMap][objIndex] = null;
                }
            }
        }
    }

    public void interactNPC(int npcIndex) { // npcIndex for gp.npc (Character[])
        if (npcIndex != 999) {
            Character npc = gp.npc[gp.currentMap][npcIndex];
            if (keyH.enterPressed) {
                attackCanceled = true;
                npc.speak(); // Call Character's speak
            }
            // npc.move(direction); // If NPC reacts to player presence by moving
        }
    }

    public void contactMonster(int monsterIndex) { // monsterIndex for gp.monster (Character[])
        if (monsterIndex != 999) {
            Character monster = gp.monster[gp.currentMap][monsterIndex];
            if (!invincible && !monster.dying) {
                gp.playSE(6);
                int damage = monster.attack - defense;
                if (damage < 1) damage = 1;
                life -= damage;
                invincible = true;
                transparent = true;
            }
        }
    }
    public void damageMonster(int monsterIndex, GameObject attackerSource, int attackPower, int knockBackPwr) {
        if (monsterIndex != 999) {
            Character monster = gp.monster[gp.currentMap][monsterIndex];
            if (monster == null || monster.invincible) return;

            gp.playSE(5);
            if (knockBackPwr > 0) {
                monster.setKnockBack(monster, this, knockBackPwr); // monster is target, player is attacker
            }
            if (monster.offBalance) {
                attackPower *= 2;
            }

            int damage = attackPower - monster.defense;
            if (damage <= 0) damage = 1;

            monster.life -= damage;
            gp.ui.addMessage(damage + " damage!");
            monster.invincible = true;
            monster.damageReaction();

            if (monster.life <= 0) {
                monster.dying = true;
                gp.ui.addMessage("Killed the " + monster.name + "!");
                gp.ui.addMessage("Exp +" + monster.exp + "!");
                exp += monster.exp;
                checkLevelUp();
            }
        }
    }
    public void damageInteractiveTile(int iTileIndex) {
        if (iTileIndex != 999) {
            // Assuming iTiles are Game_Objects and have necessary properties
            // OBject tile = gp.iTile[gp.currentMap][iTileIndex];
            // if (tile.destructible && tile.isCorrectItem(this) && !tile.invincible) {
            //    tile.playSE();
            //    tile.life--; // Assuming iTile has life
            //    tile.invincible = true;
            //    generateParticle(tile, tile);
            //    if (tile.life == 0) {
            //        gp.iTile[gp.currentMap][iTileIndex] = tile.getDestroyedForm();
            //    }
            // }
        }
    }
    public void damageProjectile(int projectileIndex) { // projectileIndex for gp.projectile (Projectile[])
        if (projectileIndex != 999) {
            Projectile proj = gp.projectile[gp.currentMap][projectileIndex];
            if (proj != null && proj.alive) {
                proj.alive = false; // Destroy the projectile
                generateParticle(proj, proj); // Generate particle from the projectile itself
            }
        }
    }


    public void checkLevelUp() {
        while (exp >= nextLevelExp) {
            level++;
            exp -= nextLevelExp;
            nextLevelExp = (level <= 4) ? nextLevelExp + 4 : nextLevelExp + 8; // Simpler progression
            maxLife += 2;
            maxMana +=1; // Example
            strength++;
            dexterity++;
            attack = getAttack();
            defense = getDefense();
            gp.playSE(8);
            setDialogue(); // Update level up message
            startDialogue(this, 0); // Show level up message
            life = maxLife; // Heal on level up
            mana = maxMana;
        }
    }

    public void selectItem() {
        int itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);
        if (itemIndex < inventory.size()) {
            GameObject selectedItem = inventory.get(itemIndex); // Item is a GameObject

            if (selectedItem.type == type_sword || selectedItem.type == type_axe || selectedItem.type == type_pickaxe) {
                currentWeapon = selectedItem;
                attack = getAttack();
                getAttackImage();
            } else if (selectedItem.type == type_shield) {
                currentShield = selectedItem;
                defense = getDefense();
            } else if (selectedItem.type == type_light) {
                currentLight = (currentLight == selectedItem) ? null : selectedItem;
                lightUpdated = true;
            } else if (selectedItem.type == type_consumable) {
                if (selectedItem.use(this)) { // use() takes Character
                    if (selectedItem.amount > 1) {
                        selectedItem.amount--;
                    } else {
                        inventory.remove(itemIndex);
                    }
                }
            }
        }
    }
    public int searchItemInInventory(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) != null && inventory.get(i).name.equals(itemName)) {
                return i;
            }
        }
        return 999;
    }

    public boolean canObtainItem(OBject item) { // Parameter is OBject
        boolean canObtain = false;
        // GameObject newItem = gp.eGenerator.getObject(item.name); // Assuming eGenerator gives OBject
        OBject newItem = item; // Or just use the item directly if it's already the instance to add

        if (newItem.stackable) {
            int index = searchItemInInventory(newItem.name);
            if (index != 999) {
                inventory.get(index).amount++;
                canObtain = true;
            } else {
                if (inventory.size() < maxInventorySize) {
                    inventory.add(newItem);
                    canObtain = true;
                }
            }
        } else {
            if (inventory.size() < maxInventorySize) {
                inventory.add(newItem);
                canObtain = true;
            }
        }
        return canObtain;
    }

    // draw() method is inherited from Character, Player doesn't need to override unless specific additions.
}