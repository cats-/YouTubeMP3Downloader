package cats.ytmp3.gui.comp.state;

import cats.ytmp3.gui.DownloaderWindow;
import cats.ytmp3.gui.comp.YouTubeComponent;

import javax.swing.JScrollPane;
import java.awt.BorderLayout;

/**
 * User: cats
 * Date: 6/20/13
 * Time: 1:18 PM
 */
public class StateListPanel extends YouTubeComponent{

    private final StateList list;
    private final JScrollPane scroll;

    public StateListPanel(final DownloaderWindow window){
        super(window, 100);

        list = new StateList(this);

        scroll = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scroll, BorderLayout.CENTER);
    }

    public StateList getList(){
        return list;
    }
}
