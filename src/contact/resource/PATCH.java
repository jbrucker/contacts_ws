package contact.resource;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 * Define an annotation for the PATCH method.
 * Annotate methods with @PATCH to handle Http PATCH requests.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
public @interface PATCH
{
}
