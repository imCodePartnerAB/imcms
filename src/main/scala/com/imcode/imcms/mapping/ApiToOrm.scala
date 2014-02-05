package com.imcode.imcms.mapping

import com.imcode.imcms.api.{DocRef, I18nDocRef, DocumentLanguage}

object ApiToOrm {

  def toApi(language: orm.DocLanguage): DocumentLanguage = {
    DocumentLanguage.builder
      .code(language.getCode)
      .name(language.getName)
      .nativeName(language.getNativeName)
      .enabled(language.isEnabled).build()
  }

  def toApi(ref: orm.DocRef): DocRef = DocRef.of(ref.getDocId, ref.getVersionNo)
  def toApi(ref: orm.I18nDocRef): I18nDocRef = I18nDocRef.of(ref.getDocRef |> toApi, ref.getLanguage |> toApi)

//  def toOrm(ref: DocRef): orm.DocRef = new orm.DocRef(ref.getDocId, ref.getVersionNo)
//  def toOrm(ref: I18nDocRef): orm.I18nDocRef = new I18nDocRef.of(ref.getDocRef |> toApi, ref.getLanguage |> toApi)


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
