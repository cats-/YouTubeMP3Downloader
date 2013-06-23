package cats.ytmp3.dl;

import cats.ytmp3.YouTubeVideo;
import cats.ytmp3.dl.event.DownloadEvent;
import cats.ytmp3.dl.event.DownloadListener;
import cats.ytmp3.dl.event.DownloadServiceEvent;
import cats.ytmp3.dl.event.DownloadServiceListener;
import cats.ytmp3.dl.event.StateChangeEvent;
import cats.ytmp3.dl.event.StateChangeListener;
import cats.ytmp3.dl.request.DownloadRequest;
import cats.ytmp3.dl.request.DownloadRequestResponse;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: cats
 * Date: 6/16/13
 * Time: 11:52 AM
 */
public class DownloadService {

    public static final File DIRECTORY = new File(System.getProperty("user.home"), "YTMP3 Downloads");

    static{
        if(!DIRECTORY.exists())
            DIRECTORY.mkdir();
    }

    public static final int DOWNLOAD_THREADS = 2;

    public static int timeout = 600000;

    private final int downloadThreads;
    private final ExecutorService service;

    private final List<DownloadListener> dListeners;
    private final List<StateChangeListener> sListeners;
    private final List<DownloadServiceListener> dsListeners;
    private final List<Downloader> downloaders;

    public DownloadService(final int downloadThreads){
        service = Executors.newFixedThreadPool(this.downloadThreads = downloadThreads);

        dListeners = new LinkedList<>();

        sListeners = new LinkedList<>();

        dsListeners = new LinkedList<>();

        downloaders = Collections.synchronizedList(new LinkedList<>());
    }

    public DownloadService(){
        this(DOWNLOAD_THREADS);
    }

    public Downloader[] getDownloaders(){
        return downloaders.toArray(new Downloader[downloaders.size()]);
    }

    public Downloader getDownloaderAt(final int index){
        return downloaders.get(index);
    }

    public Downloader[] getDownloaders(final State... states){
        final List<Downloader> dlers = new LinkedList<>();
        final List<State> statesList = Arrays.asList(states);
        downloaders.stream().filter(dler -> statesList.contains(dler.getState())).forEach(dlers::add);
        return dlers.toArray(new Downloader[dlers.size()]);
    }

    public DownloadListener[] getDownloadListeners(){
        return dListeners.toArray(new DownloadListener[dListeners.size()]);
    }

    public boolean addDownloadServiceListener(final DownloadServiceListener listener){
        return dsListeners.add(listener);
    }

    public boolean removeDownloadServiceListener(final DownloadServiceListener listener){
        return dsListeners.remove(listener);
    }

    public DownloadServiceListener[] getDownloadServiceListeners(){
        return dsListeners.toArray(new DownloadServiceListener[dsListeners.size()]);
    }

    public boolean addDownloadListener(final DownloadListener listener){
        return dListeners.add(listener);
    }

    public boolean removeDownloadListener(final DownloadListener listener){
        return dListeners.remove(listener);
    }

    public StateChangeListener[] getStateChangeListeners(){
        return sListeners.toArray(new StateChangeListener[sListeners.size()]);
    }

    public void fireStateChangeEvents(final StateChangeEvent e){
        for(final StateChangeListener l : sListeners)
            l.onStateChange(e);
    }

    public boolean addStateChangeListener(final StateChangeListener l){
        return sListeners.add(l);
    }

    public boolean removeStateChangeListener(final StateChangeListener l){
        return sListeners.remove(l);
    }

    public int getDownloadThreads(){
        return downloadThreads;
    }

    public void fireDownloaderAdd(final DownloadServiceEvent e){
        for(final DownloadServiceListener l : dsListeners)
            l.onDownloaderAdd(e);
    }

    public void fireDownloadRemove(final DownloadServiceEvent e){
        for(final DownloadServiceListener l : dsListeners)
            l.onDownloaderAdd(e);
    }

    public void fireDownloadOnStart(final DownloadEvent e){
        for(final DownloadListener l : dListeners)
            l.onDownloadStart(e);
    }

    public void fireDownloadOnUpdate(final DownloadEvent e){
        for(final DownloadListener l : dListeners)
            l.onDownloadUpdate(e);
    }

    public void fireDownloadOnFinish(final DownloadEvent e){
        for(final DownloadListener l : dListeners)
            l.onDownloadFinish(e);
    }

    public DownloadRequestResponse submit(final DownloadRequest request){
        final YouTubeVideo video = request.getVideo();
        final File directory = request.getDirectory();
        if(video == null || directory == null)
            return new DownloadRequestResponse(request, false);
        final Downloader downloader = new Downloader(this, request.clone());
        if(downloaders.add(downloader)){
            fireDownloaderAdd(new DownloadServiceEvent(this, downloader));
            service.execute(downloader);
            return new DownloadRequestResponse(request, true);
        }else
            return new DownloadRequestResponse(request, false);
    }

    public DownloadRequestResponse[] submit(final Collection<DownloadRequest> requests){
        return submit(requests.toArray(new DownloadRequest[requests.size()]));
    }

    public DownloadRequestResponse[] submit(final DownloadRequest... requests){
        final DownloadRequestResponse[] responses = new DownloadRequestResponse[requests.length];
        for(int i = 0; i < requests.length; i++)
            responses[i] = submit(requests[i]);
        return responses;
    }

    public void shutdown(){
        service.shutdown();
    }

}
