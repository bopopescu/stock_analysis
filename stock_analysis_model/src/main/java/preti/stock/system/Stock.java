package preti.stock.system;

import java.util.Date;
import java.util.Set;

public interface Stock<T> {

    boolean hasHistoryAtDate(Date date);

    String getCode();

    Set<Date> getAllHistoryDates();

    int getHistorySizeBeforeDate(Date date);

    double getCloseValueAtDate(Date date);

    double getVolumeAtDate(Date date);

    T getTarget();

}
