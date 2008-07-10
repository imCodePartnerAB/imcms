package imcode.server;

import java.util.Collection;

public interface SanityCheck {

    Collection<Problem> execute() ;

    interface Problem {
        enum Severity {
            UNKNOWN,
            WARNING,
            ERROR,
        }
    
        Severity getSeverity();
        
        String getDescription();
        
    }
}
