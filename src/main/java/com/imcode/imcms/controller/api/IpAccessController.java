package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.IpAccessRuleDTO;
import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.model.IpAccessRule;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ip-rules")
public class IpAccessController {
    private final IpAccessRuleService accessRuleService;

    public IpAccessController(IpAccessRuleService accessRuleService) {
        this.accessRuleService = accessRuleService;
    }

    @GetMapping
    public List<IpAccessRule> getAllRules() {
        return accessRuleService.getAll();
    }

    @CheckAccess
    @PostMapping
    public IpAccessRule createRule(@RequestBody IpAccessRuleDTO rule) {
        return accessRuleService.create(rule);
    }

    @CheckAccess
    @PutMapping
    public IpAccessRule updateRule(@RequestBody IpAccessRuleDTO rule) {
        return accessRuleService.update(rule);
    }

    @CheckAccess
    @DeleteMapping("/{ruleId}")
    public void deleteRule(@PathVariable Integer ruleId) {
        accessRuleService.delete(ruleId);
    }
}
