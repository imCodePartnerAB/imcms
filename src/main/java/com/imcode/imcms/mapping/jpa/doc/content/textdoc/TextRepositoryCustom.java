package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.Version;

interface TextRepositoryCustom {

    Text findFirst(Version version, Language language, LoopEntryRef loopEntryRef);
}
