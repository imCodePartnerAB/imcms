package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.IpAccessRuleDTO;
import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.model.IpAccessRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class IpAccessControllerTest extends MockingControllerTest {
    private static final String PATH = "/ip-rules";

    @Mock
    private IpAccessRuleService ruleService;

    @InjectMocks
    private IpAccessController ipAccessController;

    @Override
    protected Object controllerToMock() {
        return ipAccessController;
    }

    @Test
    void getAllRules() {
        final int size = 6;

        List<IpAccessRule> rules = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            final IpAccessRuleDTO rule = new IpAccessRuleDTO();
            rule.setEnabled(true);
            rule.setIpRange(String.format("192.168.1.%d", i));
            rule.setRestricted(true);
            rules.add(rule);
        }

        given(ruleService.getAll()).willReturn(rules);

        final String response = perform(get(PATH)).getResponse();

        final List<IpAccessRuleDTO> receivedRules = fromJson(response, new TypeReference<List<IpAccessRuleDTO>>() {
        });

        assertNotNull(receivedRules);
        assertTrue(receivedRules.containsAll(rules));
        assertTrue(rules.containsAll(receivedRules));

        then(ruleService).should().getAll();
    }

    @Test
    void createRule() {
        final IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setId(42);

        given(ruleService.create(notNull())).willReturn(rule);

        final String response = perform(post(PATH), rule).getResponse();
        final IpAccessRuleDTO receivedRule = fromJson(response, IpAccessRuleDTO.class);

        assertEquals(receivedRule, rule);

        then(ruleService).should().create(notNull());
    }

    @Test
    void updateRule() {
        final IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(false);
        rule.setRestricted(true);
        rule.setId(42);
        rule.setIpRange("42.42.42.42");

        given(ruleService.update(notNull())).willReturn(rule);

        final String response = perform(put(PATH), rule).getResponse();
        final IpAccessRuleDTO receivedRule = fromJson(response, IpAccessRuleDTO.class);

        assertEquals(rule, receivedRule);

        then(ruleService).should().update(notNull());
    }

    @Test
    void deleteRule() {
        final int fakeRuleId = 42;

        perform(delete(PATH + "/" + fakeRuleId)).andExpect(status().isOk());

        then(ruleService).should().delete(fakeRuleId);
    }
}
