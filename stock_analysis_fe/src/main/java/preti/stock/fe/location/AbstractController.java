package preti.stock.fe.location;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractController {
    protected final String DATE_FORMAT = "dd/MM/yyyy";

    protected Date parseDate(String dateStr) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
