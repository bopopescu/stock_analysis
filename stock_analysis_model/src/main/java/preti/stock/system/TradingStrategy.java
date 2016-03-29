package preti.stock.system;

import java.util.Date;

public interface TradingStrategy {

	long getModelId();

	boolean enterPosition(Date d);

	boolean exitPosition(Date d);

	double calculatePositionSize(Date d, double currentTotalBalance);

	double calculateStopLossPoint(Date d);

}
