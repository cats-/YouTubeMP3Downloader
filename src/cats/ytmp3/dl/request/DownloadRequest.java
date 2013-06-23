package cats.ytmp3.dl.request;

import cats.ytmp3.YouTubeVideo;
import cats.ytmp3.dl.DownloadService;

import java.io.File;

/**
 * User: cats
 * Date: 6/17/13
 * Time: 11:48 PM
 */
public class DownloadRequest implements Cloneable{

    private final File directory;
    private final YouTubeVideo video;
    private final boolean nio;

    public DownloadRequest(final YouTubeVideo video, final File directory, final boolean nio){
        this.video = video;
        this.directory = directory;
        this.nio = nio;
    }

    public DownloadRequest(final YouTubeVideo video, final boolean nio){
        this(video, DownloadService.DIRECTORY, nio);
    }

    public boolean isNIO(){
        return nio;
    }

    public File getDirectory(){
        return directory;
    }

    public YouTubeVideo getVideo(){
        return video;
    }

    public String toString(){
        return String.format("%s to %s", video.getTitle(), directory);
    }

    public DownloadRequest clone(){
        return new DownloadRequest(video, directory, nio);
    }
}
