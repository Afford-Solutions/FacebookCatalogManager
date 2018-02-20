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

public class GrantCatalogAccessRequest {

	private final static String URL = "https://graph.facebook.com";
	private final static String VERSION = "v2.10";
	
	public static JSONObject requestCatalogAccess(String access_token, String catalog_id, String business_id) {
		
		try {
			
			String SESSION_TOKEN = access_token;
				
			String custom_url = URL+"/"+VERSION+"/"+ catalog_id +"/agencies";
				
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(custom_url);
			
			ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			
			urlParameters.add(new BasicNameValuePair("access_token",SESSION_TOKEN));
			urlParameters.add(new BasicNameValuePair("business", business_id));
			urlParameters.add(new BasicNameValuePair("permitted_roles", "['ADMIN','ADVERTISER']"));
			
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			
			HttpResponse response = client.execute(post);
				
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line;
			while((line = reader.readLine()) != null){
				buffer.append(line);
			}
				
			if(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300){
					
				JSONObject responseObj = new JSONObject(buffer.toString());
					
				if(responseObj.has("success") && responseObj.getBoolean("success")){
					
					return new JSONObject()
							.put("success", true)
							.put("message", "PRODUCT CATALOGUE ACCESS GRANTED SUCCESSFULLY.");
					
				}
				else{
					return new JSONObject()
							.put("success", false)
							.put("message", "ERROR IN GRANTING PRODUCT CATALOGUE ACCESS. EXCEPTION : " + buffer.toString());
				}
					
			}
			
			else{
				return new JSONObject()
						.put("success", false)
						.put("message", "SOMETHING WENT WRONG. PLEASE CONTACT THE SUPPORT TEAM. MESSAGE : " + buffer.toString());
			}
				
		}
		catch(Exception e) {
			return new JSONObject()
					.put("success", false)
					.put("message", "SOMETHING WENT WRONG. PLEASE CONTACT THE SUPPORT TEAM. MESSAGE : " + e.getMessage());
		}
		
	}
	
}