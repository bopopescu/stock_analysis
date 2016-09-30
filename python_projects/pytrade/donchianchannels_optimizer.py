import itertools

from pyalgotrade.barfeed import googlefeed
from pyalgotrade.broker import backtesting
from pyalgotrade.optimizer import local

from pytrade.algorithms.donchianchannels import DonchianTradingAlgorithm
from pytrade.base import TradingSystem

codes = ["ABEV3", "BBAS3", "BBDC3", "BBDC4", "BBSE3", "BRAP4", "BRFS3", "BRKM5", "BRML3", "BVMF3", "CCRO3", "CIEL3", "CMIG4", "CPFE3", "CPLE6", "CSAN3", "CSNA3", "CTIP3", "CYRE3", "ECOR3", "EGIE3", "EMBR3", "ENBR3", "EQTL3", "ESTC3", "FIBR3", "GGBR4", "GOAU4", "HYPE3", "ITSA4", "ITUB4", "JBSS3", "KLBN11", "KROT3", "LAME4", "LREN3", "MRFG3", "MRVE3", "MULT3", "NATU3", "PCAR4", "PETR3", "PETR4", "QUAL3", "RADL3", "RENT3", "RUMO3", "SANB11", "SBSP3", "SMLE3", "SUZB5", "TIMP3", "UGPA3", "USIM5", "VALE3", "VALE5", "VIVT4", "WEGE3"]
feed = googlefeed.Feed()
rowFilter = lambda row: row["Close"] == "-" or row["Open"] == "-" or row["High"] == "-" or row["Low"] == "-" or row["Volume"] == "-"
for code in codes:
    datafile = './googlefinance/' + code + '-2014.csv'
    print "Downloading %s ..." % (datafile)
    # googlefinance.download_daily_bars(code, 2014, datafile)
    feed.addBarsFromCSV(code, datafile, timezone=None, rowFilter=rowFilter)

class DonchianStrategyOptimizer(TradingSystem):
    def __init__(self, feed, entrySize, exitSize):
        broker = backtesting.Broker(10000, feed)
        super(DonchianStrategyOptimizer, self).__init__(feed, broker, DonchianTradingAlgorithm(feed, broker, entrySize, exitSize), debugMode=False)

entrySize = range(10, 20)
exitSize = range(2, 10)

local.run(DonchianStrategyOptimizer, feed, itertools.product(entrySize, exitSize), workerCount=6)
