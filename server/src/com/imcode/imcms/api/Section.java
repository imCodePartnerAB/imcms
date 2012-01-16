package com.imcode.imcms.api;

import imcode.server.document.*;

/**
 * Represents a section in imcms
 */
public class Section {
    SectionDomainObject internalSection;

    /**
     * Constructs a Section with the given SectionDomainObject used internally.
     * @param sectionDomainObject SectionDomainObject to be used internally.
     */
    public Section( SectionDomainObject sectionDomainObject ) {
        this.internalSection = sectionDomainObject;
    }

    /**
     * Returns the name of this serction
     * @return a string with name of this section
     */
   public String getName(){
       return this.internalSection.getName();
   }

    /**
     * Returns the id of this section.
     * @since 2.0
     * @return id of this section
     */
   public int getId() {
       return internalSection.getId();
   }
}
