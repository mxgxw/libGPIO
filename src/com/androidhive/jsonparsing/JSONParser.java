package com.androidhive.jsonparsing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static JSONArray jArr = null;
	static String json = "";
	private String username = null;
	private String password = null;
	private String scope = null;

	// constructor
	public JSONParser() {

	}

	/**
	 * Creates a new JSONParser object that helps to make REST
	 * requests and parse the results.
	 * @param username Username for connection
	 * @param pass Password for connection
	 * @param scope Hostname
	 */
	public JSONParser(String username, String pass, String scope) {
		this.username = username;
		this.password = pass;
		this.scope = scope;
	}

	/**
	 * Makes a "POST" request to the specified URL and returs
	 * a JSONObject.
	 * @param url Target URL
	 * @param port Target Port
	 * @return Parsed response as a JSONObject
	 * @throws Throws any exception caught on the request.
	 */
	public JSONObject postJSONFromURL(String url, int port) throws Exception {
		InputStream is = null;
		JSONObject jObj = null;
		String json = "";
		// Making HTTP request
		DefaultHttpClient httpClient = new DefaultHttpClient();

		httpClient.getCredentialsProvider().setCredentials(
				new AuthScope(scope, port),
				new UsernamePasswordCredentials(username, password));

		HttpPost httpGet = new HttpPost(url);

		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		json = sb.toString();

		jObj = new JSONObject(json);

		// return JSON String
		return jObj;

	}


	/**
	 * Makes a "GET" request to the specified URL and returns
	 * a JSONArray.
	 * @param url Target URL
	 * @param port Target Port
	 * @return JSON response parsed object
	 * @throws Throws any exception caught on the request
	 */
	public JSONArray getJSONArrayFromUrl(String url, int port) throws Exception {
		InputStream is = null;
		JSONArray jArr = null;
		String json = "";

		DefaultHttpClient httpClient = new DefaultHttpClient();

		httpClient.getCredentialsProvider().setCredentials(
				new AuthScope(scope, port),
				new UsernamePasswordCredentials(username, password));

		HttpGet httpGet = new HttpGet(url);

		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		json = sb.toString();

		jArr = new JSONArray(json);

		// return JSON String
		return jArr;

	}


	/**
	 * Makes a "GET" request to the specified URL and returns
	 * a JSONObject.
	 * @param url Target URL
	 * @param port Target Port
	 * @return JSON response parsed object
	 * @throws Throws any exception caught on the request
	 */
	public JSONObject getJSONFromUrl(String url, int port) throws Exception {
		InputStream is = null;
		JSONObject jObj = null;
		String json = "";

		DefaultHttpClient httpClient = new DefaultHttpClient();

		httpClient.getCredentialsProvider().setCredentials(
				new AuthScope(scope, port),
				new UsernamePasswordCredentials(username, password));

		HttpGet httpGet = new HttpGet(url);

		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		json = sb.toString();

		jObj = new JSONObject(json);

		// return JSON String
		return jObj;

	}
}