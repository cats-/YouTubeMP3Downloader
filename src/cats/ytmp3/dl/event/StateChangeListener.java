package cats.ytmp3.dl.event;

/**
 * User: cats
 * Date: 6/16/13
 * Time: 7:03 PM
 */
public interface StateChangeListener {

    public void onStateChange(final StateChangeEvent e);
}
