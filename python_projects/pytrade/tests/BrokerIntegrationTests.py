import unittest
from pytrade.feed import DynamicFeed
from datetime import timedelta
from pytrade.broker import PytradeBroker
from pytrade.base import TradingSystem
from pytrade.algorithms.donchianchannels import DonchianTradingAlgorithm
from pyalgotrade.tools import googlefinance
from pytrade.backtesting.backtest import GoogleFinanceBacktest

class BrokerIntegrationTests(unittest.TestCase):
    db = "./sqliteddb"
    codes = ["ABEV3", "BBAS3", "BBDC3", "BBDC4", "BBSE3", "BRAP4", "BRFS3", "BRKM5", "BRML3", "BVMF3", "CCRO3", "CIEL3",
             "CMIG4", "CPFE3", "CPLE6", "CSAN3", "CSNA3", "CTIP3", "CYRE3", "ECOR3", "EGIE3", "EMBR3", "ENBR3", "EQTL3",
             "ESTC3", "FIBR3", "GGBR4", "GOAU4", "HYPE3", "ITSA4", "ITUB4", "JBSS3", "KLBN11", "KROT3", "LAME4",
             "LREN3", "MRFG3", "MRVE3", "MULT3", "NATU3", "PCAR4", "PETR3", "PETR4", "QUAL3", "RADL3", "RENT3", "RUMO3",
             "SANB11", "SBSP3", "SMLE3", "SUZB5", "TIMP3", "UGPA3", "USIM5", "VALE3", "VALE5", "VIVT4", "WEGE3"]
    csvStorage="./googlefinance"

    @classmethod
    def setUpClass(cls):
        feed = DynamicFeed(cls.db, cls.codes)
        days = feed.getAllDays()
        if len(days)==247:
            return

        rowFilter = lambda row: row["Close"] == "-" or row["Open"] == "-" or row["High"] == "-" or row["Low"] == "-" or \
                                row["Volume"] == "-"
        googleFeed = googlefinance.build_feed(cls.codes, 2014, 2014, storage=cls.csvStorage, skipErrors=True,
                                               rowFilter=rowFilter)
        feed = DynamicFeed(cls.db, cls.codes, maxLen=10)
        feed.getDatabase().addBarsFromFeed(googleFeed)

    def testLiveBrokerDonchianAlgorithm2014(self):
        cash = 10000
        donchianEntry = 9
        donchianExit = 26
        riskFactor = 0.05
        maxLen = int(donchianExit * 1.4)
        feed = DynamicFeed(self.db, self.codes, maxLen=maxLen)

        days = feed.getAllDays()
        shares = {}
        activeOrders = {}

        for day in days:
            fromDate = day - timedelta(days=maxLen)
            toDate = day + timedelta(days=5)
            feed = DynamicFeed(self.db, self.codes, fromDateTime=fromDate, toDateTime=toDate, maxLen=maxLen)
            feed.positionFeed(day)

            broker = PytradeBroker(cash, feed, shares, activeOrders)
            strategy = TradingSystem(feed, broker, debugMode=False)
            strategy.setAlgorithm(DonchianTradingAlgorithm(feed, broker, donchianEntry, donchianExit, riskFactor))

            feed.dispatchWithoutIncrementingDate()
            feed.nextEvent()

            for order in broker.getMarketOrdersToConfirm() + broker.getStopOrdersToConfirm():
                bar = broker.getCurrentBarForInstrument(order.getInstrument())
                if bar is None:
                    continue

                if not broker.confirmOrder(order, bar):
                    broker.cancelOrder(order)

            cash = broker.getAvailableCash()
            shares = broker.getAllShares()
            activeOrders = broker.getAllActiveOrders()

        self.assertEqual(broker.getEquity(), 36922.16)

    def testBacktestingDonchianAlgorithm2014(self):
        cash = 10000
        donchianEntry = 9
        donchianExit = 26
        riskFactor = 0.05

        backtest = GoogleFinanceBacktest(instruments=self.codes, initialCash=cash, year=2014, debugMode=False,
                                         csvStorage=self.csvStorage)
        backtest.attachAlgorithm(DonchianTradingAlgorithm(backtest.getFeed(), backtest.getBroker(), donchianEntry, donchianExit, riskFactor))
        backtest.run()

        self.assertEqual(backtest.getBroker().getEquity(), 36922.16)