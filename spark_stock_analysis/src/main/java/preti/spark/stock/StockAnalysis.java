package preti.spark.stock;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;

import preti.spark.stock.reporting.FileReport;
import preti.spark.stock.run.DonchianStrategyOptimizer;
import preti.spark.stock.run.StocksRepository;
import preti.spark.stock.system.TradeSystemExecution;
import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.coremodel.Stock;
import preti.stock.system.TradingStrategy;
import preti.stock.system.TradingStrategyImpl;

public class StockAnalysis {
    private static final Log log = LogFactory.getLog(StockAnalysis.class);

    private static final String RESULTS_DIR = "/opt/spark_stock_analysis_app/resultados";

    public static void main2(String[] args) throws IOException, ParseException {
        SparkConf conf = new SparkConf();
        JavaSparkContext sc = new JavaSparkContext(conf);

        if (args.length != 1) {
            System.out.println("Must specify config file");
            System.exit(-1);
        }

        ConfigContext configContext = new ObjectMapper().readValue(new File(args[0]), ConfigContext.class);

        executeAnalysis(configContext, sc);

    }

    private static void executeAnalysis(ConfigContext configContext, JavaSparkContext sc) {

        log.info("Loading stock data ...");
        StocksRepository stocksRepository = new StocksRepository(sc);
        List<Stock> stocks = stocksRepository.loadStocks(configContext.getStockHistoryFile(),
                configContext.getStockCodesToAnalyze());

        final double accountInitialPosition = configContext.getAccountInitialValue();
        final int trainingSize = configContext.getTrainingSizeInMonths();
        final int windowSize = configContext.getWindowSizeInMonths();

        Date initialDate = configContext.getInitialDate();
        Date finalDate = configContext.getFinalDate();
        TradeSystemExecution system = new TradeSystemExecution(stocks, accountInitialPosition, accountInitialPosition,
                null);

        DateTime currentInitialDate = new DateTime(initialDate.getTime()).plusMonths(trainingSize);
        DateTime currentFinalDate = currentInitialDate.plusMonths(windowSize);
        DonchianStrategyOptimizer optimizer = new DonchianStrategyOptimizer(sc);
        Map<Long, TradingStrategy> optimzedStrategies = new HashMap<>();
        while (currentFinalDate.isBefore(finalDate.getTime() + 1)) {
            Map<Long, TradingStrategy> newStrategies = new HashMap<>();
            for (Stock s : stocks) {
                DonchianModel optimizationResult = optimizer.optimizeParameters(s, accountInitialPosition,
                        currentInitialDate.minusMonths(trainingSize).toDate(), currentInitialDate.minusDays(1).toDate(),
                        configContext.getMinDochianEntryValue(), configContext.getMaxDonchianEntryValue(),
                        configContext.getMinDonchianExitValue(), configContext.getMaxDonchianExitValue(),
                        configContext.getRiskRate());
                if (optimizationResult != null) {
                    newStrategies.put(s.getId(),
                            new TradingStrategyImpl(s, 0, optimizationResult.getEntryDonchianSize(),
                                    optimizationResult.getExitDonchianSize(), optimizationResult.getRiskRate()));
                }
            }
            log.info("Analyzing from " + currentInitialDate + " to " + currentFinalDate + " with training data from "
                    + currentInitialDate.minusMonths(trainingSize) + " to " + currentInitialDate.minusDays(1));
            optimzedStrategies = mergeStrategies(optimzedStrategies, newStrategies);

            system.setTradingStrategies(optimzedStrategies);
            system.analyzeStocks(currentInitialDate.toDate(), currentFinalDate.toDate());
            log.info("Analyze finished.");

            currentInitialDate = currentInitialDate.plusMonths(windowSize);
            currentFinalDate = currentFinalDate.plusMonths(windowSize);
        }

        System.out.println("Final balance before closing open trades: " + system.getAccountBalance());
        system.closeAllOpenTrades(finalDate);
        System.out.println("Final balance after closing open trades: " + system.getAccountBalance());

        log.info("Generating reports ...");
        // new StockReport(system, "localhost", 5001, "stock").generate();
        // new OperationsReport(system, "localhost", 5002,
        // "operations").generate();
        // new BalanceReport(system, "localhost", 5003, "balance").generate();
        new FileReport(system, RESULTS_DIR, configContext).generate();
        log.info("Reports generated");
    }

    private static Map<Long, TradingStrategy> mergeStrategies(Map<Long, TradingStrategy> oldStrategies,
            Map<Long, TradingStrategy> newStrategies) {
        Map<Long, TradingStrategy> mergedStrategies = new HashMap<>(newStrategies);

        for (Long stockId : oldStrategies.keySet()) {
            if (!newStrategies.containsKey(stockId)) {
                TradingStrategyImpl oldStrategy = (TradingStrategyImpl) oldStrategies.get(stockId);
                mergedStrategies.put(stockId, new TradingStrategyImpl(oldStrategy.getStock(), oldStrategy.getModelId(),
                        0, oldStrategy.getExitDonchianSize(), oldStrategy.getRiskRate()));
            }
        }

        return mergedStrategies;
    }

