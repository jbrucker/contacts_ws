package contact.service;

import javax.ws.rs.core.EntityTag;

/**
 * Compute Etag for entities.
 * @author jim
 *
 */
public class EtagUtil {
	private static final EntityTag ZERO_TAG = new EntityTag("0");
	/**
	 * Get the Etag of an object
	 * @param obj param to compute etag of.
	 * @return a suitable etag or 0 if obj is null
	 */
	public static EntityTag etag(Object obj) {
		if (obj == null) return ZERO_TAG;
		return new EntityTag( ""+obj.hashCode() );
	}
}
