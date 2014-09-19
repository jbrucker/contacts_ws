package contact.service.mem;

import contact.service.*;
import contact.service.mem.MemContactDao;

/**
 * MemDaoFactory is a factory for getting instances of entity DAO object
 * that use memory-based persistence, which isn't really persistence at all!
 * 
 * @see contact.service.DaoFactory
 * @version 2014.09.19
 * @author jim
 */
public class MemDaoFactory extends DaoFactory {
	/** instance of the entity DAO */
	private ContactDao daoInstance;
	
	public MemDaoFactory() {
		daoInstance = new MemContactDao();
	}
	
	@Override
	public ContactDao getContactDao() {
		return daoInstance;
	}
	
	@Override
	public void shutdown() {
		//TODO here's your chance to show your skill!
		// Use JAXB to write all your contacts to a file on disk.
		// Then recreate them the next time a MemFactoryDao and ContactDao are created.
	}
}
