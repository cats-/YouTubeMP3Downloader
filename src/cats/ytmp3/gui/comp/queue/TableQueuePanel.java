package cats.ytmp3.gui.comp.queue;

import cats.ytmp3.gui.DownloaderWindow;
import cats.ytmp3.gui.comp.YouTubeComponent;

import javax.swing.JScrollPane;
import java.awt.BorderLayout;

/**
 * User: cats
 * Date: 6/20/13
 * Time: 1:59 PM
 */
public class TableQueuePanel extends YouTubeComponent{

    private final TableQueue table;
    private final JScrollPane scroll;

    public TableQueuePanel(final DownloaderWindow window){
        super(window, Integer.MAX_VALUE);

        table = new TableQueue(this);

        scroll = new JScrollPane(table);

        add(scroll, BorderLayout.CENTER);
    }

    public TableQueue getTable(){
        return table;
    }

}
