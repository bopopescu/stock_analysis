from pyalgotrade.broker.backtesting import Broker
from pyalgotrade import logger

class PytradeBroker(Broker):
    LOGGER_NAME = "pytrade.broker"

    def __init__(self, cash, barFeed, commission=None, shares={}, activeOrders={}, nextOrderId=1):
        super(PytradeBroker, self).__init__(cash, barFeed, commission)

        self.initializeShares(shares)
        self.initializeActiveOrders(activeOrders, nextOrderId)

        self.setLogger(logger.getLogger(PytradeBroker.LOGGER_NAME))

    def onBars(self, dateTime, bars):
        self.getLogger().info("before onBars: %s" % (dateTime))

        #TODO: carrega cash, shares e orders do storage
        super(PytradeBroker, self).onBars(dateTime, bars)
        #TODO: salva cash, shares e orders no storage

        self.getLogger().info("after onBars")