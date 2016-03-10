package com.twopicode.smct.result;

import android.content.Context;

import com.twopicode.smct.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**********************************
 * Created by Mikel on 14-Nov-15.
 *********************************/
public class ServiceResult {

    public ArrayList<DateResult> GetTwoPiResult;

    public static ArrayList<DateTime> getUniqueDatesForResults(ArrayList<DateResult> getDateResult) {

        ArrayList<DateTime> uniqueDates = new ArrayList<>();

        for (DateResult dateResult : getDateResult) {

            boolean containsDate = false;

            for (DateTime dateTime : uniqueDates)
                if ((dateTime).equals(dateResult.getServiceDateTime().withTimeAtStartOfDay()))
                    containsDate = true;

            if (!containsDate)
                uniqueDates.add(dateResult.getServiceDateTime().withTimeAtStartOfDay());
        }

        Collections.sort(uniqueDates, new Comparator<DateTime>() {
            @Override
            public int compare(DateTime lhs, DateTime rhs) {
                return lhs.compareTo(rhs);
            }
        });

        return uniqueDates;
    }

    public static String getDateStringForCategory(DateTime dateTime, Context context) {

        Interval today = new Interval(DateTime.now().withTimeAtStartOfDay(), Days.ONE);
        Interval tomorrow = new Interval(DateTime.now().withTimeAtStartOfDay().plusDays(1), Days.ONE);

        if (today.contains(dateTime)) {
            return context.getString(R.string.today);
        } else if (tomorrow.contains(dateTime)) {
            return context.getString(R.string.tomorrow);
        }

        DateTimeFormatter dtf = DateTimeFormat.forPattern("EEE dd MMM YYYY").withLocale(Locale.ENGLISH);
        return dateTime.toString(dtf);
    }

    public static ArrayList<String> getUniqueFuneralDirectors(ArrayList<DateResult> dateResults) {

        ArrayList<String> uniqueFuneralDirectors = new ArrayList<>();

        for (DateResult dateResult : dateResults) {

            boolean containsFD = false;

            for (String string : uniqueFuneralDirectors)
                if (string.equals(dateResult.getDirector()))
                    containsFD = true;

            if (!containsFD)
                uniqueFuneralDirectors.add(dateResult.getDirector());

        }

        return uniqueFuneralDirectors;
    }

    public static ArrayList<String> getUniqueFuneralDirectorsGeneric(ArrayList<Object> objects) {
        ArrayList<DateResult> dateResults = new ArrayList<>();
        for (Object object : objects)
            if (object instanceof DateResult)
                dateResults.add((DateResult)object);
        return getUniqueFuneralDirectors(dateResults);
    }
}
