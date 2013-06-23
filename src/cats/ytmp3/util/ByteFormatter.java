package cats.ytmp3.util;

import java.text.DecimalFormat;

/**
 * User: cats
 * Date: 6/17/13
 * Time: 6:24 PM
 */
public class ByteFormatter {

    public static final long KILOBYTES = 1024;
    public static final long MEGABYTES = KILOBYTES * KILOBYTES;
    public static final long GIGABYTES = MEGABYTES * KILOBYTES;

    public static String format(final long bytes, final int precision){
        if(bytes >= GIGABYTES)
            return String.format("%sGB", round(bytes / (double)GIGABYTES, precision));
        else if(bytes >= MEGABYTES)
            return String.format("%sMB", round(bytes / (double)MEGABYTES, precision));
        else if(bytes >= KILOBYTES)
            return String.format("%sKB", round(bytes / (double)KILOBYTES, precision));
        else
            return Double.toString(bytes)+"B";
    }

    private static double round(final double bytes, final int precision){
        return Math.round(bytes * Math.pow(10, precision)) / Math.pow(10, precision);
    }

}
