package imcode.server.document;

public class TemplateDomainObject implements Comparable<TemplateDomainObject> {
    private final String name;
    private final String fileName;

    public TemplateDomainObject(String name, String fileName) {
        this.name = name ;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public int compareTo(TemplateDomainObject o) {
        return name.compareToIgnoreCase(o.getName()) ;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final TemplateDomainObject that = (TemplateDomainObject) o;

        return fileName.equals(that.fileName);

    }

    public int hashCode() {
        return fileName.hashCode();
    }
}
