package imcode.server.document;

import imcode.util.IdNamePair;


public class TemplateDomainObject extends IdNamePair {
    private final String fileName;

    public TemplateDomainObject(int id, String name, String fileName) {
        super(id, name);

        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

}
