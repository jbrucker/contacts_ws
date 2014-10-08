package contact.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MyHttpUrlConnection {

	public String get(String urlstr) throws IOException {
		URL url = new URL(urlstr);
		URLConnection conn = url.openConnection();
		
		conn.addRequestProperty("Accept", "application/xml");
		InputStream instream = conn.getInputStream();
		
		String result = readFully( instream );
		return result;
	}
	
	public static String readFully( InputStream in ) {
		InputStreamReader reader = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		char[] buff = new char[1024];
		try {
			int count;
			while( (count = reader.read(buff)) > 0 ) {
				sb.append(buff, 0, count);
			}
		} catch (IOException screwit) { /* couldn't read result */ }
		finally {
			try { reader.close(); } catch (IOException ioe) { }
		}
		return sb.toString();
	}
}
