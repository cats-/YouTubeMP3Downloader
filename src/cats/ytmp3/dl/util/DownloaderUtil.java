package cats.ytmp3.dl.util;

import cats.ytmp3.dl.Downloader;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * User: cats
 * Date: 6/20/13
 * Time: 8:14 PM
 */
public final class DownloaderUtil {

    private DownloaderUtil(){}

    public static Object[] create(final Downloader downloader){
        final Object[] data = new Object[9];
        data[0] = downloader.getTitle();
        update(downloader, data);
        return data;
    }

    public static void update(final Downloader downloader, final Object[] data){
        data[1] = downloader.getBytesPerSecond();
        data[2] = downloader.getBytesReadAsString();
        data[3] = downloader.getPercentAsInt();
        data[4] = downloader.getTotalBytesAsString();
        data[5] = downloader.getBytesRemainingAsString();
        data[6] = downloader.getElapsedTime().toDurationString();
        data[7] = downloader.getRemainingTime().toDurationString();
        data[8] = downloader.getState().toString();
    }
}
