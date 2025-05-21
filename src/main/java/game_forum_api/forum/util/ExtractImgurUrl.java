package game_forum_api.forum.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ExtractImgurUrl {

    public static String extractFirstImgurUrl(String html) {
        Document doc = Jsoup.parse(html);
        Element img = doc.select("img[src^=https://i.imgur.com]").first();
        return img != null ? img.attr("src") : null;
    }

}
