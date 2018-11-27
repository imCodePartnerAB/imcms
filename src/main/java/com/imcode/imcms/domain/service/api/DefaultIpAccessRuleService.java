package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.IpAccessRuleDTO;
import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.model.IpAccessRule;
import com.imcode.imcms.persistence.entity.IpAccessRuleJPA;
import com.imcode.imcms.persistence.repository.IpAccessRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultIpAccessRuleService implements IpAccessRuleService {

    private final IpAccessRuleRepository ipAccessRuleRepository;

    DefaultIpAccessRuleService(IpAccessRuleRepository ipAccessRuleRepository) {
        this.ipAccessRuleRepository = ipAccessRuleRepository;
    }

    @Override
    public IpAccessRule getById(int id) {
        final IpAccessRuleJPA rule = ipAccessRuleRepository.findOne(id);

        return (null == rule) ? null : new IpAccessRuleDTO(rule);
    }

    @Override
    public List<IpAccessRule> getAll() {
        return ipAccessRuleRepository.findAll().stream()
                .map(IpAccessRuleDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public IpAccessRule create(IpAccessRule rule) {
        return new IpAccessRuleDTO(ipAccessRuleRepository.save(new IpAccessRuleJPA(rule)));
    }

    @Override
    public IpAccessRule update(IpAccessRule rule) {
        return new IpAccessRuleDTO(ipAccessRuleRepository.save(new IpAccessRuleJPA(rule)));
    }

    @Override
    public void delete(int ruleId) {
        ipAccessRuleRepository.delete(ruleId);
    }
}
