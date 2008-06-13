package com.imcode.imcms.api;

import imcode.server.document.SectionDomainObject;

public class Section {
    SectionDomainObject internalSection;
    public Section( SectionDomainObject sectionDomainObject ) {
        this.internalSection = sectionDomainObject;
    }

   public String getName(){
       return this.internalSection.getName();
   }

    /**
         @since 2.0
     */
   public int getId() {
       return internalSection.getId();
   }
}
