package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.IpAccessRule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IpAccessRuleDTO extends IpAccessRule {

    private static final long serialVersionUID = 8100237945720178767L;

    public IpAccessRuleDTO(IpAccessRule from) {
        super(from);
    }
}
