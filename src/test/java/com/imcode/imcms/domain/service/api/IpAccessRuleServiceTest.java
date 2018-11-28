package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.IpAccessRuleDTO;
import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.model.IpAccessRule;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class IpAccessRuleServiceTest extends WebAppSpringTestConfig {
    private final static String IP_RANGE_TEMPLATE_IP_V4 = "192.168.0.1-192.168.0.2";
    private final static String IP_RANGE_TEMPLATE_IP_V6_SHORT = "2001:db8:ac10:fe01::-2001:db8:ac10:fe02::";
    private final static String IP_TEMPLATE_V6_SHORT = "2001:db8:ac10:fe01::";
    private final static String IP_TEMPLATE_V4 = "0.0.0.2";
    private final static String IP_RANGE_TEMPLATE_IP_V6_FULL = "2001:0DB8:AC10:FE01:000:0000:0000:0001-2001:0DB8:AC10:FE01:000:0000:0000:0030";

    @Autowired
    private IpAccessRuleService accessRuleService;

    @Test
    public void create_WhenEntityNotExist_Expect_EntityCreated() {
        assertTrue(accessRuleService.getAll().isEmpty());

        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);
        rule.setRestricted(true);

        accessRuleService.create(rule);

        List<IpAccessRule> savedRules = accessRuleService.getAll();

        assertFalse(accessRuleService.getAll().isEmpty());
        assertEquals(1, savedRules.size());

        assertEquals(rule.getIpRange(), savedRules.get(0).getIpRange());
    }

    @Test
    public void getById_WhenEntityExist_ExpectCorrectEntity() {
        assertTrue(accessRuleService.getAll().isEmpty());

        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);
        rule.setRestricted(true);

        IpAccessRule savedRule = accessRuleService.create(rule);

        IpAccessRule ruleToTest = accessRuleService.getById(savedRule.getId());

        assertFalse(accessRuleService.getAll().isEmpty());
        assertEquals(savedRule, ruleToTest);
    }

    @Test
    public void getAll_WhenEntitiesExist_ExpectCorrectEntitiesList() {
        assertTrue(accessRuleService.getAll().isEmpty());

        final int size = 6;
        for (int i = 0; i < size; i++) {
            final IpAccessRule rule = new IpAccessRuleDTO();
            rule.setEnabled(true);
            rule.setIpRange(i % 2 == 0 ? IP_RANGE_TEMPLATE_IP_V4 : IP_RANGE_TEMPLATE_IP_V6_FULL);
            rule.setRestricted(true);
            accessRuleService.create(rule);
        }

        final List<IpAccessRule> savedRules = accessRuleService.getAll();

        assertFalse(accessRuleService.getAll().isEmpty());
        assertEquals(size, savedRules.size());
    }

    @Test
    public void update_WhenEntityExist_ExpectUpdatedEntity() {
        assertTrue(accessRuleService.getAll().isEmpty());

        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);
        rule.setRestricted(true);

        IpAccessRule savedRule = accessRuleService.create(rule);
        IpAccessRule ruleToTest = accessRuleService.getById(savedRule.getId());

        ruleToTest.setIpRange(IP_RANGE_TEMPLATE_IP_V6_FULL);

        assertFalse(accessRuleService.getAll().isEmpty());
        assertEquals(savedRule.getId(), ruleToTest.getId());
        assertEquals(IP_RANGE_TEMPLATE_IP_V6_FULL, ruleToTest.getIpRange());
        assertEquals(savedRule.isEnabled(), ruleToTest.isEnabled());
    }

    @Test
    public void delete_WhenEntityExist_ExpectEntityDeleted() {
        assertTrue(accessRuleService.getAll().isEmpty());

        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);
        rule.setRestricted(true);

        IpAccessRule savedRule = accessRuleService.create(rule);

        accessRuleService.delete(savedRule.getId());

        assertTrue(accessRuleService.getAll().isEmpty());
    }


    @Test
    public void delete_WhenEntityNotExist_ExpectCorrectException() {
        final int fakeId = -1;
        assertNull(accessRuleService.getById(fakeId));
        assertThrows(EmptyResultDataAccessException.class, () -> accessRuleService.delete(fakeId));
    }

    @Test
    public void isAllowedToAccess_WhenUserNotInRules_ExpectTrue() throws UnknownHostException {
        assertTrue(accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V6_SHORT), Imcms.getUser()));
    }

    @Test
    public void isAllowedToAccess_WhenRestrictedByUser_ExpectFalse() throws UnknownHostException {
        UserDomainObject user = Imcms.getUser();
        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setUserId(user.getId());

        IpAccessRule savedRule = accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V6_SHORT), user));
    }

    @Test
    public void isAllowedToAccess_WhenRestrictedByRole_ExpectFalse() throws UnknownHostException {
        UserDomainObject user = Imcms.getUser();
        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setRoleId(Roles.USER.getId());

        IpAccessRule savedRule = accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V6_SHORT), user));
    }

    @Test
    public void isAllowedToAccess_WhenRestrictedByIpRange_ExpectFalse() throws UnknownHostException {
        UserDomainObject user = Imcms.getUser();
        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_FULL);

        IpAccessRule savedRule = accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V6_SHORT), user));
    }

    @Test
    public void isAllowedToAccess_WhenRestrictedByIpRangeDisabled_ExpectTrue() throws UnknownHostException {
        UserDomainObject user = Imcms.getUser();
        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(false);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_FULL);

        IpAccessRule savedRule = accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V6_SHORT), user));
    }

    @Test
    public void isAllowedToAccess_WhenAllowedByIpRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject user = Imcms.getUser();
        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);

        IpAccessRule savedRule = accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V4), user));
    }

    @Test
    public void isAllowedToAccess_WhenAllowedByIpRangeAndRole_ExpectTrue() throws UnknownHostException {
        UserDomainObject user = Imcms.getUser();
        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);
        rule.setRoleId(Roles.USER.getId());

        IpAccessRule savedRule = accessRuleService.create(rule);

        accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V4), user);
    }

    @Test
    public void isAllowedToAccess_WhenAllowedByIpRangeAndUserId_ExpectTrue() throws UnknownHostException {
        UserDomainObject user = Imcms.getUser();
        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);
        rule.setUserId(user.getId());

        IpAccessRule savedRule = accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V4), user));
    }

    @Test
    public void isAllowedToAccess_WhenAllowedByIpRangeAndRoleId_ExpectFalse() throws UnknownHostException {
        UserDomainObject user = Imcms.getUser();
        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);
        rule.setRoleId(Roles.SUPER_ADMIN.getId());

        IpAccessRule savedRule = accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V4), user));
    }

    @Test
    public void isAllowedToAccess_WhenRestrictedByUserIdAndAllowedByIp_ExpectFalse() throws UnknownHostException {
        assertTrue(accessRuleService.getAll().isEmpty());

        UserDomainObject user = Imcms.getUser();

        IpAccessRule allowRule = new IpAccessRuleDTO();

        allowRule.setEnabled(true);
        allowRule.setRestricted(false);
        allowRule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);
        allowRule.setRoleId(Roles.SUPER_ADMIN.getId());

        IpAccessRule restrictRule = new IpAccessRuleDTO();

        restrictRule.setEnabled(true);
        restrictRule.setRestricted(true);
        restrictRule.setUserId(user.getId());

        accessRuleService.create(allowRule);
        accessRuleService.create(restrictRule);

        assertEquals(2, accessRuleService.getAll().size());

        assertFalse(accessRuleService.isAllowedToAccess(InetAddress.getByName(IP_TEMPLATE_V4), user));
    }

}
