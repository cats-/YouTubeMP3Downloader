package cats.ytmp3.image;

import javax.swing.ImageIcon;

/**
 * User: cats
 * Date: 6/20/13
 * Time: 1:46 PM
 */
public final class ImageIconLoader {

    private ImageIconLoader(){}

    public static ImageIcon loadIcon(final String name){
        return new ImageIcon(ImageIconLoader.class.getResource(name));
    }
}
