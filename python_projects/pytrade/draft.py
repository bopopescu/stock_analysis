# -*- coding: utf-8 -*-

from pyalgotrade import strategy
from pyalgotrade.tools import googlefinance
from pyalgotrade.barfeed import googlefeed
from pyalgotrade import dataseries
from pyalgotrade import technical
from pyalgotrade.broker import Order
import datetime

class MaxDonchianWindow(technical.EventWindow):
    def getValue(self):
        ret = None
        if self.windowFull():
            ret = self.getValues()[:-1].max()
        return ret

class MinDonchianWindow(technical.EventWindow):
    def getValue(self):
        ret = None
        if self.windowFull():
            ret = self.getValues()[:-1].max()
        return ret

class DonchianChannel(dataseries.SequenceDataSeries):
    def __init__(self, entrySeries, exitSeries, entrySize, exitSize, maxLen=None):

        super(DonchianChannel, self).__init__(maxLen)

        self.__entryWindow = technical.EventBasedFilter(entrySeries, MaxDonchianWindow(entrySize))
        self.__exitWindow = technical.EventBasedFilter(exitSeries, MinDonchianWindow(exitSize))

    def entryValue(self):
        return self.__entryWindow[-1];

    def exitValue(self):
        return self.__exitWindow[-1];

class DonchianChannelStrategy(strategy.BacktestingStrategy):
    def __init__(self, feed, instrument):
        super(DonchianChannelStrategy, self).__init__(feed, 10000)

        self.__instrument = instrument
        self.__position = None

        self.__donchian = DonchianChannel(feed[instrument].getHighDataSeries(), feed[instrument].getLowDataSeries(), 10, 2)


    def enterPosition(self, instrument, quantity, stopLossValue):
        assert quantity > 0;
        assert  instrument not in self.getBroker().getActiveInstruments()
        self.marketOrder(instrument=self.__instrument, quantity=1)
        self.stopOrder(instrument=self.__instrument, stopPrice=stopLossValue, quantity=-1, goodTillCanceled=True, allOrNone=True)

    def exitPosition(self, instrument):
        assert instrument in self.getBroker().getActiveInstruments()
        qty = self.getBroker().getShares(instrument)
        assert qty>0

        stopOrder = self.getBroker().getActiveOrders(instrument=instrument)[0]
        assert stopOrder.getType() == Order.Type.STOP
        self.getBroker().cancelOrder(stopOrder)

        self.marketOrder(instrument=self.__instrument, quantity=(-1*qty))




    def onBars(self, bars):
        #esse método deve ter a lógica de sizing, entrada e saída dos trades, com a estratégia
        #de entrada/saída/risco abstraída em outra classe a ser passada pra essa.
        if self.__donchian.entryValue() is None:
            return


        bar = bars[self.__instrument]
        if bar.getDateTime() == datetime.datetime(2014, 5, 16, 0, 0, 0):
            self.enterPosition(self.__instrument, 1, 12)
        if bar.getDateTime() == datetime.datetime(2014, 8, 1, 0, 0, 0):
            self.exitPosition(self.__instrument)


# Load the yahoo feed from the CSV file
code = "PETR4"
datafile = '/tmp/'+code+'-2014.csv'
#googlefinance.download_daily_bars(code, 2014, datafile)
feed = googlefeed.Feed()
feed.addBarsFromCSV(code, datafile, timezone=None, rowFilter=lambda row: row["Close"]=="-" or row["Open"]=="-" or row["High"]=="-" or row["Low"]=="-" or row["Volume"]=="-")

# Evaluate the strategy with the feed's bars.
myStrategy = DonchianChannelStrategy(feed, code)

from pyalgotrade.stratanalyzer import returns
from pyalgotrade import plotter

# Attach a returns analyzers to the strategy.
returnsAnalyzer = returns.Returns()
myStrategy.attachAnalyzer(returnsAnalyzer)

plt = plotter.StrategyPlotter(myStrategy)
# Include the SMA in the instrument's subplot to get it displayed along with the closing prices.
# Plot the simple returns on each bar.
plt.getOrCreateSubplot("returns").addDataSeries("Simple returns", returnsAnalyzer.getReturns())


myStrategy.run()
print "Final portfolio value: $%.2f" % myStrategy.getBroker().getEquity()
plt.plot()

# pos = myStrategy.getActivePositions().pop()
# myStrategy