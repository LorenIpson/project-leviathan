package game_forum_api.forum.util;

import org.jsoup.Jsoup;

public class ExtractHtmlTags {

    public static String removeHtmlTags(String html, int limit) {
        String text = Jsoup.parse(html).text().replaceAll("\\n+", "\n").trim();
        return text.length() > limit ? text.substring(0, limit) + " ..." : text;
    }

}
