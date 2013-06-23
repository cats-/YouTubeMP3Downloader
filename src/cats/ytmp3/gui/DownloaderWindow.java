package cats.ytmp3.gui;

import cats.ytmp3.dl.DownloadService;
import cats.ytmp3.gui.comp.input.ButtonPanel;
import cats.ytmp3.gui.comp.queue.TableQueuePanel;
import cats.ytmp3.gui.comp.state.StateListPanel;
import cats.ytmp3.image.ImageIconLoader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Image;

/**
 * User: cats
 * Date: 6/20/13
 * Time: 11:24 AM
 */
public class DownloaderWindow extends JFrame {

    private final StateListPanel stateListPanel;
    private final TableQueuePanel tableQueuePanel;

    private final ButtonPanel buttonPanel;

    private final JSplitPane splitPane;
    private final JPanel centerPanel;

    private final DownloadService service;

    public DownloaderWindow(){
        super("Youtube To MP3 Downloader");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        service = new DownloadService();

        stateListPanel = new StateListPanel(this);

        tableQueuePanel = new TableQueuePanel(this);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, stateListPanel, tableQueuePanel);
        splitPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(splitPane, BorderLayout.CENTER);

        buttonPanel = new ButtonPanel(this);

        add(buttonPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        stateListPanel.getList().setSelectedIndex(0);
    }

    public StateListPanel getStateListPanel(){
        return stateListPanel;
    }

    public TableQueuePanel getTableQueuePanel(){
        return tableQueuePanel;
    }

    public DownloadService getService(){
        return service;
    }

    public void display(){
        setSize(800, 500);
        setVisible(true);
        setAlwaysOnTop(true);
    }
}
