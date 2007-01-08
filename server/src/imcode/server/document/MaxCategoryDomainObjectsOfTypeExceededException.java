package imcode.server.document;

import com.imcode.imcms.mapping.DocumentSaveException;

/**
 * @author kreiger
 */
public class MaxCategoryDomainObjectsOfTypeExceededException extends DocumentSaveException {
    public MaxCategoryDomainObjectsOfTypeExceededException(String message) {
        super(message) ;
    }
}
