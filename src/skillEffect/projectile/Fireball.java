package skillEffect.projectile;

import character.Character;
import imageProcessor.SkillImageProcessor;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Fireball extends Projectile {

    SkillImageProcessor sip;

    public Fireball(GamePanel gp, SkillImageProcessor sip) {
        super(gp);
        if (gp != null) { // Thêm kiểm tra null cho gp
            solidArea.width = (int) (gp.getTileSize() * 0.75); // Kích thước mới cho Fireball
            solidArea.height = (int) (gp.getTileSize() * 0.75); // Kích thước mới cho Fireball
        } else { // Fallback nếu gp null (không nên xảy ra)
            solidArea.width = 36; // Giá trị mặc định nếu gp null
            solidArea.height = 36; // Giá trị mặc định nếu gp null
        }
        this.sip = sip;
    }

    private void applyDamage() {
        gp.getCombatSystem().checkAoEAttack(caster, solidArea, this.damage);
    }

    @Override
    public void set(int startWorldX, int startWorldY, String direction, Character caster, int damage) {
        this.worldX = startWorldX - solidArea.width / 2;   // Căn giữa fireball tại điểm xuất phát
        this.worldY = startWorldY - solidArea.height / 2;
        this.direction = direction;
        this.caster = caster;
        this.damage = damage; // Sát thương được truyền vào

        // Các thuộc tính riêng của Fireball
        this.speed = 5; // Tốc độ của Fireball
        this.maxRange = 3 * gp.getTileSize(); // Tầm bay tối đa (ví dụ: 10 ô)
        this.alive = true;
        this.distanceTraveled = 0;
    }

    @Override
    public void update() {
        if (!alive) {
            return;
        }

        gp.getCombatSystem().checkSingleAttack(this);

        // Di chuyển skillEffect.projectile
        int prevWorldX = worldX;
        int prevWorldY = worldY;
        double moveAmountX = 0;
        double moveAmountY = 0;

        switch (direction) {
            case "up": moveAmountY = -speed; break;
            case "down": moveAmountY = speed; break;
            case "left": moveAmountX = -speed; break;
            case "right": moveAmountX = speed; break;
        }
        worldX += (int)moveAmountX;
        worldY += (int)moveAmountY;

        distanceTraveled += Math.sqrt(Math.pow(worldX - prevWorldX, 2) + Math.pow(worldY - prevWorldY, 2));
        // Cập nhật hoạt ảnh
        sip.update();
        // Kiểm tra va chạm với tile (tại tâm của skillEffect.projectile)
        if (checkTileCollision(worldX + solidArea.width / 2, worldY + solidArea.height / 2)) {
            // alive đã được đặt là false trong checkTileCollision nếu va chạm
            // System.out.println("Fireball hit wall"); // Debug
            return; // Không xử lý thêm nếu đã va chạm tường
        }

        // Kiểm tra va chạm với quái vật (nếu caster là Player)
//        if (caster instanceof character.Role.Player) { // Đảm bảo import character.Role.Player
//            for (Monster monster : gp.getMON_GreenSlime()) { // Lấy danh sách quái vật từ GamePanel
//                if (monster != null && monster.getCurrentHealth() > 0) {
//                    // Cập nhật vị trí solidArea của fireball và monster cho kiểm tra chính xác
//                    this.solidArea.x = worldX;
//                    this.solidArea.y = worldY;
//
//                    monster.solidArea.x = monster.worldX + monster.solidAreaDefaultX;
//                    monster.solidArea.y = monster.worldY + monster.solidAreaDefaultY;
//
//                    if (this.solidArea.intersects(monster.solidArea)) {
//                        // gp.getUi().showMessage("Fireball hit " + monster.getName()); // Debug
//                        gp.getCombatSystem().handleProjectileHit(this, monster);
//                        //monster.receiveDamage(this.damage, this.caster);
//                        // Phát âm thanh va chạm (nếu có)
//                        gp.playSoundEffect(Sound.SFX_FIREBALL_HIT);
//                        this.alive = false; // Fireball biến mất sau khi trúng mục tiêu
//                        return;
//                    }
//                }
//            }
//            for (Monster batMonster : gp.getMON_Bat()) {
//                if (batMonster != null && batMonster.getCurrentHealth() > 0) {
//                    // Cập nhật vị trí solidArea của fireball và batMonster cho kiểm tra chính xác
//                    this.solidArea.x = worldX;
//                    this.solidArea.y = worldY;
//
//                    batMonster.solidArea.x = batMonster.worldX + batMonster.solidAreaDefaultX;
//                    batMonster.solidArea.y = batMonster.worldY + batMonster.solidAreaDefaultY;
//
//                    if (this.solidArea.intersects(batMonster.solidArea)) {
//                        // gp.getUi().showMessage("Fireball hit " + batMonster.getName()); // Debug
//                        gp.getCombatSystem().handleProjectileHit(this, batMonster);
//                        batMonster.receiveDamage(this.damage, this.caster); // Gây sát thương cho MON_Bat
//                        // Phát âm thanh va chạm (nếu có)
//                        //gp.playSoundEffect(Sound.SFX_FIREBALL_HIT);
//                        this.alive = false; // Fireball biến mất sau khi trúng mục tiêu
//                        return; // Thoát khỏi phương thức update sau khi xử lý va chạm
//                    }
//                }
//            }
//        }
        // (Tùy chọn) Kiểm tra va chạm với Player nếu caster là Monster (chưa làm ở đây)

        // Kiểm tra tầm bay tối đa
        if (distanceTraveled > maxRange) {
            this.alive = false;
            // System.out.println("Fireball reached max range"); // Debug
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage currentFrame = sip.getCurFrame(this.direction);
        if (!alive || currentFrame == null) {
            return;
        }

        int screenX = worldX - gp.getPlayer().worldX + gp.getPlayer().screenX;
        int screenY = worldY - gp.getPlayer().worldY + gp.getPlayer().screenY;

        // Chỉ vẽ nếu skillEffect.projectile nằm trong màn hình
        if (worldX + solidArea.width > gp.getPlayer().worldX - gp.getPlayer().screenX &&
                worldX - solidArea.width < gp.getPlayer().worldX + gp.getPlayer().screenX && // Sửa: trừ solidArea.width
                worldY + solidArea.height > gp.getPlayer().worldY - gp.getPlayer().screenY &&
                worldY - solidArea.height < gp.getPlayer().worldY + gp.getPlayer().screenY) { // Sửa: trừ solidArea.height

            // Vẽ fireball với kích thước của solidArea (đã được đặt dựa trên ảnh)
            g2.drawImage(currentFrame, screenX, screenY, solidArea.width, solidArea.height, null);
            // Nếu muốn vẽ kích thước gốc của ảnh:
            //g2.drawImage(image, screenX, screenY, image.getWidth(), image.getHeight(), null);
            //g2.drawImage(image, screenX, screenY, image.getWidth() * gp.getScale(), image.getHeight() * gp.getScale(), null);
            // Optional: Vẽ vùng solidArea để debug
             g2.setColor(Color.RED);
             g2.drawRect(screenX, screenY, solidArea.width , solidArea.height);
        }
    }
}