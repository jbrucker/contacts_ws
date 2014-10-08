package contact;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.security.authentication.DigestAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletProperties;

import contact.main.RequestLogFilter;

/**
 * <p>
 * This example shows how to deploy a RESTful web service using a Jetty server
 * that is created and started via code.
 * </p>
 * <p>
 * In this example, the resource classes are in the package "demo.resource".
 * Each resource class should be annotated with JAX-RS @Path("/something") and
 * the request handler methods should be annotated with @GET, @POST, etc.
 * </p>
 * <p>
 * This class creates a Jetty server on the specified port (default is PORT), a
 * ContextHandler that represents a Context containing a context path and
 * mapping of pathspecs to Servlets.
 * <p>
 * <tt>
 * handler.setContextPath("/")
 * </tt>/p>
 * <p>
 * Then the servlet holder is mapped to a path component (inside the context
 * path) using:
 * <p>
 * <tt>
 * handler.addServlet( servletHolder, "/*" );
 * </tt>
 * </p>
 * <p>
 * which means "map" everything inside this context to servletHolder. In a more
 * complex application (context), you could have many servlets and map different
 * pathspecs to different servlets.
 * <p>
 * In the case of a JAX-RS web service, each "resource" class also has a
 * @Path("/something") annotation, which can be used to map different paths to
 * different resources, so one ServletHolder can manage all your resource
 * classes.
 * </p>
 * 
 * <p>
 * I tested this with Jersey 2.12 and Jetty 9.2. I used the following JARs,
 * referenced as libraries in Eclipse: Jersey: lots of JARs! I included
 * everything from: jersey/lib directory, jersey/ext directory, and
 * jersey/api/jaxrs.ws.rs-api-2.01.jar Some of these JARs probably aren't
 * necessary, but I got runtime errors about missing classes when I omitted JARs
 * from the ext/ directory. jersey/ext contains jars from other projects; this
 * may cause a problem if you have another version of the same JARs in your
 * project! If you do, compare the JARs, or switch to a Maven project so Maven
 * will manage your dependencies.
 * 
 * @author jim
 * 
 */
public class JettyMain {
	/** Package(s) where REST resource classes are */
	static final String RESOURCE_PACKAGE = "contact.resource";
	/**
	 * The default port to listen on. Typically 80 or 8080. On Ubuntu or MacOS
	 * if you are not root then you must use a port > 1024.
	 */
	static final int PORT = 8080;

	static Server server = null;

	public static void startServer(int port) {

		server = new Server(port);

		// (1) Use a ServletContextHandler to hold a "context" (our application)
		// that will be deployed on the server.
		// The parameter is a bitwise "or" of options, defined in
		// ServletContextHandler.
		// Options are: SESSIONS, NO_SESSIONS, SECURITY, NO_SECURITY
		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);

		// (2) Define the path that server should map to your context.
		// If you use "/" it means the server root (map all requests to
		// context).
		context.setContextPath("/");

		// (3) Add servlets and mapping of requests to requests to servlets to
		// the ContextHandler.
		// The ServletContextHandler class gives you several ways to do this:
		// To add a Servlet class and its pathspec:
		// context.addServlet( Class<? extends Servlet> clazz, String pathspec )
		// To add an object (a ServletHolder):
		// context.addServlet( ServletHolder servletHolder, String pathspec )

		// A Jetty ServletHolder holds a javax.servlet.Servlet instance along
		// with a name,
		// initialization parameters, and state information. It implements the
		// ServletConfig interface.
		// Here we use a ServletHolder to hold a Jersey ServletContainer.
		ServletHolder holder = new ServletHolder(
				org.glassfish.jersey.servlet.ServletContainer.class);

		// (4) Configure the Jersey ServletContainer so it will manage our resource
		// classes and pass HTTP requests to our resources (our web service).
		// Do this by setting initialization parameters.
		// The ServletContainer javadoc tells you what the initialization
		// parameter are.
		// The PROVIDER_PACKAGES initialization parameter tells Jersey to
		// auto-configure
		// all resource classes (classes with @Path annotation) in the named
		// package(s).
//		holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, RESOURCE_PACKAGE);
		holder.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, ContactApplication.class.getName());
		// holder.setInitParameter(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE,
		// "false");

		context.addServlet(holder, "/*");

		// (optional) add a filter. Here I wrote a filter to log requests.
		context.addFilter(RequestLogFilter.class, "/",
				EnumSet.of(DispatcherType.REQUEST));

		// (5) Add the context (our application) to the Jetty server.
		server.setHandler( getSecurityHandler(context) );

		System.out.println("Starting Jetty server on port " + port);
		try {
			server.start();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Wrap a handler (such as ServletHandler) in a security handler.
	 * @param handler the Jetty Handler to wrap
	 * @return a SecurityHandler that wraps the handler parameter
	 */
	public static Handler getSecurityHandler(Handler handler) {
		
		// the parameters, which Jetty doesn't document, are realm, and properties file.
		// the properties file contains user data in the format: user: password,role[,...]
		LoginService loginService = new HashLoginService("myrealm", "src/myrealm.properties");
		server.addBean( loginService );
		
		Constraint constraint = new Constraint();
		constraint.setName("auth");
		constraint.setAuthenticate( true );
		// Only allow users that have these roles. 
		// It is more appropriate to specify this in the resource itself using annotations.
		// But if I comment this out, Jetty returns 403 Forbidden (instead of 401 Unauthorized).
		constraint.setRoles( new String[] {"user", "admin"} );
		
		// A mapping of resource paths to this constraint
		ConstraintMapping mapping = new ConstraintMapping();
		mapping.setPathSpec("/*");
		mapping.setConstraint( constraint );

		ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
		// setConstraintMappings requires an array or List of ConstraintMapping
		securityHandler.setConstraintMappings(new ConstraintMapping[] { mapping } );
		securityHandler.setAuthenticator(new DigestAuthenticator());
		securityHandler.setLoginService(loginService);
		
		securityHandler.setHandler(handler);
		return securityHandler;
	}
	
	public static void stopServer() throws Exception {
		if (server != null && server.isRunning()) server.stop();
	}

	/**
	 * Create a Jetty server and a context, add Jetty ServletContainer which
	 * dispatches requests to JAX-RS resource objects, and start the Jetty
	 * server.
	 * 
	 * @param args
	 *            not used
	 * @throws Exception
	 *             if Jetty server encounters any problem
	 */
	public static void main(String[] args) throws Exception {
		int port = PORT;
		
		startServer( port );

		System.out.println("Server started.  Press ENTER to stop it.");
		int ch = System.in.read();
		System.out.println("Stopping server.");
		stopServer();
	}

}
