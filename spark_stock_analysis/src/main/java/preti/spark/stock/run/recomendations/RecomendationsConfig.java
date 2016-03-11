package preti.spark.stock.run.recomendations;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import preti.stock.analysismodel.donchian.Account;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown = true)
public class RecomendationsConfig implements Serializable {
	private final String DATE_FORMAT = "yyyy-MM-dd";

	private String stockHistoryFile;
	private Account account;
	private String recomendationDate;
	private String outputFile;

	public String getStockHistoryFile() {
		return stockHistoryFile;
	}

	public void setStockHistoryFile(String stockHistoryFile) {
		this.stockHistoryFile = stockHistoryFile;
	}

	public String getRecomendationDate() {
		return recomendationDate;
	}

	public Date getParsedRecomendationDate() {
		try {
			return new SimpleDateFormat(DATE_FORMAT).parse(recomendationDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public void setRecomendationDate(String recomendationDate) {
		this.recomendationDate = recomendationDate;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
