package cats.ytmp3.dl.event;

import cats.ytmp3.dl.Downloader;
import cats.ytmp3.dl.State;

/**
 * User: cats
 * Date: 6/16/13
 * Time: 7:03 PM
 */
public class StateChangeEvent {

    private final Downloader downloader;
    private final State oldState;
    private final State newState;
    private final long time;

    public StateChangeEvent(final Downloader downloader, final State oldState, final State newState){
        this.downloader = downloader;
        this.oldState = oldState;
        this.newState = newState;

        time = System.currentTimeMillis();
    }

    public Downloader getDownloader(){
        return downloader;
    }

    public State getOldState(){
        return oldState;
    }

    public State getNewState(){
        return newState;
    }

    public long getTime(){
        return time;
    }
}
