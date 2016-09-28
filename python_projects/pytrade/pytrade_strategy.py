from pytrade_base import BaseTradingStrategy
import datetime
import math

from pyalgotrade import technical
from pyalgotrade import dataseries




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
            ret = self.getValues()[:-1].min()
        return ret

class DonchianChannel(dataseries.SequenceDataSeries):
    def __init__(self, entrySeries, exitSeries, entrySize, exitSize, maxLen=None):

        super(DonchianChannel, self).__init__(maxLen)

        self.__entryWindow = technical.EventBasedFilter(entrySeries, MaxDonchianWindow(entrySize))
        self.__exitWindow = technical.EventBasedFilter(exitSeries, MinDonchianWindow(exitSize))

    def getMinDonchian(self):
        return self.__exitWindow

    def getMaxDonchian(self):
        return self.__entryWindow

    def entryValue(self):
        return self.__entryWindow[-1];

    def exitValue(self):
        return self.__exitWindow[-1];

    def isReady(self):
        return self.entryValue() is not None and self.exitValue() is not None

class DonchianTradingStrategy(BaseTradingStrategy):
    def __init__(self, instrument, feed, broker, donchianEntrySize, donchianExitSize):
        super(DonchianTradingStrategy, self).__init__(instrument, feed, broker)

        self.__instrument = instrument
        self.__donchian = DonchianChannel(feed[instrument].getHighDataSeries(), feed[instrument].getLowDataSeries(), donchianEntrySize, donchianExitSize, maxLen=60)

    def shouldAnalyze(self, bar):
        return self.__donchian.isReady()

    def shouldBuyStock(self, bar):
        return bar.getVolume()>10000000 and bar.getClose() > self.__donchian.entryValue()

    def shouldSellStock(self, bar):
        return bar.getClose() < self.__donchian.exitValue()


    def calculateEntrySize(self, bar):
        riskRate = 0.03

        totalCash = self.getBroker().getTotalCash(includeShares=True)
        closeValue = bar.getClose()
        stopLossPoint = self.calculateStopLoss(bar)

        return math.floor ( (totalCash * riskRate) / (closeValue - stopLossPoint) )

    def calculateStopLoss(self, bar):
        return self.__donchian.exitValue()

    def getMinDonchian(self):
        return self.__donchian.getMinDonchian()

    def getMaxDonchian(self):
        return self.__donchian.getMaxDonchian()