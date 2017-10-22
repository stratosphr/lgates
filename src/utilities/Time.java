package utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by gvoiron on 18/06/17.
 * Time : 16:00
 */
public final class Time {

    private final long nanoSeconds;

    public Time(long nanoSeconds) {
        this.nanoSeconds = nanoSeconds;
    }

    private long getNanoSeconds() {
        return nanoSeconds;
    }

    public static long now() {
        return System.nanoTime();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date(getNanoSeconds() / 1000000));
    }

}
