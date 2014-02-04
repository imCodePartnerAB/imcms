package com.imcode.imcms.mapping

import com.imcode.imcms.api.DocumentLanguage

object ApiToOrm {

  def toApi(language: orm.DocumentLanguage): DocumentLanguage = {
    DocumentLanguage.builder
      .code(language.getCode)
      .name(language.getName)
      .nativeName(language.getNativeName)
      .enabled(language.isEnabled).build()
  }

  //    public com.imcode.imcms.mapping.orm.DocumentLanguage toOrmDocumentLanguage(DocumentLanguage language) {
  //
  //        return DocumentLanguage.builder()
  //                .code(language.getCode())
  //                .name(language.getName())
  //                .nativeName(language.getNativeName())
  //                .enabled(language.isEnabled())
  //                .build();
  //    }
}
