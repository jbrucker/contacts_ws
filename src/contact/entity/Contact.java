package contact.entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A person is a contact with a name, title, and email.
 * title is text to display for this contact in a list of contacts,
 * such as a nickname or company name.
 */
@Entity
@XmlRootElement(name="contact")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@XmlAttribute
	private long id;
	/** the display title of this contact, such as nickname */
	@XmlElement(required=true,nillable=false)
	private String title;
	/** full name of this contact */
	private String name;
	/** contact's email */
	@XmlElement(name="email")
	private List<String> emails;
	/** URL of photo */
	private String photoUrl;
	/** person who owns this contact */
	private String owner;
	
	/** owner of this contact */
	//@XmlTransient
//	private User owner;
	
	/** Create a new contact with no data.  Intended for use by persistence framework. */
	public Contact() { 
		emails = new ArrayList<String>();
	}
	
	/** Create a new contact with the given title, name, and email address. */
	public Contact(String title, String name, String email ) {
		this();
		this.title = title;
		this.name = name;
		emails.add( email );
		this.photoUrl = "";
		this.owner = "";
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Contact(long id) {
		this.id = id;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photo) {
		this.photoUrl = photo;
	}

  
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getEmails() {
		return emails;
	}
	
	public String getEmail() {
		if (emails.size()>0) return emails.get(0);
		return "";
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public void addEmail(String email) { emails.add(email); }
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
//	public User getOwner() { return owner; }

//	public void setOwner(User owner) { this.owner = owner; }

	@Override
	public String toString() {
		StringBuilder ebuf = new StringBuilder();
		for(String email: emails) ebuf.append("<").append(email).append("> ");
		return String.format("%s: %s <%s> (%d)", title, name, ebuf.toString(), id);
	}
	
	/** Two contacts are equal if they have the same id,
	 * even if other attributes differ.
	 * @param other another contact to compare to this one.
	 */
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass()) return false;
		Contact contact = (Contact) other;
		return contact.getId() == this.getId();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((photoUrl == null) ? 0 : photoUrl.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}


	/**
	 * Copy another contact's data into this contact.
	 * The id of this contact is not changed.  This allows
	 * complete updates of an existing contact without
	 * changing the object's identity.
	 * @param other another Contact whose fields are copied to this contact.
	 */
	public void copyOf(Contact other) {
		if (other == null) throw new IllegalArgumentException("source contact may not be null");
		// don't check the id value. Its the caller's responsibility to supply correct argument
		this.setTitle(other.getTitle()); 
		this.setName(other.getName());
		Collections.copy(this.emails, other.emails);
		this.setPhotoUrl(other.getPhotoUrl());
	}
	
	/**
	 * Update this contact's data from another Contact.
	 * The id field of the update must either be 0 or the same value as this contact!
	 * @param update the source of update values
	 */
	public void applyUpdate(Contact update) {
		if (update == null) return;
		if (update.getId() != 0 && update.getId() != this.getId() )
			throw new IllegalArgumentException("Update contact must have same id as contact to update");
		// Since title is used to display contacts, don't allow empty title
		if (! isEmpty( update.getTitle()) ) this.setTitle(update.getTitle()); // empty nickname is ok
		// other attributes: allow an empty string as a way of deleting an attribute in update (this is hacky)
		if (update.getName() != null ) this.setName(update.getName()); 
//		if (update.getEmail() != null) this.setEmail(update.getEmail());
		if (update.getPhotoUrl() != null) this.setPhotoUrl(update.getPhotoUrl());
	}
	
	/**
	 * Test if a string is null or only whitespace.
	 * @param arg the string to test
	 * @return true if string variable is null or contains only whitespace
	 */
	private static boolean isEmpty(String arg) {
		return arg == null || arg.matches("\\s*") ;
	}
	
}