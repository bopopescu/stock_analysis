import matplotlib
from datetime import timedelta
from pytrade.algorithms.donchianchannels import DonchianTradingAlgorithm
from pytrade.backtesting.backtest import GoogleFinanceBacktest
from pytrade.feed import DynamicFeed
from pytrade.broker import PytradeBroker
from pytrade.base import TradingSystem


matplotlib.use('PDF')

#codes = ["BBDC4","BDLL4","BGIP4","BOBR4","BRAP4","BRIV4","CMIG4","CRIV4","CTNM4","ELPL4","ESTR4","FJTA4","GETI4","GGBR4","GOAU4","GOLL4","GUAR4","INEP4","ITSA4","LAME4","LIXC4","MGEL4","MTSA4","MWET4","PCAR4","PETR4","POMO4","RAPT4","RCSL4","SAPR4","SHUL4","SLED4","TEKA4","TOYB4","TRPL4"]
codes = ["ABEV3", "BBAS3", "BBDC3", "BBDC4", "BBSE3", "BRAP4", "BRFS3", "BRKM5", "BRML3", "BVMF3", "CCRO3", "CIEL3", "CMIG4", "CPFE3", "CPLE6", "CSAN3", "CSNA3", "CTIP3", "CYRE3", "ECOR3", "EGIE3", "EMBR3", "ENBR3", "EQTL3", "ESTC3", "FIBR3", "GGBR4", "GOAU4", "HYPE3", "ITSA4", "ITUB4", "JBSS3", "KLBN11", "KROT3", "LAME4", "LREN3", "MRFG3", "MRVE3", "MULT3", "NATU3", "PCAR4", "PETR3", "PETR4", "QUAL3", "RADL3", "RENT3", "RUMO3", "SANB11", "SBSP3", "SMLE3", "SUZB5", "TIMP3", "UGPA3", "USIM5", "VALE3", "VALE5", "VIVT4", "WEGE3"]


# backtest = GoogleFinanceBacktest(instruments=codes, initialCash=10000, year=2014, debugMode=False, csvStorage="./googlefinance")
# backtest.attachAlgorithm(DonchianTradingAlgorithm(backtest.getFeed(), backtest.getBroker(), 9, 26, 0.05))
# backtest.run()
# backtest.generatePdfReport('/tmp/stock_analysis.pdf')

############################################################################################################################

# rowFilter = lambda row: row["Close"] == "-" or row["Open"] == "-" or row["High"] == "-" or row["Low"] == "-" or \
#                         row["Volume"] == "-"
# instruments = ["PETR4", "PETR3"]
# googleFeed = googlefinance.build_feed(codes, 2014, 2014, storage="./googlefinance", skipErrors=True,
#                                       rowFilter=rowFilter)
db = "./sqliteddb"
# feed = DynamicFeed    (db, codes, maxLen=10)
# feed.getDatabase().addBarsFromFeed(googleFeed)
################################################################################################

maxLen=int(26*1.4)
feed = DynamicFeed(db, codes, maxLen=maxLen)

#$36922.16
days =  feed.getAllDays()
cash = 10000
shares = {}
activeOrders = {}
nextOrderId = 1

for day in days:
    fromDate = day - timedelta(days=maxLen)
    toDate = day + timedelta(days=5)
    feed = DynamicFeed(db, codes, fromDateTime=fromDate, toDateTime=toDate, maxLen=maxLen)
    feed.positionFeed(day)

    broker = PytradeBroker(cash, feed, shares, activeOrders, nextOrderId)
    strategy = TradingSystem(feed, broker, debugMode=False)
    strategy.setAlgorithm(DonchianTradingAlgorithm(feed, broker, 9, 26, 0.05))

    # feed.getNewValuesEvent().unsubscribe(broker.onBars)
    feed.dispatchWithoutIncrementingDate()

    feed.nextEvent()
    for order in broker.getMarketOrdersToConfirm()+broker.getStopOrdersToConfirm():
        bar = broker.getCurrentBarForInstrument(order.getInstrument())
        if bar is None:
            continue

        if not broker.confirmOrder(order, bar):
            broker.cancelOrder(order)


    cash = broker.getAvailableCash()
    shares = broker.getAllShares()
    activeOrders = broker.getAllActiveOrders()
    nextOrderId = broker.getNextOrderIdWithoutIncrementing()

broker.getEquity()