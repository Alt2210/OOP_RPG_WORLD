package sound;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Sound implements  LineListener {
    // Bỏ: Clip clip; // Không dùng một clip chung nữa, mà dùng cache
    private Map<Integer, Clip> clipCache = new HashMap<>(); // Cache để lưu các Clip đã tải
    private Clip currentMusicClip = null; // Dành riêng cho nhạc nền đang phát (nếu cần quản lý riêng)
    private List<Integer> backgroundMusicPlaylist = new ArrayList<>();
    private Random random = new Random();
    private boolean manuallyStopped = false;

    URL soundURL[] = new URL[30];

    // ĐỊNH NGHĨA CÁC HẰNG SỐ CHO CHỈ SỐ ÂM THANH
    public static final int MUSIC_BACKGROUND = 0;
    public static final int SFX_COIN = 1;
    public static final int SFX_POWERUP = 2;
    public static final int SFX_UNLOCK_DOOR = 3;
    public static final int SFX_FANFARE = 4;
    public static final int SFX_PICKUP_KEY = 5;
    public static final int SFX_FIREBALL_WHOOSH = 6;
    public static final int SFX_FIREBALL_EXPLO = 7;
    public static final int SFX_SHOOT = 8;
    public static final int SFX_SWORD_SWING = 9;
    public static final int SFX_WATER_SPLASH = 10; // Giữ lại nếu bạn có file hitmonster.wav
    public static final int SFX_SHINE_SOUND = 11;
    public static final int SFX_LASER = 16;
    public static final int SFX_HIT = 17; // Giữ lại nếu bạn có file hitmonster.wav
    // ... (thêm các hằng số khác)
    public static final int MUSIC_GAME_1 = 12;
    public static final int MUSIC_GAME_2 = 13;
    public static final int MUSIC_GAME_3 = 14;
    public static final int MUSIC_GAME_4 = 15;
    public static final int SFX_CHEST_OPEN = 18;
    public static final int SFX_CHEST_CLOSE = 19;
    public static final int SFX_CURSOR_MOVE = 20;
    public static final int SFX_ITEM_TRANSFER = 21;
    public static final int SFX_EQUIP_WEAPON = 22;
    public static final int SFX_USE_POTION = 23;
    public static final int SFX_ARM_SHOT = 24;
    public Sound() {
        try {
            soundURL[MUSIC_BACKGROUND] = getClass().getResource("/sound/BlueBoyAdventure.wav");
            soundURL[SFX_COIN] = getClass().getResource("/sound/coin.wav"); // Giữ lại nếu bạn có file
            soundURL[SFX_POWERUP] = getClass().getResource("/sound/powerup.wav");
            soundURL[SFX_UNLOCK_DOOR] = getClass().getResource("/sound/unlock.wav");
            soundURL[SFX_FANFARE] = getClass().getResource("/sound/levelup.wav");
            soundURL[SFX_PICKUP_KEY] = getClass().getResource("/sound/coin.wav"); // Sử dụng coin.wav cho pickup_key
            soundURL[SFX_FIREBALL_WHOOSH] = getClass().getResource("/sound/fireball_whoosh.wav"); // Tạm dùng powerup.wav
            soundURL[SFX_FIREBALL_EXPLO] = getClass().getResource("/sound/explosion.wav"); // Cần file hitmonster.wav hoặc tương tự
            soundURL[SFX_SHOOT] = getClass().getResource("/sound/fireball_impact.wav"); // Cần file hitmonster.wav hoặc tương tự
            soundURL[SFX_WATER_SPLASH] = getClass().getResource("/sound/water_splash.wav");
            soundURL[SFX_SHINE_SOUND] = getClass().getResource("/sound/shine_sound.wav"); // Giữ lại nếu bạn có file shine.wav
            soundURL[SFX_SWORD_SWING] = getClass().getResource("/sound/sword_swing.wav"); // Giữ lại nếu bạn có file sword_swing.wav
            soundURL[SFX_LASER] = getClass().getResource("/sound/laser.wav"); // Giữ lại nếu bạn có file laser.wav
            soundURL[SFX_HIT] = getClass().getResource("/sound/hitmonster.wav");
            soundURL[SFX_CHEST_OPEN] = getClass().getResource("/sound/chest_open.wav");
            soundURL[SFX_CHEST_CLOSE] = getClass().getResource("/sound/chest_close.wav");
            soundURL[SFX_CURSOR_MOVE] = getClass().getResource("/sound/inventory.wav");
            soundURL[SFX_ITEM_TRANSFER] = getClass().getResource("/sound/collect_item.wav");
            soundURL[SFX_EQUIP_WEAPON] = getClass().getResource("/sound/equip.wav");
            soundURL[SFX_USE_POTION] = getClass().getResource("/sound/drink_potion.wav"); // Giữ lại nếu bạn có file use_potion.wav
            soundURL[SFX_ARM_SHOT] = getClass().getResource("/sound/stone_man_punch.wav"); // Giữ lại nếu bạn có file arm_shot.wav
            // Tùy chọn: Kiểm tra null cho các URL (rất hữu ích để debug)
            // for (int i = 0; i <= SFX_PICKUP_KEY; i++) { // Chỉ ví dụ đến SFX_PICKUP_KEY
            //     if (soundURL[i] == null && i != SFX_COIN /*ví dụ nếu coin.wav chưa có*/) {
            //         System.err.println("Sound Constructor Error: Cannot find sound file for index " + i);
            //     }
            // }
            soundURL[MUSIC_GAME_1] = getClass().getResource("/sound/game_music_1.wav");
            soundURL[MUSIC_GAME_2] = getClass().getResource("/sound/game_music_2.wav");
            soundURL[MUSIC_GAME_3] = getClass().getResource("/sound/game_music_3.wav");
            soundURL[MUSIC_GAME_4] = getClass().getResource("/sound/game_music_4.wav");

            backgroundMusicPlaylist.add(MUSIC_GAME_1);
            backgroundMusicPlaylist.add(MUSIC_GAME_2);
            backgroundMusicPlaylist.add(MUSIC_GAME_3);
            backgroundMusicPlaylist.add(MUSIC_GAME_4);
        } catch (Exception e) {
            System.err.println("Error initializing sound URLs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void setVolume(Clip clip, float decibels) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(decibels);
        }
    }
    /**
     * Chuẩn bị một Clip để phát. Nếu đã có trong cache thì dùng lại.
     * @param soundIndex Chỉ số của âm thanh trong mảng soundURL.
     * @return Đối tượng Clip đã sẵn sàng, hoặc null nếu có lỗi.
     */

    private Clip getClip(int soundIndex) {
        if (soundIndex < 0 || soundIndex >= soundURL.length || soundURL[soundIndex] == null) {
            System.err.println("Sound Error: Invalid sound index or soundURL is null for index " + soundIndex);
            return null;
        }

        // Kiểm tra xem clip đã có trong cache chưa
        if (clipCache.containsKey(soundIndex)) {
            Clip cachedClip = clipCache.get(soundIndex);
            // Đảm bảo clip không bị đóng (closed) nếu đã từng closeAll() rồi lại muốn dùng
            if (cachedClip != null && !cachedClip.isOpen()) {
                // Cố gắng mở lại clip. Điều này có thể cần tải lại AudioInputStream.
                // Để đơn giản, nếu clip đã đóng, chúng ta sẽ tạo lại.
                // Hoặc, đảm bảo closeAll() chỉ được gọi khi thoát game.
                // System.out.println("Sound Info: Re-opening cached clip for index " + soundIndex);
                // Để an toàn, nếu clip đã close, ta nên remove nó khỏi cache và tải lại
                clipCache.remove(soundIndex); // Xóa clip đã đóng khỏi cache
            } else if (cachedClip != null) {
                return cachedClip; // Trả về clip từ cache nếu nó vẫn mở
            }
        }

        // Nếu chưa có trong cache hoặc clip đã bị đóng và xóa, tải mới
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[soundIndex]);
            Clip newClip = AudioSystem.getClip();
            newClip.open(ais);
            clipCache.put(soundIndex, newClip); // Thêm clip mới vào cache
            return newClip;
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Sound Error (Unsupported): " + soundURL[soundIndex] + " - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Sound Error (IO): " + soundURL[soundIndex] + " - " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Sound Error (LineUnavailable): " + soundURL[soundIndex] + " - " + e.getMessage());
        } catch (Exception e) { // Bắt các lỗi không lường trước khác
            System.err.println("Sound Error (Unexpected): " + soundURL[soundIndex] + " - " + e.getMessage());
        }
        return null; // Trả về null nếu có lỗi khi tải
    }


    public void play(int soundIndex) {
        Clip clipToPlay = getClip(soundIndex);
        if (clipToPlay != null) {
            if (soundIndex == SFX_SHOOT) {
                setVolume(clipToPlay, -8.0f); // Giảm âm thanh SHOOT đi 15dB
            }
            else if (soundIndex == SFX_LASER) {
                setVolume(clipToPlay, -15.0f); // Giảm âm thanh LASER đi 20dB
            }
            else if (soundIndex == SFX_WATER_SPLASH) {
                setVolume(clipToPlay, -5.0f); // Giảm âm thanh LASER đi 20dB
            }
            if (clipToPlay.isRunning()) { // Nếu âm thanh này đang phát, dừng và phát lại từ đầu
                clipToPlay.stop();
            }
            clipToPlay.setFramePosition(0);
            clipToPlay.start();
        }
    }

    public void loop(int soundIndex) {
        // Dừng nhạc nền hiện tại (nếu có) trước khi phát nhạc mới
        stopMusic();
        manuallyStopped = true;
        currentMusicClip = getClip(soundIndex); // Lấy (hoặc tải) clip cho nhạc nền
        if (currentMusicClip != null) {
            setVolume(currentMusicClip, -5.0f);
            currentMusicClip.setFramePosition(0);
            currentMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop(int soundIndex) { // Dừng một âm thanh cụ thể nếu cần
        if (clipCache.containsKey(soundIndex)) {
            Clip clipToStop = clipCache.get(soundIndex);
            if (clipToStop != null) {
                clipToStop.stop();
            }
        }
    }

    public void stopMusic() { // Dừng nhạc nền hiện tại
        if (currentMusicClip != null) {
            manuallyStopped = true;
            currentMusicClip.removeLineListener(this);
            currentMusicClip.stop();
        }
    }

    // Đổi tên close() thành closeAll() để tránh nhầm lẫn và thể hiện rõ chức năng
    public void closeAllClips() {
        System.out.println("Closing all sound clips...");
        if (currentMusicClip != null) { // Dừng và đóng clip nhạc nền hiện tại
            currentMusicClip.stop();
            currentMusicClip.close();
            currentMusicClip = null;
        }
        for (Clip clip : clipCache.values()) {
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.close();
            }
        }
        clipCache.clear(); // Xóa cache sau khi đã đóng tất cả
        System.out.println("All sound clips closed and cache cleared.");
    }
    @Override
    public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
            if (!manuallyStopped) { // Chỉ phát bài tiếp theo nếu không phải do người dùng dừng
                playNextRandomSong();
            }
        }
    }

    private void playNextRandomSong() {
        if (backgroundMusicPlaylist.isEmpty()) {
            return;
        }
        manuallyStopped = false; // Reset cờ

        if (currentMusicClip != null) {
            currentMusicClip.removeLineListener(this);
            currentMusicClip.close();
        }

        int musicIndex = backgroundMusicPlaylist.get(random.nextInt(backgroundMusicPlaylist.size()));
        currentMusicClip = getClip(musicIndex);

        if (currentMusicClip != null) {
            setVolume(currentMusicClip, -5.0f);
            currentMusicClip.setFramePosition(0);
            currentMusicClip.addLineListener(this);
            currentMusicClip.start();
        }
    }

    public void startMusicPlaylist() {
        playNextRandomSong();
    }
}