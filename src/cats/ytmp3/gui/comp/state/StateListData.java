package cats.ytmp3.gui.comp.state;

import cats.ytmp3.dl.DownloadService;
import cats.ytmp3.dl.State;
import cats.ytmp3.dl.event.StateChangeEvent;
import cats.ytmp3.dl.event.StateChangeListener;
import cats.ytmp3.image.ImageIconLoader;

import javax.swing.ImageIcon;

/**
 * User: cats
 * Date: 6/21/13
 * Time: 3:47 PM
 */
public class StateListData{

    private final String name;
    private final ImageIcon icon;
    private final State[] states;

    public StateListData(final String name, final String iconName, final State... states){
        this.name = name;
        icon = ImageIconLoader.loadIcon(iconName);
        this.states = states;
    }

    public String getName(){
        return name;
    }

    public ImageIcon getIcon(){
        return icon;
    }

    public State[] getStates(){
        return states;
    }

    public int getCount(final DownloadService service){
        return service.getDownloaders(states).length;
    }
}
