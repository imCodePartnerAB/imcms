package imcode.external ;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface GetDocControllerInterface {

    String  createString(HttpServletRequest req) throws IOException;

}