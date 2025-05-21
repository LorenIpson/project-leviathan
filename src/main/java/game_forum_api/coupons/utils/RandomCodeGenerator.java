package game_forum_api.coupons.utils;

import java.util.Random;

public class RandomCodeGenerator {

	public static String generateRandomCode(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(characters.length()); // 隨機生成索引
			stringBuilder.append(characters.charAt(index)); // 根據索引選擇字符
		}

		return stringBuilder.toString();
	}
}
