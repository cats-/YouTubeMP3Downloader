package cats.ytmp3.main;

import cats.ytmp3.gui.DownloaderWindow;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.dnd.DnDConstants;

/**
 * User: cats
 * Date: 6/16/13
 * Time: 2:01 PM
 */
public class Main {

    public static void main(String args[]) {
        SwingUtilities.invokeLater(
                () -> {
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);
                    SubstanceLookAndFeel.setSkin(BusinessBlackSteelSkin.class.getName());
                    SubstanceLookAndFeel.setToUseConstantThemesOnDialogs(true);
                    final DownloaderWindow window = new DownloaderWindow();
                    window.display();
                }
        );
    }
}
