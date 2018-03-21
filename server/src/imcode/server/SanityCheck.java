package imcode.server;

import java.util.Collection;

public interface SanityCheck {

    Collection<Problem> execute();

    interface Problem {
        Severity getSeverity();

        String getDescription();

        enum Severity {
            UNKNOWN,
            WARNING,
            ERROR,
        }

    }
}
