package cats.ytmp3.dl.event;

/**
 * User: cats
 * Date: 6/20/13
 * Time: 7:16 PM
 */
public interface DownloadServiceListener {

    public void onDownloaderAdd(final DownloadServiceEvent e);

    public void onDownloaderRemove(final DownloadServiceEvent e);
}
