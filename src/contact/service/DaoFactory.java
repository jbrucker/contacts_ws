package contact.service;

import contact.entity.Contact;
import contact.service.mem.MemContactDao;

/**
 * DaoFactory defines methods for obtaining instance of data access objects.
 * To create a DAO you first get an instance of a concrete factory by invoking
 * <p>
 * <tt>DaoFactory factory = DaoFactory.getInstance(); </tt>
 * <p>
 * Then use the <tt>factory</tt> object to get instances of actual DAO.
 * This factory is an abstract class.  There are concrete subclasses for
 * each persistence mechanism.  You can add your own factory by subclassing
 * this factory and implementing the abstract <tt>getXxxDao()</tt> methods.
 * 
 * @author jim
 */
public abstract class DaoFactory {
	// singleton instance of this factory
	private static DaoFactory factory;
	private static Object lock = new Object();
	
	/** this class shouldn't be instantiated, but constructor must be visible to subclasses. */
	protected DaoFactory() {
		// nothing to do
	}
	
	/**
	 * Get a singleton instance of the DaoFactory.
	 * @return instance of a concrete DaoFactory
	 */
	public static DaoFactory getInstance() {
		if (factory == null) {
			synchronized(lock) {
				if (factory == null) {
					factory = new contact.service.jpa.JpaDaoFactory();
					factory.init();
				}
			}
		}
		return factory;
	}
	
	/**
	 * Get an instance of a data access object for Contact objects.
	 * Subclasses of the base DaoFactory class must provide a concrete
	 * instance of this method that returns a ContactDao suitable
	 * for their persistence framework.
	 * @return instance of Contact's DAO
	 */
	public abstract ContactDao getContactDao();
	
	/**
	 * Initialize some aspect of DAO.
	 * I added this method as a hook so I can generate
	 * test contacts.  init() may never be invoked.
	 */
	public void init() {
		createTestContacts();
	}
	
	/**
	 * Shutdown all persistence services.
	 * This method gives the persistence framework a chance to
	 * gracefully save data and close databases before the
	 * application terminates.
	 */
	public abstract void shutdown();
	
	
	/** add contacts for testing.  id is the starting id to use. */
	protected void createTestContacts( ) {
		ContactDao dao = getContactDao();
		long id = 1;
		if (dao.find(id) == null) {
			Contact test = new Contact("Test contact", "Joe Experimental", "none@testing.com");
			test.setId(id);
			dao.save(test);
		}
		id++;
		if (dao.find(id) == null) {
			Contact test2 = new Contact("Another test contact", "Testosterone", "testee@foo.com");
			test2.setId(id);
			dao.save(test2);
		}
	}
}
