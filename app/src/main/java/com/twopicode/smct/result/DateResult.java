package com.twopicode.smct.result;

import android.graphics.drawable.Drawable;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**********************************
 * Created by Mikel on 14-Nov-15.
 *********************************/
public class DateResult implements Serializable {

    public static final String DATE_PATTERN = "YYYYMMdd";
    public static final String TIME_PATTERN = "hh:mm aa";

    public String director = "";
    public String location = "";
    public String name = "";
    public String servdate = "";
    public String time = "";
    public String type = "";

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern(TIME_PATTERN).withLocale(Locale.ENGLISH);
        return getServiceDateTime().toString(dtf);
    }

    public String getDirector() {
        return director;
    }

    public String getType() {
        return type;
    }

    public Drawable getLogo() {
        return null;
    }

    public DateTime getServiceDateTime() {

        return DateTimeFormat.forPattern(DATE_PATTERN)
                .withLocale(Locale.ENGLISH)
                .parseDateTime(servdate)
                .plusSeconds(Integer.valueOf(time));
    }

    public static class DateComparator implements Comparator<Object> {
        public int compare(Object d1, Object d2) {
            return ((DateResult) d1).getServiceDateTime().compareTo(((DateResult) d2).getServiceDateTime());
        }
    }
}
