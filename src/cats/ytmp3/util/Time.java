package cats.ytmp3.util;

import java.util.concurrent.TimeUnit;

/**
 * User: cats
 * Date: 6/16/13
 * Time: 1:49 PM
 */
public class Time {

    private long ms;
    private long s;
    private long m;
    private long h;
    private long d;

    public Time(final long duration, final TimeUnit unit){
        update(duration, unit);
    }

    public Time(){
        this(0, TimeUnit.MILLISECONDS);
    }

    public long getMilliseconds(){
        return ms;
    }

    public long getSeconds(){
        return s;
    }

    public long getMinutes(){
        return m;
    }

    public long getHours(){
        return h;
    }

    public long getDays(){
        return d;
    }

    public long getTotal(final TimeUnit unit){
        long ms = this.ms;
        ms += TimeUnit.SECONDS.toMillis(s);
        ms += TimeUnit.MINUTES.toMillis(m);
        ms += TimeUnit.HOURS.toMillis(h);
        ms += TimeUnit.DAYS.toMillis(d);
        return unit.convert(ms, TimeUnit.MILLISECONDS);
    }

    public void update(final long duration, final TimeUnit unit){
        ms = unit.toMillis(duration);
        s = ms / 1000;
        ms -= s * 1000;
        m = s / 60;
        s -= m * 60;
        h = m / 60;
        m -= h * 60;
        d = h / 24;
        h -= d * 24;
    }

    public String toNameString(){
        if(d > 0)
            return String.format("%02d days %02d hours %02d minutes %02d seconds", d, h, m, s);
        else if(h > 0)
            return String.format("%02d hours %02d minutes %02d seconds", h, m, s);
        else if(m > 0)
            return String.format("%02d minutes %02d seconds", m, s);
        else if(s > 0)
            return String.format("%02d seconds", s);
        else
            return String.format("%02d milliseconds", ms);
    }

    public String toDurationString(){
        return d > 0 ? String.format("%02d:%02d:%02d:%02d", d, h, m, s) : String.format("%02d:%02d:%02d", h, m, s);
    }

    public String toString(){
        return toDurationString();
    }
}
