package contact.resource;
import java.net.URI;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.DaoFactory;
import contact.service.ContactDao;
import contact.service.EtagUtil;
  
/**
 * Provide web access to our contacts.
 * See the documentation for what methods are allowed
 * and URL patterns.
 * 
 * @author Jim Brucker
 */
@Path("/contacts")
@Singleton
public class ContactResource {
	private ContactDao dao;
	@Context
	UriInfo uriInfo;  // info about the request URI.
	
	public ContactResource() {
		dao = DaoFactory.getInstance().getContactDao();
	}
	
	/**
	 * Get all contacts.
	 * TODO: add query parameters to limit the number of values returned,
	 * and set starting index.
	 */
	@GET
	@PermitAll
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public List<Contact> getContacts( @Context SecurityContext sec ) {
//		if ( ! sec.isUserInRole("user") ) 
		return dao.findAll( );
	}
	
	/**
	 * Plain text response to root resource
	 */
//	@GET
//	@Produces({MediaType.TEXT_PLAIN})
	public String getTextPlain( ) {
		return "Please see the contacts API document at ...";
	}
	
	/**
	 * Get one contact given his id.
	 * @param id the id of desired contact
	 * @return
	 */
	@GET
	@Path("/{id: \\d+}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getContact( @PathParam("id") long id, @Context Request request ) {
		
		Contact contact = dao.find(id);
		if (contact == null) return Response.status(Status.NOT_FOUND).build();
		
		EntityTag etag = EtagUtil.etag(contact);
		ResponseBuilder builder = request.evaluatePreconditions( etag );
		if (builder != null) return builder.build();
		
		return Response.ok(contact).tag(etag).build();
	}
	
	
	/**
	 * Get one contact by his nickname (contact title).
	 * A string match is used, so this may return more than one
	 * result. Or none at all.
	 * @return List of matching contacts
	 */
	@GET
	@Path("/query")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getByTitle( @QueryParam("title") String query ) {
		
		if (query == null || query.isEmpty() ) 
			throw new WebApplicationException(Status.BAD_REQUEST); // same as returning Response.status(NOT_FOUND) ?
		List<Contact> contacts = dao.findByTitle(query);
		GenericEntity<List<Contact>> result = new GenericEntity<List<Contact>>( contacts ) { };
		return Response.ok(result).build();
	}


	/**
	 * Delete a contact from persistent storage.
	 * @param id the id of contact to delete
	 * @param request Request object
	 * @return a Response indicating OK or problem with request
	 */
	@DELETE
	@Path("/{id: \\d+}")
	public Response delete(@PathParam("id") long id, 
			@Context Request request, @Context SecurityContext sec) {
		
		String user = getAuthUser( sec );
		
		Contact contact = dao.find(id);
		if (contact == null) return Response.status(Status.NOT_FOUND).build();
		
		if (! user.equals(contact.getOwner() ) )
				throw new WebApplicationException(Status.FORBIDDEN);
		
		EntityTag etag = EtagUtil.etag(contact);
		ResponseBuilder builder = request.evaluatePreconditions( etag );
		if (builder != null) return builder.build();

		boolean ok = dao.delete(id);
		System.out.printf("Performed Delete %d\n", id);
		if (ok) return Response.ok().build();
		return Response.status(Status.NOT_ACCEPTABLE).build();
	}
	
	/**
	 * Get the username from security context.
	 * If no security context or no user then throw WebApplicationException with 403 Unauthorized.
	 * @param sec
	 * @return
	 */
	private String getAuthUser(SecurityContext sec) {
		if (sec != null && sec.getUserPrincipal() != null) {
			String username = sec.getUserPrincipal().getName();
			if (username != null) return username;
		}
		throw new WebApplicationException(Status.UNAUTHORIZED);
	}

	/**
	 * Add a new contact to persistent storage.
	 * @param element
	 * @param sec
	 * @return appropriate HTTP response code. CREATED, BAD_REQUEST, or CONFLICT
	 */
	@POST
	@RolesAllowed( { "user" } )
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML} )
	public Response createContact(JAXBElement<Contact> element, @Context SecurityContext sec ) 
	{
		System.out.println("POST");
		Contact contact = null;
		try {
			contact = element.getValue();
		} catch (Exception ex) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		// security context info is injected
		String name = getAuthUser( sec );
		contact.setOwner(name);

		boolean ok = dao.save(contact);
		
		// return the URI of the new resource
		if (ok) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			URI uri = builder.path( ""+contact.getId() ).build();
			System.out.println("POST: created " + uri.toString() );
			return Response.created(uri).build();
		}
		return Response.status(Status.BAD_REQUEST).build();
	}
	
	/**
	 * Update data for a contact.
	 * @param update request body. Automatically converted to object by JAX-RS.
	 * @return
	 */
	@PUT
	@Path("/{id: \\d+}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
	public Response put( @PathParam("id") long id, Contact update, @Context Request request ) 
	{
		Contact contact = dao.find(id);
		if (contact == null) return Response.status(Status.NOT_FOUND).build();
		
		EntityTag etag = EtagUtil.etag(contact);
		ResponseBuilder builder = request.evaluatePreconditions( etag );
		if (builder != null) return builder.build();
		
		// make sure the request is valid
		if (update.getId() != 0 && id != update.getId() ) 
			return Response.status(Status.BAD_REQUEST).build();
		
		update.setId( id ); // in case id is 0 in request
		
		boolean ok = dao.update(update);
		if (! ok ) return Response.status(Status.NOT_ACCEPTABLE).build();
		
		// return the URI of the updated resource
		UriBuilder uribuilder = uriInfo.getAbsolutePathBuilder();
		URI uri = uribuilder.build();
		System.out.println("PUT: uri = " + uri.toString() );
		return Response.ok().location(uri).build();
	}
}
