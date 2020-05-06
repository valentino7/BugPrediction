package common.rest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import common.parser.JSONException;
import common.strings.Strings;


public class JsonRest {

	private JsonRest() {

	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONArray readJsonArrayFromUrl(String url) throws IOException {
		URL myURL = new URL(url);
		HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
		myURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		myURLConnection.setRequestProperty("Accept", "application/json");
		myURLConnection.setRequestProperty("Authorization", "Bearer " + Strings.TOKEN);
		myURLConnection.setRequestMethod("GET");	
		InputStreamReader is = new InputStreamReader(myURLConnection.getInputStream(), StandardCharsets.UTF_8);

		try (BufferedReader rd = new BufferedReader(is)){
			String jsonText = readAll(rd);
			return new JSONArray(jsonText);  
		}
	}

	public static JSONObject readJsonFromUrlGithub(String url) throws IOException {
		URL myURL = new URL(url);
		HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
		myURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		myURLConnection.setRequestProperty("Accept", "application/json");
		myURLConnection.setRequestProperty("Authorization", "Bearer " + Strings.TOKEN);
		myURLConnection.setRequestMethod("GET");	
		InputStreamReader is = new InputStreamReader(myURLConnection.getInputStream(), StandardCharsets.UTF_8);
		try (BufferedReader rd = new BufferedReader(is)){
			String jsonText = readAll(rd);
			return new JSONObject(jsonText); 
		}
	}




	public static JSONObject readJsonFromUrl(String url) throws IOException {
		InputStream is = new URL(url).openStream();
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8))){
			String jsonText = readAll(rd);
			return new JSONObject(jsonText); 
		}
	}
}