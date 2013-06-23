package cats.ytmp3.gui.comp.input;

import cats.ytmp3.dl.DownloadService;
import cats.ytmp3.dl.request.DownloadRequest;
import cats.ytmp3.gui.DownloaderWindow;
import cats.ytmp3.gui.comp.YouTubeComponent;
import cats.ytmp3.image.ImageIconLoader;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * User: cats
 * Date: 6/21/13
 * Time: 7:07 PM
 */
public class ButtonPanel extends YouTubeComponent implements ActionListener, DropTargetListener {

    private static final URI YOUTUBE_URI = URI.create("http://www.youtube.com/");

    private final JButton addButton;
    private final DropTarget addTarget;
    private final JButton youtubeButton;
    private final JButton openDirectoryButton;

    private final JPanel container;

    public ButtonPanel(final DownloaderWindow window){
        super(window);

        youtubeButton = new JButton(ImageIconLoader.loadIcon("youtube_icon.png"));
        youtubeButton.setToolTipText("Opens YouTube in your browser");
        youtubeButton.addActionListener(this);

        addButton = new JButton(ImageIconLoader.loadIcon("add.png"));
        addButton.setToolTipText("Input (Drag and Drop) a YouTube URL to add to the queue");
        addButton.addActionListener(this);

        addTarget = new DropTarget(addButton, this);

        openDirectoryButton = new JButton(ImageIconLoader.loadIcon("open_directory.png"));
        openDirectoryButton.setToolTipText("Opens the directory in which files get downloaded to");
        openDirectoryButton.addActionListener(this);

        container = new JPanel(new GridBagLayout());
        container.setBorder(new EmptyBorder(5, 5, 5, 5));

        final GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        container.add(youtubeButton, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        container.add(addButton, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        container.add(openDirectoryButton, gbc);

        add(container, BorderLayout.CENTER);
    }

    public void dragEnter(final DropTargetDragEvent e){

    }

    public void dragExit(final DropTargetEvent e){

    }

    public void dragOver(final DropTargetDragEvent e){

    }

    public void dropActionChanged(final DropTargetDragEvent e){

    }

    public void drop(final DropTargetDropEvent e){
        final Object source = e.getSource();
        if(source.equals(addTarget)){
            final Transferable transfer = e.getTransferable();
            if(e.isDataFlavorSupported(DataFlavor.stringFlavor)){
                try{
                    e.acceptDrop(DnDConstants.ACTION_COPY);
                }catch(Exception ex){}
                try{
                    final String text = (String)transfer.getTransferData(DataFlavor.stringFlavor);
                    try {
                        final DownloadRequest request = DownloadRequestDialog.getDownloadRequest(window, text);
                        if (request == null)
                            throw new Exception("Invalid YouTube URL");
                        e.dropComplete(true);
                        window.getService().submit(request);
                        return;
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error fetching requested url: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }catch(Exception ex){}
            }else
                e.rejectDrop();
        }
    }

    public void actionPerformed(ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(addButton)){
            try{
                final DownloadRequest request = DownloadRequestDialog.getDownloadRequest(window);
                if(request == null)
                    throw new Exception("Invalid YouTube URL");
                window.getService().submit(request);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(null, "Error fetching requested url: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else if(source.equals(openDirectoryButton)){
            try{
                Desktop.getDesktop().open(DownloadService.DIRECTORY);
            }catch(Exception ex){}
        }else if(source.equals(youtubeButton)){
            try{
                Desktop.getDesktop().browse(YOUTUBE_URI);
            }catch(Exception ex){}
        }
    }
}
