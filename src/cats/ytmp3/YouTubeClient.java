package cats.ytmp3;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * User: cats
 * Date: 6/18/13
 * Time: 11:14 AM
 */
public final class YouTubeClient {

    public static final String AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36";

    public static int timeout = 60000;

    private YouTubeClient(){}

    public static YouTubeVideo getVideoByURL(final String url) throws Exception{
        return getVideoByID(getID(url));
    }

    private static String getID(final String url){
        if(url.contains("watch?v="))
            return validateID(url.substring(url.indexOf("watch?v=") + 8));
        else if(url.contains("tu.be/"))
            return validateID(url.substring(url.indexOf("tu.be/") + 6));
        else
            return null;
    }

    private static String validateID(final String id){
        final StringBuilder builder = new StringBuilder();
        for(final char c : id.toCharArray()){
            if(!Character.isLetterOrDigit(c) && c != '-' && c != '_')
                break;
            builder.append(c);
        }
        return builder.toString();
    }

    public static YouTubeVideo getVideoByID(final String id) throws Exception {
        final String validID = validateID(id);
        final URL url = new URL(String.format("https://gdata.youtube.com/feeds/api/videos/%s?v=2&alt=json", validID));
        final URLConnection connection = url.openConnection();
        connection.addRequestProperty("User-Agent", AGENT);
        connection.setReadTimeout(timeout);
        connection.setConnectTimeout(timeout);
        connection.setUseCaches(false);
        final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8");
        final String data = reader.nextLine();
        reader.close();
        final String author = parse(String.class, data, "yt\\$display", true);
        final String title = parse(String.class, data, "media\\$title", 3, true);
        final String desc = parse(String.class, data, "media\\$description", 3, true).replaceAll("\\\\n", "\n");
        final long seconds = parse(Long.class, data, "seconds", true);
        final double rating = parse(Double.class, data, "average", false);
        final long views = parse(Long.class, data, "viewCount", true);
        final long likes = parse(Long.class, data, "numLikes", true);
        final long dislikes = parse(Long.class, data, "numDislikes", true);
        return new YouTubeVideo(validID, author, title, desc, seconds, rating, views, likes, dislikes);
    }

    private static <T> T parse(final Class<T> clazz, final String data, final String key, final boolean useQuotes){
        return parse(clazz, data, key, -1, useQuotes);
    }

    private static <T> T parse(final Class<T> clazz, final String data, final String key, final int index, final boolean useQuotes){
        final String info = data.split(String.format("\"%s\":", key))[1].split(useQuotes ? "\"" : ",")[index == -1 ? useQuotes ? 1 : 0 : index];
        if(clazz.equals(String.class))
            return (T)info;
        try{
            final Method method = clazz.getDeclaredMethod("valueOf", String.class);
            return (T)method.invoke(null, info);
        }catch(Exception e){
            return null;
        }
    }

}
