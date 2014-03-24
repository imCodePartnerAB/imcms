package com.imcode.imcms.api;

import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.OptionalInt;

// scala
public class DocGetterCallbacks {

    private enum DocVersionType {
        WORKING, DEFAULT, CUSTOM
    }

    /**
     * Creates a callback and sets it to the user.
     */
    public static void updateUserDocGetterCallback(HttpServletRequest request, ImcmsServices services, UserDomainObject user) {
        DocGetterCallback currentDocGetterCallback = user.getDocGetterCallback();
        DocumentLanguageSupport dls = services.getDocumentLanguageSupport();
        DocumentLanguage defaultLanguage = dls.getDefault();
        DocumentLanguage preferredLanguage = Optional
                .of(StringUtils.trimToEmpty(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)))
                .map(dls::getByCode)
                .orElse(null);

        if (preferredLanguage == null && currentDocGetterCallback != null) {
            preferredLanguage = currentDocGetterCallback.documentLanguages().getPreferred();
        }

        if (preferredLanguage == null) {
            preferredLanguage = dls.getForHost(request.getServerName());
        }

        if (preferredLanguage == null) {
            preferredLanguage = defaultLanguage;
        }

        DocumentLanguages documentLanguages = new DocumentLanguages(preferredLanguage, defaultLanguage);
        OptionalInt docIdOpt = parseUnsignedInt(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID));
        OptionalInt versionNoOpt = parseVersionString(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION));
        DocGetterCallback docGetterCallback = null;

        if (docIdOpt.isPresent() && versionNoOpt.isPresent() && !user.isDefaultUser()) {
            docGetterCallback = versionNoOpt.getAsInt() == DocumentVersion.WORKING_VERSION_NO
                    ? new WorkingDocGetterCallback(documentLanguages, docIdOpt.getAsInt())
                    : new CustomDocGetterCallback(documentLanguages, docIdOpt.getAsInt(), versionNoOpt.getAsInt());
        }

        if (docGetterCallback == null && currentDocGetterCallback != null) {
            docGetterCallback = currentDocGetterCallback.copy(documentLanguages);
        }

        if (docGetterCallback == null) {
            docGetterCallback = new DefaultDocGetterCallback(documentLanguages);
        }

        user.setDocGetterCallback(docGetterCallback);
    }

    static OptionalInt parseUnsignedInt(String string) {
        try {
            return OptionalInt.of(Integer.parseUnsignedInt(string));
        } catch (Exception e) {
            return OptionalInt.empty();
        }
    }

    static OptionalInt parseVersionString(String str) {
        String noOrAlias = StringUtils.trimToEmpty(str);

        return noOrAlias.isEmpty() || noOrAlias.equals(ImcmsConstants.REQUEST_PARAM_VALUE__DOC_VERSION__ALIAS_DEFAULT)
                ? OptionalInt.empty()
                : noOrAlias.equals(ImcmsConstants.REQUEST_PARAM_VALUE__DOC_VERSION__ALIAS_WORKING)
                        ? OptionalInt.of(DocumentVersion.WORKING_VERSION_NO)
                        : parseUnsignedInt(noOrAlias);
    }
}