    // public static void main(String[] args) throws IOException {
    // File f = new File("/tmp", "teste");
    // System.out.println(f.exists());
    // f.createNewFile();
    // System.out.println(f.exists());
    // }

    public static void main(String[] args) throws ParseException, IOException {
        SparkConf conf = new SparkConf();
        JavaSparkContext sc = new JavaSparkContext(conf);

        int[] trainingSizes = new int[24];
        for (int i = 1; i <= 24; i++) {
            trainingSizes[i - 1] = i;
        }

        int[] windowSizes = new int[24];
        for (int i = 1; i <= 24; i++) {
            windowSizes[i - 1] = i;
        }

        double[] riskRates = new double[10];
        for (int i = 1; i <= 10; i++) {
            riskRates[i - 1] = ((double) i) / 100d;
        }

        String[] stockCodesToAnalyze = { "BBDC4", "BDLL4", "BGIP4", "BOBR4", "BRAP4", "BRIV4", "CMIG4", "CRIV4",
                "CTNM4", "ELPL4", "ESTR4", "FJTA4", "GETI4", "GGBR4", "GOAU4", "GOLL4", "GUAR4", "INEP4", "ITSA4",
                "LAME4", "LIXC4", "MGEL4", "MTSA4", "MWET4", "PCAR4", "PETR4", "POMO4", "RAPT4", "RCSL4", "SAPR4",
                "SHUL4", "SLED4", "TEKA4", "TOYB4", "TRPL4" };

        for (int trainingSize : trainingSizes) {
            for (int windowSize : windowSizes) {
                for (double riskRate : riskRates) {
                    String initialDate;
                    String finalDate;

                    initialDate = "2006-01-01";
                    finalDate = "2007-01-01";
                    executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                            sc);

                    initialDate = "2007-01-01";
                    finalDate = "2008-01-01";
                    executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                            sc);

                    initialDate = "2008-01-01";
                    finalDate = "2009-01-01";
                    executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                            sc);

                    initialDate = "2009-01-01";
                    finalDate = "2010-01-01";
                    executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                            sc);

                    initialDate = "2010-01-01";
                    finalDate = "2011-01-01";
                    executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                            sc);

                    initialDate = "2011-01-01";
                    finalDate = "2012-01-01";
                    executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                            sc);

                    initialDate = "2012-01-01";
                    finalDate = "2013-01-01";
                    executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                            sc);

                    initialDate = "2013-01-01";
                    finalDate = "2014-01-01";
                    executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                            sc);

                    initialDate = "2014-01-01";
                    finalDate = "2015-01-01";
                    executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                            sc);

                    // initialDate = "2006-01-01";
                    // finalDate = "2015-01-01";
                    // executeAnalysis(trainingSize, windowSize, riskRate, initialDate, finalDate, stockCodesToAnalyze,
                    // sc);
                }
            }
        }

    }

    private static void executeAnalysis(int trainingSize, int windowSize, double riskRate, String initialDate,
            String finalDate, String[] stockCodesToAnalyze, JavaSparkContext sc) throws ParseException, IOException {
        File statusFile = new File(RESULTS_DIR,
                String.format("%s_%s_%s_%s_%s", trainingSize, windowSize, riskRate, initialDate, finalDate));
        if (statusFile.exists()) {
            log.info("############################################################################");
            log.info(String.format(
                    "Results for traingSize=%s windowSize=%s riskRate=%s initialDate=%s finalDate=%s already exists.",
                    trainingSize, windowSize, riskRate, initialDate, finalDate));
            log.info("############################################################################");
            return;
        }

        log.info(String.format(
                "Executing analysis trainingSize=%s windowSize=%s riskRate=%s initialDate=%s finalDate=%s",
                trainingSize, windowSize, riskRate, initialDate, finalDate));

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ConfigContext configContext = new ConfigContext();
        configContext.setStockHistoryFile("/tmp/cotacoes.txt");
        configContext.setStockCodesToAnalyze(Arrays.asList(stockCodesToAnalyze));
        configContext.setMinDochianEntryValue(2);
        configContext.setMaxDonchianEntryValue(50);
        configContext.setMinDonchianExitValue(2);
        configContext.setMaxDonchianExitValue(50);

        Date parsedInitialDate = dateFormat.parse(initialDate);
        configContext.setInitialDate(
                dateFormat.format(new DateTime(parsedInitialDate.getTime()).minusMonths(trainingSize).toDate()));
        configContext.setFinalDate(finalDate);
        configContext.setRiskRate(riskRate);
        configContext.setTrainingSizeInMonths(trainingSize);
        configContext.setWindowSizeInMonths(windowSize);
        configContext.setAccountInitialValue(10000);

        executeAnalysis(configContext, sc);

        log.info("############################################################################");
        log.info(String.format("Creating file %s", statusFile.getAbsolutePath()));
        statusFile.createNewFile();

        statusFile = new File(RESULTS_DIR,
                String.format("%s_%s_%s_%s_%s", trainingSize, windowSize, riskRate, initialDate, finalDate));
        log.info(String.format("File created: %s", statusFile.exists()));
        log.info("############################################################################");

    }
}
