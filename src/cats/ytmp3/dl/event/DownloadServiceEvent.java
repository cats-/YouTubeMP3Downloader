package cats.ytmp3.dl.event;

import cats.ytmp3.dl.DownloadService;
import cats.ytmp3.dl.Downloader;

/**
 * User: cats
 * Date: 6/20/13
 * Time: 7:17 PM
 */
public class DownloadServiceEvent {

    private final DownloadService service;
    private final Downloader downloader;
    private final long time;

    public DownloadServiceEvent(final DownloadService service, final Downloader downloader){
        this.service = service;
        this.downloader = downloader;

        time = System.currentTimeMillis();
    }

    public DownloadService getService(){
        return service;
    }

    public Downloader getDownloader(){
        return downloader;
    }

    public long getTime(){
        return time;
    }
}
