package contact.client;

import java.util.Iterator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.*;
import org.glassfish.jersey.filter.LoggingFilter;

import contact.entity.Contact;

/**
 * This is a client for the Contacts service using Jersey client.
 * Alternatively, you could use the JDK's URLConnection class or
 * (more powerful) Apache HttpClient.
 * 
 * This is using the Jersey 2.x Client, which is way TOO COMPLEX!
 * Oracle and Glassfish have really screwed up usability of Jersey.
 * The Jersey 1.x Client is MUCH easier to use than the Jersey 2.x Client.
 * Consider switching to Resteasy.
 */
public class ContactsClient {
	private static final String CONTEXT_ROOT = "http://localhost:8080/contacts";
	
	private static String username = "jim";
	private static String password = "jim";
	private Client client;

	public ContactsClient() {
		ClientConfig config = new ClientConfig();
		config.property(ClientProperties.FOLLOW_REDIRECTS, true);
		client = ClientBuilder.newClient(config);
	
		// Logging filter can use a java.util.logging.Logger or any PrintStream
		LoggingFilter logFilter = new LoggingFilter( );
		// You can add filters to a client or a WebResource
		client.register( logFilter );
	}
	
	/**
	 * Get one todo
	 * @param id is the todo id
	 */
	void testGetAll( ) {
		WebTarget target = client.target(CONTEXT_ROOT);
		
		// Jersey 2 is Fuckin' TOO COMPLEX!
		// Jersey 1.x WebTarget was much easier and had more useful interface.
		Invocation.Builder builder = target.request(MediaType.APPLICATION_XML);
		// How to add request header:
		// builder.header("Some-header", "value");
		
		Response response = builder.get();

		System.out.println("Status: " + response.getStatus() );
		MultivaluedMap<String, Object> headers = response.getHeaders();
		for( String key : headers.keySet() ) {
			System.out.printf("%s: %s\n", key, headers.getFirst(key) );
		}
		if ( response.hasEntity() ) {
			Object entity = response.getEntity();
			System.out.println("Got entity of type " + entity.getClass().getName());
		}
		
	}
	
	void testGet(long id) {
		WebTarget target = client.target(CONTEXT_ROOT).path(Long.toString(id));
		Invocation.Builder builder = target.request(MediaType.APPLICATION_XML);
		System.out.println("Getting contact #"+id);
		Contact contact = builder.get(Contact.class);

		System.out.println( contact );
	}
	
//	void testPut() {
//		WebResource res = client.resource(CONTEXT_ROOT);	
//		// Digest Authentication filter
//		ClientFilter authFilter = new HTTPDigestAuthFilter(username, password);	
//		res.addFilter( authFilter );		
//		//res.accept(MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE);		
//		Todo todo = new Todo("This is Hacker's todo: hack security","soon", 99);
//		res.put(todo);
//		System.out.println( res.get(String.class) );
//	}
	
	public static void main(String[] args) {
		ContactsClient tc = new ContactsClient();
//		tc.testPut();
		
		tc.testGetAll();
	}
}
