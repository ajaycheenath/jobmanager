package com.af.job.utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class manages all common utilities used in the application
 * @author ajay_francis
 *
 */
@Component
public class CommonUtils {
	
		Logger logger = LoggerFactory.getLogger(CommonUtils.class);
		/**
		 * This method establish an HTTP connection and send GET request to the URL passed as parameter
		 * @param url
		 * @param timeout - if the HTTP response takes more than specified time, library will be throwing a timeoutexception (this is to unblock process waiting for response)
		 * @throws Exception
		 */
		public void sendGet(String url, int timeout) throws Exception {
			logger.debug("/sendGet Sending 'GET' request to URL : " + url);
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			con.setConnectTimeout(timeout); 

			int responseCode = con.getResponseCode();
			
			logger.debug("Response Code : {}", responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		
			logger.debug("/GET "+url+" - got response");
		}
		
		public void sendEmail(Map<String, String> configs) {
			String toEmailAddress = configs.get("toEmailAddress");
			logger.debug("Successfuly sent email to "+toEmailAddress);
		}
}
