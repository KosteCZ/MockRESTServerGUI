package cz.koscak.jan.mockservergui.ws.rest;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class RESTClient {

	public static final String UTF_8 = "UTF-8";
	
	public static String useHttpClientPOST(CredentialsItem credentialsItem, String messageBody) throws Exception {
		
		//LOGGER.log(Level.DEBUG, "Creating message...");
	    
	    CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		
		HttpPost httpPost = new HttpPost(credentialsItem.getUrl());
		
		//LOGGER.log(Level.DEBUG, "URL: " + credentialsItem.getUrl());
		//LOGGER.log(Level.DEBUG, "Username: " + credentialsItem.getUsername());
		//LOGGER.log(Level.DEBUG, "Password: " + "*****");

		// Authentication
		if (credentialsItem.getUsername() != null && credentialsItem.getPassword() != null) {
			//String encoding = DatatypeConverter.printBase64Binary("PI_TEST:Dexter01".getBytes("UTF-8"));
			String encoding = DatatypeConverter.printBase64Binary((credentialsItem.getUsername()+":"+credentialsItem.getPassword()).getBytes(UTF_8));
			httpPost.setHeader("Authorization", "Basic " + encoding);
		}
		
		HttpEntity entity = new ByteArrayEntity(messageBody.getBytes(UTF_8));
		httpPost.setEntity(entity);
		
	    CloseableHttpResponse response2 = null;
	    
    	//Date dateStart = new Date();	
		//String dateStartString = DATE_FORMAT.format(dateStart) + " ";
		//LOGGER.log(Level.INFO, dateStartString + "Sending message...");

	    try {
	    	response2 = httpclient.execute(httpPost);
	    } catch(Exception e) {
	    //	Date dateError = new Date();	
		//	String dateErrorString = DATE_FORMAT.format(dateError) + " ";
		//	LOGGER.log(Level.ERROR, dateErrorString + "ERROR: During executing request: " + e.getMessage());
			throw(e);
		}	
	    
    	//Date dateFinish = new Date();	
		//String dateFinishString = DATE_FORMAT.format(dateFinish) + " ";
		//LOGGER.log(Level.INFO, dateFinishString + "Message sent.");
	    
	    String response = null;
	    
		try {

		    //LOGGER.log(Level.INFO, "Response status code: " + response2.getStatusLine());
		    
		    /*System.out.println("# Response:");*/
			HttpEntity entity2 = response2.getEntity();
		    // do something useful with the response body
		    // and ensure it is fully consumed
			response = convertStreamToString(entity2.getContent());

		    EntityUtils.consume(entity2);
		    
			//LOGGER.log(Level.INFO, "OK: Message processed successfully.");
			
		} catch(Exception e) {
			//LOGGER.log(Level.ERROR, "ERROR: Processing response failed due to: " + e.getMessage());
			throw(e);
		} finally {
		    response2.close();
		}
		
		return response;
		
	}
	
	@SuppressWarnings("resource")
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
}
