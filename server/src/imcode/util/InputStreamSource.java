/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-mar-10
 * Time: 17:29:09
 */
package imcode.util;

import java.io.InputStream;
import java.io.IOException;

public interface InputStreamSource {

    public InputStream getInputStream() throws IOException;

}