package com.facebook.catalog.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class FeedCreatorRequest {

	private final static String URL = "https://graph.facebook.com";
	private final static String VERSION = "v2.12";
	
	public static JSONObject createFeed(JSONObject queryObject) throws Exception {
	
			String catalog_id = queryObject.getString("catalog_id");
			String feed_name = queryObject.getString("feed_name");
			String access_token = queryObject.getString("access_token");
			String country = queryObject.getString("country");
			String currency = queryObject.getString("currency");
			String hostname = queryObject.getString("hostname");
						
			String custom_url = URL+"/"+VERSION+"/"+ catalog_id +"/product_feeds";
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost postreq = new HttpPost(custom_url);
			
			ArrayList<NameValuePair> urlparams = new ArrayList<NameValuePair>();
			urlparams.add(new BasicNameValuePair("access_token", access_token));
			urlparams.add(new BasicNameValuePair("country", country));
			urlparams.add(new BasicNameValuePair("default_currency", currency));
			urlparams.add(new BasicNameValuePair("deletion_enabled", "false"));
			urlparams.add(new BasicNameValuePair("name", feed_name));
			
			JSONObject schedule = new JSONObject();
			schedule.put("interval", "HOURLY");
			schedule.put("hour", 0);
			schedule.put("minute", 0);
			schedule.put("interval_count", 4);
			schedule.put("url", "http://capacus.com/customer-data/product-catalog/" + hostname + ".csv");
			
			urlparams.add(new BasicNameValuePair("schedule", schedule.toString()));
			
			postreq.setEntity(new UrlEncodedFormEntity(urlparams));
			
			HttpResponse responseFacebook = httpclient.execute(postreq);
	
			BufferedReader rd = new BufferedReader(new InputStreamReader(responseFacebook.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			if(responseFacebook.getStatusLine().getStatusCode() >= 200 && responseFacebook.getStatusLine().getStatusCode() < 300){
				
				JSONObject responseObj = new JSONObject(result.toString());
				
				if(responseObj.has("id")){
					return new JSONObject()
							.put("success", true)
							.put("feed_id", responseObj.getString("id"))
							.put("message", "PRODUCT CATALOGUE FEED IS SUCCESSFULLY CREATED." + result.toString());
				}
				else{
					return new JSONObject()
							.put("success", false)
							.put("feed_id", "NA")
							.put("message", "SOMETHING WENT WRONG. PLEASE CONTACT US WITH THIS STRING : " + result.toString());
				}
				
			}
			else{
				return new JSONObject()
						.put("success", false)
						.put("feed_id", "NA")
						.put("message", "SOMETHING WENT WRONG. PLEASE CONTACT US WITH THIS STRING : " + result.toString());
			}
	
	}
	
}