package com.facebook.catalog.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.facebook.catalog.request.GrantCatalogAccessRequest;

public class GrantCatalogAccess extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().println("GRANT CATALOG ACCESS VERSION 1 : " + request.getQueryString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		
		try {
		
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
			String query = builder.toString();
			
			JSONObject queryString = new JSONObject(query);
			
			if(queryString.has("access_token") && queryString.has("catalog_id") && queryString.has("business_id")) {
				
				JSONObject responseStatus = GrantCatalogAccessRequest.requestCatalogAccess(queryString.getString("access_token"), queryString.getString("catalog_id"), queryString.getString("business_id"));
				
				if(responseStatus.has("success")) {
					out.println(responseStatus);
				}
				else {

					out.println(new JSONObject()
							.put("success", false)
							.put("message", "SOMETHING WENT WRONG. INVALID RESPONSE STRING. MESSAGE : " + responseStatus.toString())
							.put("error_code", "501"));
				
				}
				
			}
			
			else {

				out.println(new JSONObject()
						.put("success", false)
						.put("message", "ONE OR MORE PARAMETERS ARE MISSING IN THE REQUEST (BAD REQUEST).")
						.put("error_code", "502"));
			
			}
			
		}
		
		catch(Exception e) {
		
			out.println(new JSONObject()
					.put("success", false)
					.put("message", "SOMETHING WENT WRONG. PLEASE CONTACT SUPPORT TEAM. EXCEPTION : " + e.toString())
					.put("error_code", "503"));
		
		}
		
		finally {
			out.close();
		}
		
	}

}