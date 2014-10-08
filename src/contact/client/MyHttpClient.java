package contact.client;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Use Apache HttpClient to access web service.
 * @author jim
 */
public class MyHttpClient {
	CloseableHttpClient httpclient = HttpClients.createDefault();
	
	
	public String get(String url) throws IOException {
		HttpGet get = new HttpGet( url );
		get.setHeader("Accept", "application/xml");
		// org.apache.http.client.fluent.Response 
		CloseableHttpResponse response;
		try {
			response = httpclient.execute( get );
		} catch ( IOException e1 ) {
			e1.printStackTrace();
			return null;
		}
		
		int status = response.getStatusLine().getStatusCode();
		if (status != 200) return null;
		String result = null;
		try {
			result = EntityUtils.toString( response.getEntity() );
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		
		response.close(); // throws IOException
		return result;
	}
	
	
}
