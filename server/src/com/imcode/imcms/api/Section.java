package com.imcode.imcms.api;

import imcode.server.document.*;

public class Section {
    SectionDomainObject internalSection;
    public Section( SectionDomainObject sectionDomainObject ) {
        this.internalSection = sectionDomainObject;
    }

   public String getName(){
       return this.internalSection.getName();
   }

   int getId() {
       return internalSection.getId();
   }
}
