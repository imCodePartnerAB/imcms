package imcode.server.document;

public class TemplateDomainObject implements Comparable<TemplateDomainObject>, Cloneable {
    private final String name;
    private final String fileName;
    private final boolean isHidden;

    public TemplateDomainObject(String name, String fileName, boolean isHidden) {
        this.name = name;
        this.fileName = fileName;
        this.isHidden = isHidden;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public String getNameAdmin() {
        return name + ((isHidden) ? "(Hidden)" : "");
    }

    public boolean isHidden() {
        return isHidden;
    }

    public int compareTo(TemplateDomainObject o) {
        return name.compareToIgnoreCase(o.getName());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TemplateDomainObject that = (TemplateDomainObject) o;

        return fileName.equals(that.fileName);

    }

    public int hashCode() {
        return fileName.hashCode();
    }

    @Override
    public TemplateDomainObject clone() {
        try {
            return (TemplateDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
