package cats.ytmp3.dl.event;

import cats.ytmp3.YouTubeVideo;
import cats.ytmp3.dl.Downloader;

/**
 * User: cats
 * Date: 6/16/13
 * Time: 11:56 AM
 */
public class DownloadEvent {

    private final Downloader downloader;
    private final Exception exception;
    private final long time;

    public DownloadEvent(final Downloader downloader, final Exception exception){
        this.downloader = downloader;
        this.exception = exception;

        time = System.currentTimeMillis();
    }

    public DownloadEvent(final Downloader downloader){
        this(downloader, null);
    }

    public Downloader getDownloader(){
        return downloader;
    }

    public Exception getException(){
        return exception;
    }

    public YouTubeVideo getVideo(){
        return downloader.getRequest().getVideo();
    }

    public boolean isSuccess(){
        return exception == null;
    }

    public long getTime(){
        return time;
    }

}
