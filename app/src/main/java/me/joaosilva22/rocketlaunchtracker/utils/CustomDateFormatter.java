package me.joaosilva22.rocketlaunchtracker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class CustomDateFormatter {

    public static abstract class Formats {
        public static final String SQL = "yyyy-MM-dd HH:mm:ss";
        public static final String DISPLAY = "MMMM dd, yyyy HH:mm:ss";
        public static final String DEFAULT = "MMMM dd, yyyy HH:mm:ss 'UTC'";
    }

    public static abstract class TimeZones {
        public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    }

    private CustomDateFormatter() {}

    public static String toSQL(String date, String format) {
        return toSQL(date, format, null);
    }

    public static String toSQL(String date, String format, TimeZone timeZone) {
        return convert(date, format, Formats.SQL, timeZone);
    }

    public static String toDisplay(String date, String format) {
        return toDisplay(date, format, null);
    }

    public static String toDisplay(String date, String format, TimeZone timeZone) {
        return convert(date, format, Formats.DISPLAY, timeZone);
    }

    public static Long toMillis(String date, String format) {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat(format, Locale.US);

        try {
            d = f.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d.getTime();
    }

    private static String convert(String date, String oldFormat, String newFormat, TimeZone timeZone) {
        Date d = new Date();
        SimpleDateFormat o = new SimpleDateFormat(oldFormat, Locale.US);
        SimpleDateFormat n = new SimpleDateFormat(newFormat, Locale.US);

        if (timeZone != null) {
            o.setTimeZone(timeZone);
            n.setTimeZone(TimeZone.getDefault());
        }

        try {
            d = o.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return n.format(d);
    }
}
