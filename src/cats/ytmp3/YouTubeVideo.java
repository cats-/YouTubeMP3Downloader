package cats.ytmp3;

import cats.ytmp3.dl.DownloadService;
import cats.ytmp3.util.Time;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * User: cats
 * Date: 6/16/13
 * Time: 12:01 PM
 */
public final class YouTubeVideo implements Cloneable{

    private static final String VIDEO_BASE = "http://www.youtube.com/watch?v=";
    private static final String CONVERTER_URL = "http://www.youtube-mp3.org/a/itemInfo/?video_id=";

    private final String url;
    private final String id;
    private final String author;
    private final String title;
    private final String desc;
    private final Time duration;
    private final double rating;
    private final long views;
    private final long likes;
    private final long dislikes;

    protected YouTubeVideo(final String id, final String author, final String title, final String desc, final long seconds, final double rating, final long views, final long likes, final long dislikes){
        this.id = id;
        url = VIDEO_BASE + id;
        this.author = author;
        this.title = title;
        this.desc = desc;
        this.rating = rating;
        this.views = views;
        this.likes = likes;
        this.dislikes = dislikes;


        duration = new Time(seconds, TimeUnit.SECONDS);
    }

    public YouTubeVideo clone(){
        return new YouTubeVideo(id, author, title, desc, duration.getTotal(TimeUnit.SECONDS), rating, views, likes, dislikes);
    }

    public String getFormattedViews(){
        return String.format("%,d", views);
    }

    public long getViews(){
        return views;
    }

    public String getFormattedLikes(){
        return String.format("%,d", likes);
    }

    public long getLikes(){
        return likes;
    }

    public String getFormattedDislikes(){
        return String.format("%,d", dislikes);
    }

    public long getDislikes(){
        return dislikes;
    }

    public String getID(){
        return id;
    }

    public Time getDuration(){
        return duration;
    }

    public String getURL(){
        return url;
    }

    public String getAuthor(){
        return author;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return desc;
    }

    public String getFormattedRating(){
        return String.format("%.2f", rating);
    }

    public double getRating(){
        return rating;
    }

    public URL getDownloadURL() throws IOException{
        final URL url = new URL(CONVERTER_URL + id);
        final URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        connection.setReadTimeout(DownloadService.timeout);
        connection.setConnectTimeout(DownloadService.timeout);
        connection.addRequestProperty("User-Agent", YouTubeClient.AGENT);
        final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8");
        final String line = reader.nextLine();
        reader.close();
        final String hash = line.split("\"h\"")[1].split("\"")[1].trim();
        return new URL(String.format("http://www.youtube-mp3.org/get?video_id=%s&h=%s", id, hash));
    }

    public String toString(){
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("Video info for: %s (ID: %s)", url, id));
        final char[] starsArray = new char[builder.length()];
        Arrays.fill(starsArray, '*');
        final String stars = new String(starsArray);
        builder.append(String.format("\n%s\n", stars));
        builder.append(String.format("Title:\n\t%s\n", title));
        builder.append(String.format("Uploader:\n\t%s\n", author));
        builder.append(String.format("Description:\n\t%s\n", desc.replaceAll("\n", "\n\t")));
        builder.append(String.format("Duration:\n\t%s\n", duration.toDurationString()));
        builder.append(String.format("Views:\n\t%,d\n", views));
        builder.append(String.format("Rating:\n\t%f\n", rating));
        builder.append(String.format("Likes:\n\t%,d\n", likes));
        builder.append(String.format("Dislikes:\n\t%,d\n", dislikes));
        builder.append(stars);
        return builder.toString();
    }

}
