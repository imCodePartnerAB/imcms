package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.IpAccessRule;

import java.util.List;

public interface IpAccessRuleService {

    IpAccessRule getById(int id);

    List<IpAccessRule> getAll();

    IpAccessRule create(IpAccessRule rule);

    IpAccessRule update(IpAccessRule rule);

    void delete(int ruleId);

}
