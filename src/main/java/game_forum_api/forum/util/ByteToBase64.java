package game_forum_api.forum.util;

import java.util.Base64;

public class ByteToBase64 {

    public static String toBase64(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "沒有上傳封面。";
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

}
