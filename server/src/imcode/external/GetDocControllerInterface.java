package imcode.external ;

import java.util.List ;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;

public interface GetDocControllerInterface {


    String  createString(HttpServletRequest req) throws ServletException, IOException;



}