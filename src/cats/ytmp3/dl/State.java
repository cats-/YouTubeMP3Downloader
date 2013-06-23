package cats.ytmp3.dl;

import cats.ytmp3.image.ImageIconLoader;

import javax.swing.ImageIcon;
import java.util.Locale;

/**
 * User: cats
 * Date: 6/20/13
 * Time: 10:52 AM
 */

public enum State{
    WAITING, INITIALIZING, DOWNLOADING, SUCCESS, FAIL;

    private final ImageIcon icon = ImageIconLoader.loadIcon("state_" + super.toString().toLowerCase(Locale.CANADA) + ".png");

    public ImageIcon getIcon(){
        return icon;
    }

    public String toString(){
        final String s = super.toString();
        return s.charAt(0) + s.substring(1).toLowerCase(Locale.CANADA);
    }

    public static State getState(final String name){
        for(final State state : values())
            if(state.toString().equals(name))
                return state;
        return null;
    }
}
