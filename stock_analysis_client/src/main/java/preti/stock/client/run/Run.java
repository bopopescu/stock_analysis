package preti.stock.client.run;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import preti.stock.analysismodel.donchian.Account;
import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.coremodel.Trade;

public class Run {

	public static void main2(String[] args) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper jsonMapper = new ObjectMapper();
		Account account = jsonMapper.readValue(new File("/tmp/input.json"), Account.class);
		System.out.println(jsonMapper.writeValueAsString(account));

		RestTemplate restTemplate = new RestTemplate();

		Map<String, String> parameters = new HashMap<>();

		parameters.put("date", "2014-03-12");
		account = restTemplate.postForObject("http://localhost:8080/recomendations/generate?date={date}", account,
				Account.class, parameters);

		parameters.put("date", "2014-03-13");
		account = restTemplate.postForObject("http://localhost:8080/recomendations/generate?date={date}", account,
				Account.class, parameters);

		parameters.put("date", "2014-03-14");
		account = restTemplate.postForObject("http://localhost:8080/recomendations/generate?date={date}", account,
				Account.class, parameters);

		System.out.println(account);

	}

	public static void main3(String[] args)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		ObjectMapper jsonMapper = new ObjectMapper();
		RestTemplate restTemplate = new RestTemplate();

		DonchianModel[] model = jsonMapper.readValue(new File("/tmp/model_janeiro.json"), DonchianModel[].class);

		String[] stockCodesToAnalyze = { "BBDC4", "BDLL4", "BGIP4", "BOBR4", "BRAP4", "BRIV4", "CMIG4", "CRIV4",
				"CTNM4", "ELPL4", "ESTR4", "FJTA4", "GETI4", "GGBR4", "GOAU4", "GOLL4", "GUAR4", "INEP4", "ITSA4",
				"LAME4", "LIXC4", "MGEL4", "MTSA4", "MWET4", "PCAR4", "PETR4", "POMO4", "RAPT4", "RCSL4", "SAPR4",
				"SHUL4", "SLED4", "TEKA4", "TOYB4", "TRPL4" };

		Account account = new Account();
		account.setBalance(10000);
		account.setInitialPosition(10000);
		account.setModel(Arrays.asList(model));
		account.setStockCodesToAnalyze(Arrays.asList(stockCodesToAnalyze));
		account.setWallet(new ArrayList<>());

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date initialDate = dateFormat.parse("2014-02-01");
		Date finalDate = dateFormat.parse("2014-10-01");

		Date secondModelDate = dateFormat.parse("2014-06-01");
		boolean secondModelRead = false;
		Date thirdModelDate = dateFormat.parse("2014-10-01");
		boolean thirdModelRead = false;

		Map<String, String> parameters = new HashMap<>();
		DateTime currentDate = new DateTime(initialDate.getTime());
		while (currentDate.isBefore(finalDate.getTime())) {
			System.out.println("Analysing " + dateFormat.format(currentDate.toDate()));
			if (currentDate.isEqual(secondModelDate.getTime()) && !secondModelRead) {
				System.out.println("Reading model /tmp/model_maio.json");
				DonchianModel[] newModel = jsonMapper.readValue(new File("/tmp/model_maio.json"),
						DonchianModel[].class);
				DonchianModel[] oldModel = account.getModel().toArray(new DonchianModel[] {});
				account.setModel(Arrays.asList(mergeModels(newModel, oldModel)));
				secondModelRead = true;
			}

			if (currentDate.isEqual(thirdModelDate.getTime()) && !thirdModelRead) {
				System.out.println("Reading model /tmp/model_setembro.json");
				DonchianModel[] newModel = jsonMapper.readValue(new File("/tmp/model_setembro.json"),
						DonchianModel[].class);
				DonchianModel[] oldModel = account.getModel().toArray(new DonchianModel[] {});
				account.setModel(Arrays.asList(mergeModels(newModel, oldModel)));
				thirdModelRead = true;
			}

			parameters.put("date", dateFormat.format(currentDate.toDate()));
			account = restTemplate.postForObject("http://localhost:8080/recomendations/generate?date={date}", account,
					Account.class, parameters);

			currentDate = currentDate.plusDays(1);
		}

		System.out.println("Final Balance: " + account.getBalance());
		parameters.put("date", "2015-01-01");
		account = restTemplate.postForObject("http://localhost:8080/account/closeAllOpenTrades?date={date}", account,
				Account.class, parameters);
		System.out.println(jsonMapper.writeValueAsString(account));
		System.out.println("Final Balance: " + account.getBalance());
	}

	public static void main(String[] args) throws ParseException {
		ObjectMapper jsonMapper = new ObjectMapper();
		RestTemplate restTemplate = new RestTemplate();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date initialDate = dateFormat.parse("2014-02-01");
		Date finalDate = dateFormat.parse("2014-10-01");

		Map<String, String> parameters = new HashMap<>();
		DateTime currentDate = new DateTime(initialDate.getTime());
		while (currentDate.isBefore(finalDate.getTime())) {
			System.out.println("Analysing " + dateFormat.format(currentDate.toDate()));

			parameters.put("date", dateFormat.format(currentDate.toDate()));
			Trade[] trades = restTemplate.getForObject("http://localhost:8080/recomendations/generate?date={date}",
					Trade[].class, parameters);

			System.out.println("Trades: " + trades.length);
			restTemplate.postForLocation("http://localhost:8080/trades/realize", trades);

			currentDate = currentDate.plusDays(1);
		}
		
//		FALTA CHAMAR CLOSE ALL OPEN TRADES;
	}

	private static DonchianModel[] mergeModels(DonchianModel[] newModel, DonchianModel[] oldModel) {
		Map<String, DonchianModel> mapModels = new HashMap<>();
		for (DonchianModel m : newModel) {
			mapModels.put(m.getStock(), m);
		}

		for (DonchianModel m : oldModel) {
			if (!mapModels.containsKey(m.getStock())) {
				mapModels.put(m.getStock(),
						new DonchianModel(m.getStock(), 0,  0, m.getExitDonchianSize(), m.getRiskRate()));
			}
		}

		DonchianModel[] mergedModels = new DonchianModel[mapModels.size()];
		int i = 0;
		for (DonchianModel m : mapModels.values()) {
			mergedModels[i] = m;
			i++;
		}
		return mergedModels;
	}

}
