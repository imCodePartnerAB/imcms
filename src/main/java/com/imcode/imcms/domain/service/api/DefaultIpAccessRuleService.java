package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.persistence.repository.IpAccessRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultIpAccessRuleService implements IpAccessRuleService {

    private final IpAccessRuleRepository ipAccessRuleRepository;

    DefaultIpAccessRuleService(IpAccessRuleRepository ipAccessRuleRepository) {
        this.ipAccessRuleRepository = ipAccessRuleRepository;
    }
}
