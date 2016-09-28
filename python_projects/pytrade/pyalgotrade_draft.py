from matplotlib.backends.backend_pdf import PdfPages

from pytrade_base import TradingSystem
from pytrade_strategy import DonchianTradingStrategy

from pyalgotrade.tools import googlefinance
from pyalgotrade.barfeed import googlefeed

import matplotlib
matplotlib.use('PDF')

feed = googlefeed.Feed()
rowFilter = lambda row: row["Close"]=="-" or row["Open"]=="-" or row["High"]=="-" or row["Low"]=="-" or row["Volume"]=="-"

#codes = ["PETR4", "BGIP4", "ITSA4"]
#codes = ["BBDC4","BDLL4","BGIP4","BOBR4","BRAP4","BRIV4","CMIG4","CRIV4","CTNM4","ELPL4","ESTR4","FJTA4","GETI4","GGBR4","GOAU4","GOLL4","GUAR4","INEP4","ITSA4","LAME4","LIXC4","MGEL4","MTSA4","MWET4","PCAR4","PETR4","POMO4","RAPT4","RCSL4","SAPR4","SHUL4","SLED4","TEKA4","TOYB4","TRPL4"]
codes = ["ABEV3", "BBAS3", "BBDC3", "BBDC4", "BBSE3", "BRAP4", "BRFS3", "BRKM5", "BRML3", "BVMF3", "CCRO3", "CIEL3", "CMIG4", "CPFE3", "CPLE6", "CSAN3", "CSNA3", "CTIP3", "CYRE3", "ECOR3", "EGIE3", "EMBR3", "ENBR3", "EQTL3", "ESTC3", "FIBR3", "GGBR4", "GOAU4", "HYPE3", "ITSA4", "ITUB4", "JBSS3", "KLBN11", "KROT3", "LAME4", "LREN3", "MRFG3", "MRVE3", "MULT3", "NATU3", "PCAR4", "PETR3", "PETR4", "QUAL3", "RADL3", "RENT3", "RUMO3", "SANB11", "SBSP3", "SMLE3", "SUZB5", "TIMP3", "UGPA3", "USIM5", "VALE3", "VALE5", "VIVT4", "WEGE3"]
#codes = ["CRIV4"]
for code in codes:
    datafile = '/tmp/'+code+'-2014.csv'
    print "Downloading %s ..." % (datafile)
    # googlefinance.download_daily_bars(code, 2014, datafile)
    feed.addBarsFromCSV(code, datafile, timezone=None, rowFilter=rowFilter)


from pyalgotrade.broker import backtesting
broker = backtesting.Broker(10000, feed)

strategies = {}
for code in codes:
    strategies[code] = DonchianTradingStrategy(code, feed, broker, 20, 10)

myStrategy = TradingSystem(feed, broker, strategies)


from pyalgotrade.stratanalyzer import returns
from pyalgotrade.stratanalyzer import sharpe
from pyalgotrade.stratanalyzer import trades
from pyalgotrade import plotter

returnsAnalyzer = returns.Returns()
sharpeAnalyzer = sharpe.SharpeRatio()
tradesAnalyzer = trades.Trades()

myStrategy.attachAnalyzer(returnsAnalyzer)

plotters = []

plotters.append(plotter.StrategyPlotter(myStrategy, plotAllInstruments=False,  plotPortfolio=True, plotBuySell=False))
plotters[0].getOrCreateSubplot("returns").addDataSeries("Simple returns", returnsAnalyzer.getReturns())
plotters[0].getOrCreateSubplot("sharpe").addDataSeries("Sharpe", sharpeAnalyzer.getReturns())
plotters[0].getOrCreateSubplot("trade").addDataSeries("Trade", tradesAnalyzer.getAllReturns())

for i in range(0, len(codes), 3):
    p = plotter.StrategyPlotter(myStrategy, plotAllInstruments=False,  plotPortfolio=False)
    p.getInstrumentSubplot(codes[i])
    plotters.append(p)
    if i < len(codes) - 1:
        p.getInstrumentSubplot(codes[i + 1])
    if i < len(codes) - 2:
        p.getInstrumentSubplot(codes[i + 2])

myStrategy.run()
print "Final portfolio value: $%.2f" % myStrategy.getBroker().getEquity()


pdf = PdfPages('/tmp/stock_analyze.pdf')
for p in plotters:
    pdf.savefig(p.buildFigure())
pdf.close()