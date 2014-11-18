package contact.entity;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;
/**
 * A wrapper for serializing a list.
 * the desired output is:
 * <contacts>
 *   <contact id="1111">
 *       <name>Barrack Obama</name>
 *       <nickname>pres</nickname>
 *       ...
 *   </contact>
 *   <contact id="2222">
 *       ...
 *   </contact>
 * </contacts>
 *
 */
@XmlRootElement(name="contacts")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contacts implements Serializable {
	@XmlElement(name="contact")
	List<Contact> contacts;

	public Contacts() {
		contacts = new java.util.ArrayList<Contact>();
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> list) {
		this.contacts = list;
	}

	public boolean add(Contact contact) {
		return this.contacts.add(contact);
	}
	
	public boolean remove(Contact contact) {
		return this.contacts.remove(contact);
	}
}
