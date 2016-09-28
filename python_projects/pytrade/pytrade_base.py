# -*- coding: utf-8 -*-
from pyalgotrade import strategy
from pyalgotrade.broker import Order
import abc

class TradingSystem(strategy.BacktestingStrategy):
    def __init__(self, feed, broker, tradingStrategies):
        super(TradingSystem, self).__init__(feed, broker)
        self.__tradingStrategies = tradingStrategies

    def onOrderUpdated(self, order):
        self.info(order)

        assert order.getType() == Order.Type.MARKET or order.getType() == Order.Type.STOP

        if order.getType() == Order.Type.STOP:
            return

        assert order.getAction() == Order.Action.BUY or order.getAction() == Order.Action.SELL

        instrument = order.getInstrument()
        if order.getAction() == Order.Action.BUY and (order.getState() == Order.State.FILLED or order.getState() == Order.State.PARTIALLY_FILLED):
            shares = self.getBroker().getShares(instrument)
            self.stopOrder(instrument=instrument, stopPrice=order.stopLossValue, quantity=(-1*shares), goodTillCanceled=True)
        elif order.getAction() == Order.Action.SELL and order.getState() == Order.State.FILLED:
            stopOrder = self.getBroker().getActiveOrders(instrument=instrument)[0]
            assert stopOrder.getType() == Order.Type.STOP
            self.getBroker().cancelOrder(stopOrder)

    def isOpenPosition(self, instrument):
        return instrument in self.getBroker().getActiveInstruments()

    def enterPosition(self, instrument, quantity, stopLossValue):
        assert quantity > 0;
        assert  not self.isOpenPosition(instrument)

        order = self.marketOrder(instrument=instrument, quantity=quantity, goodTillCanceled=False, allOrNone=False)
        order.stopLossValue = stopLossValue #o ideal seria um ter um novo tipo de ordem pra preencher esse valor.
        # self.stopOrder(instrument=instrument, stopPrice=stopLossValue, quantity=(-1*quantity), goodTillCanceled=True, allOrNone=True)

    def exitPosition(self, instrument):
        assert instrument in self.getBroker().getActiveInstruments()
        qty = self.getBroker().getShares(instrument)
        assert qty>0

        #stopOrder = self.getBroker().getActiveOrders(instrument=instrument)[0]
        #assert stopOrder.getType() == Order.Type.STOP
        #self.getBroker().cancelOrder(stopOrder)

        self.marketOrder(instrument=instrument, quantity=(-1*qty))

    def onBars(self, bars):
        for instrument in bars.getInstruments():
            strategy = self.__tradingStrategies[instrument]
            if(not (strategy and strategy.shouldAnalyze(bars.getDateTime()) )):
                self.info("Skipping stock %s at date %s" % (len(bars.getInstruments()), bars.getDateTime()))
                continue

            bar = bars.getBar(instrument)
            if self.isOpenPosition(instrument): #open position
                if strategy.shouldSellStock(bar):
                    self.exitPosition(instrument)
            elif strategy.shouldBuyStock(bar):
                size = strategy.calculateEntrySize(bar)
                stopLoss = strategy.calculateStopLoss(bar)
                self.enterPosition(instrument, size, stopLoss)


class BaseTradingStrategy(object):
    __metaclass__ = abc.ABCMeta

    def __init__(self, instrument, feed, broker):
        self.__instrument = instrument
        self.__feed = feed
        self.__broker = broker

    def getBroker(self):
        return self.__broker

    @abc.abstractmethod
    def shouldAnalyze(self, bar):
        """
        :param dateTime: datetime of the event.
        :type dateTime: dateTime.datetime.
        """
        raise NotImplementedError()

    @abc.abstractmethod
    def shouldSellStock(self, bar):
        raise NotImplementedError()

    @abc.abstractmethod
    def shouldBuyStock(self, bar):
        raise NotImplementedError()

    @abc.abstractmethod
    def calculateEntrySize(self, bar):
        raise  NotImplementedError()

    @abc.abstractmethod
    def calculateStopLoss(self, bar):
        raise  NotImplementedError()