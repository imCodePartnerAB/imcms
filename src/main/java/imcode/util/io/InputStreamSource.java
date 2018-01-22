package imcode.util.io;

import java.io.IOException;
import java.io.Serializable;

public interface InputStreamSource extends Serializable, org.springframework.core.io.InputStreamSource {

    long getSize() throws IOException;

}
