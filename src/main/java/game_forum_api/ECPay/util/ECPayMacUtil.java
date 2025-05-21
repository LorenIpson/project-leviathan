package game_forum_api.ECPay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ECPayMacUtil {
//    確定OK的。ID為3002607。
//    private static final String HASH_KEY = "pwFHCqoQZGmho4w6";  
//    private static final String HASH_IV = "EkRm7iFT261dpevs";  

    // 設定 ECPay 提供的 HashKey & HashIV
    private static final String HASH_KEY = "5294y06JbISpM5x9";  
    private static final String HASH_IV = "v77hoKGq4kWxNNIS";  

    /**
     * 產生 CheckMacValue
     * @param params 交易參數
     * @return CheckMacValue
     */
    public static String generateCheckMacValue(Map<String, String> params) {
    	
        // 1️⃣ 先移除 `CheckMacValue`
        params.remove("CheckMacValue");
        
        // 2️⃣ 按照 Key 名稱進行排序
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);

        // 3️⃣ 產生 Query String（符合 ECPay 格式）
        StringBuilder rawData = new StringBuilder("HashKey=" + HASH_KEY + "&");
        for (String key : sortedKeys) {
            rawData.append(key).append("=").append(params.get(key)).append("&");
        }
        rawData.append("HashIV=").append(HASH_IV);

        // 4️⃣ URL Encode（符合 ECPay 規範）
        String urlEncodedData = urlEncode(rawData.toString()).toLowerCase();

        // Debug Log（檢查每一步）
        System.out.println("🔍 排序後的 Key 值：" + sortedKeys);
        System.out.println("🔍 原始字串：" + rawData.toString());
        System.out.println("🔍 URL Encoded：" + urlEncodedData);
        System.out.println("🔍 SHA-256 Hash：" + encodeSHA256(urlEncodedData).toUpperCase());

        // 5️⃣ SHA-256 加密，轉大寫
        return encodeSHA256(urlEncodedData).toUpperCase();
    	
    }

    /**
     * 驗證 CheckMacValue 是否正確
     * @param params ECPay 回傳的參數
     * @param receivedCheckMacValue ECPay 給的 CheckMacValue
     * @return boolean
     */
    public static boolean verifyCheckMacValue(Map<String, String> params, String receivedCheckMacValue) {
        String computedCheckMacValue = generateCheckMacValue(params);
        return computedCheckMacValue.equalsIgnoreCase(receivedCheckMacValue);
    }

    /**
     * 進行 URL Encode 並符合 ECPay 的特殊規則
     * @param data 需要編碼的字串
     * @return 已編碼的字串
     */
    private static String urlEncode(String data) {
        try {
            String encoded = URLEncoder.encode(data, StandardCharsets.UTF_8.toString());
            // ECPay 特殊規則轉換
            return encoded.replace("%21", "!")
                          .replace("%28", "(")
                          .replace("%29", ")")
                          .replace("%2a", "*")
                          .replace("%2d", "-")
                          .replace("%2e", ".")
                          .replace("%5f", "_");

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL Encode 失敗：" + e.getMessage());
        }
    }

    /**
     * 進行 SHA-256 加密
     * @param data 要加密的字串
     * @return SHA-256 雜湊值
     */
    private static String encodeSHA256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 加密失敗：" + e.getMessage());
        }
    }
}
