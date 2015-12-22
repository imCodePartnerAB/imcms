package com.imcode.imcms.imagearchive.tag.func;

import com.imcode.imcms.imagearchive.dto.LibraryEntryDto;
import com.imcode.imcms.imagearchive.entity.Images;
import com.imcode.imcms.imagearchive.service.Facade;
import org.apache.commons.lang3.StringUtils;
import org.apache.sanselan.common.RationalNumber;
import org.apache.sanselan.common.RationalNumberUtilities;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.PageContext;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Functions {
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\r\n?");

    public static String newlineToBr(String value) {
        if (value == null) {
            return null;
        } else {
            return NEWLINE_PATTERN.matcher(value).replaceAll("<br/>");
        }
    }

    public static String abbreviate(String value, int maxLength) {
        return StringUtils.abbreviate(value, maxLength);
    }

    public static <T> String join(Collection<T> elements, String separator) {
        if (elements == null || elements.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        Iterator<T> it = elements.iterator();
        while (it.hasNext()) {
            builder.append(it.next());

            if (it.hasNext()) {
                builder.append(separator);
            }
        }

        return builder.toString();
    }

    public static boolean isInArchive(LibraryEntryDto img, PageContext pageContext) {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
        Facade facade = context.getBean(Facade.class);

        List<Images> archiveImages = facade.getImageService().getAllImages();
        for (Images image : archiveImages) {
            if (img != null && image.getImageNm().equals(img.getFileName()) && image.getFileSize() == img.getFileSize()) {
                return true;
            }
        }

        return false;
    }

    public static String doubleToFractionsString(Double number) {
        if (number == null) {
            return null;
        }

        RationalNumber rationalNumber = RationalNumberUtilities.getRationalNumber(number);
        return rationalNumber.numerator + "/" + rationalNumber.divisor;
    }
}
