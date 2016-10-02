from pyalgotrade import logger
from pyalgotrade.barfeed import googlefeed
from pyalgotrade.tools import googlefinance
from pyalgotrade.broker import backtesting
from pytrade.base import TradingSystem
from pyalgotrade.stratanalyzer import returns
from pytrade.backtesting.analyzer.dalytradingresults import DailyTradingResults
from pyalgotrade import plotter
from matplotlib.backends.backend_pdf import PdfPages
import os.path

class GoogleFinanceBacktest(object):

    LOGGER_NAME = "GoogleFinanceBacktest"

    def __init__(self, instruments, initialCash, year, debugMode=True, csvDirs="./googlefinance", downloadFiles=True):
        self.__logger = logger.getLogger(GoogleFinanceBacktest.LOGGER_NAME)
        self.__finalPortfolioValue = 0

        # Create Feed
        self.__feed = googlefeed.Feed()
        rowFilter = lambda row: row["Close"] == "-" or row["Open"] == "-" or row["High"] == "-" or row["Low"] == "-" or \
                                row["Volume"] == "-"
        for code in instruments:
            datafile = ('%s/%s-%s.csv') % (csvDirs, code, year)
            if downloadFiles:
                self.__logger.info("Downloading file %s" % (datafile))
                if(os.path.exists(datafile)):
                    self.__logger.debug("File %s already exists, not downloading it" % (datafile))
                else:
                    googlefinance.download_daily_bars(code, year, datafile)

            self.__logger.info("Feeding file %s" % (datafile))
            self.__feed.addBarsFromCSV(code, datafile, timezone=None, rowFilter=rowFilter)

        # Create Broker
        comissionModel = backtesting.FixedPerTrade(10)
        self.__broker = backtesting.Broker(initialCash, self.__feed, commission=comissionModel)
        self.__strategy = TradingSystem(self.__feed, self.__broker, debugMode=debugMode)

        # Create Analyzers
        returnsAnalyzer = returns.Returns()
        self.__strategy.attachAnalyzer(returnsAnalyzer)
        dailyResultsAnalyzer = DailyTradingResults()
        self.__strategy.attachAnalyzer(dailyResultsAnalyzer)

        # Create plotters
        self.__plotters = []
        self.__plotters.append(
            plotter.StrategyPlotter(self.__strategy, plotAllInstruments=False, plotPortfolio=True, plotBuySell=False))
        self.__plotters[0].getOrCreateSubplot("returns").addDataSeries("Simple returns", returnsAnalyzer.getReturns())
        self.__plotters[0].getOrCreateSubplot("dailyresult").addDataSeries("Daily Results", dailyResultsAnalyzer.getTradeResults())

        for i in range(0, len(instruments), 3):
            p = plotter.StrategyPlotter(self.__strategy, plotAllInstruments=False, plotPortfolio=False)
            p.getInstrumentSubplot(instruments[i])
            self.__plotters.append(p)
            if i < len(instruments) - 1:
                p.getInstrumentSubplot(instruments[i + 1])
            if i < len(instruments) - 2:
                p.getInstrumentSubplot(instruments[i + 2])

    def getBroker(self):
        return self.__broker

    def getFeed(self):
        return self.__feed

    def attachAlgorithm(self, tradingAlgorithm):
        self.__strategy.setAlgorithm(tradingAlgorithm)

    def run(self):
        self.__strategy.run()
        self.__finalPortfolioValue = self.__strategy.getBroker().getEquity()
        self.__logger.info("Final portfolio value: $%.2f" % self.__strategy.getBroker().getEquity())

    def generatePdfReport(self, pdfFile):
        pdf = PdfPages(pdfFile)
        for p in self.__plotters:
            pdf.savefig(p.buildFigure())
        pdf.close()
