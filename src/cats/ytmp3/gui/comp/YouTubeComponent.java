package cats.ytmp3.gui.comp;

import cats.ytmp3.dl.DownloadService;
import cats.ytmp3.gui.DownloaderWindow;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * User: cats
 * Date: 6/20/13
 * Time: 1:10 PM
 */
public class YouTubeComponent extends JPanel{

    protected final DownloaderWindow window;
    protected final DownloadService service;

    protected YouTubeComponent(final DownloaderWindow window, final int maxWidth){
        super(new BorderLayout());
        if(maxWidth > 0)
            setMaximumSize(new Dimension(maxWidth, window.getPreferredSize().height));
        this.window = window;

        service = window.getService();
    }

    protected YouTubeComponent(final DownloaderWindow window){
        this(window, -1);
    }

    public DownloaderWindow getWindow(){
        return window;
    }
}
