package preti.stock.web;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import preti.stock.analysismodel.donchian.Account;
import preti.stock.coremodel.Stock;

public class TestClient {

	public static void main2(String[] args) throws ClientProtocolException, IOException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		Stock[] stocks = restTemplate.postForObject("http://localhost:8080/loadStocks",
				new String[] { "PETR4", "CMIG4" }, Stock[].class);
		System.out.println(stocks);
	}
	
	public static void main3(String[] args) throws JsonParseException, JsonMappingException, IOException{
		Account acc = new ObjectMapper().readValue(new File("/tmp/input.json"), Account.class);
		System.out.println(acc);
	}

}
