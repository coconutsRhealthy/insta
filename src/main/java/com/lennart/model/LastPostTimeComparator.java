package com.lennart.model;

import java.util.Comparator;

/**
 * Created by LennartMac on 16/07/2022.
 */
public class LastPostTimeComparator implements Comparator<String> {
    @Override
    public int compare(String lastPostTime1, String lastPostTime2) {
        String lastPostTime1Period = getPeriodFromPostTimeString(lastPostTime1);
        String lastPostTime2Period = getPeriodFromPostTimeString(lastPostTime2);

        int toReturn;

        if(lastPostTime1Period.equals(lastPostTime2Period)) {
            int lastPostTime1Integer = Integer.valueOf(lastPostTime1.substring(0, lastPostTime1.indexOf(" ")));
            int lastPostTime2Integer = Integer.valueOf(lastPostTime2.substring(0, lastPostTime2.indexOf(" ")));

            if(lastPostTime1Integer < lastPostTime2Integer) {
                toReturn = -1;
            } else if(lastPostTime1Integer == lastPostTime2Integer) {
                toReturn = 0;
            } else {
                toReturn = 1;
            }
        } else {
            if(lastPostTime1Period.equals("minute")) {
                toReturn = -1;
            } else if(lastPostTime1Period.equals("hour")) {
                if(lastPostTime2Period.equals("minute")) {
                    toReturn = 1;
                } else {
                    toReturn = -1;
                }
            } else if(lastPostTime1Period.equals("day")) {
                if(lastPostTime2Period.equals("minute") || lastPostTime2Period.equals("hour")) {
                    toReturn = 1;
                } else {
                    toReturn = -1;
                }
            } else if(lastPostTime1Period.equals("week")) {
                if(lastPostTime2Period.equals("minute") || lastPostTime2Period.equals("hour") ||
                        lastPostTime2Period.equals("day")) {
                    toReturn = 1;
                } else {
                    toReturn = -1;
                }
            } else if(lastPostTime1Period.equals("month")) {
                if(lastPostTime2Period.equals("minute") || lastPostTime2Period.equals("hour") ||
                        lastPostTime2Period.equals("day") || lastPostTime2Period.equals("week")) {
                    toReturn = 1;
                } else {
                    toReturn = -1;
                }
            } else {
                toReturn = 1;
            }
        }

        return toReturn;
    }

    private String getPeriodFromPostTimeString(String postTimeString) {
        String period = "unknown";

        if(postTimeString.contains("minute")) {
            period = "minute";
        } else if(postTimeString.contains("hour")) {
            period = "hour";
        } else if(postTimeString.contains("day")) {
            period = "day";
        } else if(postTimeString.contains("week")) {
            period = "week";
        } else if(postTimeString.contains("month")) {
            period = "month";
        } else if(postTimeString.contains("year")) {
            period = "year";
        }

        return period;
    }
}
