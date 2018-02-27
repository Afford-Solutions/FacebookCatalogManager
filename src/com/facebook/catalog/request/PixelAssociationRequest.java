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

public class PixelAssociationRequest {

	private final static String URL = "https://graph.facebook.com";
	private final static String VERSION = "v2.10";
	
	public static JSONObject associatePixel(JSONObject queryObject) throws Exception {
	
			String pixel_id = queryObject.getString("pixel_id");
			String catalog_id = queryObject.getString("catalog_id");
			String access_token = queryObject.getString("access_token");
						
			String custom_url = URL+"/"+VERSION+"/"+ catalog_id +"/external_event_sources";
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost postreq = new HttpPost(custom_url);
			
			ArrayList<NameValuePair> urlparams = new ArrayList<NameValuePair>();
			urlparams.add(new BasicNameValuePair("access_token", access_token));
			urlparams.add(new BasicNameValuePair("external_event_sources", "[\""+ pixel_id +"\"]"));
			
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
				
				if(responseObj.has("success") && responseObj.getBoolean("success")){
					return new JSONObject()
							.put("success", true)
							.put("message", "PRODUCT CATALOGUE IS SUCCESSFULLY LINKED TO THE PIXEL." + result.toString())
							.put("error_code", "");
				}
				else{
					return new JSONObject()
							.put("success", false)
							.put("message", "SOMETHING WENT WRONG. PLEASE CONTACT US WITH THIS STRING : " + result.toString())
							.put("error_code", String.valueOf(responseFacebook.getStatusLine().getStatusCode()));
				}
				
			}
			else{
				
				JSONObject responseObj = new JSONObject(result.toString());
				
				return new JSONObject()
						.put("success", false)
						.put("message", responseObj.getJSONObject("error").getString("message"))
						.put("error_code", String.valueOf(responseObj.getJSONObject("error").getInt("code")));
			}
	
	}
	
}