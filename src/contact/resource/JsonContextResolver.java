package contact.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

//import org.glassfish.jersey.jettison.JettisonJaxbContext;

import contact.entity.Contact;

/**
 * Got the idea from org/glassfish/jersey/examples/jettison
 * and the @{link javax.ws.rs.ext.ContextResolver ContextResolver} API doc.
 * 
 * According to API, this class can be auto-discovered by JAX-RS if it
 * is annotated with @Provider
 * 
 * @author jim
 *
 * NOTE: to disable this ContentResolver, comment out the @Provider annotation.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonContextResolver implements ContextResolver<JAXBContext> {
	
	private JAXBContext context;
	/** this sucks! Hardcoding the names of classes to be handling by Jettison. */
	private static final Class<?> [] types = {Contact.class};
	
	public JsonContextResolver() throws JAXBException {
		
//			context = new JettisonJaxbContext( "contact.entity" );
			
			System.out.println("Dude, it works! Something just instantiated "+this.getClass().getName());
		
	}

	@Override
	public JAXBContext getContext(Class<?> type) {
		
		return context;
	}
}
