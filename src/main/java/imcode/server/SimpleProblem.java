package imcode.server;

public class SimpleProblem implements SanityCheck.Problem {

    private final Severity severity;
    private final String description;

    public SimpleProblem(Severity severity, String description) {
        this.severity = severity;
        this.description = description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    };

}