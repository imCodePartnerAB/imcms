package com.imcode.imcms.domain.dto;

import com.jcabi.w3c.Defect;
import lombok.Data;

/**
 * Class is a wrapper for defect in text validation.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
@Data
class TextValidationDefect {

    private String message;
    private String source;
    private String explanation;
    private int column;
    private int line;

    TextValidationDefect(Defect defect) {
        this.message = defect.message();
        this.source = defect.source().replace("&#60;/body&#62;&#60;/html&#62;", ""); // fixme: what's this???
        this.explanation = defect.explanation();
        this.column = defect.column();
        this.line = defect.line();
    }
}
