package sound;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap; // << THÊM IMPORT HashMap
import java.util.Map;    // << THÊM IMPORT Map

public class Sound {
    // Bỏ: Clip clip; // Không dùng một clip chung nữa, mà dùng cache
    private Map<Integer, Clip> clipCache = new HashMap<>(); // Cache để lưu các Clip đã tải
    private Clip currentMusicClip = null; // Dành riêng cho nhạc nền đang phát (nếu cần quản lý riêng)
    // Hoặc bạn có thể có 2 instance Sound: một cho music, một cho SFX

    URL soundURL[] = new URL[30];

    // ĐỊNH NGHĨA CÁC HẰNG SỐ CHO CHỈ SỐ ÂM THANH
    public static final int MUSIC_BACKGROUND = 0;
    public static final int SFX_COIN = 1;
    public static final int SFX_POWERUP = 2;
    public static final int SFX_UNLOCK_DOOR = 3;
    public static final int SFX_FANFARE = 4;
    public static final int SFX_PICKUP_KEY = 5;
    public static final int SFX_FIREBALL_SHOOT = 6; // Âm thanh khi bắn Fireball
    public static final int SFX_FIREBALL_HIT = 7;   // Âm thanh khi Fireball trúng mục tiêu/tường
    // ... (thêm các hằng số khác)

    public Sound() {
        try {
            soundURL[MUSIC_BACKGROUND] = getClass().getResource("/sound/BlueBoyAdventure.wav");
            soundURL[SFX_COIN] = getClass().getResource("/sound/coin.wav"); // Giữ lại nếu bạn có file
            soundURL[SFX_POWERUP] = getClass().getResource("/sound/powerup.wav");
            soundURL[SFX_UNLOCK_DOOR] = getClass().getResource("/sound/unlock.wav");
            soundURL[SFX_FANFARE] = getClass().getResource("/sound/fanfare.wav");
            soundURL[SFX_PICKUP_KEY] = getClass().getResource("/sound/coin.wav"); // Sử dụng coin.wav cho pickup_key
            soundURL[SFX_FIREBALL_SHOOT] = getClass().getResource("/sound/burning.wav"); // Tạm dùng powerup.wav
            soundURL[SFX_FIREBALL_HIT] = getClass().getResource("/sound/hitmonster.wav"); // Cần file hitmonster.wav hoặc tương tự
            // Tùy chọn: Kiểm tra null cho các URL (rất hữu ích để debug)
            // for (int i = 0; i <= SFX_PICKUP_KEY; i++) { // Chỉ ví dụ đến SFX_PICKUP_KEY
            //     if (soundURL[i] == null && i != SFX_COIN /*ví dụ nếu coin.wav chưa có*/) {
            //         System.err.println("Sound Constructor Error: Cannot find sound file for index " + i);
            //     }
            // }
        } catch (Exception e) {
            System.err.println("Error initializing sound URLs: " + e.getMessage());
            e.printStackTrace();
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

    /**
     * Phương thức này không còn cần thiết nếu GamePanel dùng 2 instance Sound
     * (một cho music, một cho SFX) và gọi playSFX(index), playMusic(index).
     * Hoặc nếu bạn muốn một instance Sound có thể set file hiện tại:
     */
    public void setFileAndPrepare(int i) {
        // Nếu bạn có một đối tượng Sound chung và muốn thay đổi âm thanh nó sẽ phát
        // thì bạn có thể dùng currentMusicClip (hoặc một biến tương tự) ở đây.
        // Tuy nhiên, với cách dùng 2 instance (music, soundEffect) trong GamePanel,
        // GamePanel sẽ trực tiếp gọi playMusic(index) hoặc playSoundEffect(index).
        // Dưới đây là ví dụ nếu bạn muốn giữ lại một 'currentMusicClip' cho instance Sound này:
        Clip clipToPlay = getClip(i);
        if (clipToPlay != null) {
            // Nếu đây là instance dùng cho nhạc nền, dừng nhạc nền cũ
            if (currentMusicClip != null && currentMusicClip.isRunning()) {
                currentMusicClip.stop();
            }
            currentMusicClip = clipToPlay;
        }
    }


    public void play(int soundIndex) {
        Clip clipToPlay = getClip(soundIndex);
        if (clipToPlay != null) {
            if (clipToPlay.isRunning()) { // Nếu âm thanh này đang phát, dừng và phát lại từ đầu
                clipToPlay.stop();
            }
            clipToPlay.setFramePosition(0);
            clipToPlay.start();
        }
    }

    public void loop(int soundIndex) {
        // Dừng nhạc nền hiện tại (nếu có) trước khi phát nhạc mới
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
            // Không cần close ở đây vì getClip sẽ quản lý cache
        }

        currentMusicClip = getClip(soundIndex); // Lấy (hoặc tải) clip cho nhạc nền
        if (currentMusicClip != null) {
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
}