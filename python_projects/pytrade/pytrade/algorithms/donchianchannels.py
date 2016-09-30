import math

from pyalgotrade import dataseries
from pyalgotrade import technical

from pytrade.base import TradingAlgorithm


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

class DonchianTradingAlgorithm(TradingAlgorithm):
    def __init__(self, feed, broker, donchianEntrySize, donchianExitSize):
        super(DonchianTradingAlgorithm, self).__init__(feed, broker)

        self.__donchians = {}
        for instrument in feed.getRegisteredInstruments():
            self.__donchians[instrument] = DonchianChannel(feed[instrument].getHighDataSeries(), feed[instrument].getLowDataSeries(), donchianEntrySize, donchianExitSize, maxLen=60)

    def shouldAnalyze(self, bar, instrument):
        return self.__donchians[instrument].isReady()

    def shouldBuyStock(self, bar, instrument):
        return bar.getVolume()>10000000 and bar.getClose() > self.__donchians[instrument].entryValue()

    def shouldSellStock(self, bar, instrument):
        return bar.getClose() < self.__donchians[instrument].exitValue()


    def calculateEntrySize(self, bar, instrument):
        riskRate = 0.03

        totalCash = self.getBroker().getTotalCash(includeShares=True)
        closeValue = bar.getClose()
        stopLossPoint = self.calculateStopLoss(bar, instrument)

        return math.floor ( (totalCash * riskRate) / (closeValue - stopLossPoint) )

    def calculateStopLoss(self, bar, instrument):
        return self.__donchians[instrument].exitValue()

    def getMinDonchian(self, instrument):
        return self.__donchians[instrument].getMinDonchian()

    def getMaxDonchian(self, instrument):
        return self.__donchians[instrument].getMaxDonchian()