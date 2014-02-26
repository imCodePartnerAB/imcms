package com.imcode.imcms.api;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.imcode.imcms.mapping.OrmToApi;
import com.imcode.imcms.mapping.dao.DocLanguageDao;
import com.imcode.imcms.mapping.dao.SystemPropertyDao;
import com.imcode.imcms.mapping.orm.DocLanguage;
import com.imcode.imcms.mapping.orm.SystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

// todo: add security
@Service
public class DocumentLanguageService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private DocLanguageDao languageDao;

    @Inject
    private SystemPropertyDao systemDao;

    public List<DocumentLanguage> getAll() {
        return Lists.transform(languageDao.findAll(), new Function<DocLanguage, DocumentLanguage>() {
            public DocumentLanguage apply(DocLanguage input) {
                return OrmToApi.toApi(input);
            }
        });
    }

    public DocumentLanguage getByCode(String code) {
        DocLanguage docLanguage = languageDao.findByCode(code);

        return docLanguage == null ? null : OrmToApi.toApi(docLanguage);
    }

    public boolean isDefault(DocumentLanguage language) {
        return Objects.equals(language, getDefault());
    }

    public DocumentLanguage getDefault() {
        SystemProperty property = systemDao.findByName("DefaultLanguageId");

        if (property == null) {
            String message = "Configuration error. DefaultLanguageId property is not set.";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        Integer languageId = Ints.tryParse(Strings.nullToEmpty(property.getValue()));

        if (languageId == null) {
            String message = "Configuration error. DefaultLanguageId property is not a valid id.";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        DocLanguage language = languageDao.findOne(languageId);

        if (language == null) {
            String message = String.format("Configuration error. Default language (id: %d) can not be found.", languageId);
            logger.error(message);
            throw new IllegalStateException(message);
        }

        return OrmToApi.toApi(language);
    }
}
