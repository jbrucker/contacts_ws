package contact.service;

import java.util.List;

import contact.entity.*;
/**
 * Interface defines the operations required by 
 * a DAO for Contacts.
 * 
 * @author jim
 */
public interface UserDao {

	/** Find a user by ID in contacts.
	 * @param the id of user to find
	 * @return the matching user or null if the id is not found
	 */
	public abstract User find(long id);

	/**
	 * Return all the persisted users as a List.
	 * There is no guarantee what implementation of
	 * List is returned, so caller should use only
	 * List methods (not, say ArrayList).
	 * @return list of all users in persistent storage.
	 *   If no users, returns an empty list.
	 */
	public abstract List<User> findAll(User owner);
	
	/**
	 * Find a user by name
	 * @return one user with matching name
	 */
	public abstract User findByName(String name);

	/**
	 * Save or replace a user.
	 * If the contact.id is 0 then it is assumed to be a
	 * new (not saved) contact.  In this case a unique id
	 * is assigned to the contact.  
	 * If the user id is not zero and there is a saved
	 * user with same id, then the old contact is replaced.
	 * @param contact the contact to save or replace.
	 * @return true if saved successfully
	 */
	public abstract boolean save(User user);

	/**
	 * Update a Contact.  If the contact with same id
	 * as the update is already in persistent storage,
	 * then all fields of the contact are replaced with
	 * values in the update (including null values!).
	 * The id of the update must match the id of a contact
	 * already persisted.  If not, false is returned.
	 * @param update update info for the contact.
	 * @return true if the update is applied successfully.
	 */
	public abstract boolean update(Contact update);
}