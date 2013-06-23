package cats.ytmp3.dl;

import cats.ytmp3.dl.event.DownloadEvent;
import cats.ytmp3.dl.event.StateChangeEvent;
import cats.ytmp3.dl.event.StateChangeListener;
import cats.ytmp3.dl.request.DownloadRequest;
import cats.ytmp3.util.ByteFormatter;
import cats.ytmp3.util.FileNameValidator;
import cats.ytmp3.util.Time;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * User: cats
 * Date: 6/16/13
 * Time: 12:02 PM
 */
public class Downloader implements Runnable{

    private final DownloadRequest request;
    private final File directory;
    private final File destination;

    private final DownloadService service;
    private State state;

    private long totalBytes;
    private long bytesRead;
    private long startMillis;
    private final Time elapsed;
    private final Time remaining;

    public Downloader(final DownloadService service, final DownloadRequest request){
        this.service = service;
        this.request = request.clone();

        directory = request.getDirectory();
        destination = new File(directory, FileNameValidator.validate(request.getVideo().getTitle()) + ".mp3");

        setState(State.WAITING);

        totalBytes = bytesRead = startMillis = 0;

        elapsed = new Time();
        remaining = new Time();
    }

    public File getDirectory(){
        return directory;
    }

    public DownloadRequest getRequest(){
        return request;
    }

    public DownloadService getService(){
        return service;
    }

    public State getState(){
        return state;
    }

    public boolean isRunning(){
        return state == State.DOWNLOADING;
    }

    public File getDestination(){
        return destination;
    }

    private void setState(final State state){
        final State old = this.state;
        this.state = state;
        service.fireStateChangeEvents(new StateChangeEvent(this, old, state));
    }

    public Time getElapsedTime(){
        return elapsed;
    }

    public long getSpeed(){
        try{
            return bytesRead / elapsed.getTotal(TimeUnit.SECONDS);
        }catch(Exception e){
            return 0;
        }
    }

    public String getBytesPerSecond(){
        return ByteFormatter.format(getSpeed(), 2) + "/s";
    }

    public String getBytesReadAsString(){
        return ByteFormatter.format(bytesRead, 2);
    }

    public String getTotalBytesAsString(){
        return ByteFormatter.format(totalBytes, 2);
    }

    public long getBytesRemaining(){
        return totalBytes - bytesRead;
    }

    public String getBytesRemainingAsString(){
        return ByteFormatter.format(totalBytes - bytesRead, 2);
    }

    public long getBytesRead(){
        return bytesRead;
    }

    public long getTotalBytes(){
        return totalBytes;
    }

    public double getPercent(){
        try{
            return ((double)bytesRead*100) / totalBytes;
        }catch(Exception e){
            return 0;
        }
    }

    public int getPercentAsInt(){
        return (int)getPercent();
    }

    public String getPercentAsString(){
        return String.format("%1.2f%%", getPercent());
    }

    public Time getRemainingTime(){
        return remaining;
    }

    private void updateTime(){
        elapsed.update(System.currentTimeMillis() - startMillis, TimeUnit.MILLISECONDS);
        try{
            remaining.update((getBytesRemaining() * 1000) / getSpeed(), TimeUnit.MILLISECONDS);
        }catch(Exception e){}
    }

    public String getTitle(){
        return request.getVideo().getTitle();
    }

    protected void downloadNIO(){
        InputStream input = null;
        FileOutputStream output = null;
        bytesRead = totalBytes = startMillis = 0;
        elapsed.update(0, TimeUnit.MILLISECONDS);
        remaining.update(0, TimeUnit.MILLISECONDS);
        try{
            setState(State.INITIALIZING);
            service.fireDownloadOnStart(new DownloadEvent(this));
            if(destination.exists())
                destination.delete();
            output = new FileOutputStream(destination, false);
            final FileChannel outChannel = output.getChannel();
            final URL url = request.getVideo().getDownloadURL();
            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            connection.setReadTimeout(DownloadService.timeout);
            connection.setConnectTimeout(DownloadService.timeout);
            totalBytes = connection.getContentLength();
            input = connection.getInputStream();
            final ReadableByteChannel inChannel = Channels.newChannel(input);
            setState(State.DOWNLOADING);
            startMillis = System.currentTimeMillis();
            final Thread t = new Thread(
                    () -> {
                        try{
                            outChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
                        }catch(Exception e){
                            try{
                                inChannel.close();
                                outChannel.close();
                            }catch(Exception ex){}
                        }
                    }
            );
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            while(bytesRead < totalBytes && inChannel.isOpen() && outChannel.isOpen()){
                bytesRead = outChannel.size();
                service.fireDownloadOnUpdate(new DownloadEvent(this));
                updateTime();
            }
            if(!inChannel.isOpen() && !outChannel.isOpen())
                throw new IOException("Failed to download to: " + destination);
            inChannel.close();
            outChannel.close();
            updateTime();
            setState(State.SUCCESS);
            service.fireDownloadOnFinish(new DownloadEvent(this));
        }catch(Exception e){
            setState(State.FAIL);
            service.fireDownloadOnFinish(new DownloadEvent(this, e));
            try{
                output.close();
                input.close();
            }catch(Exception ex){}
        }
    }

    protected void download(){
        final byte[] buffer = new byte[15600];
        InputStream input = null;
        FileOutputStream output = null;
        int temp = 0;
        bytesRead = totalBytes = startMillis = 0;
        elapsed.update(0, TimeUnit.MILLISECONDS);
        remaining.update(0, TimeUnit.MILLISECONDS);
        try{
            setState(State.INITIALIZING);
            service.fireDownloadOnStart(new DownloadEvent(this));
            if(destination.exists())
                destination.delete();
            output = new FileOutputStream(destination, false);
            final URL url = request.getVideo().getDownloadURL();
            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            connection.setReadTimeout(DownloadService.timeout);
            connection.setConnectTimeout(DownloadService.timeout);
            totalBytes = connection.getContentLength();
            input = connection.getInputStream();
            setState(State.DOWNLOADING);
            startMillis = System.currentTimeMillis();
            while((temp = input.read(buffer)) != -1){
                bytesRead += temp;
                output.write(buffer, 0, temp);
                service.fireDownloadOnUpdate(new DownloadEvent(this));
                updateTime();
            }
            output.flush();
            setState(State.SUCCESS);
            service.fireDownloadOnFinish(new DownloadEvent(this));
            output.close();
            input.close();
            updateTime();
        }catch(Exception e){
            e.printStackTrace();
            setState(State.FAIL);
            service.fireDownloadOnFinish(new DownloadEvent(this, e));
            try{
                output.flush();
                output.close();
                input.close();
            }catch(Exception ex){}
        }
    }

    public void run(){
        if(request.isNIO())
            downloadNIO();
        else
            download();
    }

    public String toString(){
        return String.format("Elapsed: %s | Remaining: %s | %s | %s/%s | (%1.2f%%)", elapsed, remaining, getBytesPerSecond(), getBytesReadAsString(), getTotalBytesAsString(), getPercent());
    }
}
