package preti.spark.stock.reporting;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import preti.spark.stock.system.StockContext;
import preti.spark.stock.system.TradeSystem;

public class BalanceReport extends AbstractReport {
	@SuppressWarnings("serial")
	public class BalanceEvent implements Serializable {

		private String indexName;
		private Date date;
		private Double accountBalance;
		private Double openPositionsValue;

		public BalanceEvent(Date d, Double accountBalance, Double openPositionsValue, String indexName) {
			this.date = d;
			this.accountBalance = accountBalance;
			this.openPositionsValue = openPositionsValue;
			this.indexName = indexName;
		}

		public String getIndexName() {
			return indexName;
		}

		public String getDate() {
			return new SimpleDateFormat("yyyyMMdd").format(date);
		}

		public Double getAccountBalance() {
			return accountBalance;
		}

		public Double getOpenPositionsValue() {
			return openPositionsValue;
		}

		public long getUnixTimestamp() {
			return date.getTime() / 1000l;
		}
	}

	public BalanceReport(TradeSystem system, String outputIp, int outputPort, String indexName) {
		super(system, outputIp, outputPort, indexName);
	}

	public void executeReport() throws IOException {
		log.info("Generating balance report ");
		Socket socket = new Socket(outputIp, outputPort);
		OutputStream stream = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(stream, true);

		ObjectMapper mapper = new ObjectMapper();

		Map<Date, Double> balanceHistory = system.getBalanceHistory();
		Collection<StockContext> wallet = system.getWallet();

		for (Date d : balanceHistory.keySet()) {
			double openPositionsValue = 0;
			for (StockContext st : wallet) {
				openPositionsValue += st.getOpenPositionsValueAtDate(d);
			}

			writer.println(mapper.writeValueAsString(
					new BalanceEvent(d, balanceHistory.get(d), openPositionsValue, this.indexName)));
		}
		writer.close();
		socket.close();
	}

}
