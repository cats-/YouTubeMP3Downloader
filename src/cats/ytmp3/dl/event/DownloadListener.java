package cats.ytmp3.dl.event;

/**
 * User: cats
 * Date: 6/16/13
 * Time: 11:56 AM
 */
public interface DownloadListener {

    public void onDownloadStart(final DownloadEvent e);
    public void onDownloadUpdate(final DownloadEvent e);
    public void onDownloadFinish(final DownloadEvent e);
}
