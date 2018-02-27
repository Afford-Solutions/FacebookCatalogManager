package com.facebook.catalog.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.facebook.catalog.request.FeedCreatorRequest;

public class FeedCreator extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String requestParams = request.getQueryString();
		
		response.getWriter().print("CATALOG FEED CREATION VERSION 1 : " + requestParams);
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		
		try{
			
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
			String query = builder.toString();
		
			JSONObject queryObject = new JSONObject(query);
			
			if(queryObject.has("catalog_id") || queryObject.has("feed_name") || queryObject.has("access_token") || queryObject.has("country") || queryObject.has("currency") || queryObject.has("hostname")){
				
				JSONObject requestObj = FeedCreatorRequest.createFeed(queryObject);
				
				if(null != requestObj){
					
					if(requestObj.has("success")){
						
						if(requestObj.getBoolean("success")){
								
							out.println(requestObj.toString());
						
						}
						else{
							out.println(requestObj.toString());
						}
					}
					else{
						out.println(new JSONObject().put("success", false).put("feed_id", "NA").put("message", "SOMETHING WENT WRONG (BAD RESPONSE). PLEASE TRY AGAIN.").put("error_code", "501"));
					}
					
				}
				else{
					out.println(new JSONObject().put("success", false).put("feed_id", "N/A").put("message", "SOMETHING WENT WRONG (BAD RESPONSE). PLEASE TRY AGAIN.").put("error_code", "502"));
				}
				
			}
			else{
				out.println(new JSONObject().put("success", false).put("feed_id", "NA").put("message", "ONE OR MORE MANDATORY FIELDS ARE MISSING. PLEASE VERIFY YOUR REQUEST AGAIN.").put("error_code", "503"));
			}
		
		}catch(Exception e){

			out.println(
					new JSONObject()
					.put("success", false)
					.put("feed_id", "NA")
					.put("message", "SOMETHING WENT WRONG. PLEASE CONTACT US WITH THIS STRING. EXCEPTION : " + e)
					.put("error_code", "504")
			);
			
		}
		
		finally {
			out.close();
		}
			
	}

}