package preti.spark.stock.reporting;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;

import preti.spark.stock.system.ConfigContext;
import preti.spark.stock.system.TradeSystem;

public class FileReport implements Report {

	private TradeSystem system;
	private ConfigContext configContext;
	private String outputDir;

	@SuppressWarnings("serial")
	public class FileReportEvent implements Serializable {
		private double initialBalance, finalBalance;
		private int minDochianEntryValue, maxDonchianEntryValue, minDonchianExitValue, maxDonchianExitValue;
		private String initialDate, finalDate;
		private double riskRate;
		private int trainingSizeInMonths, windowSizeInMonths;

		public FileReportEvent(double initialBalance, double finalBalance, int minDochianEntryValue,
				int maxDonchianEntryValue, int minDonchianExitValue, int maxDonchianExitValue, String initialDate,
				String finalDate, double riskRate, int trainingSizeInMonths, int windowSizeInMonths) {
			super();
			this.initialBalance = initialBalance;
			this.finalBalance = finalBalance;
			this.minDochianEntryValue = minDochianEntryValue;
			this.maxDonchianEntryValue = maxDonchianEntryValue;
			this.minDonchianExitValue = minDonchianExitValue;
			this.maxDonchianExitValue = maxDonchianExitValue;
			this.initialDate = initialDate;
			this.finalDate = finalDate;
			this.riskRate = riskRate;
			this.trainingSizeInMonths = trainingSizeInMonths;
			this.windowSizeInMonths = windowSizeInMonths;
		}

		public double getInitialBalance() {
			return initialBalance;
		}

		public double getFinalBalance() {
			return finalBalance;
		}

		public int getMinDochianEntryValue() {
			return minDochianEntryValue;
		}

		public int getMaxDonchianEntryValue() {
			return maxDonchianEntryValue;
		}

		public int getMinDonchianExitValue() {
			return minDonchianExitValue;
		}

		public int getMaxDonchianExitValue() {
			return maxDonchianExitValue;
		}

		public String getInitialDate() {
			return initialDate;
		}

		public String getFinalDate() {
			return finalDate;
		}

		public double getRiskRate() {
			return riskRate;
		}

		public int getTrainingSizeInMonths() {
			return trainingSizeInMonths;
		}

		public int getWindowSizeInMonths() {
			return windowSizeInMonths;
		}

	}

	public FileReport(TradeSystem system, String outputDir, ConfigContext configContext) {
		super();
		this.system = system;
		this.outputDir = outputDir;
		this.configContext = configContext;
	}

	@Override
	public void generate() {
		String fileName = String.format("%s_stock_analysis_result.json",
				new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
		File outputFile = new File(outputDir, fileName);

		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(outputFile,
					new FileReportEvent(system.getAccountInitialPosition(), system.getAccountBalance(),
							configContext.getMinDochianEntryValue(), configContext.getMaxDonchianEntryValue(),
							configContext.getMinDonchianExitValue(), configContext.getMaxDonchianExitValue(),
							configContext.getFormatedInitialDate(), configContext.getFormatedFinalDate(),
							configContext.getRiskRate(), configContext.getTrainingSizeInMonths(),
							configContext.getWindowSizeInMonths()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
