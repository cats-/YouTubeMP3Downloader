package cats.ytmp3.gui.comp.input;

import cats.ytmp3.YouTubeClient;
import cats.ytmp3.YouTubeVideo;
import cats.ytmp3.dl.request.DownloadRequest;
import cats.ytmp3.image.ImageIconLoader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: cats
 * Date: 6/21/13
 * Time: 8:43 PM
 */
public final class DownloadRequestDialog extends JDialog implements ActionListener {

    private static final ImageIcon LOGO = ImageIconLoader.loadIcon("youtube_logo.png");

    private final JFrame window;

    private final JTextField urlBox;
    private final JCheckBox nioBox;
    private final JButton okButton;
    private final JButton cancelButton;
    private final JPanel buttonPanel;
    private final JPanel urlPanel;
    private final JLabel logoLabel;

    private boolean cancelled;

    private DownloadRequestDialog(final JFrame window){
        super(window, "Input YouTube Video URL", true);
        this.window = window;

        cancelled = false;

        logoLabel = new JLabel(LOGO);

        urlBox = new JTextField(20);
        urlBox.setHorizontalAlignment(JLabel.CENTER);

        nioBox = new JCheckBox("NIO", true);
        nioBox.setToolTipText("Determines whether to use the new I/O streams or the standard I/O streams");

        urlPanel = new JPanel(new BorderLayout());
        urlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        urlPanel.add(urlBox, BorderLayout.CENTER);
        urlPanel.add(nioBox, BorderLayout.EAST);

        okButton = new JButton("OK");
        okButton.addActionListener(this);

        cancelButton = new JButton("CANCEL");
        cancelButton.addActionListener(this);

        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(okButton, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        buttonPanel.add(cancelButton, gbc);

        add(logoLabel, BorderLayout.NORTH);
        add(urlPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(okButton)){
            if(!urlBox.getText().trim().isEmpty())
                dispose();
        }else if(source.equals(cancelButton)){
            cancelled = true;
            dispose();
        }
    }

    private DownloadRequest getRequest() throws Exception{
        return cancelled ? null : new DownloadRequest(YouTubeClient.getVideoByURL(urlBox.getText().trim()), nioBox.isSelected());
    }

    public void display(){
        setAlwaysOnTop(true);
        pack();
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    public static DownloadRequest getDownloadRequest(final JFrame window) throws Exception {
        return getDownloadRequest(window, null);
    }

    public static DownloadRequest getDownloadRequest(final JFrame window, final String initial) throws Exception{
        final DownloadRequestDialog dialog = new DownloadRequestDialog(window);
        if(initial != null)
            dialog.urlBox.setText(initial);
        dialog.display();
        while(dialog.isVisible());
        return dialog.getRequest();
    }
}
