package imcode.util;

import org.apache.commons.lang.UnhandledException;

public class ShouldNotBeThrownException extends UnhandledException {
    public ShouldNotBeThrownException(Throwable cause) {
        super(cause) ;
    }
}
