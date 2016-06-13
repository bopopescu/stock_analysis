package preti.stock.api.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControllerTools {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String formatDate(Date d) {
        return new SimpleDateFormat(DATE_FORMAT).format(d);
    }

    public static Date parseDate(String d) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
